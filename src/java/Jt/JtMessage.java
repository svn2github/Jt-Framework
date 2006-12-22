

package Jt;

import java.io.*;

/**
 *  Jt Messages used for the implementation of the Jt messaging pattern.
 *  This class is used to pass information to Jt objects.
 */

public class JtMessage extends JtObject implements Serializable {

   private Object msgId;
   private Object msgTo;
   private Object msgFrom;
   private Object msgSubject;
   private Object msgContent;
   private Object msgData;
   private Object msgReplyTo;
   private Object msgAttachment;


   public JtMessage () {

   }

   public JtMessage (String msgId) {
     this.msgId = msgId;
   }

/*
   public JtMessage (Object source) {

   }
*/

   /**
    * Returns the message ID. 
    */

   public Object getMsgId () {
	return msgId;
   }

   /**
    * Sets the message ID. This ID will be used by the recipient object to determine
    * how the message should be processed.
    * @param newMsgId message ID
    */

   public void setMsgId(Object newMsgId) {
    msgId = newMsgId;
   }


   /**
    * Sets the message recipient. This attribute specifies where the message is going to.
    * @param newMsgTo message recipient
    */

   public void setMsgTo (Object newMsgTo) {
    msgTo = newMsgTo;
   }


   /**
    * Returns the message recipient. 
    */

   public Object getMsgTo() {
    return msgTo;
   }

   /**
    * Sets the message sender. This attribute specifies where the message is coming from.
    * @param newMsgFrom message sender
    */

   public void setMsgFrom (Object newMsgFrom) {
    msgFrom = newMsgFrom;
   }


   /**
    * Returns the message sender. 
    */

   public Object getMsgFrom() {
    return msgFrom;
   }

   /**
    * Sets the message subject. This additional message information may be helpful
    * while processing Jt Messages.
    * @param newMsgSubject message subject 
    */

   public void setMsgSubject(Object newMsgSubject) {
    msgSubject = newMsgSubject;
   }


   /**
    * Returns the message subject. 
    */

   public Object getMsgSubject() {
    return msgSubject;
   }


   /**
    * Sets the message content. 
    * @param newMsgContent message content
    */

   public void setMsgContent(Object newMsgContent) {
    msgContent = newMsgContent;
   }

   /**
    * Returns the message content. 
    */

   public Object getMsgContent() {
    return msgContent;
   }


   /**
    * Specifies additional message data. This attribute is seldom used. 
    * @param newMsgData message data
    */

   public void setMsgData(Object newMsgData) {
    msgData = newMsgData;
   }

   /**
    * Returns additional message data. 
    */

   public Object getMsgData() {
    return msgData;
   }

    /**
    * Specifies the message attachment. This attribute is seldom used. 
    * @param newMsgAttachment message attachment
    */

   public void setMsgAttachment(Object newMsgAttachment) {
    msgAttachment = newMsgAttachment;
   }



   /**
    * Returns the message attachment. 
    */

   public Object getMsgAttachment() {
    return msgAttachment;
   }


   /**
    * Specifies the object that should receive the reply message.
    * @param newMsgReplyTo object that should receive the reply.
    */


   public void setMsgReplyTo(Object newMsgReplyTo) {
    msgReplyTo = newMsgReplyTo;
   }




   /**
    * Returns the object that should receive the reply message.
    */
 
   public Object getMsgReplyTo() {
    return msgReplyTo;
   }


}
