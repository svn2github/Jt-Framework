

package Jt;


/**
 * Implements a queue of objects.
 */

public class JtQueue extends JtList {


  private static final long serialVersionUID = 1L;


  public JtQueue() {
  }

  
  private Object test () {

    JtMessage msg;
    Object obj;


   // Enqueue couple of objects

    msg = new JtMessage ("JtENQUEUE");
    msg.setMsgContent (new Integer (1));
    System.out.println ("Enqueue object ... " + new Integer (1));
    sendMessage (this, msg);


    msg = new JtMessage ("JtENQUEUE");
    msg.setMsgContent (new Integer (2));
    System.out.println ("Enqueue object ... " + new Integer (2));
    sendMessage (this, msg);


    // Dequeue all the elements in the queue

    for (;;) {
      obj = (Object) sendMessage (this, new JtMessage ("JtDEQUEUE"));
      if (obj == null)
         break;
      System.out.println ("Dequeue object ... " + obj);
    }
    return (this);

  }
  
  /**
    * Process object messages.
    * <ul>
    * <li> JtENQUEUE - Enqueues the object specified by msgContent
    * <li> JtDEQUEUE - Dequeues and returns the object at the beginning of the queue.
    * </ul>
    * @param message Jt Message
    */

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage e = (JtMessage) message;
   Object content;
   //Object data;
   JtMessage tmp;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();


     // Remove this object
     if (msgid.equals ("JtREMOVE")) {

       return (null);     
     }

     // Enqueue message
     if (msgid.equals ("JtENQUEUE")) {
        // Add an object to the queue

        tmp = new JtMessage ("JtADD");
        tmp.setMsgContent (content);    
        return (super.processMessage (tmp));
     }     


     // Dequeue message

     if (msgid.equals ("JtDEQUEUE")) {


        return (super.processMessage (new JtMessage ("JtREMOVE_FIRST")));
     }     



     // Test this object
     if (msgid.equals ("JtTEST")) {

       return (test ());     
     }
         
     handleError ("JtQueue.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
    * Unit tests the messages processed by JtQueue.
    */ 

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtQueue queue;

    //Object obj;



    // Create a Queue

    queue = (JtQueue) main.createObject ("Jt.JtQueue", "queue");

    main.sendMessage (queue, new JtMessage ("JtTEST"));

    main.removeObject (queue);
  }

}


