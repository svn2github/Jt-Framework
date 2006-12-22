package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;


/**
 * Demonstrates the use of the Visitor pattern.
 */


public class Visitable extends JtObject {


  public Visitable() {
  }


  /**
    * Process object messages. 
    * <ul>
    * <li>JtACCEPT - Accept operation
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

     if (msgid.equals ("JtACCEPT")) {

       if (content == null) {
         handleError ("processMessage: invalid ACCEPT message; visitor reference is null");
         return (null);
       }

       // Send a JtVISIT message to the visitor.
       // Include a reference to this object

       aux = new JtMessage ("JtVISIT");
       aux.setMsgContent (this);
       return (sendMessage (content, aux));   
     }


     handleError ("Visitable.processMessage: invalid message id:" + msgid);
     return (null);

  }


}
