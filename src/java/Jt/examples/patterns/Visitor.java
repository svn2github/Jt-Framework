package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;


/**
 * Demonstrates the use of the Visitor pattern.
 */


public class Visitor extends JtObject {


  public Visitor() {
  }

   // Encode the object using XML format

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

  // Additional functionality implemented by the Visitor (print the object 
  // using XML format)
 
  private Object printObject (Object obj) {

    Object aux;

    if (obj == null)
      return (null);

    aux = encodeObject (obj);
    System.out.println (aux);
    return (aux);
  }

  /**
    * Process object messages. 
    * <ul>
    * <li>JtVISIT - visit the object specified by msgContent.
    * </ul>
    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;
   Object data;
   JtMessage aux;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();

     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     if (msgid.equals ("JtVISIT")) {
       return (printObject (content));     
     }

     handleError ("JtVisitor.processMessage: invalid message id:" + msgid);
     return (null);

  }

  /**
    * Test program   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    Visitor visitor;
    Visitable node;



    // Create an instance of JtVisitor

    visitor = (Visitor)
      main.createObject ("Jt.examples.patterns.Visitor", 
      "visitor");

    node = (Visitable) main.createObject ("Jt.examples.patterns.Visitable", 
      "node");

    System.out.println ("This visitor prints the XML representation of the object  ...\n");

    // Send an JtACCEPT message to the node. The message contains
    // a reference to the visitor
    msg = new JtMessage ("JtACCEPT");
    msg.setMsgContent (visitor);
    main.sendMessage (node, msg);


    // Remove objects

    main.removeObject (visitor);
    main.removeObject (node);



    
         

  }
}
