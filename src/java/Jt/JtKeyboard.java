
package Jt;

import java.io.*;

/**
  *  Class used to receive input from
  *  the keyboard.
  */


public class JtKeyboard extends JtObject {


  private static final long serialVersionUID = 1L;
  String command = null;
  Process process = null;
  int status = 0;
  String stdout;
  String line = null;
  String prompt;


/**
  * Specifies a prompt.
  */

  public void setPrompt (String prompt) {
     this.prompt = prompt;
  }

/**
  * Returns the prompt.
  */

  public String getPrompt () {
     return (prompt);
  }

  private byte readln ()[] throws IOException {
  byte buffer[] ;
  int i;
  byte ch;

	buffer = new byte[1024];
	buffer[0] = '\0';

	i = 0;   
	while ((ch = (byte) System.in.read ()) >= 0) {
	    if (i >= 1024)
		break;	


	    buffer[i++] = ch;	
	    if (ch == '\n') // check
		break;       

	}
        if (i == 0)
          line = null;
        else
          line = new String (buffer, 0, i);

        if (line != null)
          line = line.trim (); // check
        //System.out.println ("Line:" + i + ":" + line); 
	return buffer;

  }



  // activate: activate

  void activate () {
  //byte buffer[];

    if (prompt != null)
      System.out.print (prompt);

    try {
      readln ();  
      //buffer = readln ();
    } catch (IOException ex) {
      handleException (ex);
      //line = null;
      return; 
    }
    //line = new String (buffer);
    //System.out.println ("Line:" + line);    

  }


  /**
   * Process object messages.
   * <ul>
   * <li> JtACTIVATE - Read input from the keyboard
   * </ul>
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

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtACTIVATE")) {
	activate ();
	return (line);
     }
     handleError ("JtKeyboard.processMessage: invalid message id:" + msgid);

     return null;
  }


  /**
   * Unit tests the messages processed by JtKeyBoard.
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;
    String oline;

 
    
    // main.setObjTrace (1);

    main.createObject ("Jt.JtKeyboard", "keyboard");
    msg = new JtMessage ("JtACTIVATE");
    //System.out.println ("Press any key to continue ...");
    main.setValue ("keyboard", "prompt", "Press any key to continue ...");
    oline = (String) main.sendMessage ("keyboard", msg);
    System.out.println ("Keyboard input:" + oline);
    main.removeObject ("keyboard");

    
 
  }
}


