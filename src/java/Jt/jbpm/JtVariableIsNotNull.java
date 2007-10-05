package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Verify if the value of a jBPM variable is not NULL and
 *  decide what execution path (next transition) should be taken. 
 *  This class can be readily included in the jBPM process definition. 
 */

public class JtVariableIsNotNull extends JtObject {


  private static final long serialVersionUID = 1L;
  private String jbpmVariable;
  private String trueTransition;
  private String falseTransition;
  private ExecutionContext context = null;

  public JtVariableIsNotNull() {
  }

/**
 * Returns the jBPM variable. 
 */
  
public String getJbpmVariable() {
	return jbpmVariable;
}

/**
 * Specifies the jBPM variable. 
 * @param jbpmVariable JBPM variable.
 */
public void setJbpmVariable(String jbpmVariable) {
	this.jbpmVariable = jbpmVariable;
}


/**
 * Returns the transition to be taken when the jBPM variable is null. 
 */

public String getFalseTransition() {
	return falseTransition;
}

/**
 * Specifies the transition to be taken when the jBPM variable is null. 
 * @param falseTransition process transition.
 */

public void setFalseTransition(String falseTransition) {
	this.falseTransition = falseTransition;
}

/**
 * Returns the transition to be taken when the jBPM variable is not null. 
 */

public String getTrueTransition() {
	return trueTransition;
}

/**
 * Specifies the transition to be taken when the jBPM variable is not null. 
 * @param trueTransition process transition.
 */

public void setTrueTransition(String trueTransition) {
	this.trueTransition = trueTransition;
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

/**
 * Execute method (JBPM ActionHandler interface). Takes a transition path based
 * on the value of the jBPM variable. 
 */

public void execute(ExecutionContext context) throws Exception {
	Object variable;
	
    this.context = context;
    if (jbpmVariable == null) {
        handleError ("jbpmVariable attribute should be set.");
        return;
    }
        
    if (trueTransition == null) {
        handleError ("trueTransition attribute should be set.");
        return;
    }
 
    if (falseTransition == null) {
        handleError ("falseTransition attribute should be set.");
        return;
    }
    
    try {
        variable = context.getContextInstance().getVariable (jbpmVariable);

        if (variable != null) {
            handleTrace ("JtVariableIsNotNull(true): leaving the Node to "
                    + trueTransition);
            context.leaveNode (trueTransition);
        } else {
            handleTrace ("JtVariableIsNotNull(false): leaving the Node to "
                    + falseTransition);
            context.leaveNode (falseTransition);    	
        }
    } catch (Exception ex) {
        handleException (ex);
    }
}


}



