package Jt.jbpm;

import Jt.*;



import org.jbpm.graph.exe.ExecutionContext;

/**
 * Retrieves the attribute value of a Jt object. JBPM variables store
 * the Jt object reference and the attribute name. 
 * This class can be readily included in the jBPM process definition. 
 */

public class JtValueGetter extends JtObject {


  private static final long serialVersionUID = 1L;
  private String jbpmObject;
  private String jbpmAttribute;
  private String jbpmVariable;
  private ExecutionContext context = null;

  public JtValueGetter() {
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
 * Returns the jBPM variable used to store the attribute value. 
 */

public String getJbpmVariable() {
	return jbpmVariable;
}

/**
 * Specifies the jBPM variable used to store the attribute value. 
 * @param jbpmVariable jBPM variable
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
 * Execute method (JBPM ActionHandler interface). Retrieves the attribute value
 * of a JtObject and stores it using a jBPM variable (jpmVariable). 
 */


public void execute(ExecutionContext context) throws Exception {
	Object obj;
	Object value;
	
    this.context = context;
    if (jbpmObject == null) {
      handleError ("jbpmObject needs to be set.");        
      return;  
    }
    if (jbpmAttribute == null) {
        handleError ("jbpmAttribute needs to be set.");        
        return;  
    }
    if (jbpmVariable == null) {
        handleError ("jbpmVariable needs to be set.");        
        return;  
    }
    
    try {
	  obj = context.getContextInstance().getVariable (jbpmObject);
      if (obj == null) {
         handleError ("Invalid parameter(jbmObject):" + jbpmObject);
         return;
      }
          
      value = getValue (obj, jbpmAttribute);
    	
	  context.getContextInstance().setVariable (jbpmVariable, value);
    } catch (Exception ex) {
      handleException (ex);
    }
}


}



