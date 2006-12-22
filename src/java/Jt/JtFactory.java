

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Factory Method pattern.
 */

public class JtFactory extends JtObject {




  public JtFactory () {
  }



  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be required before this object is removed.
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

     return (super.processMessage (event)); 
/*
     if (msgid.equals ("JtREMOVE")) {
       return (super.processMessage (null));     
     }


     handleError ("JtFactory.processMessage: invalid message id:" + msgid);
     return (null);
*/

  }

 
  /**
   * Unit tests the messages processed by JtFactory
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtObject obj;


    // Create JtFactory

    obj = (JtObject) factory.createObject ("Jt.JtObject", "object");
    System.out.println (obj);



  }

}


