package Jt.examples.struts;

import Jt.JtCollection;
import Jt.JtMessage;
import Jt.JtObject;
import Jt.examples.DAOMember;

import org.apache.struts.action.DynaActionForm;

public class FindRecords extends JtObject {
    private static final long serialVersionUID = 1L;
    
    public String getEmailDomain (String email) {
        int index;
          if (email == null)
            return (null);
          if ((index = email.indexOf("@")) < 0)
            return (null);
          return (email.substring (index));
        }   
     
        public Object findRecords (DynaActionForm form) {
            String query = null;
            String domain = null;
            String email;
            
            DAOMember member;
            JtMessage msg = new JtMessage ("JtFIND_RECORDS");
            JtCollection collection;
            Object jtReply;
 /*           
            if (jtMessage == null) {
                handleError ("Invalid message");
                return (null);
            }
            
            form = (DynaActionForm) jtMessage.getMsgData();
*/            
            if (form == null) {
                return (null);            
            }
            
            //email = (String) getValue (form, "email");
            // Retrieve the email field from the ActionForm
            email = (String) form.get ("email");
            
            if (email == null) {
                handleError ("Invalid email");
                return (null);
            }    
            
            domain = getEmailDomain (email);
            
            if (domain == null)
                return (null);
            
            query = "select * from member where email like " + "'%"+domain+"';";
            
            member = (DAOMember) createObject ("Jt.examples.DAOMember", "member");
            setValue (member, "key", "email");
            setValue (member, "table", "member");
            
            msg.setMsgContent(query);
            
            collection = (JtCollection) sendMessage (member, msg);
            
            // Propagate the exception
            if (member.getObjException() != null)
                this.setObjException(member.getObjException());
            
            jtReply = (collection == null)?null:collection.getCollection();
            
            sendMessage (member, new JtMessage("JtREMOVE"));
            return (jtReply);
        }
        
        /**
         * Process object messages.
         * <ul>
         * <li> JtACTIVATE
         * </ul>
         * @param message message
         */

        public Object processMessage (Object message) {
            Object data;

            JtMessage e = (JtMessage) message;

            if (e == null ||  (e.getMsgId() == null))
                return (null);

            // Remove this object
            if (e.getMsgId().equals ("JtREMOVE")) {
                return (this);     
            }


            if (e.getMsgId().equals("JtACTIVATE")) {

                // pass the form information   
                data = e.getMsgData ();
                return ( findRecords ((DynaActionForm) data));
            }

            handleError ("invalid message id:"+ e.getMsgId());
            return (null);
        }
}
