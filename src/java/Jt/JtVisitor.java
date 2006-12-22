package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;


/**
 * Jt Implementation of the Visitor pattern.
 */


public class JtVisitor extends JtObject {


  public JtVisitor() {
  }



  /**
    * Process object messages. 
    * <ul>
    * <li>JtVISIT - visit the object specified by msgContent. Subclasses need to
    * override this method and provide a complete implementation for this message.
    * </ul>
    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;
   Object data;


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
       return (this);     
     }

     handleError ("JtVisitor.processMessage: invalid message id:" + msgid);
     return (null);

  }

  /**
    * Unit Tests the messages processed by JtVisitor.   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    JtVisitor visitor;


    // Create an instance of JtVisitor

    visitor = (JtVisitor)
      main.createObject ("Jt.JtVisitor", 
      "visitor");


    main.removeObject (visitor);


    
         

  }

}
