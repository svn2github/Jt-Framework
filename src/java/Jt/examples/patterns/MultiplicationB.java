

package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;

/**
 * Multiply two numbers using repetitive addition.
 */

public class MultiplicationB extends JtObject {



  public MultiplicationB () {
  }


  private int multiply (int op1, int op2) {
    int i, o1, o2;
    int result = 0;
    int sign = 0;

    if (op1 == 0 || op2 == 0)
      return (0);

    if (op1 < 0 && op2 < 0) {
      o1 = -op1;
      o2 = -op2;
      sign = 1;
    } else if (op1 < 0) {
      o1 = -op1;
      o2 = op2;
      sign = -1;
    }  else if (op2 < 0) {
      o1 = op1;
      o2 = -op2;
      sign = -1;

    } else { 
    sign = 1;
    o1 = op1;
    o2 = op2;
    }

    for (i = 0; i < o1; i++)
        result += o2;

    if (sign > 0)       
      return (result);
    else
      return (-result);  

  }


  /**
    * Process object messages.
    * <ul>

    * </ul>
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
       return (new Integer (multiply (op1, op2)));   
     }


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("MultiplicationB.processMessage: invalid message id:" + msgid);
     return (null);

  }



}


