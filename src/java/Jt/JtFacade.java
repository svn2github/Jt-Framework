

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Facade pattern.
 */

public class JtFacade extends JtComposite {


  public JtFacade () {
  }


  // Test this facade

  private Object test () {
    JtMessage msg, tmp;

    // Broadcast the JtTEST message to all the subsystems
    // of the facade

    System.out.println 
     ("Sending a JtTEST message to all the subsystems of the facade ...");

    msg = new JtMessage ("JtBROADCAST");

    tmp = new JtMessage ("JtTEST");

    msg.setMsgContent (tmp);


    return (sendMessage (this, msg));

  }

  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVES - Performs any housekeeping that may be needed before the object is removed.
    * </ul>
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
     //data = e.getMsgData ();


     if (msgid.equals ("JtTEST")) {
       return (test ());     
     }

     return (super.processMessage (event));
/*
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("JtFacade.processMessage: invalid message id:" + msgid);
     return (null);
*/

  }

 
  /**
   * Unit tests the messages processed by JtFacade.
   */


  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtFacade facade;
    JtEcho echo1;
    JtEcho echo2; 
    JtMessage msg;

    // Create an instance of JtFacade

    facade = (JtFacade) factory.createObject ("Jt.JtFacade", "facade");


    echo1 = (JtEcho) factory.createObject ("Jt.JtEcho", "echo1");
    echo2 = (JtEcho) factory.createObject ("Jt.JtEcho", "echo2");


    System.out.println ("JtFacade(JtADD_CHILD): adding a subsystem ...");

    msg = new JtMessage ("JtADD_CHILD");

    factory.setValue (msg, "msgContent", echo1);
    factory.setValue (msg, "msgData", "echo1");

    factory.sendMessage (facade, msg);


    System.out.println ("JtFacade(JtADD_CHILD): adding a subsystem ...");

    msg = new JtMessage ("JtADD_CHILD");

    factory.setValue (msg, "msgContent", echo2);
    factory.setValue (msg, "msgData", "echo2");

    factory.sendMessage (facade, msg);


    factory.sendMessage (facade, new JtMessage ("JtTEST"));

    factory.removeObject ("facade");


  }


}


