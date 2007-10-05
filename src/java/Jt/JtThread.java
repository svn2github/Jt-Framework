package Jt;


/** 
 *  Jt objects that inherit from this class execute in a separate/independent thread: 
 *  Jt Messages are processed asynchronously using a separate thread. 
 *  A queue of messages is required to accomplish this asynchronous behavior.
 *  When a Jt message is sent to this object (sendMessage), 
 *  the message is automatically added to the message queue. 
 *  The run method (separate thread) is contantly extracting the next message in 
 *  the queue and calling processMessage() to process it.
 */


public class JtThread extends JtObject implements Runnable {


  private static final long serialVersionUID = 1L;
  // Object states

  public static final int JtINACTIVE_STATE = 0; // Before start
  public static final int JtACTIVE_STATE = 1;   // After start
  //public static final int JtSUSPENDED_STATE = 2;
  public static final int JtSTOPPED_STATE = 3;  // After stop event
  public static final int JtIDLE_STATE = 4;     // No messages in the queue
 

  // Events

  private static final int JtSTART = 21;       // Start the object (start processing messages)                                             
  private static final int JtSTOP = 20;        // Stop the object
  private static final int JtEMPTY_QUEUE = 15; // Message queue is empty
  private static final int JtNEW_MESSAGE = 16; // New message in the queue

  //public static final int JtRESUME = 19;
  
  //public static final int JtSUSPEND = 18;
  //public static final int JtNOEVENT = 0;


  private int state = JtINACTIVE_STATE;       // Current state
  private boolean daemon = true;              // Daemon thread (default)
  private int priority = Thread.NORM_PRIORITY; 

  private transient Thread thread = null;     // Thread
  JtQueue msgQueue = new JtQueue ();          // Message queue


  public JtThread() {
  }

/**
  * Returns the Thread instance.
  */

  public Thread getThread () {
    return (thread);
  }

/**
  * Changes the Thread instance.
  * @param thread thread
  */

  public void setThread (Thread thread) {
    this.thread = thread; // check
  }

/**
  * Returns this object's state.
  */

  public int getState () {
    return (state);
  }

/**
  * Changes this object's state.
  */

  public void setState (int state) {
    this.state = state;
  }

/**
  * Returns the thread priority.
  */

  public int getPriority () {
    if (thread != null)
      priority = thread.getPriority ();
    return (priority);
  }


/**
  * Changes the thread priority.
  */

  public void setPriority (int priority) {

    if (thread != null) {
      thread.setPriority (priority);  // check
      this.priority = thread.getPriority ();
    } else
      this.priority = priority;


  }




/**
  * Verifies if the object's thread is a daemon thread or a user thread.
  */

  public boolean getDaemon () {

   if (thread != null) {
      daemon = thread.isDaemon ();  // check
   } 
     
    return (daemon);
  }


/**
  * Marks the object's thread as either a daemon thread or a user thread.
  */


  public void setdaemon (boolean daemon) {
    
    if (thread != null) {
      try {
       thread.setDaemon (daemon);  // check
      } catch (Exception e) {
       handleException (e);
      }
      this.daemon = thread.isDaemon ();
    } else
      this.daemon = daemon;

  }

  // Activate object

  void activate () {

     thread = new Thread ((Runnable) this);

     if (thread == null) {
        handleError("JtThread.activate: unable to create new thread");
        return;
     }

     handleTrace ("JtThread.activate: ..." + thread.getName());

     try { // just in case


        if (daemon)
         thread.setDaemon (true);

        thread.start ();

        thread.setPriority (priority);  // check
     } catch (Exception e) {
        //updateState (JtINACTIVE_STATE); check
        handleException (e);
     }

  }



  // sleep_for_awhile: sleep for a period of time

  void sleep_for_awhile (long period) {

    try {
        Thread.sleep (period);
    } catch (Exception e) {
        handleException (e);
    }

  }
  

  // updateState


  synchronized private void updateState (int state)
  {
    this.state = state;
    handleTrace ("JtThread.updateState:" + stateName (state));
  }


  // State name

  private String stateName (int state) {

    switch (state) {
      case JtINACTIVE_STATE:
        return ("INACTIVE_STATE");
      case JtACTIVE_STATE:
        return ("ACTIVE_STATE");
      //case JtSUSPENDED_STATE:
        //return ("SUSPENDED_STATE");
      case JtSTOPPED_STATE:
        return ("STOPPED_STATE");
      case JtIDLE_STATE:
        return ("IDLE_STATE");
      default:
        return ("UNKNOWN");

    }

  }


