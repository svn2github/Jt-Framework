
package Jt.examples.patterns;

import Jt.*;
import java.io.*;
import java.util.*;
import Jt.xml.*;
import java.beans.*;

/**
 * Calculator implementation based on Command, Memento, Strategy and Factory Method.
 * Creating a subclass of JtCommand and implementing the processMessage
 * method is basically all that is needed. Command requests are logged via
 * the inherited logMessage() method. 
 */

public class Calculator3 extends JtCommand {

  transient Object state = null;        // Object state
  private int total = 0;                // Calculator Total
  private JtMemento memento = null;     // Contains the state of the object
                                        // needed to implement the undo operation
 

  public Calculator3 () {
  }

  public void setTotal (int total) {
    this.total = total;
  }

  public int getTotal () {
    return (total);
  }

  // Save calculator state

  private void saveState () {

    JtMessage msg = new JtMessage ("JtENCODE_OBJECT");
    JtObject tmp = new JtObject();
    Object aux;

    msg.setMsgContent (this);
    aux = tmp.processMessage  (msg);
    
    if (aux == null) {
      handleWarning ("saveSate: Unable to save the current state"); 
      return;
    }

    // Use an instance of JtMemento to store the state

    if (memento == null)
      memento = new JtMemento ();

    memento.setState (aux);

    handleTrace ("saveState: saving state ...\n" + aux);

  }


  private void restoreState () {

    JtMessage msg = new JtMessage ("JtDECODE_OBJECT");
    Calculator3 tmp;
    JtObject copier = new JtObject ();


    if (memento == null) {
      handleWarning ("Warning: nothing to undo ....");
      return;
    }

    state = memento.getState ();
    memento = null;
 
    if (state == null) {
      handleWarning ("Warning: nothing to undo ....");
      return;
    }

    handleTrace ("restoreState (state) ...\n" + state);

    msg.setMsgContent (state);
    //tmp = (Calculator1) xmlHelper.processMessage (msg);
    tmp = (Calculator3) copier.processMessage (msg);

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
    * <li>ADD - Add a number (msgContent) to the total
    * <li>MULTIPLY - Multiply a number (msgContent)
    * </ul>
    */

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage msg = (JtMessage) message;
   Object content;
   JtMessage tmp;
   Integer mres;

     if (msg == null)
	return null;

     // Retrieve Message ID and content

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
	return null;

     content = msg.getMsgContent();

     // Add a number to the total

     if (msgid.equals ("ADD")) {

        // Saves the state of the object
        saveState ();

        // Log the request (message). This method is inherited from JtCommand
   
        logMessage (msg);  

        total += ((Integer) content).intValue ();            

        return (new Integer (total));
     }


     if (msgid.equals ("MULTIPLY")) {


        // Saves the state of the object
        saveState ();

        // Log the message   
        logMessage (msg);

        // Put together the MULTIPLY message
  
        tmp = new JtMessage ("MULTIPLY");
        tmp.setMsgContent (new Integer (total));
        tmp.setMsgData (content);

        // the multiplication object (child of this object) is used
        // to process multiplications.

        mres = (Integer) sendMessage ("multiplication", tmp);      

        total = mres.intValue ();             

        return (new Integer (total));
     }


     if (msgid.equals ("UNDO")) {

 
        restoreState ();
        
        return (new Integer (total));
     }

     // JtRemove message (Remove Object)

     if (msgid.equals ("JtREMOVE")) {
       return (null);
     }

     handleError ("Calculator3.processMessage: invalid message id:" + msgid);
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
    Calculator3 calculator;
    Multiplication multiplication;


    // Create calculator, keyboard and multiplication

    calculator = (Calculator3) main.createObject ("Jt.examples.patterns.Calculator3", "calculator");
    keyboard = (JtKeyboard) main.createObject ("Jt.JtKeyboard", "keyboard");
    multiplication = (Multiplication) calculator.createObject 
                  ("Jt.examples.patterns.Multiplication", "multiplication");

    do {
      System.out.println  ("Please  select a multiplication strategy ...");
      System.out.println  ("1. Standard multiplication");
      System.out.println  ("2. Repetitive addition");

      input = (String) main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));

      if (input == null)
        continue;

      input = input.trim ();

      if (input.equals ("1")) {
        main.setValue (multiplication, "concreteStrategy", new MultiplicationA ());
        break;
      } else  if (input.equals ("2")) {
        main.setValue (multiplication, "concreteStrategy", new MultiplicationB ());
        break;
      }
    } while (true);
    
    for (;;) {

      System.out.println  ("Please  select an operation ...");
      System.out.println  ("1. Addition");
      System.out.println  ("2. Multiplication");
      System.out.println  ("3. Undo the last operation");
      System.out.println  ("4. Exit");


      input = (String) main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));
      if (input == null)
        continue;

      input = input.trim ();

      if (input.equals ("4"))
        break;

      if ("3".equals (input)) {
        msg =  new JtMessage ("UNDO");
        main.sendMessage ("calculator", msg);
        System.out.println 
         (">>> Total:" + main.getValue ("calculator", "total") + " <<<");
        continue;
      }

      if (input.equals ("1")) {
        msg =  new JtMessage ("ADD");
      } else if (input.equals ("2")) {
        msg =  new JtMessage ("MULTIPLY");
      } else
        continue;

      System.out.print ("Enter a number --> ");


      // Read input (number) from the keyboard (JtACTIVATE message)

      input = (String) main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));

      if (input != null)
        input = input.trim ();

      if (input.equals (""))
        continue;


      try {
        num = Integer.parseInt (input);
      } catch (Exception e) {

        System.err.println (e);
        continue;
      }


      // Add the number to the Total (ADD Message)
      //msg =  new JtMessage ("ADD");
      msg.setMsgContent (new Integer (num));   
      total = main.sendMessage ("calculator", msg);

      System.out.println (">>> Total:" + total + " <<<");

    }

    // Print the log (list of requests executed by the calculator)
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



