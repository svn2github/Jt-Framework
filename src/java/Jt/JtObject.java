
package Jt;

import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import java.beans.*;



/**
  * Root class of the Jt Class hierarchy; it implements the functionality 
  * required by the Jt Messaging pattern.
  */


public class JtObject implements Serializable, JtInterface {

   private static String version = "1.5 - 11/01/06";    // Framework version
   private Hashtable objTable = null;			// Children of this object
   private static Hashtable resTable = null;            // Attribute values
   private static int initted = 0;
   private static int log = 0;
   private static String logFile = null;                // Log file
   private static PrintWriter logStream = null;
   private static String resourceFile = ".Jtrc";        // Resource file
   private String objName = null;			// Qualified object name
   private int objTrace = 0;                            // Enable/disable trace messages
   private Object objException;	                        // Exception
   private int objStatus = 0;				// Status

   // The following attributes are being deprecated or changed
   private long objId;					// ID
   private String objStore = ".Jt";			// Directory where the
							// object should
							// be stored

   private String objPath = null;


   

   public JtObject () {

	//objId = id++;

        if (initted == 0) {
           initialize ();
           initted = 1;
        }
	if (log == 1)
           setObjTrace (1);
    } 


   /**
    * Creates a Jt object of the specified class. The object is assigned the ID passed as parameter. 
    * The new object is a child of this object. 
    * If another child with the same ID already exists, an error is produced.
    * After creating the object, it sets its attributes using the Jt resource file. 
    * @param class_name class name
    * @param id  object id
    * @return object created or  null
    */

   public Object createObject (Object class_name, Object id) {
      Object obj, tmp = null;
      Class jtclass;
	

	handleTrace ("JtObject.createObject:" + class_name + "," + id);

	if (class_name == null || id == null) {
	   handleError ("JtObject.createObject: invalid paramenters");
	   return (null);
	}


/*
        if (objTable == null)
          objTable = new Hashtable ();
*/

	obj = lookupObject (id);

	// check for duplicates
	if (obj != null) {
           if (log == 0)
	     handleWarning 
		("JtObject.createObject: unable to create a duplicate entry " + id);
           else
	     handleError 
		("JtObject.createObject: unable to create a duplicate entry " + id);
	   return (null);
	}

	try {
	   jtclass = Class.forName ((String) class_name);
	} catch (Exception e) {
	   handleException (e);
	   return (null);
	}

	// Create a new instance of the object

        try {


           if (JtSingleton.class.isAssignableFrom (jtclass)) {
             obj = JtSingleton.getInstance ();
             if (obj == null) {
               obj = jtclass.newInstance ();
               JtSingleton.setInstance (obj);
               tmp = JtSingleton.getInstance ();        
               // Although unlikely, another thread may create the Singleton instance
               // first. In this case, an error should be produced. 
               if (tmp != obj) {
                 handleError 
                 ("JtObject.createObject: attempt to create another instance of a Singleton (" +
                 class_name + ")"); 
                 return (null); 
               }


             } else {
               handleError 
               ("JtObject.createObject: attempt to create another instance of a Singleton (" +
                 class_name + ")"); 
               return (null); 
             }
           } else
             obj = jtclass.newInstance ();

           if (obj instanceof JtObject) {
           ((JtObject)obj).setObjName (absolute_name ((String) id));
	   if (log == 1)
              ((JtObject)obj).setObjTrace (1);
	   else
              ((JtObject)obj).setObjTrace (this.getObjTrace ());
           }

           // ((JtObject)obj).setObjSession (this.getObjSession ());
	   // add the new object to the object table
           add (absolute_name ((String) id), obj);
           load_object_resources (obj, class_name);
         } catch (Exception e) {
            handleException (e);
	 }


	 return (obj);
   }



   // absolute_name: determine the absolute name of the object

   String absolute_name (String name) {
	if (name == null)
	   return (null);
	
	if (name.indexOf (".") > 0)
	   return (name);

	if (this.getObjName () == null)
	   return (name);


	return (this.getObjName () + "." + name);

   }


   String calculateAbsoluteName (Object id) {
     String name;

	if (id == null)
	   return (null);

        if (id instanceof JtObject) {
          if (((JtObject) id).getObjName () == null)
            handleWarning ("calculateAbsoluteName:JObject is missing name" +
              ((JtObject) id).getObjName ());
          return (((JtObject) id).getObjName ());
        }

        if (id instanceof String) {
          name = (String) id;
        } else
          name = id.toString ();
	
	if (name.indexOf (".") > 0)
	   return (name);

	if (this.getObjName () == null)
	   return (name);

	return (this.getObjName () + "." + name);

   }


