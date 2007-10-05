package Jt.jbpm;

import Jt.*;
import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Invoke a delegate class to evaluate a boolean condition and
 *  decide what execution path (next transition) should be taken.
 *  This class can be readily included in the jBPM process definition.  
 */
public class JtConditional extends JtObject {


  private static final long serialVersionUID = 1L;
  private String trueTransition;
  private String falseTransition;
  private String delegateClass;
  private String jbpmVariable;
  private ExecutionContext context = null;

  public JtConditional() {
  }

/**
 * Returns the variable to be used during the condition evaluation. 
 */

    
public String getJbpmVariable() {
    return jbpmVariable;
}

/**
 * Specifies the variable to be used during the condition evaluation. 
 * @param jbpmVariable process variable.
 */

public void setJbpmVariable(String jbpmVariable) {
    this.jbpmVariable = jbpmVariable;
}

/**
 * Returns the delegate Jt class. 
 */

public String getDelegateClass() {
    return delegateClass;
}

/**
 * Specifies the delegate Jt class used to evaluate the condition. 
 * @param delegateClass delegate class.
 */

public void setDelegateClass(String delegateClass) {
    this.delegateClass = delegateClass;
}


/**
 * Returns the transition to be taken when the boolean condition is false. 
 */

public String getFalseTransition() {
	return falseTransition;
}

/**
 * Specifies the transition to be taken when the boolean condition is false. 
 * @param falseTransition process transition.
 */

public void setFalseTransition(String falseTransition) {
	this.falseTransition = falseTransition;
}

/**
 * Returns the transition to be taken when the boolean condition is true. 
 */

public String getTrueTransition() {
	return trueTransition;
}

/**
 * Specifies the transition to be taken when the boolean condition is true. 
 * @param trueTransition process transition.
 */

public void setTrueTransition(String trueTransition) {
	this.trueTransition = trueTransition;
}

private void leaveNode (ExecutionContext context, String transition) {

    if (context == null)
        return;
    
    try {
        context.leaveNode (transition);     
    } catch (Exception ex) {
        handleException (ex);
    }
    
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
 * Execute method (JBPM ActionHandler interface). Takes a transition path based
 * on the evaluation of an expression. 
 */

public void execute(ExecutionContext context) throws Exception {
  JtMessage msg = new JtMessage ("JtACTIVATE");
  JtInterface tmp;
  Boolean bool;
  boolean value = false;
    
    this.context = context;
    if (delegateClass != null) {
        tmp = (JtInterface) createObject (delegateClass, "tmp");
        msg.setMsgContent (context);
        msg.setMsgData (jbpmVariable);
        bool = (Boolean) sendMessage (tmp, msg);
        destroyObject (tmp);

        if (bool == null) {
            handleError (delegateClass + ":invalid return value (null)"); //check
            return;
        }
        value = bool.booleanValue();
    }
	try {

	    if (value) {
	        handleTrace ("JtCondition(true): leaving the Node to "
	                + trueTransition);
	        //context.leaveNode (trueTransition);
	        leaveNode (context, trueTransition);

	    } else {
	        handleTrace ("JtCondition(false): leaving the Node to "
	                + falseTransition);
	        //context.leaveNode (falseTransition);
	        leaveNode (context, falseTransition);

	    }
	} catch (Exception ex) {
	    handleException (ex);
	}


}


}


