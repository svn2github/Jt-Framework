

package Jt.examples.patterns;

import Jt.*;

/**
 * Chat room member (runs in a separate thread)
 */

public class Colleague extends JtThread {


private static final long serialVersionUID = 1L;
Object mediator;
String greetingMessage = "Hi there ";


/**
  * Specifies the greeting message.
  *
  * @param greetingMessage greetingMessage
  */

  public void setGreetingMessage (String greetingMessage) {
     this.greetingMessage = greetingMessage; 

  }

/**
  * Returns the greeting message.
  */

  public String getGreetingMessage () {
     return (greetingMessage);
  }


/**
  * Specifies the mediator object.
  *
  * @param mediator mediator
  */

  public void setMediator (Object mediator) {
     this.mediator = mediator; 

  }

/**
  * Returns the mediator.
  */

  public Object getMediator () {
     return (mediator);
  }


  public Colleague () {
  }


  // sleep for a period of time

  private void sleep (long period) {

    try {
        Thread.sleep (period);
    } catch (Exception e) {
        handleException (e);
    }

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
   //Object content;
   //Object data;
   JtMessage tmp;



     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();
     //data = e.getMsgData ();

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }


     if (msgid.equals ("JtACTIVATE")) {

       if (mediator == null) {
         handleError ("the mediator attribute needs to be set");
         return (null);
       }


       // Join the chat room

       tmp = new JtMessage ("JOIN");
       tmp.setMsgFrom (this);
       sendMessage (mediator, tmp);

       // Send a message

       tmp = new JtMessage ("MESSAGE");
       tmp.setMsgContent (greetingMessage);
       tmp.setMsgFrom (this);
       sendMessage (mediator, tmp);

       // Sleep for a few seconds

       sleep (3000L);

       // Exit the chat room

       tmp = new JtMessage ("EXIT");
       tmp.setMsgFrom (this);
       sendMessage (mediator, tmp);

       return (this);     
     }

     return (super.processMessage (message));     
   

  }




}