   /**
    * Looks for an object based on its ID. 
    * @param id object ID
    * @return object found or null
    */

   public Object lookupObject (Object id) {
     String obj_id;
     Hashtable ht;

        if (!id.getClass().getName().equals ("java.lang.String"))
        	return (id); // check

	
	obj_id = absolute_name ((String) id);

//        if (objSession == null) {

          if (objTable == null)
            return (null);

	  return (objTable.get (obj_id));
//        }

/*
        if (sessionTable == null)
          return (null);

        ht = (Hashtable) sessionTable.get (objSession);

        if (ht == null) 
          return (null);
        
        return (ht.get (obj_id));
*/
   }



  private boolean checkModifiers (Class cl, String prop) {

    //Class cl;
    Field field;
    int mod;


    if (cl == null || prop == null)
      return (false);


    field = null;
    try {
      field = cl.getDeclaredField (prop); // property dup property names
      //System.out.println ("class:" + cl.getName ());

    } catch (Exception e) {
      
      //handleException (e);

      if (cl.getSuperclass () == null) {
        handleException (e);
        return (false);
      }
    }
 
    if (field == null) {
      cl = cl.getSuperclass ();
      return (checkModifiers (cl, prop));
    }

    mod = field.getModifiers ();

    if (Modifier.isTransient (mod)) {
      return (false);
    }
    if (Modifier.isStatic (mod)) {
      return (false);
    } 
    return (true);       
  }

  private Object copyObject (Object obj, Object target) {
   Object args[];
   PropertyDescriptor[] prop;
   Class p;
   Method m;
   BeanInfo info = null;
   Object value, svalue = null;
   int i;


     if (obj == null || target == null)
       return (null);

     if (obj.getClass () != target.getClass ()) {
       handleError 
         ("Jt.copyObject: object classes should be the same (source and target)");
       return (null);
     }

     try {
       info = Introspector.getBeanInfo(
              obj.getClass (), java.lang.Object.class);
     } catch(Exception e) {
        handleException (e);
        return (null); //check
     }

     prop = info.getPropertyDescriptors();


     for(i = 0; i < prop.length; i++) {

       if (!checkModifiers (obj.getClass (),prop[i].getName())) {
         continue;
       }

       value = getValue (obj, prop[i].getName()); // check

       svalue = null;
       if (value instanceof Integer ||
             value instanceof Long || 
             value instanceof Float ||
             value instanceof Byte ||
             value instanceof Boolean ||
             value instanceof Short ||
             value instanceof Double) {
         svalue =  value.toString (); 
       } else
         svalue = value;

       
       //if (svalue != null) 
       setValue (target, prop[i].getName(), svalue);

     }

      return (null); // check
  } 



   /** 
     * Sets the value of an attribute. 
     * This method is able to convert the value from String to the correct type.
     * Automatic convertion from String is done for the following types:
     * byte, short, int, long, float, double, boolean and java.util.Date.
     * @param id object id
     * @param att attribute name
     * @param value attribute value
     */

