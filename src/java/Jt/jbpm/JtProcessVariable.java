package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Passes a variable to the process.
 *  This class can be readily included in the jBPM process definition.  
 */
public class JtProcessVariable extends JtObject {



  private static final long serialVersionUID = 1L;
  private String jbpmVariable;
  private String jbpmAdapter;
  private ExecutionContext context = null;


  public JtProcessVariable() {
  }

  
/**
 * Returns the jBPM variable to be added to the process. 
 */

public String getJbpmVariable() {
	return jbpmVariable;
}

/**
 * Specifies the jBPM variable to be added. 
 * @param jbpmVariable JBPM variable.
 */

public void setJbpmVariable(String jbpmVariable) {
	this.jbpmVariable = jbpmVariable;
}




/**
 * Returns the child process where the variable should be stored.
 */

public String getJbpmAdapter() {
	return jbpmAdapter;
}

/**
 * Specifies the child process where the variable should be stored. 
 * @param trueTransition process transition.
 */

public void setJbpmAdapter(String trueTransition) {
	this.jbpmAdapter = trueTransition;
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
 * Execute method (JBPM ActionHandler interface). 
 */

public void execute(ExecutionContext context) throws Exception {
	Object adapter;
	JtMessage msg = new JtMessage ("JtPASS_VARIABLE");
	
    this.context = context;
    
    if (jbpmAdapter == null) {
      handleError ("jbpmAdapter attribute needs to be set.");
      return;
    }
    
    if (jbpmVariable == null) {
        handleError ("jbpmVariable attribute needs to be set.");
        return;
    }
    
    try {
      adapter = context.getContextInstance().getVariable (jbpmAdapter);
      msg.setMsgContent (jbpmVariable);// check
      sendMessage (adapter, msg);    
    } catch (Exception ex) {
      handleException (ex);  
    }
    
}


}



