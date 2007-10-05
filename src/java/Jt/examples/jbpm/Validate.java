package Jt.examples.jbpm;

import Jt.*;
import org.jbpm.graph.exe.ExecutionContext;

/**
 *  Evaluates an expression (option >= 1 && option <= 5) 
 */
public class Validate extends JtObject {



  private static final long serialVersionUID = 1L;



  public Validate() {
  }


 // convert to integer 

int convertToInt (String input) {
  int num;  
  if (input == null)
    return (-1);
  
  try {
    num = Integer.parseInt (input);
  } catch (Exception e) {
    return (-1);
  }
  return (num);
}

/**
 * Process object messages.
 * <ul>
 * <li> JtACTIVATE - Returns true if option >= 1 && option <=5 
 * </ul>
 * @param message message
 */

public Object processMessage (Object message) {
//String content;
//String query;
JtMessage e = (JtMessage) message;
ExecutionContext context;
String jbpmVariable;


   if (e == null ||  (e.getMsgId() == null))
       return (null);

  // Remove this object
  if (e.getMsgId().equals ("JtREMOVE")) {
    return (this);     
  }

   if (e.getMsgId().equals("JtACTIVATE")) {
    context = (ExecutionContext) e.getMsgContent ();
    jbpmVariable = (String) e.getMsgData ();
    return ( evaluateExpression (context, jbpmVariable));
   }

   handleError 
    ("processMessage: invalid message id:"+
        e.getMsgId());
   return (null);
}

// Evaluate the expression

private Boolean evaluateExpression (ExecutionContext context, String jbpmVariable) {
    String value = null;
    boolean output = false;
    int option;
    
    try {

      if (jbpmVariable != null)
        value = (String) context.getContextInstance().getVariable (jbpmVariable);
      
    } catch (Exception ex) {
      handleException (ex);  
    }

    if (value != null) {
      option = convertToInt (value);
      if (option < 1 || option > 5)
        output = false;
      else
        output = true;
    } 
    return (new Boolean (output));

}


}




