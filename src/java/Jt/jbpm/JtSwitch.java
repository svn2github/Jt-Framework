package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Acts as a switch. Decides what execution path (next transition) should be taken based 
 *  on the value of the JBPM variable.
 *  This class can be readily included in the jBPM process definition.  
 */
public class JtSwitch extends JtObject {



  private static final long serialVersionUID = 1L;
  private String jbpmVariable;
  private ExecutionContext context = null;


  public JtSwitch () {
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
	String value;

    this.context = context;
    if (jbpmVariable == null) {
      handleError ("jbpmVariable attribute needs to be set.");
      return;
    }
    
    try {
      value = (String) context.getContextInstance().getVariable (jbpmVariable);

      if (value == null) {
          handleError ("invalid jBPM variable value:" + jbpmVariable);
          return;
      }
	  handleTrace ("JtSwitch(" + jbpmVariable  + ")" + ": leaving the Node to "
			+ value);
      context.leaveNode (value);
    } catch (Exception ex) {
        handleException (ex);
    }
    
}


}



