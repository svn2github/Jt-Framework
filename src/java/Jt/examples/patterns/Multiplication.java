package Jt.examples.patterns;

import Jt.*;
import java.io.*;
import java.util.*;
import Jt.xml.*;
import java.beans.*;

/**
 * Calculator implementation based on Strategy. Refer to Calculator3.java
 */

public class Multiplication extends JtStrategy {


  public Multiplication () {
  }


  /**
    * Process object messages (Command requests).
    */

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage msg = (JtMessage) message;
   Object content;

     if (msg == null)
	return null;

     // Retrieve Message ID and content

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
	return null;

     content = msg.getMsgContent();

     // Let the superclass (JtStrategy) handle this message.
     // Strategy will forward the message to its concrete strategy.


     if (msgid.equals ("MULTIPLY")) {

        return (super.processMessage (message)); 

     }


     // JtRemove message (Remove Object)

     if (msgid.equals ("JtREMOVE")) {
       return (null);
     }

     handleError ("Multiplication.processMessage: invalid message id:" + msgid);
     return (null);
  }


}



