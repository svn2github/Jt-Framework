
package Jt.jbpm;

import Jt.*;

import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Creates a Jt message.  Notice that msgContent, msgData and msgId 
 *  are Strings. This imposes a limitation. 
 *  JtValueSetterFromVariable can be used when other types are needed.
 *  This class can be readily included in the jBPM process definition. 
 */

public class JtMessageWrapper extends JtObject {


   private static final long serialVersionUID = 1L;
   private String msgId;
   private String jbpmMessage;
   private String msgContent;
   private String msgData;
   private ExecutionContext context = null;
   public JtMessageWrapper () {

   }


/**
 * Returns the message content. 
 */
   
public String getMsgContent() {
	return msgContent;
}

/**
 * Sets the message content. 
 * @param msgContent message content
 */
public void setMsgContent(String msgContent) {
	this.msgContent = msgContent;
}

/**
 * Returns the jBPM variable used to store the Jt message. 
 */

public String getJbpmMessage() {
	return jbpmMessage;
}

/**
 * Specifies the jBPM variable used to store the Jt message. 
 * @param jbpmMessage JBPM variable
 */

public void setJbpmMessage(String jbpmMessage) {
	this.jbpmMessage = jbpmMessage;
}



   /**
    * Returns the message ID. 
    */

   public String getMsgId () {
	return msgId;
   }

   /**
    * Sets the message ID. This ID will be used by the recipient object to determine
    * how the message should be processed.
    * @param newMsgId message ID
    */

   public void setMsgId(String newMsgId) {
    msgId = newMsgId;
   }

   /**
    * Sets the message subject. This additional message information may be helpful
    * while processing Jt Messages.
    * @param newMsgData message data 
    */

   public void setMsgData(String newMsgData) {
    msgData = newMsgData;
   }


   /**
    * Returns the message subject. 
    */

   public String getMsgData() {
    return msgData;
   }

   
   // handle exceptions
   
   public void handleException (Throwable ex) {
       
       JtJBPMAdapter jbpmAdapter = null;
       
       try {           
         if (context != null) {
             jbpmAdapter = (JtJBPMAdapter) context.getContextInstance().getVariable ("JtJBPMAdapter");
         }
         if (jbpmAdapter != null)
             jbpmAdapter.setObjException(ex); // Propagate the exception to the JBPM adapter
             
       } catch (Exception ex1) {
           
       }
       super.handleException(ex);
   }

   /**
    * Execute method (JBPM ActionHandler interface). Creates a Jt message and stores it
    * using the jbpmMessage variable.
    */
   
   public void execute(ExecutionContext context) throws Exception {
		JtMessage msg;
	    msg = new JtMessage ();
	    msg.setMsgId (msgId);

        this.context = context;
	    if (jbpmMessage == null) {
	    	handleError ("Attribute jbpmMessage needs to be set.");
	    	return;
	    }	

	    if (msgId == null) {
	    	handleError ("Attribute msgId needs to be set.");
	    	return;
	    }

	    setValue (msg, "msgId", msgId);
	    
	    if (msgContent != null)
	      setValue (msg, "msgContent", msgContent);

	    if (msgData != null)
	      setValue (msg, "msgData", msgData);
	    	    
	    try {
          context.getContextInstance().setVariable (jbpmMessage, msg); 
	    } catch (Exception e) {
          handleException (e);
	    }
	}
}
