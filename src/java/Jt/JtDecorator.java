

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Decorator pattern.
 */

public class JtDecorator extends JtObject {

  private Object component = null;
 

/**
  * Specifies the object that is being decorated (extra functionality added).
  * @param component component
  */

  public void setComponent (Object component) {
     this.component = component;
  }


/**
  * Returns the object that is being decorated.
  */

  public Object getComponent () {
     return (component);
  }

  public JtDecorator () {
  }




  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be needed before the object is removed.
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


     if (msgid.equals ("JtREMOVE")) {
       return (super.processMessage (null));     
     }


     handleError ("JtDecortator.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtDecorator.
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();

    JtDecorator decorator;
 
    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create JtDecorator

    decorator = (JtDecorator) factory.createObject ("Jt.JtDecorator", "decorator");

    System.out.println (decorator);

    factory.removeObject ("decorator");


  }

}