  // Event name

  private String eventName (int event) {

    switch (event) {
      case JtSTOP:
        return ("STOP");
      //case JtRESUME:
        //return ("RESUME");
      //case JtSUSPEND:
        //return ("SUSPEND");
      case JtSTART:
        return ("START");
      case JtEMPTY_QUEUE:
        return ("EMPTY_QUEUE");
      case JtNEW_MESSAGE:
        return ("NEW_MESSAGE");
      case 0:
        return ("NOEVENT");
      default:
        return ("UNKNOWN");

    }

  }

  // Request state transition

  synchronized private void requestStateTransition (int event) {

    if (event < 0)
      return;
    
    switch (state) {
      case JtINACTIVE_STATE: 
        if (event == JtSTART || event == JtNEW_MESSAGE) {
          updateState (JtACTIVE_STATE); // check         
          activate ();
          break;
        }
        if (event == JtSTOP) {
          updateState (JtSTOPPED_STATE);                 
          break;        
        }
        invalidTransition (state, event);
        break;
      case JtACTIVE_STATE:
        if (event == JtEMPTY_QUEUE) {
          updateState (JtIDLE_STATE); 
          suspendThread ();
          break;                   
        }
        if (event == JtNEW_MESSAGE || event == JtSTART) {
          break;                   
        }
        if (event == JtSTOP) {
          updateState (JtSTOPPED_STATE);                 
          break;        
        }
        invalidTransition (state, event);
        break;
      case JtIDLE_STATE:
        if (event == JtNEW_MESSAGE) {
          updateState (JtACTIVE_STATE); 
          resumeThread ();                  
          break; 
        }
        if (event == JtSTOP) {
          updateState (JtSTOPPED_STATE);  
          // resume the Thread 
          resumeThread ();                
          break;        
        }
        invalidTransition (state, event);
        break;
       default:
     }

  }

  // Invalid state transition

  private void invalidTransition (int state, int event) {

    handleError ("JtThread.invalidTransition: invalid state transition(state, event):"
            + stateName(state)
            + "," + eventName (event));

  }


  // Dequeue a message
  
  synchronized private Object dequeueMessage () {

    Object omsg;

    if (msgQueue == null)
      return (null); // this should never happen    

    if (msgQueue.getSize () == 0)
      return (null);

    omsg = msgQueue.processMessage (new JtMessage ("JtDEQUEUE"));

    return (omsg);
  }



  // Check empty queue

  synchronized private boolean checkEmptyQueue ()
  {

    if (msgQueue.getSize () == 0) {
      handleTrace 
       ("JtThread.checkEmptyQueue:empty queue");


      requestStateTransition (JtEMPTY_QUEUE);    
      return (true);
    } else
      return (false);    
   
  }



  // processQueue: process queue of messages

  private void processQueue ()
  {
    int i;
    Object omsg;
    int size;



    if (checkEmptyQueue ())
      return;

    //handleTrace 
    //   ("JtThread.processQueue:queue size:" + msgQueue.getSize ());

    size =  msgQueue.getSize (); 
    for (i = 0; i < size; i++)
    {
      try { // just in case


        if (state == JtSTOPPED_STATE) // Do I need to stop ?
          break;

        //processNextMessage ();
        omsg = dequeueMessage ();
        processMessage (omsg);

      } catch (Exception e) {
        handleException (e);
      }
    }
  }


  /**
   * Extracts Jt messages from the queue and processes them via
   * processMessage (). Returns when the state becomes JtSTOPPED_STATE (JtSTOP message).
   */

  public final void run () {

    while (true) {

      if (state == JtSTOPPED_STATE)
        break;
      
      //sleep_for_awhile (2000L);

      processQueue (); // Process queue of messages

    }
    handleTrace ("JtThread.run: object (thread) is stopping ..." + thread.getName());
  }

  // Thread management 

  private synchronized void suspendThread () {


    handleTrace ("JtThread.suspendThread:suspending thread " + thread.getName());
    try {
      wait ();
    } catch (Exception e) {
      handleException (e);
    }
    handleTrace ("JtThread.suspendThread:resuming ... " + thread.getName());
  }