   public void setValue (Object id, Object att, Object value) {

    Object obj;
    Object args[];
    PropertyDescriptor[] prop;
    int i;
    Class p;
    Method m;
    BeanInfo info = null;

/*    
        if (objSession != null)
	  handleTrace ("Session(" + objSession + "): JtObject.setValue:" + id + "," + att + "," + value);
        else
*/
	  handleTrace ("JtObject.setValue:" + id + "," + att + "," + value);

	if (id == null | att == null) {
	   handleError ("JtObject.setValue: invalid paramenters");
	   return;
	}

        obj = lookupObject (id);

        if (obj == null) {
            handleError ("setValue: unable to find object: " + id);
            return;
        }

/*
        if (obj instanceof JtValueObject) {
          ((JtValueObject) obj).setValue (id, att, value);
          return;
        }
*/

        try {

            info = Introspector.getBeanInfo(
              obj.getClass (), java.lang.Object.class);
        } catch(Exception e) {
            handleException (e);
            return;
        }

        prop = info.getPropertyDescriptors();
        for(i = 0; i < prop.length; i++) {

            if (prop[i].getName().equals ((String) att)) {

              p = prop[i].getPropertyType();
              args = new Object[1];
              //System.out.println ("setValue:type" + p);
//              if (value == null) {
//               args[0] = value;

              try {
              if (p.getName().equals ("java.lang.String") ){
               args[0] = value;
              } else if (p.getName().equals ("byte") ){
               args[0] = (value instanceof String)?Byte.valueOf ((String) value):value;
              } else if (p.getName().equals ("short") ){
               args[0] = (value instanceof String)?Short.valueOf ((String) value):value;
              } else if (p.getName().equals ("int") ){
               args[0] = (value instanceof String)?Integer.valueOf ((String) value):value;
              //} else if (p.getName().equals ("java.lang.Long") ){
              } else if (p.getName().equals ("long") ){
               args[0] = (value instanceof String)?Long.valueOf ((String) value):value;
	      } else if (p.getName().equals ("float") ){
               args[0] = (value instanceof String)?Float.valueOf ((String) value):value;
	      } else if (p.getName().equals ("double") ){
               args[0] = (value instanceof String)?Double.valueOf ((String) value):value;
	      } else if (p.getName().equals ("boolean") ){
               args[0] = (value instanceof String)?Boolean.valueOf ((String) value):value;
	      } else if (p.getName().equals ("java.util.Date") ){
               DateFormat df = DateFormat.getDateInstance();
               //SimpleDateFormat df;
               if (value instanceof String)
               try {
                 //df = new SimpleDateFormat ((String) value);
                 args[0] = df.parse ((String) value);
                 //args[0] = new SimpleDateFormat ((String) value);
               } catch (Exception e) {
                 handleException (e);
                 return;
               }
               else
                args[0] = value;
	      } else
	       args[0] = value;
              } catch (Exception e) {
                 handleException (e);
                 return;
              }

              try {
               m = prop[i].getWriteMethod ();
               if (m == null) {
                 handleError 
                  ("JtObject.setValue failed:missing setter for attribute " + att);  
                 return;
               }
               m.invoke (obj, args);
              } catch (Exception e) {
               handleException (e);
              }
              return;

	    }
	}

        handleError ("JtObject.setValue: invalid attribute:"+  att);


   }


   /** 
     * Gets the value of an attribute. An object is always returned.  
     * For primitive types, the corresponding object type is returned.
     * For instance, Integer is returned if
     * the attribute is of type int. 
     *
     * @param id object id
     * @param att attribute name
     * @return attribute value
     */

   public Object getValue (Object id, Object att) {
   Object obj;
   Method m;
   Class p;
   BeanInfo info = null;
   PropertyDescriptor[] prop;
   int i;

	handleTrace ("JtObject.getValue: " + id + "," + att);

	if (id == null || att == null) {
	   handleError ("JtObject.getValue: invalid paramenters");
	   return (null);
	}

	obj = lookupObject (id);

        if (obj == null) {
           handleError ("getValue: unable to find object " + id);
           return (null);
        }
/*
        if (obj instanceof JtValueObject) {
          return (((JtValueObject) obj).getValue (id, att));
        }
*/

        try {

          info = Introspector.getBeanInfo(obj.getClass (),
             java.lang.Object.class);

        } catch(Exception e) {
            handleException (e);
            return (null);
        }

        prop = info.getPropertyDescriptors();

        for(i = 0; i < prop.length; i++) {


         if (prop[i].getName().equals ((String) att))    {

                try {
                  m = prop[i].getReadMethod ();
                  if (m == null) {
                    handleError 
			("JtObject.getValue: getReadMethod returned null");
                    return (null);
                  }
                  return (m.invoke (obj, null));
                } catch (Exception e) {
                  handleException(e);
                  return (null);
                }
	 }
	}
        handleError ("JtObject.getValue:invalid attribute:" + att);
        return (null);
	   
    }


   /**
    * Removes an object. The object is removed from the table of children kept by 
    * its parent object. A JtREMOVE message is sent to the object. All Jt objects
    * should process JtREMOVE and release any resources that were allocated 
    * (sockets, Database connections, etc.).  
    * @param id  object id
    */
   
   public void removeObject (Object id) {
      Object obj;

	handleTrace ("JtObject.removeObject: " + id);

	if (id == null) {
	   handleError 
		("JtObject.removeObject: invalid paramenters");
	   return;
	}
	
	obj = lookupObject (id);

	if (obj == null) {
           handleError ("JtObject.removeObject: object not found: "+
		id);
	   return;
	}

        sendMessage (obj, new JtMessage ("JtREMOVE"));

        //if (id instanceof String)
	if (remove (calculateAbsoluteName (id)) == null)
	    handleError 
		("JtObject.removeObject: unable to remove object " + id);

   }

