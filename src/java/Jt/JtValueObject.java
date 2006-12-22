package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;


/**
 * Jt Implementation of the J2EE Value Object pattern. This class relies
 * on introspection to create a hashmap containing the attribute values.
 */


public class JtValueObject extends JtHashTable {


  private transient Object subject = null;
  private HashMap attributes = null;

  public JtValueObject() {
  }

/**
  * Specifies the subject.
  *
  * @param subject subject
  */

  public void setSubject (Object subject) {
     this.subject = subject; 

  }

/**
  * Returns the subject (Object whose Value Object needs to be stored).
  */

  public Object getSubject () {
     return (subject);
  }


/**
  * Specifies the attributes.
  *
  * @param attributes attributes
  */


  public void setAttributes (HashMap attributes) {
     this.attributes = attributes; 

  }


/**
  * Returns the attributes.
  */

  public HashMap getAttributes () {
     return (attributes);
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
        handleWarning (e.getMessage ());
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


  private HashMap calcValueObject () {

   Object args[];
   PropertyDescriptor[] prop;
   int i;
   Class p;
   Method m;
   BeanInfo info = null;
   Object value;
   HashMap attr;

     if (subject == null) {
       handleError ("getValueObject: the subject attribute needs to be set");
       return (null);
     }

     attr = new HashMap ();

     try {
       info = Introspector.getBeanInfo(
              subject.getClass (), java.lang.Object.class);
     } catch(Exception e) {
        handleException (e);
        return (null);
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {

       if (!checkModifiers (subject.getClass (),prop[i].getName())) {
       //System.out.println ("Skipping (modifiers):" + 
            //prop[i].getName());
         continue;
       }

       //System.out.println ("Attribute:" + 
       //     prop[i].getName());
       p = prop[i].getPropertyType();
       
       if (!(p.isPrimitive () || Serializable.class.isAssignableFrom (p))) {
       //System.out.println ("Skipping:" + 
       //     prop[i].getName());
         continue;
       }

       try {
         m = prop[i].getReadMethod ();
         if (m == null) {
           handleWarning
	     ("JtValueObject: getReadMethod returned null");
             continue;
             //return (null);
         }

         value = m.invoke (subject, null);

/*
         if (!(value instanceof Integer ||
             value instanceof Long || 
             value instanceof Float ||
             value instanceof Byte ||
             value instanceof Boolean ||
             value instanceof Short ||
             value instanceof Double || value instanceof String))
           continue;
*/
         attr.put (prop[i].getName(), value); 


        } catch (Exception e) {
         handleException(e);
         return (null);
        }
      }

      return (attr);
   }


  private Object getKeys () {
    JtIterator jit = new JtIterator ();
    Collection col;
    HashMap tmp;

    tmp = getHashmap ();
    if (tmp == null)
      return (null);    

    col = tmp.keySet ();
    if (col == null || (col.size () == 0))
      return (null);

    jit.setIterator (col.iterator ());
    return jit;     

  }

  public String toString () {

    JtIterator jit = (JtIterator) getKeys ();
    Object key, value;
    JtMessage msg = new JtMessage ("JtNEXT");
    JtMessage msg1 = new JtMessage ("JtGET");
    StringBuffer buffer = null;

    if (jit == null)
      return (null);

    for (;;) {
            
      key = jit.processMessage (msg);
      if (key == null)
        break; 
      msg1.setMsgData (key);
      value = this.processMessage (msg1);      
      if (buffer == null)
       buffer = new StringBuffer (); 
      buffer.append (key + ":" + value + "\n");
    }    

    return ((buffer == null)?null:buffer.toString ());

  }

  /**
    * Process object messages. 
    * <ul>
    * <li>JtACTIVATE - returns the Value Object associated with the subject.
    * </ul>
    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     // Let the subject process the request


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtACTIVATE")) {

       if (subject == null) {
         handleError ("JtValueObject.process: the subject attribute needs to be set");
         return (null);
       }

       setHashmap (calcValueObject ());
       return (getHashmap ());     
     }

     if (msgid.equals ("JtGET_KEYS")) {
       return (getKeys ());     
     }

/*
     if (msgid.equals ("JtPRINT")) {
       return (printObject ());     
     }
*/

     return (super.processMessage (event));


  }



  /**
    * Unit Tests the messages processed by JtValueObject.   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    Jt.examples.HelloWorld helloWorld;
    JtValueObject valueObj;


    // Create an instance of JtValueObject

    valueObj = (JtValueObject)
      main.createObject ("Jt.JtValueObject", 
      "valueObject");
    helloWorld = (Jt.examples.HelloWorld) main.createObject ("Jt.examples.HelloWorld", 
      "helloWorld");

    main.setValue (valueObj, "subject", helloWorld);


    msg = new JtMessage("JtACTIVATE");
    main.sendMessage (valueObj, msg);
         
    //main.sendMessage (valueObj, new JtMessage ("JtPRINT_OBJECT"));

    //System.out.println (main.getValue (valueObj, "objName"));
    //System.out.println (main.getValue (valueObj, "greetingMessage"));

    msg = new JtMessage ("JtGET");
    msg.setMsgData ("objName");

    System.out.println (main.sendMessage (valueObj, msg));

    msg = new JtMessage ("JtGET");
    msg.setMsgData ("greetingMessage");

    System.out.println (main.sendMessage (valueObj, msg));

    System.out.println (valueObj);
  }

}
