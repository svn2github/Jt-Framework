
package Jt.xml;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;

/**
 *  Converts JT API calls into XML messages. These messages can be sent
 *  to remote objects. This class is being deprecated.
 */

public class JtXMLConverter extends JtObject {

  private String xmlMsg = null;
  private StringBuffer buffer = null;
  private String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";


  public JtXMLConverter() {
  }


  public Object createObject (Object class_name, Object id) {
    if (class_name == null || id == null) // check
      return (null);

    if (buffer == null)
      buffer = new StringBuffer ();

    buffer.append ("<Object>\n");
    buffer.append ("<classname>Jt.JtMessage</classname>\n");
    buffer.append ("<msgId>JtCREATE_OBJECT</msgId>\n");

    buffer.append ("<msgContent>"
      + class_name + "</msgContent>\n");

    buffer.append ("<msgData>"
      +  id + "</msgData>\n");
    buffer.append ("</Object>\n");

    return (null);
  }

  public Object sendMessage (Object id, Object msgid) {

    if (id == null || msgid == null)
      return (null);

    if (buffer == null)
      buffer = new StringBuffer ();

    buffer.append ("<Object>\n");
    buffer.append ("<classname>Jt.JtMessage</classname>\n");
    buffer.append ("<msgId>JtSEND_MESSAGE</msgId>\n");

    buffer.append ("<msgContent>"
      + id + "</msgContent>\n");

    buffer.append ("<msgData>"
      +  msgid + "</msgData>\n");
    buffer.append ("</Object>\n");

    //buffer.insert (0, xmlHeader);

/*
    return (xmlHeader + "<JtCollection>\n" +
             buffer.toString () +
             "</JtCollection>\n");
*/
    return (null);
  } 



  public void removeObject (Object id) {

    if (id == null)
      return;

    if (buffer == null)
      buffer = new StringBuffer ();

    buffer.append ("<Object>\n");
    buffer.append ("<classname>Jt.JtMessage</classname>\n");
    buffer.append ("<msgId>JtREMOVE_OBJECT</msgId>\n");

    buffer.append ("<msgContent>"
      + id + "</msgContent>\n");

    buffer.append ("</Object>\n");
  } 
 
  public void setValue (Object id, Object att, 
    Object value) {

    if (id == null || att == null || value == null)
      return;

    if (buffer == null)
      buffer = new StringBuffer ();

    buffer.append ("<Object>\n");
    buffer.append ("<classname>Jt.JtMessage</classname>\n");
    buffer.append ("<msgId>JtSET_VALUE</msgId>\n");
    buffer.append ("<msgSubject>"
      + id + "</msgSubject>\n");

    buffer.append ("<msgContent>"
      + att + "</msgContent>\n");

    buffer.append ("<msgData>"
      +  value + "</msgData>\n");

    buffer.append ("</Object>\n");

  }


  public Object getValue (Object id, Object att) {

    if (id == null || att == null)
      return (null);

    if (buffer == null)
      buffer = new StringBuffer ();

    buffer.append ("<Object>\n");
    buffer.append ("<classname>Jt.JtMessage</classname>\n");
    buffer.append ("<msgId>JtGET_VALUE</msgId>\n");


    buffer.append ("<msgContent>"
      + id + "</msgContent>\n");

    buffer.append ("<msgData>"
      +  att + "</msgData>\n");

    buffer.append ("</Object>\n");

    return (null);

  }

  String convertToXML () {

    if (buffer == null)
      return (null);

    return (xmlHeader + "<Object>\n" +
             "<classname>Jt.JtList</classname>\n" +
             buffer.toString () +
             "</Object>\n");

  }

  // Process object messages

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

     if (msgid.equals ("JtCONVERT"))
       return (convertToXML ());

     // Let the parent class process the message

     return (super.processMessage (event));     
     //handleError ("JtTemplate.processMessage: invalid message id:" + msgid);


  }


 /**
   * Unit tests all the message processed by JtXMLConverter. 
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg1;
    Integer count;
    JtXMLConverter converter;
    String str;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    msg1 = new JtMessage ();

    msg1.setMsgId ("JtCONVERT");


    converter = (JtXMLConverter) main.createObject ("Jt.xml.JtXMLConverter", 
     "adapter");

    converter.createObject ("Jt.JtCommand", "command");
    converter.setValue ("command", "command", "notepad");
    converter.sendMessage ("command", "JtEXECUTE");
    converter.getValue ("command", "command");

    str = (String) main.sendMessage (converter, msg1);

    System.out.println ("XML =" + str);

  }

}


