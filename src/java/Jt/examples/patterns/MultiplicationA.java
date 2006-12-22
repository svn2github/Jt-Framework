

package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;

/**
 * Multiply two numbers
 */

public class MultiplicationA extends JtObject {




  public MultiplicationA () {
  }




  /**
    * Process object messages.
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;
   Object data;
   int op1, op2;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();
     data = e.getMsgData ();

     op1 = ((Integer) content).intValue  ();

     op2 = ((Integer) data).intValue  ();

     if (msgid.equals ("MULTIPLY")) {
       return (new Integer (op1 * op2));    
     }


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("MultiplicationA.processMessage: invalid message id:" + msgid);
     return (null);

  }

 


}


