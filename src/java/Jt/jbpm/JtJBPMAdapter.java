package Jt.jbpm;


import java.io.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import Jt.*;

/**
 * Jt adapter for the jBPM interface.
 */

public class JtJBPMAdapter extends JtAdapter {

private static final long serialVersionUID = 1L;
private String processDefinition;
private transient InputStream inputStream;
private transient boolean initted = false; 
private transient ProcessInstance instance = null;
private transient ExecutionContext parentContext = null;
private transient boolean exitProcessOnException = true; 
private boolean propagateExceptions = true;


    public JtJBPMAdapter() {
    }

    /**
     * Returns the XML file that contains the process definition.
     */
	  
	public String getProcessDefinition() {
		return processDefinition;
	}

    /**
     * Specifies the XML file that contains the process definition.
     * @param processDefinition process definition (XML format)
     */

	public void setProcessDefinition(String processDefinition) {
		this.processDefinition = processDefinition;
	}
	

    /**
     * Returns the input stream associated with the  process definition.
     */
    
	public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Specifies the input stream associated with the process definition.
     * @param inputStream process definition input stream
     */
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    
    /**
     * Returns boolean attribute exitProcessOnException
     */

    public boolean getExitProcessOnException() {
        return exitProcessOnException;
    }

    /**
     * Returns boolean attribute propagateExceptions
     */
    
    public boolean getPropagateExceptions() {
        return propagateExceptions;
    }

    /**
     * Specifies if the exceptions should be propagated to the adapter.
     * The default is true. Refer to JtMessageSender.
     * @param propagateExceptions boolean attribute
     */
    
    public void setPropagateExceptions(boolean propagateExceptions) {
        this.propagateExceptions = propagateExceptions;
    }
    
    
    /**
     * Specifies if the process should exit when an exception is detected.
     * The default is true.
     * @param exitProcessOnException boolean attribute
     */
    
    public void setExitProcessOnException(boolean exitProcessOnException) {
        this.exitProcessOnException = exitProcessOnException;
    }
    
    private Object activate () {

        if (instance == null)
            return (null);
        try {

            handleTrace ("JtBPMAdapter: starting the process ...");

            // Move the process instance from its start state to the end state.

            while (!instance.hasEnded()) {
                instance.signal();
                // check for exceptions
                if (getObjException () != null) {
                    handleTrace ("JtBPMAdapter: an exception has been detected ... exiting");
                    break;
                }    
            }    
            handleTrace ("JtBPMAdapter: the process has ended ...");
            initted = false;

        } catch (Exception ex) {
            handleException (ex);
        }

        // check for exceptions. If there is an exception propagate it to
        // this object

        propagateException ((Exception) instance.getContextInstance().getVariable("jtException"));

        // returns the output of this process (stored in the jbpmOutput variable


        //return (instance.getContextInstance().getVariable("jbpmOutput") );
        return (instance.getContextInstance().getVariable("jtReply") );
    }
    
    // Propagate the exception 

    private Exception propagateException (Exception ex)
    {
      if (ex != null)
        this.setObjException(ex);  

      return (ex);
    }

    // Initialize the adapter using the process definition file or the input stream
    
	private void initializeAdapter () {
		//FileInputStream fis;
        InputStream iStream = null;
		
		if (processDefinition == null && inputStream == null) {
			handleError ("Attribute processDefinition or inputStream need to be set");
			return;
		}
			
		try {
            
            if (inputStream == null) {
		      iStream = new FileInputStream(processDefinition);
              handleTrace ("JtJBPMAdapter: parsing process definition:" + processDefinition);
            } else
              iStream = inputStream;
            
		    ProcessDefinition processDefinition = ProcessDefinition.parseXmlInputStream(iStream);

		    instance = new ProcessInstance(processDefinition);
            if (instance != null)
              instance.getContextInstance().setVariable ("JtJBPMAdapter", this);
	    } catch (Exception ex) {
	      handleException (ex);
	    }
					
	}
	
	public void execute(ExecutionContext context) throws Exception {

        parentContext = context;
	    //if (!initted) { // check
	      initializeAdapter ();
	      initted = true;
	    //}	
            
        super.execute (context);


    }

    // Add a process variable

	private Object addVariable (String variable, Object value) {
	    if (instance == null)
	        return (null);
	    if (variable == null || value == null)
	        handleError ("addVariable: invalid parameters (null)");
	    try {
	        instance.getContextInstance().setVariable (variable, value);
	    } catch (Exception ex) {
	        handleException (ex);  
	    }
	    return (this);
	}

	
	// Pass a process variable to the child process
	
	private Object passVariable (String variable) {
		Object value;
        if (instance == null || parentContext == null)
          return (null);
        
        
        if (variable == null) {
          handleError ("passVariable: invalid parameter (null)");
        }
        
        try {
        	value = parentContext.getContextInstance().getVariable (variable);
        	
            instance.getContextInstance().setVariable (variable, value);
        } catch (Exception ex) {
    	    handleException (ex);  
        }
        return (null);
	}
	  /**
	    * Process object messages based on the msgID.
	    * <ul>
	    * <li>JtACTIVATE -  Executes a jBPM process according to its process definition.
	    * <li>JtPASS_VARIABLE -  Passes a variable to the child process. msgContent specifies
        * the process variable to be passed.
	    * </ul>
	    */

	  public Object processMessage (Object event) {

	   String msgid = null;
	   JtMessage e = (JtMessage) event;
	   Object content;
	   Object data;


	     if (e == null)
		return null;

	     msgid = (String) e.getMsgId ();

	     if (msgid == null)
		return null;

	     content = e.getMsgContent();
	     data = e.getMsgData ();

	     // Remove this object
	     
	     if (msgid.equals ("JtREMOVE")) {
	       return (this);     
	     }
	     
	     if (msgid.equals ("JtACTIVATE")) {
	       if (!initted) {
	    	   initializeAdapter ();
	    	   initted = true;
	       }	   
	       return (activate ());     
	     }
     
	     if (msgid.equals ("JtADD_VARIABLE")) {
	         if (!initted) {
	             initializeAdapter ();
	             initted = true;
	         }
	         return (addVariable ((String) content, 
	                 data));     
	     }
	       
	     
	     if (msgid.equals ("JtPASS_VARIABLE")) {
             return (passVariable ((String) content));     
		 }
	     
	     handleError ("processMessage: invalid message id:" + msgid);
	     return (null);

	  }

	 
	  /**
	   * Unit tests the messages processed by JtJBPMAdapter.
	   */

	  public static void main(String[] args) {

	    JtObject main = new JtFactory ();
	    JtJBPMAdapter jbpmAdapter;

	    //main.setLogFile("stderr");
	    //main.setObjTrace (1);
        
          if (args.length < 1) {
        	System.err.println ("Usage: java Jt.jbpm.JtJBPMAdapter processdefinition.xml");
        	return;
          }
           
        
	    // Create JtBPMAdapter

	    jbpmAdapter = (JtJBPMAdapter) main.createObject ("Jt.jbpm.JtJBPMAdapter", "jbpmAdapter");
	    main.setValue (jbpmAdapter, "processDefinition", args[0]);
 	    main.sendMessage(jbpmAdapter, new JtMessage ("JtACTIVATE"));
	    main.removeObject ("jbpmAdapter");


	  }




	}
	
