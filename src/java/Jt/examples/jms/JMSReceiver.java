package Jt.examples.jms;

import Jt.*;
import Jt.jms.*;
import java.io.*;


// Demonstrates the use of JtJMSQueueAdapter (synchronous mode).
// Use the adapter itself (Jt.jms.JtJMSQueueAdapter) to send messages to the 
// JMS queue.

public class JMSReceiver  {


  // Test program (JMSQueue adapter)

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();  // Jt Factory
    JtMessage msg;
    String greeting;
    String reply;
    Jt.jms.JtJMSQueueAdapter jmsAdapter;


    // Create the JMS adapter (point-to-point)

    jmsAdapter = (Jt.jms.JtJMSQueueAdapter) main.createObject 
        ("Jt.jms.JtJMSQueueAdapter", "jmsAdapter");

       
    // Receive all the messages (Jt messages)

    for (;;) {

      msg = (JtMessage) main.sendMessage (jmsAdapter, new JtMessage ("JtRECEIVE"));

      if (msg == null) {
        System.out.println ("no more messages");
        break;
      } 

      System.out.println ("msgId:" + msg.getMsgId ());

    }
 

    // Remove jmsAdapter

    main.removeObject ("jmsAdapter");

        

  }

}



