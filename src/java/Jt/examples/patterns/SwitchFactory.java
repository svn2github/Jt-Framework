package Jt.examples.patterns;

import Jt.*;


/**
 * Demonstrates the use of the Abstract Factory pattern (see Flyweight.java).
 */


public class SwitchFactory extends JtAbstractFactory {




private static final long serialVersionUID = 1L;




public SwitchFactory() {
  }




  /**
    * Process object messages. 
    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;
   String data;
   Object output;

 
     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();


     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     if (msgid.equals ("JtCREATE_FLYWEIGHT")) {
       data = (String) e.getMsgData ();
       if (data == null) {
         handleError ("JtCREATE_FLYWEIGHT: invalid key");
         return (null);
       }

       if (data.equals ("On"))
         output = new OnSwitch ();
       else
         output = new OffSwitch ();
       
       return (output);     
     }

     handleError ("processMessage: invalid message id:" + msgid);
     return (null);


  }



}