   /**
    * Destroys an object (deprecated).  
    * @param id  object id
    */

  
   public void destroyObject (Object id) {
      Object obj;
        handleWarning 
          ("JtObject.destroyObject has been deprecated. Please use removeObject");
        removeObject (id);

/*
	handleTrace ("JtObject.destroyObject: " + id);

	if (id == null) {
	   handleError 
		("JtObject.destroyObject: invalid paramenters");
	   return;
	}
	
	obj = lookupObject (id);

	if (obj == null) {
           handleError ("JtObject.destroyObject: object not found: "+
		id);
	   return;
	}

        sendMessage (obj, new JtMessage ("JtDESTROY"));

	if (remove (absolute_name ((String) id)) == null)
	   handleError 
		("JtObject.destroyObject: unable to remove object " + id);
*/
   }

   // realizeObject: realize an object (being deprecated)
  
   void realizeObject (Object id) {
      Object obj;

        handleTrace ("JtObject.realizeObject:" + id);

        if (id == null) {
           handleError
                ("JtObject.realizeObject: invalid paramenters");
           return;
        }

        obj = lookupObject (id);

        if (obj == null) {
           handleError ("JtObject.realizeObject: object not found:"+
                id);
           return;
        }

        ((JtObject) obj).realize ();
   }


   // activateObject: activate an object (being deprecated)
   
   void activateObject (Object id) {
      Object obj;

	handleTrace ("JtObject.activateObject: " + id);

	if (id == null) {
	   handleError 
		("JtObject.activateObject: invalid parameters");
	   return;
	}
	
	obj = lookupObject (id);

	if (obj == null) {
           handleError ("JtObject.activateObject: object not found: "+
		id);
	   return;
	}

	((JtObject) obj).activate ();
   }


   // Save Object (being deprecated)

   void save () {
    ObjectOutputStream   p = null;
    BufferedOutputStream ostream = null;
    String fpath = null;

    if (objPath == null) {

    if (this.getObjName () != null) {
	fpath = ".Jt/" + this.getObjName ();
    } else {
	fpath = ".Jt/" + this.getObjId ();
    }

    } else
	fpath = objPath;

    // Try to create the directory if it is not already there
    create_dir (fpath);

    try {

        ostream = new BufferedOutputStream (new FileOutputStream (fpath));

        p       = new ObjectOutputStream(ostream);
    } catch (Exception e) {
        handleException (e);
        return;
    }


    try {
        p.writeObject(this);
        p.flush();
        p.close();
    } catch (Exception e) {
        handleException (e);
    }
  }


  // restore_object: restore an object (being deprecated)

  Object restore () {
    ObjectInputStream   p = null;
    BufferedInputStream istream = null;
    String fpath;
    Object obj = null;
    File file = null;

    
    if (objPath == null)
       if (this.getObjName () != null) {
	fpath = ".Jt/" + this.getObjName ();
       } else {
	fpath = ".Jt/" + this.getObjId ();
       }
    else
       fpath = objPath;

    handleTrace ("Jt.restore: " + fpath); 

    if (fpath == null)
        return (null);

    file = new File (fpath);

    if (!file.exists ()) {
	handleTrace ("Jt.restore: " + fpath + " does not exists");
	return (null);
    }

    // check file
    try {
        istream = new BufferedInputStream (new FileInputStream
                (fpath));

        p       = new ObjectInputStream(istream);
    } catch (Exception e) {
        handleException (e);
        return (null);
    }

    try {
        obj =  p.readObject();
        p.close();
    } catch (Exception e) {
        handleException (e);
    }

    return (obj);

   }



   // handle_message_trace: 

   private void handle_message_trace (JtMessage msg) {

        String nl = System.getProperty("line.separator");
	String tmp;


    
	tmp = "JtMessage.MsgId:" + msg.getMsgId ();
        if (msg.getMsgSubject () != null)
	   tmp +=nl+ "JtMessage.MsgSubject:"+ msg.getMsgSubject();
        if (msg.getMsgContent () != null)
	   tmp += nl + "JtMessage.MsgContent:"+ msg.getMsgContent();
	handleTrace (tmp);

   }

   private Object object_name (Object obj) {
     String name;

     if (!(obj instanceof JtObject))
       return (obj);

     name = ((JtObject) obj).getObjName (); 
     if (name == null)
       return (obj);

     return (name);
   }

   /**
     * Sends a Jt Message to another object. 
     *
     * @param id    object ID
     * @param msgid message ID
     */

