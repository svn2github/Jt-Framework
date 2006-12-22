

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Echo the message received using XML format.
 */

public class JtEcho extends JtObject {



  public JtEcho() {
  }




  /**
    * Process object messages (echoes the message using XML format).
    * <ul>
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
     //data = e.getMsgData ();
     System.out.println ("JtEcho("+ this.getObjName() +  "):received a message:" 
     + msgid + "...");

     e.processMessage (new JtMessage ("JtPRINT_OBJECT"));

/*
     if (msgid.equals ("JtPRINT_OBJECT")) {
       return (super.processMessage (this));     
     }
*/
     return (this);

/*

     if (msgid.equals ("JtREMOVE")) {
       return (super.processMessage (null));     
     }


     handleError ("JtECHO.processMessage: invalid message id:" + msgid);
     return (null);
*/

  }

 
  /**
   * Unit tests the messages processed by JtEcho.
   */

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();
    JtEcho echo;
 

    // Create JtDecorator

    echo = (JtEcho) main.createObject ("Jt.JtEcho", "echo");

    main.sendMessage (echo, new JtMessage ("JtTEST"));


    //main.removeObject ("echo");


  }

}


