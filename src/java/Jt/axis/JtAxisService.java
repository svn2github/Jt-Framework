package Jt.axis;

import Jt.xml.*;
import Jt.*;
//import org.apache.axis.*;
//import org.apache.axis.session.*;

/**
 * Axis Web Service used to interface with the Jt Framework. 
 */

public class JtAxisService extends JtObject {



  private static final long serialVersionUID = 1L;
  JtScriptInterpreter reader = null;
  JtXMLHelper xmlHelper = null;

  public JtAxisService() {
  }


  // convertToXML: convert script output to XML
 
  String convertToXML (Object scriptOutput) {

    Exception ex;
    JtMessage msg = new JtMessage ();
    JtList lst;

    ex = (Exception) 
      getValue (reader, "objException");

    if (xmlHelper == null)
      xmlHelper = new JtXMLHelper ();
   
    if (ex == null) {
      msg.setMsgId ("JtCONVERT_OBJECT_TO_XML");
      msg.setMsgContent (scriptOutput);
      return ((String) sendMessage (xmlHelper, msg));
    }

    lst = new JtList ();

    msg.setMsgId ("JtADD");
    if (scriptOutput != null) {
      msg.setMsgContent (scriptOutput);
      //sendMessage (lst, msg);
      lst.processMessage (msg);
    }  
    //msg.setMsgContent (new JtException (ex.getMessage ())); // check
    msg.setMsgContent (ex);
    sendMessage (lst, msg);

    msg.setMsgId ("JtCONVERT_OBJECT_TO_XML");
    msg.setMsgContent (lst);
    return ((String) sendMessage (xmlHelper, msg));

  }


   /**
     * Process Jt Messages that have been previously converted to the XML format.
     * 
     * @param xmlMessage  Jt Message in XML format
     * @return reply in XML format
     */


  public String processMessage (String xmlMessage) {
  JtMessage msg;
  Object scriptOutput = null;
      //MessageContext mc = MessageContext.
         //getCurrentContext();
      //Session session = mc.getSession();
      //String name = (String)session.get("name");

    if (xmlMessage == null)
      return (null);

    

    //System.out.println ("JtAxisService Instance:" + this);
    //setLogFile ("c:\\log.txt");

    //handleTrace ("JtAxisService.processMessage ...\n"  + xmlMessage);
    

    msg = new JtMessage ("JtPARSE");
    //msg.setMsgReplyTo (this);

    // Send Jt script to the Message Interpreter for execution


    if (reader == null) {
      reader = (JtScriptInterpreter) this.createObject ("Jt.xml.JtScriptInterpreter", 
        "root");
      setValue (reader, "remoteInvocation", "true");
    }

    setValue (reader, "content", xmlMessage);
    setValue (reader, "objException", null);
    //System.out.println ("JtAxisService.processMessage:" + xmlMessage);    

    scriptOutput = sendMessage (reader, msg);

    return (convertToXML (scriptOutput));


  }


 /**
   * Unit tests all the message processed by JtAxisService. 
   */


  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg1, msg2;
    //Integer count;
    //JtXMLMsgReader reader; // XML reader;
    JtAxisService adapter;
    String str;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    if (args.length < 1) {
      System.err.println ("Usage: java JtAxisService uri");
      System.exit (1);
    }

    msg1 = new JtMessage ();
    msg2 = new JtMessage ();


    main.createObject ("Jt.JtFile", "file");
    main.setValue ("file", "name", args[0]);    
    main.setValue (msg1, "msgId", "JtCONVERT_TO_STRING");

    str = (String) main.sendMessage ("file", msg1);

    msg1.setMsgId ("JtMessage1");
    msg2.setMsgId ("JtMessage2");


    // Create XML adapter 


    adapter = (JtAxisService) main.createObject ("Jt.axis.JtAxisService", 
     "adapter");

    adapter.processMessage (str);
      

  }

}