   public Object sendMessage (Object id, Object msgid) {
   Object obj;
   Object msg;
   Object reply;


/*
     if (objSession != null)
       handleTrace ("Session(" + objSession + "): JtObject.sendMessage:"+ 
          object_name (id) + ", "+ object_name (msgid) + " ...");
     else
*/
       handleTrace ("JtObject.sendMessage:"+ 
          object_name (id) + ", "+ object_name (msgid) + " ...");

     if (id == null || msgid == null) {
	handleError ("JtObject.sendMessage: invalid paramenters");
	return (null);
     }

     obj = lookupObject (id);

     if (obj == null) {
	handleError ("JtObject.sendMessage: unable to find object " + id);
	return (null);
     }
	
     msg = lookupObject (msgid);

     if (msg == null) {
	handleError ("JtObject.sendMessage: unable to find object " + msgid);
	return (null);
     }
    
     if (msg instanceof JtMessage) 
	handle_message_trace ((JtMessage) msg);

     // If the recipient object is running in a separate/independent thread,
     // enqueue the message (Asynchronuos processing)

     if (obj instanceof JtThread)
       reply = ((JtThread) obj).enqueueMessage ((Object) msg);
     else 
       reply = ((JtInterface) obj).processMessage ((Object) msg);
     return (reply);

   }


   private Object encodeObject (Object obj) {

     ByteArrayOutputStream stream = new ByteArrayOutputStream ();
     XMLEncoder e; 
     Object result = null;


     if (obj == null)
       return (null);

     try {

       e = new XMLEncoder(
                          new BufferedOutputStream(stream));
       e.writeObject(obj);
       e.close();
       result = stream.toString ();

     } catch (Exception ex) {
       handleException (ex);
       return (null);
     }    
     return (result); 

   }

   private Object decodeObject (Object obj) {

     ByteArrayInputStream stream;
     Object result = null;
     XMLDecoder d;
     

     if (obj == null)
       return (null);

     stream = new ByteArrayInputStream (((String) obj).getBytes ());

     try {

       d = new XMLDecoder(
                          new BufferedInputStream(stream));
       result = d.readObject();
       d.close();

     } catch (Exception ex) {
       handleException (ex);
     } 
     return (result);    

   }

   /**
     * Process a Jt Message. This is the only method required by the Jt interface (Jt Interface).
     * 
     *
     * @param message  Jt Message
     */


   public Object processMessage (Object message) {

     String msgid;
     JtMessage msg = (JtMessage) message;

     // handleTrace ("JtObject.processMessage called");
     if (msg == null)
       return (null);

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
       return (null);

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtSET_VALUE")) {
       setValue (msg.getMsgSubject (),
                 msg.getMsgContent (),
                 msg.getMsgData());
       return (null);
     }

     if (msgid.equals ("JtGET_VALUE")) {
       if (msg.getMsgSubject () == null)
         return (getValue (this,
                 msg.getMsgContent ()));
       else
         return (getValue (msg.getMsgSubject (),
                 msg.getMsgContent ()));

     }

     if (msgid.equals ("JtCREATE_OBJECT")) {
       createObject (msg.getMsgContent (),
                 msg.getMsgData());
       return (null);
     }

     if (msgid.equals ("JtREMOVE_OBJECT")) {
       removeObject (msg.getMsgContent ());
       return (null);
     }

     if (msgid.equals ("JtSEND_MESSAGE")) {
       return (sendMessage (msg.getMsgContent (),
                    msg.getMsgData ()));

     }

     if (msgid.equals ("JtCOPY_OBJECT")) {
       return (copyObject (msg.getMsgContent (),
                    msg.getMsgData ()));

     }

     // Encode Object (XML format)

     if (msgid.equals ("JtENCODE_OBJECT")) {
       return (encodeObject (msg.getMsgContent ()));
     }

     if (msgid.equals ("JtDECODE_OBJECT")) {
       return (decodeObject (msg.getMsgContent ()));
     }

     if (msgid.equals ("JtPRINT_OBJECT")) {
       System.out.println (encodeObject (this));
       return (this);
     }

