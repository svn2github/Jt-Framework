package Jt.struts;
import java.io.IOException;
import java.io.InputStream;

import org.apache.struts.action.*;

import Jt.JtFactory;
import Jt.JtInterface;
import Jt.JtMessage;
import Jt.JtObject;
import Jt.jbpm.JtJBPMAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** 
 *  Jt Adapter for the Struts API. It allows the user to easily integrate the Jt framework with the Struts framework.
 *  The business logic is implemented by a delegate class or via a jBPM business process.
 */



public class JtStrutsAction extends Action {
    ActionErrors errors;
    JtFactory main;
    static final String RESOURCE_PATH = "/WEB-INF/" + JtObject.RESOURCE_FILE;
    
    public ActionForward execute (ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) throws IOException, ServletException {
        String className = null;
        //JtFactory main = new JtFactory ();
        JtMessage msg = null, msg1 = null;
        JtInterface delegate;
        Object jtReply;
        String msgId;
        Exception ex;
        String parameter;
        JtJBPMAdapter jbpmAdapter;
        InputStream inputStream;
        
        main = new JtFactory ();
        errors = new ActionErrors ();
        //main.setObjTrace(1);        
        
        inputStream = determineResourceStream (JtObject.WEB_RESOURCE_PATH);
 
        //main.setLogFile("stderr");
        
        // Loading resources from input stream
        
        if (inputStream != null) {
            main.handleTrace("reading resources from input stream " + "(" + JtObject.WEB_RESOURCE_PATH + ") ...");
            main.setResourceStream(inputStream);
        }
        
        
        parameter = mapping.getParameter();  // Parameter within the action element (struts-config.xml)
                                             // This parameter must specify the delegate class (Jt interface)
                                             // or the jBPM process definition XML file
        
        if (parameter == null) {
            errorDetected ("The action parameter (struts-config.xml) needs to be set.");
            if (!errors.isEmpty ())                
                saveErrors (request, errors);
  
            return (mapping.findForward("failure"));
        }       
        
        // jBPM process definition file
        
        if (isProcessDefinitionFile (parameter)) { 
            
            // Create an instance of JtJBPMAdapter class.
            // This class will be used to execute the BPM business process
            
            jbpmAdapter = new JtJBPMAdapter ();
            
            inputStream = determineResourceStream (parameter);
            
            //System.out.println ("inputStream:" + inputStream);
            
            main.setValue (jbpmAdapter, "inputStream", inputStream);
            
            
            msgId = request.getParameter("jtMsgId");
 /*           
            if (msgId == null) {
                errorDetected ("JtStrutsAction: the parameter jtMsgId needs to be set.");
                if (!errors.isEmpty ())                
                    saveErrors (request, errors);
      
                return (mapping.findForward("failure"));
            } 
*/            
            // Add a variable to the process (jtMessage)
            
            msg = new JtMessage (msgId);
            msg.setMsgContent (request.getParameter("jtMsgContent"));        
            msg.setMsgData(form); // pass the Action Form to the process (msgData)
            
            msg1 = new JtMessage ("JtADD_VARIABLE");
            msg1.setMsgContent ("jtMessage");
            msg1.setMsgData (msg);
            
            main.sendMessage (jbpmAdapter, msg1);
            
            // Execute the business process
            
            jtReply = main.sendMessage (jbpmAdapter, new JtMessage ("JtACTIVATE"));
            ex = (Exception) main.getValue(jbpmAdapter, "objException");
            
            // check if an exception was detected during the execution
            
            if (ex != null) {
                exceptionDetected (ex);

                if (!errors.isEmpty ())                
                    saveErrors (request, errors);
                return (mapping.findForward("failure"));
            }
            
            // Set the jtReply attribute
            
            request.setAttribute("jtReply", jtReply);
            
            //System.out.println (jtReply);
            
            return (mapping.findForward("success"));
        }
        
        // delegate class (implements JtInterface)
        
        className = parameter;  //The action parameter specifies the class name
        
        // Attemp to create an instance of the delegate
        
        msg = new JtMessage ("JtCREATE");
        msg.setMsgContent(className);
        
        delegate = (JtInterface) main.processMessage (msg);
                   
        
        // Check for exceptions during creation
        
        ex = (Exception) main.getObjException();
        if (ex != null) {
            exceptionDetected (ex);

            if (!errors.isEmpty ())                
                saveErrors (request, errors);
            return (mapping.findForward("failure"));
        }

        msgId = request.getParameter("jtMsgId");
   
        if (msgId == null) {
            errorDetected ("JtStrutsAction: the parameter jtMsgId needs to be set.");
            if (!errors.isEmpty ())                
                saveErrors (request, errors);
  
            return (mapping.findForward("failure"));
        } 
    
        
        // Assemble the message
        
        msg = new JtMessage (msgId);
        msg.setMsgContent (request.getParameter("msgContent"));        
        msg.setMsgData(form); // send the Action Form to the delegate class (msgData)
        
        
        // Let the delegate class process the message
        
        jtReply = main.sendMessage (delegate, msg);
                
        // Check for exeptions
        
        ex = (Exception) main.getValue(delegate, "objException");
        if (ex != null ) {
            exceptionDetected (ex);
            
            if (!errors.isEmpty ()) {
                
                saveErrors (request, errors);
            }
            return (mapping.findForward("failure"));            
        }
        
        // Set the jtReply attribute
        
        request.setAttribute("jtReply", jtReply);
        
        //System.out.println (jtReply);
        
        return (mapping.findForward("success"));
    }
     
    private InputStream determineResourceStream (String path) {
        if (path == null)
            return (null);
        
        ServletContext context = servlet.getServletContext();
     
        return (context.getResourceAsStream(path));
        //ActionServlet ac = this.getServlet();
        //ServletContext context = ac.getServletContext ();
        //return (request.getRealPath(partialPath));
        
    }
    
    // verify if the parameter represents a process definition file
    
    private boolean isProcessDefinitionFile (String path) {
        if (path == null)
            return (false);
        
        // check if the file ends with .xml (process definition file)
        
        if (path.endsWith(".xml"))
            return (true);
        return (false);
    }
    
    // exception detected
    
    private void exceptionDetected (Exception ex) {
        
        errors.add (ActionErrors.GLOBAL_ERROR, new ActionError ("jt.exception", ex.getMessage()));
        
    }
    
    // error detected
    
    private void errorDetected (String error) {
        
        main.handleError(error); // forces an exception to be generated
        exceptionDetected ((Exception) main.getObjException()); 
        
    }
    

}
