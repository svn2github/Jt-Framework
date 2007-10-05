

package Jt.xml;

import Jt.*;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
  * Convert Jt objects from/to the XML format
  */

public class JtXMLHelper extends JtObject implements ContentHandler {


  private static final long serialVersionUID = 1L;
  private XMLReader reader = null;
  protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
  protected static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  private StringBuffer buffer;
  HashMap map;
  private Object object = null;
  private String parserName = DEFAULT_PARSER_NAME;
  private int level = 0;
  Vector mapTable = new Vector ();
  Vector objTable = new Vector ();

  public JtXMLHelper() {
  }

 /**
  * Specifies the parser.
  * @param parserName parse
  */


  void setParserName (String parserName) {
    this.parserName = parserName;
  
  }


 /**
   * Returns the parser. 
   */

  String getParserName () {
    return (parserName);  
  }


  /** 
    * Start element (SAX API). 
    */
  public void startElement(String uri, String local, String raw,
                             Attributes attrs) throws SAXException {

    if ("".equals (uri)) {
      // handleTrace ("Start element: " + raw);

      if ("Object".equals (raw)) {
        level++;

        map = new HashMap ();
        mapTable.addElement (map);
        return;    
      }
      
      //elemName = raw;
      //elemValue = null;
      buffer = null;

    } else {
      // handleTrace ("Start element: {" + uri + "}" + local);
    }
  } 