     handleError ("JtObject.processMessage: invalid msg ID:"
     + msg.getMsgId ());
     return (null);
   }

   // Open log file

   private void open_logfile () {  
     FileOutputStream fs;

          if (logFile == null || logFile.equals (""))
            return;

          if (logFile.equals ("stderr")) { // stderr
            logStream = null;
            return;
          }

          try {
            fs = new FileOutputStream (logFile);
            logStream = new PrintWriter (fs); 
          } catch (Exception e) {
            logStream = null;
            handleException (e);
            return;
          }

          handleTrace ("JtObject.open_logfile: opened log file ... " +
			logFile);
          //handleTrace ("JtObject.open_logfile:user.dir:" + 
          //  System.getProperty ("user.dir"));
          //handleTrace ("JtObject.open_logfile:user.home:" + 
          //  System.getProperty ("user.home"));
   }

   // Initialize: initialize Jt

   void initialize () {

      String rfile;

      logFile = System.getProperty ("Log");
      rfile = System.getProperty ("Resources");

      if (rfile != null)
        setResourceFile (rfile);

      if (logFile != null) {
	log = 1; // show debugging messages
	this.setObjTrace (1); 
        open_logfile (); 
      }
      //if (logFile != null)
        //open_logfile ();

      handleTrace ("Initializing Jt " + version + "...");
      load_resources ();
	
   }


   // realize
   void realize () {

   }

   // activate
   void activate () {

   }


   // destroy
   void destroy () {

   }

   /** 
     * Handles exceptions. Jt provides a consistent way of handling exceptions:
     * The exception is stored using the objException attribute.
     * The exception message and the stack trace are sent to the screen or a log file.
     * This method should be called each time an exception is detected.
     * Overide this method if your application requires special exception handling.
     * @param e exception
     */

   public void handleException (Throwable e) {

	// An exception has occured. Update the objException attribute
	objException = e;
	e.printStackTrace ();

        if (logStream == null)
           return;

        logStream.println (e);
        logStream.flush ();
   }


   /** 
     * Handles trace messages. This method should be called each time a 
     * message needs to be logged. Trace messages are send to the screen or a log file
     * depending on the logFile attribute. The objTrace attribute is used to enable/disable
     * logging. Override this method if your application requires special logging capabilities.
     * @param msg trace message
     */
   
   public void handleTrace (String msg) {

	if (objTrace <= 0)
          return;

        if (logStream == null) {
	   System.err.println (msg);
           return;
        }

        logStream.println (msg);
        logStream.flush ();
   }

   /** 
     * Handles warning messages. This method should be called each time a warning
     * message needs to be logged. Trace messages are send to the screen or a log file
     * depending on the logFile attribute. Warning messages are always logged regardless
     * of the value of the objTrace attribute.
     * @param msg trace message
     */

   public void handleWarning (String msg) {

/*
        if (objTrace > 0)
*/
           
        if (logStream == null) {
           System.err.println (msg);
           return;
        }

        logStream.println (msg);
        logStream.flush ();
   }

   // handleError: handle error messages

   /** 
     * Handles error messages. This method generates a JtException which 
     * is handled by the handleException method.
     * This causes the error message and the stack trace to be sent to the screen or a log file.
     * This method should be called each time an error is detected.
     * Override this method if your application requires special error handling.
     * @param msg error message
     */

   public void handleError (String msg) {
	try {

	  // generate a JtException 
	  throw new JtException (msg);
	
	} catch (JtException e) {

	   handleException (e);
	}

   }



   // add: add an object to the Object table

   private void add (String id, Object obj) {
   //private synchronized void add (String id, Object obj) {

     Hashtable ht;

	if (id == null || obj == null)
	   return;


//        if (objSession == null) {

          if (objTable == null)
            objTable = new Hashtable ();
          objTable.put (id, obj);
//        handleTrace ("JtObject.add:" + id + ":" + obj);
          return;
//        }

/*
        if (sessionTable == null)
          sessionTable = new Hashtable ();

   
        ht = (Hashtable) sessionTable.get (objSession);

        if (ht == null) {
          
          ht = new Hashtable ();
          sessionTable.put (objSession, ht);
          //handleTrace ("JtObject.add: new session: " + objSession);      
        }
 

        ht.put (id, obj);
*/
   }

   // remove: remove an object from the object table

   //private synchronized Object remove (String id) {
   private Object remove (Object id) {

     Hashtable ht;

	if (id == null)
	   return (null);

//        if (objSession == null) {       

          if (objTable == null)
            return (null);

          if (objTable.get (id) == null)
            handleError ("JtObject.remove: unable to remove object (not found):" +
              id);

          return (objTable.remove (id));
//        }  

/*
        if (sessionTable == null)
          return (null);

        ht = (Hashtable) sessionTable.get (objSession);

        if (ht == null) {
          handleError ("JtObject.remove: invalid session table");
          return (null);
        }

        return (ht.remove (id));
*/
   }



   /** 
     * Gets the name of the file used for logging purposes (logFile).
     */


   public String getLogFile() {
    return logFile;
   }


   /** 
     * Specifies the file to be used for logging purposes (logFile).
     * @param newLogFile name of the log file
     */

   public void setLogFile(String newLogFile) {

    if (newLogFile != null && logFile != null)
      if (newLogFile.equals(logFile))
        return;

    logFile = newLogFile;

    log = 1;
    this.setObjTrace (1);
    if (logFile != null)
      open_logfile ();

   }


   /** 
     * Gets the name of the Jt resource file. The default value for this attribute is '.Jtrc'.
     * Attribute values are loaded from this resource file just after the object is created.
     */

   public String getResourceFile() {
    return resourceFile;
   }


   /** 
     * Specifies the file to be used as the Jt resource file. The default value for this attribute is '.Jtrc'.
     * Attribute values are loaded from this resource file just after the object is created.
     * @param newResourceFile Jt resource file
     */

   public void setResourceFile(String newResourceFile) {
    resourceFile = newResourceFile;

    if (resourceFile != null)
      load_resources ();

   }


   /** 
     * Gets the value of the object ID. This attribute contains the qualified name of the object.
     */

   public String getObjName() {
    return objName;
   }


   /** 
     * Sets the value of the object ID. This attribute contains the qualified name of the object.
     */

   public void setObjName(String newObjName) {
    objName = newObjName;
   }

   // Object Id (Being


   long getObjId() {
    return objId;
   }

   void setObjId(long newObjId) {
    objId = newObjId;
   }



   /** 
     * Gets the value of objTrace. This attribute enables/disables the logging of trace messages.
     */

   public int getObjTrace() {
    return objTrace;
   }

   /** 
     * Gets the value of objTrace. This attribute enables/disables the logging of trace messages.
     */

   public void setObjTrace(int newObjTrace) {
    objTrace = newObjTrace;
   }


   /** 
     * Gets the object status (objStatus). Some framework classes use this attribute in order
     * to store status information.
     */

   public int getObjStatus() {
    return objStatus;
   }


   /** 
     * Sets the object status (objStatus). Some framework classes use this attribute in order
     * to store status information.
     */

   public void setObjStatus(int newObjStatus) {
    objStatus = newObjStatus;
   }

   // Object path

