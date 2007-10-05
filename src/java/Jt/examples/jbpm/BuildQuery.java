package Jt.examples.jbpm;

/**
 * Sample class part of jBPM business process
 */

import org.apache.struts.action.DynaActionForm;

import Jt.JtMessage;
import Jt.JtObject;

public class BuildQuery extends JtObject {

    private static final long serialVersionUID = 1L;

    // email domain

    public String getEmailDomain (String email) {
    int index;
      if (email == null)
        return (null);
      if ((index = email.indexOf("@")) < 0)
        return (null);
      return (email.substring (index));
    }   
 
    public String buildQuery (JtMessage  jtMessage) {
        String query = null;
        String domain = null;
        String email;
        DynaActionForm form;
        
        if (jtMessage == null) {
            handleError ("Invalid message");
            return (null);
        }
        
        form = (DynaActionForm) jtMessage.getMsgData();
        
        if (form == null) {
            handleError ("Invalid message");
            return (null);            
        }
        
        //email = (String) getValue (form, "email");
        email = (String) form.get ("email");
        
        if (email == null) {
            handleError ("Invalid email");
            return (null);
        }    
        
        domain = getEmailDomain (email);
        
        if (domain == null)
            return (null);
        
        query = "select * from member where email like " + "'%"+domain+"';";
        
        return (query);
    }
    
    /**
     * Process object messages.
     * <ul>
     * <li> JtACTIVATE
     * </ul>
     * @param message message
     */

    public Object processMessage (Object message) {
    Object content;

    JtMessage e = (JtMessage) message;


       if (e == null ||  (e.getMsgId() == null))
           return (null);

      // Remove this object
      if (e.getMsgId().equals ("JtREMOVE")) {
        return (null);     
      }


       if (e.getMsgId().equals("JtACTIVATE")) {
        content = e.getMsgContent ();

        return ( buildQuery ((JtMessage) content));
       }

       handleError 
        ("invalid message id:"+
            e.getMsgId());
       return (null);
    }



}
