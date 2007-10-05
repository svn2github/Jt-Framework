package Jt.examples.jms;

import Jt.*;



// Demonstrates the use of JtJMSQueueAdapter (asynchronous mode).

public class JMSAsyncReceiver {




  // Test program

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();  // Jt Factory
    //JtMessage msg;
    //String greeting;
    //String reply;
    Jt.jms.JtJMSQueueAdapter jmsAdapter;
    JtEcho echo = new JtEcho ();
    JtKeyboard keyboard = new JtKeyboard ();


    // Create the JMS adapter (point-to-point)

    jmsAdapter = (Jt.jms.JtJMSQueueAdapter) main.createObject 
        ("Jt.jms.JtJMSQueueAdapter", "jmsAdapter");

    // Asynchronous mode. Incoming messages will be redirected to echo

    main.setValue (jmsAdapter, "subject", echo);
    main.sendMessage (jmsAdapter, new JtMessage ("JtSTART_LISTENING"));


    System.out.println ("Listening for asynchronous messages ... ");
 
    main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));
        

    // Remove jmsAdapter

    main.removeObject ("jmsAdapter");

        

  }

}



