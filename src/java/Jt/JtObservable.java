

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt implementation of the Observer pattern.
 */

public class JtObservable extends JtComposite {


  public JtObservable () {
  }


  /**
    * Process object messages.
    * <ul>
    * <li>JtADD_OBSERVER - Adds an observer (msgContent)
    * <li>JtREMOVE_OBSERVER - Removes an observer (msgContent)
    * <li>JtNOTIFY_OBSERVERS - Notifies/Updates its observers when the object changes
    * state. A JtNOTIFY_OBSERVER message is sent to the observers. A reference
    * to this object is part of the message.
    * </ul>
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;
   Object data;
   JtMessage tmp, tmp1;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();
     //data = e.getMsgData ();


     if (msgid.equals ("JtADD_OBSERVER")) {
       tmp = new JtMessage ("JtADD_CHILD");
       tmp.setMsgData (content);
       tmp.setMsgContent (content);
       return (super.processMessage (tmp));     
     }

     if (msgid.equals ("JtREMOVE_OBSERVER")) {
       tmp = new JtMessage ("JtREMOVE_CHILD");
       tmp.setMsgData (content);
       tmp.setMsgContent (content);
       return (super.processMessage (tmp));     
     }


     if (msgid.equals ("JtNOTIFY_OBSERVERS")) {
       tmp = new JtMessage ("JtBROADCAST");
       
       tmp1 = new JtMessage ("JtNOTIFY_OBSERVER");
       tmp1.setMsgContent (this);
       tmp.setMsgContent (tmp1);
       return (super.processMessage (tmp));     
     }


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("JtObserver.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtObserver
   */

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();
    JtObservable observable;
    JtMessage msg;
    JtEcho echo1, echo2;
    

    // Create JtObservable instance

    observable = (JtObservable) main.createObject ("Jt.JtObservable", "observable");
    echo1 = (JtEcho) main.createObject ("Jt.JtEcho", "echo1");
    echo2 = (JtEcho) main.createObject ("Jt.JtEcho", "echo2");

    System.out.println ("JtObservable(JtADD_OBSERVER): adding an observer ...");

    msg = new JtMessage ("JtADD_OBSERVER");
    msg.setMsgContent (echo1);

    main.sendMessage (observable, msg);


    System.out.println ("JtObservable(JtADD_OBSERVER): adding an observer ...");

    msg = new JtMessage ("JtADD_OBSERVER");
    msg.setMsgContent (echo2);

    main.sendMessage (observable, msg);

    System.out.println ("JtObservable(JtNOTIFY_OBSERVERS): notifying observers ...");

    msg = new JtMessage ("JtNOTIFY_OBSERVERS");
    main.sendMessage (observable, msg);
    

    main.removeObject (observable);


  }

}


