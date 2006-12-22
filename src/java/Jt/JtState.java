

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the State pattern.
 */

public class JtState extends JtObject {


  private Object state;


  public JtState () {
  }

/**
  * Specifies the state.
  *
  * @param state state
  */

  public void setState (Object state) {
     this.state = state; 

  }

/**
  * Returns the state.
  */

  public Object getState () {
     return (state);
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

     //return (super.processMessage (event));


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("JtState.processMessage: invalid message id:" + msgid);
     return (null);


  }

 
  /**
   * Unit tests the messages processed by JtState
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtState state;


    // Create an instance of JtState

    state = (JtState) factory.createObject ("Jt.JtState", "state");


    // Remove the object

    factory.removeObject (state);


  }

}


