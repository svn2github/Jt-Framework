package Jt.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Jt.JtFactory;
import Jt.JtMessage;
import Jt.JtInterface;
import Jt.JtObject;
import Jt.xml.JtXMLHelper;


/**
 * Jt Servlet used for processing Ajax requests.
 */

public class JtAjaxServlet extends HttpServlet {


    private static final long serialVersionUID = 1L;
    //JtFactory main = new JtFactory ();
    private boolean convertToXML = true;   // Convert output to the XML format

      

    public JtAjaxServlet() {
     }

    /**
     * Returns boolean attribute convertToXML
     */

    
    public boolean getConvertToXML() {
        return convertToXML;
    }

    
    /**
     * Specifies if the output should be converted 
     * to the XML format. The default is true. 
     * @param convertToXML boolean attribute
     */
    public void setConvertToXML(boolean convertToXML) {
        this.convertToXML = convertToXML;
    }

    
    // Create the delegate class using the class name
    
    private Object createDelegate (JtObject main, String className) {
        JtMessage msg = new JtMessage ("JtCREATE");
        
        if (className == null)
            return (null);
  
        if ("Jt.JtOSCommand".equals (className) || "Jt.JtFile".equals (className)) {
            main.handleError ("Security violation: unable to create a remote instance of " +
                    className);
            return (null);
        }

        
        // Attemp to create an instance of the delegate
    
        msg.setMsgContent(className);
        
        return (main.processMessage(msg));
        
    }
    
    
    // Convert object to XML format (use JtXMLHelper to do the conversion)
    
    private String convertToXML (JtObject main, Object obj) {
        JtMessage msg = new JtMessage ("JtCONVERT_OBJECT_TO_XML");
        JtXMLHelper xmlHelper = new JtXMLHelper ();
        
        //if (obj == null)
        //    return (null);
        
        msg.setMsgContent(obj);
        return ((String) main.sendMessage(xmlHelper, msg));
    }
    
    
    // Process the HTTP Get Request
    
    public void doGet (HttpServletRequest req, HttpServletResponse res) 
    throws ServletException, IOException {
        doPost (req, res);
        
    }
    
    
    // Process the HTTP Get Request
    
    public void doPost (HttpServletRequest req, HttpServletResponse res) 
    throws ServletException, IOException {
       String jtMsgContent = req.getParameter("jtMsgContent");
       String jtClassName = req.getParameter("jtClassName");
       String jtMsgId = req.getParameter("jtMsgId");
       JtMessage msg = new JtMessage ();
       Object jtReply;
       Exception ex;

       JtFactory main = new JtFactory ();     
       res.setContentType("text/html");
       res.setHeader("Cache-Control", "no-cache");
       
       if (jtClassName == null) {
           ex = errorDetected (main, "JtAjaxServlet: request parameter jtClassName needs to be set.");

           if (convertToXML) {
             res.getWriter().write (convertToXML (main, ex));
             return;
           }  
           res.getWriter().write ((ex != null)? ex.getMessage():"");            
           return;
       }    
 
       if (jtMsgId == null) {
           ex = errorDetected (main, "JtAjaxServlet: request parameter jtMsgId needs to be set.");

           if (convertToXML) {
              res.getWriter().write (convertToXML (main, ex));
              return;
           }   
           res.getWriter().write ((ex != null)? ex.getMessage():"");             
           return;
       }    
       
    
       JtInterface delegate = (JtInterface) createDelegate (main, jtClassName);
       
       
       // Check for exceptions during creation of the delegate class
       
       ex = (Exception) main.getObjException();
       if (ex != null) {
           if (convertToXML) {
               res.getWriter().write (convertToXML (main, ex));
            } else    
                res.getWriter().write (ex.getMessage());  
           return;
       }
       
       msg.setMsgId(jtMsgId);
       msg.setMsgContent(jtMsgContent);       
       jtReply = main.sendMessage(delegate, msg);      
 


       // Check for exceptions
       
       ex = (Exception) main.getValue(delegate, "objException"); // check 
       
       if (ex != null) {
           if (convertToXML) {
             res.getWriter().write (convertToXML (main, ex));
           } else
             res.getWriter().write (ex.getMessage());          
           return;
       }    
       
       if (jtReply == null && !convertToXML) {
           res.getWriter().write ("");  //check
           return;
       }    
       
       if (!convertToXML) {          
           res.getWriter().write (jtReply.toString());
           return;
       }    
       // Convert reply to XML format
       
       res.getWriter().write (convertToXML (main, jtReply));
           
    }

   // error detected
    
    private Exception errorDetected (JtObject main, String error) {        
              
        main.handleError(error); // forces an exception to be generated
        return ((Exception) main.getObjException());          
        
    }
    

}
