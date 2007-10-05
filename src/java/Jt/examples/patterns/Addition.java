

package Jt.examples.patterns;

import Jt.*;

/**
 * Add two numbers
 */

public class Addition extends JtObject {



 private static final long serialVersionUID = 1L;


  public Addition () {
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

     if (msgid.equals ("ADD")) {
       return (new Integer (op1 + op2));    
     }


     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }


     handleError ("Addition.processMessage: invalid message id:" + msgid);
     return (null);

  }

 


}


