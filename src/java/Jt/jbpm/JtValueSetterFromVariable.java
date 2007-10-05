package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Sets the attribute value of a Jt object. A jBPM variable stores
 *  the reference to the Jt object (jbpmObjet).
 *  The attribute value is retrieved from a jbpmVariable (jbpmVariable)
 *  and  converted from its String representation. Refer to setValue ().
 *  This class can be readily included in the jBPM process definition. 
 */

public class JtValueSetterFromVariable extends JtObject {


  private static final long serialVersionUID = 1L;
  private String jbpmObject;
  private String jbpmAttribute;
  private String jbpmVariable;
  private ExecutionContext context = null;

  public JtValueSetterFromVariable() {
  }
  
/**
 * Returns the jBPM variable that references the Jt object. 
 */

public String getJbpmObject() {
	return jbpmObject;
}

/**
 * Specifies the jBPM variable that references the Jt object. 
 * @param jbpmObject JBPM variable.
 */

public void setJbpmObject(String jbpmObject) {
	this.jbpmObject = jbpmObject;
}

/**
 * Returns the attribute name. 
 */

public String getJbpmAttribute() {
	return jbpmAttribute;
}

/**
 * Specifies the attribute name. 
 * @param jbpmAttribute attribute name
 */

public void setJbpmAttribute(String jbpmAttribute) {
	this.jbpmAttribute = jbpmAttribute;
}
/**
 * Returns the jBPM variable that stores the attribute value. 
 */

public String getJbpmVariable() {
	return jbpmVariable;
}

/**
 * Specifies the jBPM variable that stores the attribute value. 
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
 * Execute method (JBPM ActionHandler interface). Sets the attribute value
 * of a JtObject. The value is retrieved from a jBPM variable and converted 
 * from its string representation (see setValue).
 * @param context Execution context  
 */


public void execute(ExecutionContext context) throws Exception {
   Object obj;
   Object variable;
   
    if (jbpmObject == null) {
        handleError ("jbpmObject attribute needs to be set.");
        return;
    }
    
    if (jbpmVariable == null) {
        handleError ("jbpmVariable attribute needs to be set.");
        return;
    }
    
    try {
      obj = context.getContextInstance().getVariable (jbpmObject); 
      variable = context.getContextInstance().getVariable (jbpmVariable);

      setValue (obj, jbpmAttribute, variable);   
    } catch (Exception ex) {
      handleException (ex);
    }
}

}



