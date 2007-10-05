

package Jt.jms;

import Jt.*;
import Jt.jndi.*;
import javax.jms.*;

/**
 * Jt Adapter for the JMS point-to-point API. 
 */

public class JtJMSQueueAdapter extends JtAdapter implements MessageListener {


  private static final long serialVersionUID = 1L;
  private String queue;
  private String connectionFactory;
  private long timeout = 1L; // Receives the next message within the timeout interval
  private Object subject = null;  
  private int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
  private int priority = Message.DEFAULT_PRIORITY;
  private long timeToLive = Message.DEFAULT_TIME_TO_LIVE; // message never expires


  private transient JtJNDIAdapter jndiAdapter = null;
  private transient boolean initted = false; 

  private transient Queue jmsQueue;
  private transient QueueConnectionFactory qcFactory;
  private transient QueueConnection queueConnection;
  private transient QueueSession queueSession;
  private transient QueueSender queueSender;
  private transient QueueReceiver queueReceiver;

  // Intialize the JMS Adapter
  
  private void initial () {
    JtMessage msg = new JtMessage ("JtLOOKUP");

        
    jndiAdapter = new JtJNDIAdapter ();

    if (connectionFactory == null) {
      handleError ("Attribute connectionFactory needs to be set.");
      return;    
    }

    msg.setMsgContent (connectionFactory);
 
    qcFactory = (QueueConnectionFactory) sendMessage (jndiAdapter, msg);

    if (qcFactory == null)
      return;

    if (queue == null) {
      handleError ("Attribute queue needs to be set.");
      return;    
    }
    msg.setMsgContent (queue); 

    jmsQueue = (Queue) sendMessage (jndiAdapter, msg);


    if (jmsQueue == null)
      return;

    try {
      queueConnection = qcFactory.createQueueConnection ();
      queueSession = queueConnection.createQueueSession (false, 
                                 Session.AUTO_ACKNOWLEDGE);

    } catch (Exception e) {
      handleException (e);
    }

  }

  /**
    * Method used by this adapter to consume JMS messages
    * asynchronously.
    * @param message JMS message
    */

  public void onMessage (Message message) {
    JtMessage msg; 
    ObjectMessage omessage;     

    if (message == null)
      return;

    
    try {

      omessage = (ObjectMessage) message;
      msg =   (JtMessage) omessage.getObject ();

      if (subject == null) {
        handleWarning ("JtJMSQueueAdapter.onMessage: attribute 'subject' needs to be set.");
        return;
      }

      // Forward Jt messages to the subject object

      sendMessage (subject, msg);

    } catch (Exception ex) {
      handleException (ex);
    }
  }
  
  /**
    * Process object messages.
    * <ul>
    * <li> JtSEND  - Send a JtMessage (msgContent) to the JMS queue.  
    * <li> JtRECEIVE  - Receive a JtMessage from the JMS queue and return it. 
    * <li> The message is consumed synchronously.
    * <li> JtSTART_LISTENING - Start listening and consume messages asynchronously.  
    * </ul>
    */

  public Object processMessage (Object message) {
  //String content;
  //String query;
  JtMessage e = (JtMessage) message;
  Object reply;
  JtMessage msg;


      if (e == null ||  (e.getMsgId() == null))
          return (null);


      if (e.getMsgId().equals ("JtREMOVE")) {
	return (null);
      }

      if (!initted) {
        initial ();
        initted = true;        
      }


      if (e.getMsgId().equals("JtSEND")) {
        msg = (JtMessage) e.getMsgContent ();        
        reply = sendJMSMessage (msg);
        return (reply);
      }


      if (e.getMsgId().equals("JtSTART_LISTENING")) {
        startListening ();
        return (null);
      }

      if (e.getMsgId().equals("JtRECEIVE")) {     
        reply = receiveJMSMessage ();
        return (reply);
      }

      // Test the Sender functionality

      if (e.getMsgId().equals("JtTEST_SENDER")) {
	reply = testSender ();
	return (reply);
      }

      // Test the Receiver functionality

      if (e.getMsgId().equals("JtTEST_RECEIVER")) {
	reply = testReceiver ();
	return (reply);
      }


      handleError 
	("processMessage: invalid message id:"+
		e.getMsgId());
      return (null);
  }



  /**
   * Specifies the JNDI name of the JMS queue.
   * @param queue queue
   */

  public void setQueue (String queue) {
    this.queue = queue;
  }


  /**
   * Returns the JNDI name of the JMS queue.
   */

  public String getQueue () {
    return (queue);
  }

  /**
   * Specifies the timeout interval (refer to javax.jms.MessageConsumer).
   * @param timeout timeout
   */

  public void setTimeout (long timeout) {
    this.timeout = timeout;
  }


  /**
   * Returns timeout (refer to javax.jms.MessageConsumer).
   */

  public long getTimeout () {
    return (timeout);
  }


  /**
   * Sets the delivery mode (persistent or non-persistent).
   * Messages will be sent to the JMS queue using this delivery mode. 
   * @param deliveryMode delivery mode
   */

  public void setDeliveryMode (int deliveryMode) {
    this.deliveryMode = deliveryMode;
  }


  /**
   * Returns the delivery mode (persistent or non-persistent)
   */

