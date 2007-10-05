package Jt.axis;

import Jt.*;
import Jt.xml.*;



/**
  * Jt Adapter for web services
  */ 

public class JtWebServicesAdapter extends JtScriptInterpreter  {


  private static final long serialVersionUID = 1L;
  JtXMLConverter xmlConverter = null;  
  JtAxisAdapter axisAdapter = null;
  private String url = null;
  private String remoteLogFile;


  public JtWebServicesAdapter () {
  }


 /**
  * Specifies the service URL.
  * @param url service url
  */

  public void setUrl (String url) {
    this.url = url;
  }

 /**
   * Returns the service URL. 
   */

  public String getUrl () {
    return (url);
  }


 /**
   * Returns the name of the remote log file
   */

  public String getRemoteLogFile () {
    return (remoteLogFile);
  }


 /**
  * Specifies the name of the remote log file
  * @param remoteLogFile remote log file
  */

  public void setRemoteLogFile (String remoteLogFile) {
    this.remoteLogFile = remoteLogFile;
  }





  // Propagate Exceptions 

  private Exception propagateException (Object obj)
  {
    Exception ex;

    if (obj == null)
      return null;

    ex = (Exception) 
     super.getValue (obj, "objException");

    if (ex != null)
      super.setValue (this, "objException", ex);

    return (ex);
  }


  // Create the object that will perform the service
  // 
 

  public Object createObject (Object class_name, Object id) {
    String xmlMsg;
    Object output;

    if (class_name == null || id == null) { // check
      handleError ("JtWebServicesAdapter.createObject: invalid parameters");
      return (null);
    }
    
    if (xmlConverter == null)
      xmlConverter = new JtXMLConverter ();

    xmlConverter.createObject (class_name, id);
    //xmlMsg = (String) super.sendMessage (xmlConverter, new JtMessage ("JtCONVERT"));    
    xmlMsg = (String) xmlConverter.processMessage (new JtMessage ("JtCONVERT"));    


    output = doit (xmlMsg);
    xmlConverter = null;

    return (output);
  }


  private void updateRemoteLogFile () {

    String xmlMsg;
    JtMessage msg;
    JtXMLConverter xmlconverter;


    if (axisAdapter == null)
      return;
    
    xmlConverter = new JtXMLConverter ();

    xmlConverter.setValue ("this", "logFile", remoteLogFile);

    xmlMsg = (String) xmlConverter.processMessage (new JtMessage ("JtCONVERT"));    


    msg = new JtMessage ("JtSCRIPT");
    msg.setMsgContent (xmlMsg);

    super.sendMessage (axisAdapter, msg);


  }

  public void destroyObject (Object id) {
    String xmlMsg;
    Object output;

    if (id == null) { // check
      handleError ("JtWebServicesAdapter.createObject: invalid parameters");
      return;
    }
    
    if (xmlConverter == null)
      xmlConverter = new JtXMLConverter ();

    xmlConverter.destroyObject (id);
    //xmlMsg = (String) super.sendMessage (xmlConverter, new JtMessage ("JtCONVERT"));    
    xmlMsg = (String) xmlConverter.processMessage (new JtMessage ("JtCONVERT"));    


    output = doit (xmlMsg);
    xmlConverter = null;


  }


  private Object doit (String xmlMsg) {

    JtMessage msg;
    Object output;

    if (xmlMsg == null)
     return (null); // check


    //handleTrace ("JtWebServicesAdapter.doit() ...."
    //             + xmlMsg);

    msg = new JtMessage ("JtSCRIPT");
    msg.setMsgContent (xmlMsg);


    if (url == null) {
      handleError ("JtWebServicesAdapter.sendMessage: invalid url (null)");
      return (null);     
    }

    if (axisAdapter == null) {
      axisAdapter = (JtAxisAdapter) super.createObject ("Jt.axis.JtAxisAdapter", 
        "adapter");
      
      super.setValue (axisAdapter, "url", url);
      updateRemoteLogFile ();
    }


    output = super.sendMessage (axisAdapter, msg);

    propagateException (axisAdapter);
    return (output);


  }


  public Object sendMessage (Object id, Object msgid) {

    String xmlMsg;
    //JtMessage msg;
    Object output;
    //Exception ex;

    if (id == null || msgid == null) {
      handleError ("JtWebServicesAdapter.sendMessage: invalid parameters");
      return (null);
    }


    if (xmlConverter == null)
      xmlConverter = new JtXMLConverter ();


    // Convert Jt API calls to XML calls

    xmlConverter.sendMessage (id, msgid);

    //xmlMsg = (String) super.sendMessage (xmlConverter, new JtMessage ("JtCONVERT"));    
    xmlMsg = (String) xmlConverter.processMessage (new JtMessage ("JtCONVERT"));    

    xmlConverter = null;

    output = doit (xmlMsg);

    //propagateException (axisAdapter);

    return (output);

  } 

 
  public void setValue (Object id, Object att, 
    Object value) {
    String xmlMsg;

    if (id == this) {
       super.setValue (id, att, value);
       return;
    }
    if (id == null || att == null) { // check null value
      handleError ("JtWebServicesAdapter.sendMessage: invalid parameters");
      return;
    }


    if (xmlConverter == null)
      xmlConverter = new JtXMLConverter ();

    // Convert Jt call to XML

    xmlConverter.setValue (id, att, value);
    //xmlMsg = (String) super.sendMessage (xmlConverter, new JtMessage ("JtCONVERT"));    
    xmlMsg = (String) xmlConverter.processMessage (new JtMessage ("JtCONVERT"));    

    xmlConverter = null;

    doit (xmlMsg);
    //propagateException (axisAdapter);
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

     //content = e.getMsgContent();

     handleError ("processMessage: invalid message ID:" + msgid);
     return (null);



  }



 /**
   * Unit tests all the messages processed by JtWebServicesAdapter. 
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();

    JtWebServicesAdapter myService;
    //String str;
    String reply = null;
    Exception ex;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create the Jt Web service Adapter

    myService = (JtWebServicesAdapter) main.createObject ("Jt.axis.JtWebServicesAdapter", 
     "service");

    // Set the service url property

    main.setValue (myService, "url", 
     "http://localhost:8080/axis/services/JtAxisService");
    main.setValue (myService, "remoteLogFile", 
     "c:\\log.txt");



    // Create a remote instance of the HelloWorld class. The Jt Web service adaptor
    // can be used to create remote instances of any Jt Framework class.
    // HelloWorld is used an example. 

    myService.createObject ("Jt.examples.HelloWorld", "helloWorld");
    //myService.createObject ("Jt.JtCommand", "command");


    // Set the greetingMessage attribute

    myService.setValue ("helloWorld", "greetingMessage", "Hello there...");


    // Create a remote message (JtHello)

    myService.createObject ("Jt.JtMessage", "message");
    myService.setValue ("message", "msgId", "JtHello");


    // Send JtHello to the remote helloWorld object

    reply = (String) myService.sendMessage ("helloWorld", "message");

    ex = (Exception) main.getValue (myService, "objException");    
    

    // Display the reply unless an exception is detected

    if (ex == null)    
      System.out.println (reply);

    // Destroy the service instance

    myService.destroyObject ("helloWorld");


  }

}


