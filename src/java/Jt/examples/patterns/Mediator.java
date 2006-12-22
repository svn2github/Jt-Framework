
package Jt.examples.patterns;

import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;

/**
 * Chat room implementation based on the Mediator pattern. Colleagues run in separate
 * threads. 
 */

public class Mediator extends JtMediator {



  public Mediator () {
  }


  // Broadcast a message to all the members

  private void broadcastMessage (JtMessage msg) {
    JtMessage tmp;

    if (msg == null)
     return;
    
    tmp = new JtMessage ("JtBROADCAST");
    tmp.setMsgContent (msg);

    processMessage (tmp);
  }




  /**
    * Process object messages.
    * <ul>
    * </ul>
    * @param message Jt Message
    */


  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage e = (JtMessage) message;
   Object content;
   Object data;
   JtMessage tmp;
   JtObject colleague;

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


     // Join the chat room

     if (msgid.equals ("JOIN")) {
      


       colleague = (JtObject) e.getMsgFrom ();       
       
       System.out.println (colleague.getObjName() + " has joined");
       tmp = new JtMessage ("MESSAGE");
       tmp.setMsgContent (colleague.getObjName() + " has joined");
       
       // Alert everyone about the new member

       broadcastMessage (tmp);

       // Add the new member to the chat room

       tmp = new JtMessage ("JtADD_CHILD");
       tmp.setMsgContent (e.getMsgFrom ());
       tmp.setMsgData (colleague.getObjName());
       processMessage (tmp);
            
       return (this);

     }    

     // Send a message to the chat room (everyone)

     if (msgid.equals ("MESSAGE")) {

       colleague = (JtObject) e.getMsgFrom ();  

       broadcastMessage (e);     
      
       System.out.println (colleague.getObjName() + ":" + content);
       return (this);

     }       

     // Exit the chat room
          
     if (msgid.equals ("EXIT")) {

       colleague = (JtObject) e.getMsgFrom ();  

       tmp = new JtMessage ("MESSAGE");
       tmp.setMsgContent (colleague.getObjName() + " is exiting ...");

       broadcastMessage (tmp);     
      
       System.out.println (colleague.getObjName() + " is exiting ...");

       tmp = new JtMessage ("JtREMOVE_CHILD");
       tmp.setMsgData (e.getMsgFrom ());

       return (this);

     } 

     // Let the superclass handle all the other messages
   
     return (super.processMessage (message));


  }


  static private char waitForInputKey () {
    char c = ' ';

      try {

      c = (char) System.in.read ();
      while (System.in.available () > 0)
        System.in.read ();

      } catch (Exception e) {
        e.printStackTrace ();
      }

      return (c);
  } 

  // Test program

  public static void main(String[] args) {

    JtObject main = new JtFactory ();

    JtMediator mediator;
    Colleague colleague1, colleague2, colleague3;

    System.out.println ("Press any key to start/stop the chat room demo ...");
    waitForInputKey (); 


    // Create an instance of the chat room

    mediator = (JtMediator) main.createObject ("Jt.examples.patterns.Mediator", "mediator");

     colleague1 = (Colleague) main.createObject ("Jt.examples.patterns.Colleague", "Jenny");
    main.setValue (colleague1, "greetingMessage", "Hi folks! How are you all doing ?");


    // Activate the first member

    main.setValue (colleague1, "mediator", mediator);   
    main.sendMessage (colleague1, new JtMessage ("JtACTIVATE"));


    // Activate the second member

    colleague2 = (Colleague) main.createObject ("Jt.examples.patterns.Colleague", "Daniel");

    main.setValue (colleague2, "mediator", mediator);   
    main.sendMessage (colleague2, new JtMessage ("JtACTIVATE"));


    // Activate the third member

    colleague3 = (Colleague) main.createObject ("Jt.examples.patterns.Colleague", "Mary");

    main.setValue (colleague3, "mediator", mediator);   
    main.sendMessage (colleague3, new JtMessage ("JtACTIVATE"));



    waitForInputKey (); 

    // Remove the objects

    main.removeObject ("mediator");
    main.removeObject ("Jenny");
    main.removeObject ("Daniel");
    main.removeObject ("Mary");
  }

}


