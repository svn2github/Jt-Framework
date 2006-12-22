package Jt.examples.jms;

import Jt.*;
import Jt.jms.*;
import java.io.*;

// Demonstrates the use of JtJMSTopicAdapter (JMS publish/subscriber adapter)

public class JMSSubscriber {



  // Test program

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();  // Jt Factory
    JtMessage msg;
    String greeting;
    String reply;
    Jt.jms.JtJMSTopicAdapter jmsAdapter;
    JtEcho echo = new JtEcho ();
    JtKeyboard keyboard = new JtKeyboard ();



    jmsAdapter = (Jt.jms.JtJMSTopicAdapter) main.createObject 
        ("Jt.jms.JtJMSTopicAdapter", "jmsAdapter");


    main.setValue (jmsAdapter, "subject", echo);
    main.sendMessage (jmsAdapter, new JtMessage ("JtSTART_LISTENING"));

    main.sendMessage (keyboard, new JtMessage ("JtACTIVATE"));
       

 

    // Remove jmsAdapter

    main.removeObject ("jmsAdapter");

        

  }

}



