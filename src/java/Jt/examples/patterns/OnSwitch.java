package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;



/**
 * Demonstrates the use of the State pattern (see Flyweight.java).
 */



public class OnSwitch extends JtState {



  public OnSwitch () {
  }




  /**
    * Process object messages. 
    * <ul>
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

     if (msgid.equals ("JtSWITCH_VALUE")) {
       return ("On");     
     }
     handleError ("processMessage: invalid message id:" + msgid);
     return (null);

  }

 
}