  Object setResources (Object obj) {
   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   //Class p;
   //Method m;
   BeanInfo info = null;
   String tval; 


     map = getAttributeMap (level);
     if (obj == null || map == null)
       return (null);

     try {
       info = Introspector.getBeanInfo(
              obj.getClass (), obj.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return (null); //check
     }

     prop = info.getPropertyDescriptors();


     for(i = 0; i < prop.length; i++) {

        tval = (String) map.get (prop[i].getName());

        if (tval != null) // check
          setValue (obj, prop[i].getName(), tval);

     }

      return (null); // check
  } 


  private Hashtable getAttributes (Object obj) {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   //Class p;
   Method m;
   BeanInfo info = null;
   Object value;
   Hashtable attr;


     if (obj == null)
       return (null);

     attr = new Hashtable ();


     try {
       info = Introspector.getBeanInfo(
              obj.getClass (), java.lang.Object.class);
     } catch(Exception e) {
        handleException (e);
        return (null);
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {

       //p = prop[i].getPropertyType();
       
       try {
         m = prop[i].getReadMethod ();
         if (m == null) {
           handleError 
	     ("getAttributes: getReadMethod returned null");
             return (null);
         }

         value = m.invoke (obj, null);
         if (value == null) {
           continue;
         }

         if (value instanceof String) {
           attr.put (prop[i].getName(), value); 
           //System.out.println ("=" + value);
           continue;
         }

         if (value instanceof Integer ||
             value instanceof Long || 
             value instanceof Float ||
             value instanceof Byte ||
             value instanceof Boolean ||
             value instanceof Short ||
             value instanceof Double) {
           attr.put (prop[i].getName(), value.toString () ); 

           continue;
         } else {
            handleWarning 
             ("JtXMLHelper.getAttributes: unable to convert attribute type to String:" + prop[i].getName());
                
         }

        } catch (Exception e) {
         handleException(e);
         return (null);
        }
      }

      return (attr);
   }


  private Object createInstance () {
  String classname;
  Class jtclass;
  Object obj;
  String value;
  String stmp;
  JtRemoteException jex;

    if (map == null)
       return (null); // check


    classname = (String) map.get ("classname");

    if (classname == null)
      return (null); // check

    try {
      jtclass = Class.forName ((String) classname);
    } catch (Exception e) {
      handleException (e);
      return (null);
    }


/*
    try {
      obj = jtclass.newInstance ();	   
    } catch (Exception e) {
      handleException (e);
      return (null);
    }
*/


    if (classname.equals ("Jt.JtRemoteException")) {
      value = (String) map.get ("value");
      stmp = (String) map.get ("trace");

      //System.out.println ("stmp:" + stmp);
            
      if (value == null) {
        handleError ("createInstance: invalid value:" +
         value);

        return (null);   
      }
      jex = new JtRemoteException (value);
      jex.setTrace (stmp);
      return (jex);
    }

    if (classname.startsWith ("Jt.")) {
      try {
        obj = jtclass.newInstance ();
        setResources (obj);
        return (obj); // check	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }
    } else if (classname.startsWith ("java.lang")) {

      value = (String) map.get ("value");
    } else {
      handleError ("createInstance: unable to handle class type:" +
         classname);
      return (null);
    } 


    if (value == null) {
      handleError ("createInstance: invalid value:" +
         value);

      return (null);   
    }

    if (classname.equals ("java.lang.Byte")) {

      try {
        obj = new Byte (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }


    if (classname.equals ("java.lang.Short")) {

      try {
        obj = new Short (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }


    if (classname.equals ("java.lang.Float")) {

      try {
        obj = new Float (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }

    if (classname.equals ("java.lang.Double")) {

      try {
        obj = new Double (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }


    if (classname.equals ("java.lang.String")) {

      try {
        obj = new String (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }

    if (classname.equals ("java.lang.Integer")) {

      try {
        obj = new Integer (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }

    if (classname.equals ("java.lang.Long")) {

      try {
        obj = new Long (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }

    if (classname.equals ("java.lang.Boolean")) {

      try {
        obj = new Boolean (value);
        return (obj);	   
      } catch (Exception e) {
        handleException (e);
        return (null);
      }             

    }

    handleError ("createInstance: unable to handle class type:" +
         classname);
    return (null); // check
  }

  HashMap getAttributeMap (int level) {
   if (level < 1)
     return (null);

   if (level > mapTable.size ())
     return (null);


   return ((HashMap) mapTable.elementAt (level - 1));

  }
 
  /** 
    * End element (SAX API). 
    */

  public void endElement(String uri,
                        String name,
                        String qName)
                throws SAXException {
    JtMessage msg;
    int i;

    map = getAttributeMap (level);
    if (qName == null || map == null)
      return;  //check
 
    if ("".equals (uri)) {
      // handleTrace ("End element: " + qName);

      if ("Object".equals (qName)) { //check
        //sendReply (msg);

        if (map == null)
          return; // check
        object = createInstance ();
        map = null;
        buffer = null;
        level--;

        if (level > 0)
          objTable.addElement (object);
        else {

          if (!(object instanceof JtCollection))
            return;
          for (i = 0; i < objTable.size (); i++) {
            msg = new JtMessage ("JtADD");
            msg.setMsgContent (objTable.elementAt (i));
            ((JtObject) object).processMessage (msg);
            //sendMessage (object, msg);
          } 

        }
        return;
      } else { 

//      if (map != null && buffer != null &&
//        qName != null) {


         // handleTrace ("JtXMLHelper.endElement:"+ qName + ":" + buffer);

         if (buffer == null)
           return; // check

         map.put (qName, buffer.toString ());

         buffer = null;
      } 

    } else {
      // handleTrace ("End element:   {" + uri + "}" + name);
    }    
  }


  /** 
    * Start document (SAX API). 
    */
  public void startDocument() throws SAXException {

  } 

 /** 
   * End document (SAX API). 
   */

  public void endDocument()
                 throws SAXException {

  }

 /** 
   * Characters (SAX API). 
   */

  public void characters(char[] ch,
                       int start,
                       int length)
                throws SAXException {
    String tmp;

    //handleTrace ("Characters:    \"");
    if (buffer == null)
      buffer = new StringBuffer ();

    for (int i = start; i < start + length; i++) {
      buffer.append (ch[i]);

/*
      switch (ch[i]) {
        case '\\':
          handleTrace("\\\\");
          break;
	case '"':
	  handleTrace("\\\"");
	  break;
	case '\n':
	  //handleTrace("\\n");
	  break;
	case '\r':
          handleTrace ("\\r");
          break;
        case '\t':
          handleTrace ("\\t");
          break;
        default:
          buffer.append (ch[i]);
          break;
      }
*/
    }

    //handleTrace("\"\n");
    //elemValue = buffer.toString ();
    tmp = buffer.toString().trim ();

    if (!tmp.equals ("")) {
      //handleTrace("JtXMLMsgReader.characters:" + 
      //  buffer);      
    }    

  }

 /** 
   * endPrefixMapping - SAX API. 
   */
  public void endPrefixMapping(String prefix)
                      throws SAXException {

  }

 /** 
   * ignorableWhitespace - SAX API. 
   */
  public void ignorableWhitespace(char[] ch,
                                int start,
                                int length)
                         throws SAXException {

  }

 /** 
   * processingInstruction - SAX API. 
   */
  public void processingInstruction(String target,
                                    String data)
                           throws SAXException {

  }

 /** 
   * setDocumentLocator - SAX API. 
   */

  public void setDocumentLocator(Locator locator) {

  }

 /** 
   * skippedEntity - SAX API. 
   */

  public void skippedEntity(java.lang.String name)
                   throws SAXException {

  }


 /** 
   * startPrefixMapping - SAX API. 
   */

  public void startPrefixMapping(String prefix,
                                 String uri)
                        throws SAXException
  {

  }

  // realize
  void realizeHelper() {

     if (reader != null)
       return;

     try{
       reader = XMLReaderFactory.createXMLReader (parserName);

       reader.setContentHandler (this);
       //reader.setErrorHandler (this);
     } catch (Exception ex) {

       handleException (ex);
     }

  }

  private Object convertXMLToObject (String string) {

    ByteArrayInputStream bstream;

    object = null;  // check

    if (string == null)
      return (null);

    if (reader == null)
      realizeHelper();


    try {

      bstream = new ByteArrayInputStream (string.getBytes());     
      reader.parse (new InputSource ((InputStream) bstream));

    } catch (Exception ex) {
      handleException (ex);
    }
    return object;
  }  

  private String convertJtObjectToXML (Object obj) {

    StringBuffer buf = new StringBuffer ();

    Hashtable tbl;  
    //Iterator it; 
    String key;
    Enumeration keys;

    if (obj == null)
      return (null);

    tbl = getAttributes (obj); 

//    if (tbl == null)
//      return (null);

    //it = tbl.keys().iterator (); 


//  buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    buf.append ("<Object>\n");
    buf.append ("<classname>" + obj.getClass().getName() + "</classname>\n");

 
    if (tbl != null) {
    keys = tbl.keys ();    
    while (keys.hasMoreElements()) {

      key = (String) keys.nextElement ();

      buf.append ("<" + key + ">");
      buf.append ((String) tbl.get (key));
      buf.append ("</" + key + ">\n");


    }
    }
    buf.append ("</Object>\n"); 

    //handleTrace ("convertJtObjectToXML (XML):" + buf);
    return (buf.toString ());

  }

  private String convertJtCollectionToXML (Object obj) {

    StringBuffer buf = new StringBuffer ();

    //Hashtable tbl;  
    //Iterator it; 
    //String key;
    JtIterator jit;
    Object tmp;
    String tmp1;

    if (obj == null)
      return (null);

//  buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    buf.append ("<Object>\n");
    buf.append ("<classname>" + obj.getClass().getName() + "</classname>\n");

    jit = (JtIterator) getValue (obj, "iterator");

    if (jit == null)
      return (null); // check

    for (;;) {
      //tmp = sendMessage (jit, new JtMessage ("JtNEXT"));
      tmp = jit.processMessage (new JtMessage ("JtNEXT"));    
      if (tmp == null)
        break;
      tmp1 = convertToXML (tmp);

      if (tmp1 == null)
        return (null); // check

      buf.append (tmp1);
    }    
    buf.append ("</Object>\n"); 

    return (buf.toString ());

  }


  private String stackTrace (Exception ex ) {
    ByteArrayOutputStream bstream;


    if (ex == null)
      return (null);

     bstream = new ByteArrayOutputStream ();


     //handleError ("hello");


     //ex = (Exception) getValue (this, "objException");

     ex.printStackTrace (new PrintWriter (bstream, true));

     //ex.printStackTrace ();

     return (bstream.toString ());
    

  }

  private String convertToXML (Object obj) {

    buffer = new StringBuffer ();

    if (obj == null) {
      buffer.append ("<Object>\n");
      buffer.append ("</Object>\n"); 
      return (buffer.toString ()); 

    }

    // handleTrace ("convertToXML ..." + obj + ":" + obj.getClass().getName());


    if (obj instanceof Byte ||
        obj instanceof Short ||
        obj instanceof Integer ||
        obj instanceof Long ||
        obj instanceof Boolean ||
        obj instanceof Float ||
        obj instanceof Double ||
        obj instanceof String) {
//    buffer.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buffer.append ("<Object>\n");
      buffer.append ("<classname>" + obj.getClass().getName() + "</classname>\n");
      buffer.append ("<value>" + obj + "</value>\n");
      buffer.append ("</Object>\n"); 
      return (buffer.toString ()); 

    } else if (obj instanceof JtCollection) { 

      return (convertJtCollectionToXML (obj));

    } else if (obj instanceof JtObject) {

      return (convertJtObjectToXML (obj));
    } else if (obj instanceof Exception) {
      buffer.append ("<Object>\n");
      //buffer.append ("<classname>" + obj.getClass().getName() + "</classname>\n");
      buffer.append ("<classname>" + "Jt.JtRemoteException" + "</classname>\n");

      buffer.append ("<value>" + ((Exception) obj).getMessage () + "</value>\n");
      buffer.append ("<trace>" + stackTrace ((Exception) obj) + "</trace>\n");
      buffer.append ("</Object>\n");
      return (buffer.toString ());              
    }
    
    handleError ("convertToXML:unable to convert object class" + obj.getClass().getName());   
    return (null);

  }

  private void initializeHelper () {
    buffer = null;
    map = null;
    object = null;
    level = 0;
    mapTable = new Vector ();
    objTable = new Vector ();
  }

  /**
   * Process object messages. 
   * <ul>
   * <li> JtCONVERT_OBJECT_TO_XML - Converts object to XML format
   * <li> JtCONVERT_XML_TO_OBJECT - Converts XML to object
   * <li> JtREMOVE - Removes this object
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

     content = e.getMsgContent();

     //

     if (msgid.equals ("JtCONVERT_OBJECT_TO_XML")) {

             
        return (XML_HEADER + convertToXML (content)); // check
     }


     if (msgid.equals ("JtCONVERT_XML_TO_OBJECT")) {

        initializeHelper ();             
        return (convertXMLToObject ((String) content));
     }

     if (msgid.equals ("JtREMOVE")) {
             
        return (null);
     }


          
     handleError ("JtXMLHelper.processMessage: invalid message id:" + msgid);
     return (null);

  }


 /**
   * Unit tests all the messages processed by JtXMLHelper
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg1, msg2;
    //Integer count;
    String tmp;
    Integer i;
    JtXMLHelper helper;
    JtOSCommand command;
    JtList col;


    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");

    msg1 = new JtMessage ();
    msg2 = new JtMessage ();


    msg1.setMsgId ("JtCONVERT_OBJECT_TO_XML");

    msg1.setMsgContent (new Integer (2));

    System.out.println ("JtCONVERT_OBJECT_TO_XML: converting Integer (2) to XML ..");

    helper = (JtXMLHelper) main.createObject ("Jt.xml.JtXMLHelper", "helper");


    tmp = (String) main.sendMessage (helper, msg1);
    System.out.println (tmp);


    msg2.setMsgId ("JtCONVERT_XML_TO_OBJECT");
    System.out.print ("JtCONVERT_XML_TO_OBJECT: converting XML to object (Integer) .. ");

    msg2.setMsgContent (tmp);
    i = (Integer) main.sendMessage (helper, msg2);

    System.out.println (i);

    if (i != null && i.intValue() == 2) {
      System.out.println ("JtCONVERT_OBJECT_TO_XML (INTEGER): PASS");
      System.out.println ("JtCONVERT_XML_TO_OBJECT (INTEGER): PASS");
    } else {
      System.out.println ("JtCONVERT_OBJECT_TO_XML (INTEGER): FAIL");
      System.out.println ("JtCONVERT_XML_TO_OBJECT (INTEGER): FAIL");

    }

    msg1.setMsgId ("JtCONVERT_OBJECT_TO_XML");

    msg1.setMsgContent ("My string");

    System.out.println ("JtCONVERT_OBJECT_TO_XML: converting String to XML ..");
    tmp = (String) main.sendMessage (helper, msg1);
    System.out.println (tmp);


    msg2.setMsgId ("JtCONVERT_XML_TO_OBJECT");
    msg2.setMsgContent (tmp);
    tmp = (String) main.sendMessage (helper, msg2);

    System.out.print ("JtCONVERT_XML_TO_OBJECT: converting XML to object (String) .. ");

    System.out.println (tmp);


    if (tmp != null && tmp.equals("My string")) {
      System.out.println ("JtCONVERT_OBJECT_TO_XML (String): PASS");
      System.out.println ("JtCONVERT_XML_TO_OBJECT (String): PASS");
    } else {
      System.out.println ("JtCONVERT_OBJECT_TO_XML (String): FAIL");
      System.out.println ("JtCONVERT_XML_TO_OBJECT (String): FAIL");

    }


    command = (JtOSCommand) main.createObject ("Jt.JtOSCommand", "cmd");
    main.setValue ("cmd", "command", "notepad");

    msg1.setMsgId ("JtCONVERT_OBJECT_TO_XML");

    msg1.setMsgContent (command);

    tmp = (String) main.sendMessage (helper, msg1);
    System.out.println ("Output =" + tmp);

    msg1.setMsgId ("JtCONVERT_XML_TO_OBJECT");

    msg1.setMsgContent (tmp);

    command = (JtOSCommand) main.sendMessage (helper, msg1);

    msg1.setMsgId ("JtEXECUTE");
    main.sendMessage (command, msg1);


    col =  (JtList) main.createObject ("Jt.JtList", "col");

    msg1 = new JtMessage ("JtADD");
    msg1.setMsgContent (new Integer (2));


    main.sendMessage (col, msg1);       

    msg1.setMsgContent ("String");

    main.sendMessage (col, msg1);  

    msg1.setMsgContent (new JtException ("JtException"));

    main.sendMessage (col, msg1); 

    msg2.setMsgId ("JtCONVERT_OBJECT_TO_XML");
    msg2.setMsgContent (col);
    tmp = (String) main.sendMessage (helper, msg2);

    System.out.println ("XtCollection=" + tmp);

    msg1.setMsgId ("JtCONVERT_XML_TO_OBJECT");

    msg1.setMsgContent (tmp);

    col = (JtList) main.sendMessage (helper, msg1);


    msg1 = new JtMessage ("JtADD");
    msg1.setMsgContent (new JtException ("JtException"));

    //main.sendMessage (col, msg1); 

    msg2.setMsgId ("JtCONVERT_OBJECT_TO_XML");
    msg2.setMsgContent (col);
    tmp = (String) main.sendMessage (helper, msg2);

    System.out.println ("XtCollection=" + tmp);

    main.removeObject (helper);

  }

}


