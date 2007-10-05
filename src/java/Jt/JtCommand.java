

package Jt;


/**
 * Jt Implementation of the Command pattern. This object inherits from JtThread.
 * It can execute using a separate/independent thread and process messages (requests) asynchronously
 * via a message queue.
 */

public class JtCommand extends JtThread {

  private static final long serialVersionUID = 1L;
  private JtList messageLog = new JtList ();
  private boolean synchronous = true;


  public JtCommand() {
  }


/**
  * Verifies if this object should process messages synchronously or asynchronously
  * via a message queue and a separate thread.
  */

  public boolean getSynchronous () {
    return (synchronous);
  }

/**
  * Specifies synchronous/asynchronous mode of processing messages.
  */

  public void setSynchronous (boolean synchronous) {
    this.synchronous = synchronous;
  }

/**
  * Returns the message (request) log. This is basically the list of messages processed
  * by this object.  
  */
  public JtList getMessageLog () {
    return (messageLog);
  }


/**
  * Changes the message (request) log.
  */

  public void setMessageLog (JtList messageLog) {

    this.messageLog = messageLog;
  }

/**
  * Enqueues a message (request) if asynchronous mode is set. Otherwise process the message
  * by calling processMesssage directly. sendMessage () calls this function when
  * the target class is a subclass of JtThread.
  */

  synchronized public Object enqueueMessage (Object msg) // chec
  {

    if (synchronous)
     return (processMessage (msg));
    else
     return (super.enqueueMessage (msg)); 

  }


/**
  * Log a message (request).
  */

  protected void logMessage (Object msg)
  {
    JtMessage m = new JtMessage ("JtADD");


    if (msg == null)
      return;

    m.setMsgContent (msg);            
    messageLog.processMessage (m);

  }

  /**
    * Process object messages. Invokes the superclass to process JtREMOVE, JtSTART and
    * JtSTOP.
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;
   //Object data;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();
     //data = e.getMsgData ();


     if (msgid.equals ("JtREMOVE") ||
         msgid.equals ("JtSTART") || msgid.equals ("JtSTOP")) {
       return (super.processMessage (event));     
     }


     handleError ("JtCommand.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtCommand
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    //JtMessage msg, msg1;
    //Integer count;
    JtCommand command;



    // Create an instance of JtCommand

    command = (JtCommand) main.createObject ("Jt.JtCommand", "command");


    main.sendMessage (command, new JtMessage ("JtSTART"));


    main.removeObject ("command");


  }

}