  private synchronized void resumeThread () {

    handleTrace ("JtThread.resumeThread: ... " + thread.getName());
    notify ();
  }

  synchronized private void checkInactiveState () {

    if (state == JtINACTIVE_STATE)
     requestStateTransition (JtNEW_MESSAGE);     

  }


/** 
 *  Add a message to the queue for further processing.
 *  When a Jt message is sent to this object via sendMessage, 
 *  the message is automatically added to the message queue. 
 *  The run method (separate thread) is contantly extracting the next message in 
 *  the queue and calling processMessage() to process it.
 */


  synchronized public Object enqueueMessage (Object msg)
  {
    JtMessage imsg, tmp;
    String msgid = null;


    if (msg == null)
      return (null);
            
    imsg = (JtMessage) msg; 
    msgid = (String) imsg.getMsgId ();

    // If the current state is inactive, activate the thread since 
    // a new message has been received

    checkInactiveState ();

    // JtSTART and JtSTOP should be processed right away (don't add these to the queue)

    if (msgid.equals ("JtSTART") || msgid.equals ("JtSTOP")) {
       processMessage (msg);
       return (msg);
    }

    // enqueue the message

    tmp = new JtMessage ("JtENQUEUE");
    tmp.setMsgContent (msg);
    msgQueue.processMessage (tmp);

    requestStateTransition (JtNEW_MESSAGE);
    return (msg); //check

  }
  /**
    * Process object messages. 
    * <ul>
    * <li> JtSTART - Starts the thread associated with this object. 
    * <li> JtSTOP - Stops the thread by resetting the state variable to JtSTOPPED_STATE.
    * This causes the run method to return.
    * <li> JtREMOVE - Performs any housekeeping needed before this object is removed.  
    * This includes stopping its execution thread. 
    * </ul>
    * @param event Jt Message    
    */


  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;

     if (e == null)
	return null;
            

     msgid = (String) e.getMsgId ();

     if (msgid == null)
       return null;

     handleTrace ("JtThread.processMessage:" + msgid);

     if (msgid.equals ("JtSTART")) {
       requestStateTransition (JtSTART);
       return (null);
     }

     if (msgid.equals ("JtSTOP") || msgid.equals ("JtREMOVE")) {
       requestStateTransition (JtSTOP);
       return (null);
     }

    
     return (null); 

/*
     handleError ("JtThread.processMessage: invalid message id:" + msgid);
     return (null);
*/

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

  static private char readInputKey () {
    char c = ' ';

      try {

      if (System.in.available () <= 0) {
        return (' ');
      }

      c = (char) System.in.read ();
      while (System.in.available () > 0)
        System.in.read ();

      } catch (Exception e) {
        e.printStackTrace ();
      }

      return (c);
  }



  /**
    * Unit tests the messages processed by JtThread and illustrates its use.
    */

  public static void main(String[] args) {

    JtThread thread;
    JtObject main;
    char c = 0;
    int flag = 0;
    int i;

    main = new JtObject ();
    //main.setObjTrace (1);
    thread = (JtThread) main.createObject ("Jt.JtThread", "thread");
    main.setValue (thread, "daemon", "true");
    main.setValue (thread, "priority", "" + Thread.MIN_PRIORITY);





    System.out.print ("Press any Key to Start the object (thread) ...");

    waitForInputKey ();

    main.sendMessage ("thread", new JtMessage ("JtSTART"));

    System.out.println ("Press X to stop the object. ");
    System.out.println ("Press any other key to start/stop sending messages to the object ...");

    
    c = waitForInputKey ();
    if (c != 'X') {
    flag = 1;
    for (i = 1; ;) {
      String  tmp = "JtTEST" + i;

      if (flag == 1) {
        i++;
        // Send a test message to the object
        main.sendMessage (thread, new JtMessage (tmp));
        System.out.println (i);

      } else
        c = waitForInputKey ();

      if (flag == 1)
        c = readInputKey ( );
      if (c != ' ') {
        flag = 1 - flag; // Start/Stop sending messages
      }

      // Stop the object
      if (c == 'X') {
        break;

      }

    }
    }

     // Stop the object

     main.sendMessage (thread, new JtMessage ("JtSTOP"));

     System.out.println ("Press any key to exit");
     waitForInputKey ();

  }

}


