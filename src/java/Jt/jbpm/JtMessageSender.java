package Jt.jbpm;

import Jt.*;


import org.jbpm.graph.exe.ExecutionContext;

/**
 * Sends a Jt message to a Jt object. jBPM variables are used to store the message, 
 * the Jt object, the reply and the exception (jbpmMessage, jbpmDestination, jbpmReply 
 * & jpmException). This class can be readily included in the jBPM process definition.
 */
public class JtMessageSender extends JtObject {


  private static final long serialVersionUID = 1L;
  private String jbpmMessage;
  private String jbpmDestination;
  private String jbpmReply;
  private String jbpmException;
  private ExecutionContext context = null;

  public JtMessageSender() {
  }




/**
  * Returns the jBPM variable that contains the Jt Message.
  */

public String getJbpmMessage() {
	return jbpmMessage;
}

/**
  * Specifies the jBPM variable that contains the Jt Message. 
  * @param jbpmMessage jBPM variable name
  */

public void setJbpmMessage(String jbpmMessage) {
	this.jbpmMessage = jbpmMessage;
}


/**
 * Returns the jBPM variable that contains the Jt Exception.
 */

public String getJbpmException() {
	return jbpmException;
}

/**
 * Specifies the jBPM variable that contains the Jt Exception. 
 * @param jbpmException jBPM variable name
 */

public void setJbpmException(String jbpmException) {
	this.jbpmException = jbpmException;
}


/**
 * Returns the jBPM variable that contains the message 
 * destination (Jt Object).
 */

public String getJbpmDestination() {
	return jbpmDestination;
}

/**
 * Specifies the jBPM variable that contains the message 
 * destination (Jt Object). 
 * @param jbpmDestination jBPM variable name
 */

public void setJbpmDestination(String jbpmDestination) {
	this.jbpmDestination = jbpmDestination;
}

/**
 * Returns the jBPM variable that stores the Jt reply. 
 */
public String getJbpmReply() {
	return jbpmReply;
}

/**
 * Specifies the jBPM variable that stores the Jt reply. 
 * @param jbpmReply jBPM variable name
 */

public void setJbpmReply(String jbpmReply) {
	this.jbpmReply = jbpmReply;
}

//handle exceptions

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


// Retrieve the jbpmAdapter object

private JtJBPMAdapter retrieveJbpmAdapter () {
    
    JtJBPMAdapter jbpmAdapter = null;
    
    try {           
      if (context != null) {
          jbpmAdapter = (JtJBPMAdapter) context.getContextInstance().getVariable ("JtJBPMAdapter");
          return (jbpmAdapter);
      }
          
    } catch (Exception ex1) {
        
    }
    return (null);
}


/**
 * Execute method (JBPM ActionHandler interface). Sends the Jt message.
 */

public void execute(ExecutionContext context) throws Exception {
    Object msg;
    Object obj;
    Object jtReply;
    Exception ex;
    JtJBPMAdapter jbpmAdapter;

    this.context = context;
    if (jbpmMessage == null) {
        handleError ("jbpmMessage attribute needs to be set.");
        return;
    }
    if (jbpmDestination == null) {
        handleError ("jbpmDestination attribute needs to be set.");
        return;
    }
    jbpmAdapter = retrieveJbpmAdapter ();
    
    try {
        msg = context.getContextInstance().getVariable (jbpmMessage);
        obj = context.getContextInstance().getVariable (jbpmDestination);

        handleTrace ("JtMessageSender.execute: about to send msg (" +
                jbpmMessage + ") to " + obj);
        jtReply = sendMessage (obj, msg);

        if (jbpmReply != null)
            context.getContextInstance().setVariable (jbpmReply, jtReply); 
        ex = (Exception) getValue (obj, "objException");        
        if (jbpmException != null ) {
            if (ex != null)
                handleTrace ("JtMessageSender.execute: exception detected: " +
                        ex);
            context.getContextInstance().setVariable (jbpmException, ex); 

        }
        if (jbpmAdapter != null && jbpmAdapter.getPropagateExceptions ()) {
            // Propagate exceptions
            jbpmAdapter.setObjException(ex);
        }
    } catch (Exception ex1) {
        handleException (ex1);    
    }
}

}



