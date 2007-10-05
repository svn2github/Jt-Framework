package Jt.jbpm;

import Jt.*;
import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Prints a message. This class can be readily included in the jBPM process definition.  
 */
public class JtPrint extends JtObject {


  private static final long serialVersionUID = 1L;
  private String message;
  private String jbpmVariable;


  public JtPrint() {
  }

  /**
   * Returns the message (or part of the message) to be displayed. 
   */
  
  public String getMessage() {
	return message;
  }

  /**
   * Specifies the message or part of the message to be displayed. 
   */
  
  public void setMessage(String message) {
	this.message = message;
  }

  
  /**
   * Returns the jBPM variable that stores part of the message
   * to be displayed. 
   */
  
  public String getJbpmVariable() {
    return jbpmVariable;
  }

  /**
   * Specifies the jBPM variable that stores part of the message
   * to be displayed. 
   */
  
  public void setJbpmVariable(String jpbmVariable) {
    this.jbpmVariable = jpbmVariable;
  }

  /**
   * Execute method (JBPM ActionHandler interface). 
   */

  public void execute(ExecutionContext context) throws Exception {

    Object variable = null;
    if (message != null)
        System.out.print(message);
    
    if (jbpmVariable != null) { 
    try {
      variable = context.getContextInstance().getVariable (jbpmVariable);
    } catch (Exception ex) {
      handleException (ex);
      return;
    }
    // print new line
    if (variable == null) {
      System.out.println("");
      return;
    }
    
    if (variable != null)
      System.out.println(variable);
    }

  }
}



