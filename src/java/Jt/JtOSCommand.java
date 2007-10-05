package Jt;

import java.io.*;


/** 
 *  Class used to invoke OS commands.
 */


public class JtOSCommand extends JtObject {


  private static final long serialVersionUID = 1L;
  String command;
  Process process = null;
  int status = 0;
  String stdout;


 /**
  * Specifies the OS command to be executed.
  * @param newCommand OS command
  */

  public void setCommand (String newCommand) {
     command = newCommand;
  }

 /**
   * Returns the OS command to be executed. 
   */

  public String getCommand () {
     return (command);
  }


 /**
   * Returns the output of the command. 
   */

  public String getStdout () {
     BufferedReader d;
     StringBuffer output = null;
     String line;
     InputStream stream;

     if (process == null)
	return (null);

     stream = process.getInputStream ();

     if (stream == null)
	return (null);

     d = new BufferedReader (new InputStreamReader (stream));
     try { 	
     	while ((line  = d.readLine ()) != null) {
	   handleTrace (line);
	   if (output == null) { 
		output = new StringBuffer ();
	   	output.append (line);
	   } else 
	   	output.append ("\n" + line); // check

	}
     } catch (Exception e) {
	handleException (e);
     }
     if (output != null)
     	stdout = output.toString ();
     return (stdout);
  }

 /**
   * Returns the exit status of the command. 
   */
  public int getStatus () {
     if (process == null) {
	handleWarning ("JtCommand: invalid process: null");
	return (-1);
     }

     try {
	process.waitFor ();
     } catch (Exception e) {
	handleException (e);
     }

     return (process.exitValue ());
  } 
  

 /**
   * Void operation. 
   */

  public void setStatus (int status) {

  } 

  // Executes the OS command

  void execute () {

    if (command == null) {
	process = null;		// just in case
	return;
    }

    try {
    	process = Runtime.getRuntime().exec (command);
    } catch (Exception e) {
	handleException (e);
    }
  }


  /**
   * Process object messages. 
   * <ul>
   * <li> JtEXECUTE - Executes the OS command specified by the command attribute.
   * </ul>
   * @param message Jt Message
   */

  public Object processMessage (Object message) {

   String msgid = null;
   //byte buffer[];
   //File file;
   JtMessage e = (JtMessage) message;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;


     // Destroy this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtEXECUTE")) {
	execute ();
	return null;
     }
     handleError ("JtCommand.processMessage: invalid message id:" + msgid);
     return null;
  }

 /**
   * Unit tests all the message processed by JtCommand. Usage: java Jt.JtCommand command  
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;
    //File tmp;
    Integer status;
    String output;
 

    if (args.length < 1) {
	System.err.println ("Usage: java Jt.JtOSCommand command");
	return;
    }
    
    // main.setObjTrace (1);

    main.createObject ("Jt.JtOSCommand", "command");
    main.setValue ("command", "command", args[0]);


    msg = new JtMessage ("JtEXECUTE");

    main.sendMessage ("command", msg);

    status = (Integer) main.getValue ("command", "status");
    System.err.println ("Exit status:" +  status);
    output = (String) main.getValue ("command", "stdout");
    System.err.println ("Command output:" +  output);

    main.removeObject ("command");   
 
  }
}


