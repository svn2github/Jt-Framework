package Jt.examples.patterns;
import Jt.*;



/**
 * Demonstrates the use of the State pattern (see Flyweight.java).
 */


public class OffSwitch extends JtState {



private static final long serialVersionUID = 1L;



public OffSwitch () {
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
   //Object content;
   //Object data;
   //JtMessage aux;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();

     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     if (msgid.equals ("JtSWITCH_VALUE")) {
       return ("Off");     
     }
     handleError ("processMessage: invalid message id:" + msgid);
     return (null);

  }

 
}
