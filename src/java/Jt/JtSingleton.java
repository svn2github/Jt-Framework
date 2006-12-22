

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Singleton pattern.
 */

public class JtSingleton extends JtObject {
  private static Object instance;



  public JtSingleton () {
  }

/**
  * Returns the unique class instance.
  */

  public static synchronized Object getInstance()
  {

    return instance;
  }



/**
  * Specifies the unique class instance.
  */

  public static synchronized void setInstance(Object newInstance)
  {
    if (instance == null)
      instance = newInstance;

  }


  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be required before
    * this object is removed.
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


     handleError ("JtSingleton.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtSingleton.
   */

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();
    JtMessage msg, msg1;
    Integer count;
    JtSingleton singleton, singleton1;

    System.out.println ("Creating an instance of a singleton ...");

    // Create a JtSingleton instance

    singleton = (JtSingleton) main.createObject ("Jt.JtSingleton", "singleton");


    System.out.println ("Attempting to create a second instance of a singleton ...");

    singleton1 = (JtSingleton) main.createObject ("Jt.JtSingleton", "singleton1");

    main.removeObject ("singleton");


  }

}


