package Jt.examples.patterns;

import Jt.*;
import Jt.xml.*;

/**
 * Calculator implementation based on the Jt Command pattern.
 * Creating a subclass of JtCommand and implementing the processMessage
 * method is basically all that is needed. Command requests are logged via
 * the inherited LogMessage method. 
 */

public class Calculator extends JtCommand {


  private static final long serialVersionUID = 1L;
  private int total = 0;                // Calculator Total

  public Calculator () {
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

        total += ((Integer) content).intValue ();    
        
        // Log the message 
        logMessage (msg);  
        return (new Integer (total));
     }

     // JtRemove message (Remove Object)

     if (msgid.equals ("JtREMOVE")) {
       return (this);
     }

     handleError ("Calculator.processMessage: invalid message id:" + msgid);
     return (null);
  }


 
  /**
   * Calculator implementation (main)
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    JtXMLHelper xmlHelper = new JtXMLHelper ();
    Object total;
    JtKeyboard keyboard;
    String input;    
    int num = 0;

    // Create calculator and Keyboard

    main.createObject ("Jt.examples.patterns.Calculator", "calculator");
    keyboard = (JtKeyboard) main.createObject ("Jt.JtKeyboard", "keyboard");

    System.out.println ("Enter a number to be added to the total (or <CR> to exit):");

    for (;;) {

      // Read input (number) from the keyboard (JtACTIVATE message)

      input = (String) main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));

      input = input.trim ();

      if ("".equals (input)) 
        break;


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
    // Use the xmlHelper to convert the log information (messageLog)
    // into XML.

    msg = new JtMessage ("JtCONVERT_OBJECT_TO_XML");
    msg.setMsgContent (main.getValue ("calculator", "messageLog"));

    System.out.println ("Log:\n" + 
      main.sendMessage (xmlHelper, msg));



    // Remove object

    main.removeObject ("calculator");        

  }

}



