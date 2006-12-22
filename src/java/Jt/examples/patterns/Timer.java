package Jt.examples.patterns;

import Jt.*;
import java.io.*;
import java.util.*;
import Jt.xml.*;


/**
 * Timer implementation based on the Command pattern. The timer runs in a separate
 * Thread. 
 */


public class Timer extends JtCommand {

  private long tstart;             // t0
  private long tend;               // t1
  private double time;             // Elapsed time in seconds (delta)

  public Timer() {
    this.setSynchronous (false);   // asyncronous processing (separate thread)
  }

 
  // Attributes

  public double getTime () {
     return (time);
  }


  public void setTime (double time) {
     this.time = time;
  }

  // Process object messages

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage msg = (JtMessage) message;
   Object content;

     if (msg == null)
	return null;

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
	return null;

     content = msg.getMsgContent();

     // Start timer

     if (msgid.equals ("START_TIMER")) {

        tstart = (new Date()).getTime ();    

        // Log the message 
        logMessage (msg);
        sendMessage (this, new JtMessage ("CHECK_TIMER"));  
        return (null);
     }

     // Check the timer

     if (msgid.equals ("CHECK_TIMER")) {

        tend = (new Date ()).getTime ();
        time = (tend - tstart)/1000.0;
        System.out.println ("Timer:" + time);
             
        // Add another CHECK_TIMER request to the queue of messages

        sendMessage (this, new JtMessage ("CHECK_TIMER"));  

        return (null);
     }

     // Stop the timer

     if (msgid.equals ("STOP_TIMER")) {

        tend = (new Date ()).getTime ();
        time = (tend - tstart)/1000.0;
        sendMessage (this, new JtMessage ("JtSTOP"));  
            
        // Log the message
        logMessage (msg);  
        return (null);
     }


     // Let the superclass handle JtSTART, JtREMOVE and JtSTOP

     if (msgid.equals ("JtREMOVE") || msgid.equals ("JtSTART") || msgid.equals ("JtSTOP")) {             
        return (super.processMessage (message));
     }
         
     handleError ("Timer.processMessage: invalid message id:" + msgid);
     return (null);

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
    JtMessage msg, msg1;
    JtXMLHelper xmlHelper = new JtXMLHelper ();
   



    // Create the timer

    main.createObject ("Jt.examples.patterns.Timer", "timer");

    System.out.println ("Press any key to start/stop the timer ....");
    waitForInputKey (); 

    // Send a message to start the timer (separate/independent thread)

    main.sendMessage ("timer", new JtMessage ("START_TIMER"));


    //System.out.println ("Press any key to stop the timer ....");
    waitForInputKey ();    


    main.sendMessage ("timer", new JtMessage ("STOP_TIMER"));

    System.out.println (main.getValue ("timer", "time") + " second(s) elapsed");

    // Remove object

    main.removeObject ("timer");        

  }

}



