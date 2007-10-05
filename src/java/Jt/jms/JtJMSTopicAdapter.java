

package Jt.jms;

import Jt.*;
import Jt.jndi.*;
import javax.jms.*;

/**
 * Jt Adapter for the JMS publish/subscribe API. 
 */

public class JtJMSTopicAdapter extends JtAdapter implements MessageListener {


  private static final long serialVersionUID = 1L;
  private String topic;
  private String connectionFactory;
  private long timeout = 1L; // Receives the next message within the timeout interval
  private Object subject = null;  
  private int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
  private int priority = Message.DEFAULT_PRIORITY;
  private long timeToLive = Message.DEFAULT_TIME_TO_LIVE; // message never expires

  private transient JtJNDIAdapter jndiAdapter = null;
  private transient boolean initted = false; 

  private transient Topic jmsTopic;
  private transient TopicConnectionFactory tcFactory;
  private transient TopicConnection topicConnection;
  private transient TopicSession topicSession;
  private transient TopicPublisher topicPublisher;
  private transient TopicSubscriber topicSubscriber;

  // Intialize the JMS Adapter
  
  private void initial () {
    JtMessage msg = new JtMessage ("JtLOOKUP");

        
    jndiAdapter = new JtJNDIAdapter ();

    if (connectionFactory == null) {
      handleError ("Attribute value needs to be set (connectionFactory)");
      return;    
    }

    msg.setMsgContent ("TestJMSConnectionFactory");
 
    tcFactory = (TopicConnectionFactory) sendMessage (jndiAdapter, msg);

    if (tcFactory == null)
      return;

    if (topic == null) {
      handleError ("Attribute value needs to be set (topic)");
      return;    
    }
    msg.setMsgContent (topic); 

    jmsTopic = (Topic) sendMessage (jndiAdapter, msg);


    if (jmsTopic == null)
      return;

    try {
      topicConnection = tcFactory.createTopicConnection ();
      topicSession = topicConnection.createTopicSession (false, 
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
        handleWarning ("JtJMSAdapter.onMessage: the subject attribute needs to be set");
        return;
      }

      sendMessage (subject, msg);

    } catch (Exception ex) {
      handleException (ex);
    }
  }
  
  /**
    * Process object messages.
    * <ul>
    * <li> JtPUBLISH - Publish a JtMessage (msgContent).  
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


      if (e.getMsgId().equals("JtPUBLISH")) {
        msg = (JtMessage) e.getMsgContent ();        
        reply = publishJMSMessage (msg);
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

      // Test the Publisher functionality

      if (e.getMsgId().equals("JtTEST_PUBLISHER")) {
	reply = testPublisher ();
	return (reply);
      }

      // Test the Subscriber functionality

      if (e.getMsgId().equals("JtTEST_SUBSCRIBER")) {
	reply = testSubscriber ();
	return (reply);
      }


      handleError 
	("processMessage: invalid message id:"+
		e.getMsgId());
      return (null);
  }



  /**
   * Specifies the JNDI name of the JMS topic.
   * @param topic topic
   */

  public void setTopic (String topic) {
    this.topic = topic;
  }


  /**
   * Returns the JNDI name of the JMS topic.
   */

  public String getTopic () {
    return (topic);
  }

  /**
   * Specifies the timeout interval (refer to javax.jms.MessageConsumer)
   * @param timeout timeout
   */

  public void setTimeout (long timeout) {
    this.timeout = timeout;
  }


  /**
   * Returns timeout (refer to javax.jms.MessageConsumer)
   */

  public long getTimeout () {
    return (timeout);
  }


  /**
   * Sets the delivery mode (persistent or non-persistent).
   * Messages will be published using this delivery mode. 
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
   * Sets the message priority. Messages will be published 
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
   * Sets the message time to live (in milliseconds). Messages will be published 
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




  private Object testSubscriber () {
    String reply = "PASS";
    //TextMessage message;
    //ObjectMessage message;
    JtMessage msg;

 

    for (;;) {

      msg = (JtMessage) sendMessage (this, new JtMessage ("JtRECEIVE"));

      if (msg == null) {
        System.out.println ("no more messages");
        break;
      } 

      System.out.println ("msgId:" + msg.getMsgId ());

    }

    return (reply);
  }


  // Send a Jt message using JMS as the transport layer

  private Object publishJMSMessage (JtMessage msg) {

    ObjectMessage omsg;
    String reply = "PASS";


    if (msg == null) {
      reply = "FAIL";
      return (reply);
    }

    try {

      if (topicPublisher == null)
        topicPublisher = topicSession.createPublisher (jmsTopic);

      omsg = topicSession.createObjectMessage (); 
      omsg.setObject (msg); 


      topicPublisher.publish (omsg, deliveryMode, priority, timeToLive);
    } catch (Exception e) {
      handleException (e);
      reply = "FAIL";
    }
    return (reply);
  }

  private void startListening () {


    try {
      if (topicSubscriber == null)
        topicSubscriber = topicSession.createSubscriber (jmsTopic);


      if (topicConnection == null) {
        handleError ("receiveJMSMessage:topicConnection is null");
        return; 
      }

      // Use the adapter as the message listener

      topicSubscriber.setMessageListener (this);

      topicConnection.start ();
    } catch (Exception ex) {

      handleException (ex);
    }

  }
  
  private JtMessage receiveJMSMessage () {

    JtMessage msg = null;
    ObjectMessage message;


    try {

      if (topicSubscriber == null)
        topicSubscriber = topicSession.createSubscriber (jmsTopic);


      if (topicConnection == null) {
        handleError ("receiveJMSMessage:topicConnection is null");
        return (null); 
      }
      topicConnection.start ();

      message = (ObjectMessage) topicSubscriber.receive (timeout);
      if (message != null) {
        msg =  (JtMessage) message.getObject ();
      } 

    } catch (Exception e) {
      handleException (e);
    }

    return (msg);

  }



  private Object testPublisher () {
    //String reply = "PASS";
    //TextMessage message;
    //ObjectMessage omsg;
    JtMessage msg = new JtMessage ("JtHELLO");
    JtMessage wrapper = new JtMessage ("JtPUBLISH");

    wrapper.setMsgContent (msg);


    return (sendMessage (this, wrapper));
  }

  /**
   * Unit tests the messages processed by JtJMSTopicAdapter. 
   */

  public static void main (String[] args) {
    JtFactory main;
    JtJMSTopicAdapter jmsAdapter;

    main = new JtFactory ();


    jmsAdapter = (JtJMSTopicAdapter) main.createObject ("Jt.jms.JtJMSTopicAdapter", "jmsAdapter");

    if (args.length < 1) {
	System.err.println ("Usage: java Jt.jms.JtJMSTopicAdapter -p or java Jt.jms.JtJMSTopicAdapter -s");
	System.exit (1);
    }

    if (args[0].equals ("-p")) {
      main.sendMessage (jmsAdapter, new JtMessage ("JtTEST_PUBLISHER"));
      System.exit (0);
    } else if (args[0].equals ("-s")) {
      main.sendMessage (jmsAdapter, new JtMessage ("JtTEST_SUBSCRIBER"));
      System.exit (0);
    } else
      System.err.println ("Usage: java Jt.jms.JtJMSTopicAdapter -p or java Jt.jms.JtJMSTopicAdapter -s");

    main.removeObject (jmsAdapter);

  }
}