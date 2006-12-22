

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Bridge pattern.
 */

public class JtBridge extends JtObject {

  private Object implementor;

  public JtBridge () {
  }

/**
  * Specifies the implementor.
  *
  * @param implementor implementor
  */

  public void setImplementor (Object implementor) {
     this.implementor = implementor; 

  }

/**
  * Returns the implementor.
  */

  public Object getImplementor () {
     return (implementor);
  }



  /**
    * Process object messages.
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

     if (implementor == null) {
       handleError ("JtBridge.process: the implementor attribute needs to be set");
       return (null);
     }

     // Let the implementor process the request

     return (sendMessage (implementor, event));

/*
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }


     handleError ("JtBridge.processMessage: invalid message id:" + msgid);
     return (null);
*/

  }

 
  /**
   * Unit tests the messages processed by JtBridge.
   */


  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtBridge bridge;

    JtMessage msg;

    // Create an instance of JtBridge

    bridge = (JtBridge) factory.createObject ("Jt.JtBridge", "bridge");

    // Remove the object 

    factory.removeObject ("bridge");


  }


}


