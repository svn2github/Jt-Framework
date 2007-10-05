

package Jt.xml;

import Jt.*;

/**
  * Jt Script interpreter 
  */


public class JtScriptInterpreter extends JtObject  {

  private static final long serialVersionUID = 1L;
  //private XMLReader reader = null;
  private String filename;
  private String content;
  private boolean remoteInvocation = false;
//Object output = null;
  JtXMLHelper xmlHelper = null;
  Object scriptOutput = null; // script output. Output of the last 
                              // sendMessage, getValue or createObject
                              // operation

  

  public JtScriptInterpreter () {
  }


 /**
  * Specifies the script filename.
  * @param filename script filename
  */

  public void setFilename (String filename) {
     this.filename = filename; 
  }


 /**
   * Returns the script filename. 
   */

  public String getFilename () {
     return (filename);
  }

 /**
  * Specifies the script content.
  * @param content script content
  */

  public void setContent (String content) {
     this.content = content; 
  }


 /**
   * Returns the script content. 
   */
  public String getContent () {
     return (content);
  }




  public void setRemoteInvocation (boolean remoteInvocation) {
     this.remoteInvocation = remoteInvocation; 
  }


  public boolean getRemoteInvocation () {
     return (remoteInvocation);
  }

  // propagateException: 

  /*
  private Exception propagateException (Object obj)
  {
    Exception ex;

    if (obj == null)
      return null;

    ex = (Exception) 
     getValue (obj, "objException");

    if (ex != null)
      setValue (this, "objException", ex);

    return (ex);
  }
  */

   // Interpret Jt Message: update scriptOutput for JtCREATE_OBJECT, JtSET_VALUE
   //                       and JtSEND_MESSAGE
 
   private Object interpretMsg (Object event) {

     String msgid;
     JtMessage msg = (JtMessage) event;
     Object result = null;
     String ctype;

     if (msg == null)
       return (null);

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
       return (null);

     if (msgid.equals ("JtSET_VALUE")) {

       if ("this".equals (msg.getMsgSubject ()))
         setValue (this,
                 msg.getMsgContent (),
                 msg.getMsgData());
       else
         setValue (msg.getMsgSubject (),
                 msg.getMsgContent (),
                 msg.getMsgData());

       //propagateException (msg.getMsgSubject ());
       return (null);
     }

     if (msgid.equals ("JtCREATE_OBJECT")) {
       //System.out.println ("JtScriptIterpreter(JtCREATE_OBJECT):" + msg.getMsgContent()
       //    + "," + msg.getMsgData());
 
       ctype = (String) msg.getMsgContent();

       if (remoteInvocation) {
         if ("Jt.JtOSCommand".equals (ctype) || "Jt.JtFile".equals (ctype)) {
           handleError ("Security violation: unable to create a remote instance of " +
             ctype); 

           return (null);
         }

       }                                  
       result = createObject (msg.getMsgContent (),
                 msg.getMsgData());

       if (remoteInvocation)
         return (null);

       scriptOutput = result;
       return (result);
     }

     if (msgid.equals ("JtGET_VALUE")) {


       result = getValue (msg.getMsgContent (),
                 msg.getMsgData());

       scriptOutput = result;       
       return (result);
     }

     if (msgid.equals ("JtREMOVE_OBJECT")) {
       removeObject (msg.getMsgContent ());
       return (null);
     }


     if (msgid.equals ("JtSEND_MESSAGE")) {

       result = sendMessage (msg.getMsgContent (),
                    msg.getMsgData ());

       //propagateException (msg.getMsgContent ());

       scriptOutput = result;
       return (result);

     }

     if (msgid.equals ("JtREMOVE")) {
       return (null);
     }

     handleError ("JtScriptInterpreter.interpretMessage: invalid msg ID:"
     + msg.getMsgId ());
     return (null);
   }


  private Object parse () {
    

    JtMessage msg;
    JtList col;
    JtMessage msg1;
    JtIterator iterator;
    String str;
    JtFile file;
    //Object result = null;
    Exception ex;

    if (filename != null && content != null) {
      handleError ("JtScriptInterpreter.parse: set uri or string (only one)");
      return null;
    } 

    if (filename == null && content == null) {
      handleError ("JtScriptInterpreter.parse: both uri and string are null");
      return null;
    }    

    scriptOutput = null;

    if (filename != null) {

      file = new JtFile ();

      setValue (file, "name", filename);

      str = (String) sendMessage (file, new JtMessage ("JtCONVERT_TO_STRING"));
    

    } else
      str = content;


    if (str == null)
      return (null);

    if (xmlHelper == null)
      xmlHelper = (JtXMLHelper) createObject ("Jt.xml.JtXMLHelper", "xmlHelper");


    msg = new JtMessage ("JtCONVERT_XML_TO_OBJECT");

    msg.setMsgContent (str);

    col = (JtList) sendMessage (xmlHelper, msg);

    //destroyObject ("xmlHelper");

    if (col == null)
      return (null);

    msg = new JtMessage ("JtNEXT");

    iterator = (JtIterator) getValue (col, "iterator");

    if (iterator == null)
      return (null);

    for (;;) {
      //msg1 = (JtMessage) sendMessage (iterator, new JtMessage ("JtNEXT"));
      msg1 = (JtMessage) iterator.processMessage (new JtMessage ("JtNEXT"));

      if (msg1 == null)
        break;    

      interpretMsg (msg1);  

      ex = (Exception) 
       getValue (this, "objException");  

      if (ex != null)
        break;   // stop processing the script
                 // if an exception is detected   
    }

    return (scriptOutput);
  }


  // Process object messages

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

//   content = e.getMsgContent();

     if (msgid.equals ("JtPARSE")) {
        return (parse ());
     }

          
     handleError ("JtScriptInterpreter.processMessage: invalid message id:" + msgid);
     return (null);

  }



 /**
   * Unit tests all the messages processed by JtScriptInterpreter. 
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    //JtMessage msg, msg1;
    //Integer count;
    //String str;

    main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    if (args.length < 1) {
      System.err.println ("Usage: java JtScriptInterpreter uri");
      System.exit (1);
    }

    // Create message reader

    main.createObject ("Jt.xml.JtScriptInterpreter", "reader");

    main.createObject ("Jt.JtMessage", "message");
    main.setValue ("message", "msgId", "JtPARSE");
    main.setValue ("reader", "filename", args[0]);


    main.setValue ("message", "msgId", "JtPARSE");


    main.sendMessage ("reader", "message");


  }

}