  public long getDeliveryMode () {
    return (deliveryMode);
  }




  /**
   * Sets the message priority. Messages will be sent to the JMS queue 
   * using this priority. 
   * @param priority message priority
   */

  public void setPriority (int priority) {
    this.priority = priority;
  }


  /**
   * Returns the message priority.
   */

  public long getPriority () {
    return (priority);
  }


  /**
   * Sets the message time to live (in milliseconds). Messages will be sent to the JMS queue 
   * using this value. 
   * @param timeToLive message time to live
   */

  public void setTimeToLive (long timeToLive) {
    this.timeToLive = timeToLive;
  }


  /**
   * Returns the message time to live (in milliseconds).
   */

  public long getTimeToLive () {
    return (timeToLive);
  }



  /**
   * Specifies the subject (JtObject). Messages received asynchronously are forwarded to
   * this Jt object for processing. 
   * @param subject subject
   */

  public void setSubject (Object subject) {
    this.subject = subject;
  }


  /**
   * Returns the subject. Messages received asynchronously are forwarded to
   * this Jt object for processing. 
   */

  public Object getSubject () {
    return (subject);
  }


  /**
   * Specifies the JNDI name of the connection factory.
   * @param connectionFactory connection factory
   */

  public void setConnectionFactory (String connectionFactory) {
    this.connectionFactory = connectionFactory;
  }


  /**
   * Returns the JNDI name of the connection factory.
   */

  public String getConnectionFactory () {
    return (connectionFactory);
  }




  private Object testReceiver () {
    String reply = "PASS";
    //ObjectMessage message;
    JtMessage msg;

 

    //for (;;) {

      msg = (JtMessage) sendMessage (this, new JtMessage ("JtRECEIVE"));

      if (msg == null) {
        System.out.println ("no more messages");
        return (reply);
      } 

      System.out.println ("msgId:" + msg.getMsgId ());

    //}

    return (reply);
  }


  // Send a Jt message using JMS as the transport layer

  private Object sendJMSMessage (JtMessage msg) {

    ObjectMessage omsg;
    String reply = "PASS";


    if (msg == null) {
      reply = "FAIL";
      return (reply);
    }

    try {

      if (queueSender == null)
        queueSender = queueSession.createSender (jmsQueue);

      omsg = queueSession.createObjectMessage (); 
      omsg.setObject (msg); 

      // send the message. Use the appropriate parameters (priority, 
      // deliveryMode, etc).
      queueSender.send (omsg, deliveryMode, priority, timeToLive);
    } catch (Exception e) {
      handleException (e);
      reply = "FAIL";
    }
    return (reply);
  }

  private void startListening () {


    try {
      if (queueReceiver == null)
        queueReceiver = queueSession.createReceiver (jmsQueue);


      if (queueConnection == null) {
        handleError ("receiveJMSMessage:queueConnection is null");
        return; 
      }

      // Use the adapter as the message listener

      queueReceiver.setMessageListener (this);

      queueConnection.start ();
    } catch (Exception ex) {

      handleException (ex);
    }

  }
  
  private JtMessage receiveJMSMessage () {

    JtMessage msg = null;
    ObjectMessage message;


    try {

      if (queueReceiver == null)
        queueReceiver = queueSession.createReceiver (jmsQueue);


      if (queueConnection == null) {
        handleError ("receiveJMSMessage:queueConnection is null");
        return (null); 
      }
      queueConnection.start ();

      message = (ObjectMessage) queueReceiver.receive (timeout);
      if (message != null) {
        msg =  (JtMessage) message.getObject ();
      } 

    } catch (Exception e) {
      handleException (e);
    }

    return (msg);

  }



  private Object testSender () {
    //String reply = "PASS";
    //TextMessage message;
    //ObjectMessage omsg;
    JtMessage msg = new JtMessage ("JtHELLO");
    JtMessage wrapper = new JtMessage ("JtSEND");

    wrapper.setMsgContent (msg); // wrapper message ("JtSEND") that contains
                                 // the real message ("JtHELLO")


    // send the message to the JMS adapter (JMS queue)
    return (sendMessage (this, wrapper));
  }

  /**
   * Unit tests the messages processed by JtJMSQueueAdapter. 
   */

  public static void main (String[] args) {
    JtFactory main;
    JtJMSQueueAdapter jmsAdapter;

    main = new JtFactory ();


    jmsAdapter = (JtJMSQueueAdapter) main.createObject ("Jt.jms.JtJMSQueueAdapter", "jmsAdapter");

    if (args.length < 1) {
	System.err.println ("Usage: java Jt.jms.JtJMSQueueAdapter -s or java Jt.jms.JtJMSQueueAdapter -r");
	System.exit (1);
    }

    if (args[0].equals ("-s")) {
      main.sendMessage (jmsAdapter, new JtMessage ("JtTEST_SENDER"));
      System.exit (0);
    }

    if (args[0].equals ("-r")) {
      main.sendMessage (jmsAdapter, new JtMessage ("JtTEST_RECEIVER"));
      System.exit (0);
    }
    
    System.err.println ("Usage: java Jt.jms.JtJMSQueueAdapter -s or java Jt.jms.JtJMSQueueAdapter -r");

    main.removeObject ("jtAdapter");

  }
}