/*
   public String getObjPath() {
    return objPath;
   }

   public void setObjPath(String newObjPath) {
    objPath = newObjPath;
   }
*/

   /** 
     * Gets the value of objException. This attribute is used to store exceptions detected
     * while processing messages. The handleException method updates this attribute. 
     * Since handleError invokes handleException, it also updates this attribute.
     */

   public Object getObjException() {
    return objException;
   }

   /** 
     * Sets the value of objException. This attribute is used to store exceptions detected
     * while processing messages. The handleException method updates this attribute.
     * Since handleError invokes handleException, it also updates this attribute.
     */

   public void setObjException(Object newObjException) {
    objException = newObjException;
   }


   // Session

/*
   public Object getObjSession() {
    return objSession;
   }
*/

/*
   public void setObjSession(Object newObjSession) {

    //handleTrace ("JtObject.setObjSession:" + newObjSession);
    objSession = newObjSession;
   }
*/

  // create_dir: create the parent directory

  void create_dir (String name) {
   File file;
   String parentpath;

        if (name == null)
           return;

        file = new File (name);
        if (file == null) {
           handleError ("JtFile: unable to create File object:" + name);
           return;
        }

        parentpath = file.getParent ();
        if (parentpath == null)
           return;

        //file = file.getParentFile ();
        file = new File (parentpath);
        if (file == null) {
           handleError ("JtFile: unable to get parent File:" + name);
           return;
        }

        if (!file.exists ())
             if (!file.mkdirs ())
                     handleError ("JtFile: unable to create directories:" +
                                parentpath);
  }


  JtResource parse_resource (String line) {

    String resource_class, tmp;
    int index;
    int length;
    JtResource res;

    if (line == null)
      return (null);

    length = line.length ();

    if (length == 0)
      return (null);

    index = line.indexOf (":");

    if (index < 0)
      return (null);
    
    if (index == length - 1)
      return (null);

    res = new JtResource ();

    res.value = line.substring (index+1, length);

    tmp = line.substring (0, index);

    index = tmp.lastIndexOf (".");

    if (index == -1)
      return (null);

    if (index == tmp.length () - 1)
      return (null);  // ends with a dot ?

    res.rclass = tmp.substring (0, index);
    res.name = tmp.substring (index + 1);  

    handleTrace ("Jt Resource: " + "(" +
       res.rclass + "," + res.name + "," + res.value
       + ")");
   
    return (res);

  }

  private void load_object_resources (Object obj,
    Object class_name) {

    Hashtable ht;
    Enumeration rnames;
    Object resource;

 
    if (resTable == null)
      resTable = new Hashtable ();

    if (obj == null || class_name == null)
       return;

    ht = (Hashtable) resTable.get (class_name);

    if (ht == null)
      return; // check

    rnames = ht.keys ();
    if (rnames == null)
      return;

    handleTrace ("Jt: Loading Jt resources into object ...");
    while (rnames.hasMoreElements ()) {
      resource = rnames.nextElement ();
      setValue (obj, resource, ht.get (resource));
    }
    handleTrace ("Jt: Loaded Jt resources");
  }

  private void update_resources (JtResource res) {
    Hashtable ht;

    if (res == null)
      return;

    if (res.rclass == null)
      return;

    if (resTable == null)
      resTable = new Hashtable ();

    if (resTable.get (res.rclass) == null) {
      resTable.put (res.rclass, new Hashtable ());
    }

    ht = (Hashtable) resTable.get (res.rclass);

    if (ht == null)
      return; // check

    ht.put (res.name, res.value);
  }
    
  void load_resources ( ) {
    String line;
    JtResource res;
    File file;

    if (resourceFile == null)
      return;

    file = new File (resourceFile);

    if (!file.exists ())
      return;

    handleTrace ("Jt: reading resources from " +
     resourceFile + " ...");
    try {
      BufferedReader d = new BufferedReader 
		(new FileReader (resourceFile));

      while ((line  = d.readLine ()) != null) {
        //System.out.println ("JtFile.read_lines:" + line);
	if (line.startsWith ("!") || 
          line.startsWith ("//"))
          continue;
        res = parse_resource (line);

        if (res != null)
          update_resources (res);
      }

    } catch (Exception ex) {
      handleException (ex);
    }
  }

  /**
   * Unit tests the messages processed by JtObject
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;
    String tmp;
    JtObject main1;

    //main.setObjTrace (1);
    //main.setResourceFile (".Jtrc1");
    //main.setLogFile ("log.txt");


    // Create the object

    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");



    if (main.lookupObject ("message") != null)
	System.out.println ("Jt.createObject: GO");
    else
	System.out.println ("Jt.createObject: FAILED");

    // Set Value

    main.setValue ("message", "msgId", "JtTestId");

    if (msg.getMsgId () != null)
	if (msg.getMsgId().equals ("JtTestId"))
	   System.out.println  ("Jt.setValue: GO");
	else
	   System.out.println  ("Jt.setValue: FAILED");
    else
	   System.out.println  ("Jt.setValue: FAILED");


    //main.sendMessage (main, "message");

    // Get value

    tmp = (String) main.getValue ("message", "msgId");

    if (tmp != null)
	if (tmp.equals ("JtTestId"))
	   System.out.println  ("Jt.getValue: GO");
	else
	   System.out.println  ("Jt.getValue: FAILED");
    else
	   System.out.println  ("Jt.getValue: FAILED");

    main.removeObject ("message");

    if (main.lookupObject ("message") == null) {
	System.out.println  ("Jt.removeObject: GO"); 
    } else
	System.out.println  ("Jt.removeObject: FAILED"); 
 
    // qualified names

    main.setObjName ("main");
    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");

    if (main.lookupObject ("main.message") != null) {
	System.out.println  ("Jt.createObject (main.message): GO"); 
    } else
	System.out.println  ("Jt.createObject (main.message): FAILED"); 

    if (main.lookupObject ("message") != null) {
	System.out.println  ("Jt.lookupObject (main.message): GO"); 
    } else
	System.out.println  ("Jt.lookupObject (main.message): FAILED"); 

    main.removeObject ("message");

    if (main.lookupObject ("message") == null 
	&& main.lookupObject ("main.message") == null) {
	System.out.println  ("Jt.removeObject (main.message): GO"); 
    } else
	System.out.println  ("Jt.removeObject (main.message): FAILED"); 


/*
    msg = (JtMessage) main.createObject ("Jt.JtMessage", "main.message");

    main.setValue ("message", "msgContent", "hello");
    main.setValue ("message", "objPath", "kk/message");
    msg.save ();
    JtMessage obj = new JtMessage ();
    obj.setObjPath ("kk/message");
 
    obj = (JtMessage) obj.restore ();

    System.out.println  ("JtObject after restore (name):" + obj.getMsgContent ());
*/

    // handleWarning

    //main.handleWarning ("JtObject: this is a warning");
    main.handleTrace ("JtObject: this is a trace");



  }
}
