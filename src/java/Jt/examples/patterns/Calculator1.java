
package Jt.examples.patterns;

import Jt.*;
import java.io.*;
import java.util.*;
import Jt.xml.*;
import java.beans.*;

/**
 * Calculator implementation based on Command and Memento.
 * Creating a subclass of JtCommand and implementing the processMessage
 * method is basically all that is needed. Command requests are logged via
 * the inherited LogMessage method. 
 */

public class Calculator1 extends JtCommand {

  transient Object state = null;        // Object state
  private int total = 0;                // Calculator Total
  private JtMemento memento = null;     // Contains the state of the object
                                        // needed to implement the undo operation
 

  public Calculator1 () {
  }

  public void setTotal (int total) {
    this.total = total;
  }

  public int getTotal () {
    return (total);
  }

  private void saveState () {

    JtMessage msg = new JtMessage ("JtENCODE_OBJECT");

    JtObject tmp = new JtObject();

    msg.setMsgContent (this);


    state = tmp.processMessage  (msg);
    
    if (state == null) {
      handleWarning ("saveSate: Unable to save the current state"); //check
      return;
    }

    if (memento == null)
      memento = new JtMemento ();

    memento.setState (state);

    handleTrace ("saveState: saving state ...\n" + state);

  }


  private void restoreState () {

    JtMessage msg = new JtMessage ("JtDECODE_OBJECT");
    Calculator1 tmp;
    JtObject copier = new JtObject ();


    if (memento == null) {
      handleWarning ("restoreState: nothing to undo ....");
      return;
    }

    state = memento.getState ();
    memento = null;
 
    if (state == null) {
      handleWarning ("restoreState: nothing to undo ....");
      return;
    }

    handleTrace ("restoreState (state) ...\n" + state);

    msg.setMsgContent (state);
    //tmp = (Calculator1) xmlHelper.processMessage (msg);
    tmp = (Calculator1) copier.processMessage (msg);

    msg = new JtMessage ("JtENCODE_OBJECT");
    msg.setMsgContent (tmp);

    //System.out.println ("...\n" + copier.processMessage (msg));
    //System.out.println ("src...\n" + tmp.getMessageLog ());

            
    msg = new JtMessage ("JtCOPY_OBJECT");

    if (tmp == null) {
      handleTrace ("restoreState failed: unable to convert object from XML"); 
      return;
    }
    
    msg.setMsgContent (tmp);
    msg.setMsgData (this);
    sendMessage (copier, msg);

    msg = new JtMessage ("JtENCODE_OBJECT");
    msg.setMsgContent (this);


  }



  /**
    * Process object messages (Command requests).
    * <ul>
    * <li>JtADD - Add a number (msgContent) to the total
    * </ul>
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

     // Add a number to the total

     if (msgid.equals ("ADD")) {


        saveState ();

        // Log the message   
        logMessage (msg);  

        total += ((Integer) content).intValue ();    

         

        return (new Integer (total));
     }


     if (msgid.equals ("UNDO")) {

 

        restoreState ();
        
        // Log the message 
        // logMessage (msg);  
        return (new Integer (total));
     }

     // JtRemove message (Remove Object)

     if (msgid.equals ("JtREMOVE")) {
       return (null);
     }

     handleError ("Calculator1.processMessage: invalid message id:" + msgid);
     return (null);
  }


 
  /**
   * Calculator implementation (main)
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg, msg1;
    Object total;
    JtKeyboard keyboard;
    String input;    
    int num = 0;
    Calculator1 calculator;

    // Create calculator and Keyboard

    calculator = (Calculator1) main.createObject ("Jt.examples.patterns.Calculator1", "calculator");
    keyboard = (JtKeyboard) main.createObject ("Jt.JtKeyboard", "keyboard");

    System.out.println 
("Enter a number to be added to the total or 'U' to undo the last operation  (or <CR> to exit):");

    for (;;) {

      // Read input (number) from the keyboard (JtACTIVATE message)

      input = (String) main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));

      if (input != null)
        input = input.trim ();


      if ("".equals (input)) 
        break;


      if ("U".equals (input) || "u".equals (input)) {
        msg =  new JtMessage ("UNDO");
        main.sendMessage ("calculator", msg);
        System.out.println ("Total:" + main.getValue ("calculator", "total"));
        continue;
      }


      try {
        num = Integer.parseInt (input);
      } catch (Exception e) {

        System.err.println (e);
      }


      // Add the number to the Total (ADD Message)
      msg =  new JtMessage ("ADD");
      msg.setMsgContent (new Integer (num));   
      total = main.sendMessage ("calculator", msg);

      System.out.println ("Total:" + total);

    }

    // Print the log (list of requests executed by the Calculator)
    // Use main to convert the log information (messageLog)
    // into XML.

    msg = new JtMessage ("JtENCODE_OBJECT");
    msg.setMsgContent (main.getValue ("calculator", "messageLog"));

    System.out.println ("Log:\n" + 
      main.sendMessage (main, msg));


    // Remove object

    main.removeObject ("calculator");        

  }

}



