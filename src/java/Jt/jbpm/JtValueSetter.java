package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Sets the attribute value of a Jt object. A jBPM variable (jbpmObject)
 *  references the Jt Object. The attribute value uses its String representation.
 *  Refer to the Jt API (setValue). This class can be readily included in the 
 *  jBPM process definition.
 */

public class JtValueSetter extends JtObject {


  private static final long serialVersionUID = 1L;
  private String jbpmObject;
  private String jbpmAttribute;
  private String jbpmValue;
  private ExecutionContext context = null;

  public JtValueSetter() {
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
 * Returns the attribute value (String representation). 
 */

public String getJbpmValue() {
	return jbpmValue;
}

/**
 * Specifies the attribute value (String). 
 * @param jbpmValue attribute value
 */


public void setJbpmValue(String jbpmValue) {
	this.jbpmValue = jbpmValue;
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
 * Execute method (JBPM ActionHandler interface). Set the attribute value
 * of a JtObject. setValue performs a conversion from the String representation.
 * @param context Execution context
 */

public void execute(ExecutionContext context) throws Exception {
	Object obj = null;
	
    this.context = context;
    if (jbpmObject == null) {
        handleError ("jbpmObject needs to be set");
        return;
    }
    if (jbpmAttribute == null) {
        handleError ("jbpmAttribute needs to be set");
        return;
    }
    if (jbpmValue == null) {
        handleError ("jbpmValue needs to be set");
        return;
    }   
    
    try {
	  obj = context.getContextInstance().getVariable (jbpmObject); 
      setValue (obj, jbpmAttribute, jbpmValue);
    } catch (Exception ex) {
        handleException (ex);
    }
    
}

}



