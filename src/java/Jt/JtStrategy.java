

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt implementation of the Strategy pattern.
 */

public class JtStrategy extends JtObject {


  private Object concreteStrategy = null; // Concrete Strategy object


  public JtStrategy () {
  }



/**
  * Specifies the reference to the concrete strategy.
  */


  public void setConcreteStrategy (Object concreteStrategy) {
     this.concreteStrategy = concreteStrategy;
  }


/**
  * Returns the reference to the concrete strategy.
  */

  public Object getConcreteStrategy () {
     return (concreteStrategy);
  }






  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be needed before the object
    * is removed.
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
       return (null);     
     }

     // Let the concrete strategy object handle the message

     if (concreteStrategy == null) {
       handleError ("processMessage: concreteStrategy attribute must be set");
       return (null);
     }

     return (((JtObject) concreteStrategy).processMessage (event));


  }

 
  /**
   * Unit tests the messages processed by JtStrategy.
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtStrategy strategy;
    JtObject concreteStrategy;

    // Create an instance of JtStrategy

    strategy = (JtStrategy) factory.createObject ("Jt.JtStrategy", "strategy");

    // Specify the concrete strategy to be executed

    concreteStrategy = (JtObject) factory.createObject ("Jt.JtEcho", "concreteStrategy");
    factory.setValue (strategy, "concreteStrategy", concreteStrategy);

    
    factory.sendMessage (strategy, new JtMessage ("JtEXECUTE_STRATEGY"));

    factory.removeObject ("strategy");


  }

}


