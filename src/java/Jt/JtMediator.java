

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Mediator pattern.
 */

public class JtMediator extends JtComposite {


  public JtMediator () {
  }



  /**
    * Process object messages. Let the superclass handle JtComposite related
    * messages.
    * <ul>
    * </ul>
    * @param message Jt Message
    */


  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage e = (JtMessage) message;
   Object content;
   Object data;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();
     data = e.getMsgData ();

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     // Let the superclass handle JtComposite related

     return (super.processMessage (message));              

  }

 
  /**
   * Unit tests the messages processed by JtMediator
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();

    JtMediator mediator;

    // Create an instance of JtColletion

    mediator = (JtMediator) main.createObject ("Jt.JtMediator", "mediator");


    main.removeObject ("mediator");


  }

}


