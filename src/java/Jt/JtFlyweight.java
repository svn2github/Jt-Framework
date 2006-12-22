

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Flyweight pattern.
 */

public class JtFlyweight extends JtComposite {

  private Object factory = null;


  public JtFlyweight () {
  }



/**
  * Specifies the factory.
  *
  * @param factory factory
  */

  public void setFactory (Object factory) {
     this.factory = factory; 

  }

/**
  * Returns the factory.
  */

  public Object getFactory () {
     return (factory);
  }




  /**
    * Process object messages.
    * <ul>
    * <li> JtGET_FLYWEIGHT- Returns the flyweight specified by msgData if it exists.
    * If it doesn't exist, create it and return it. 
    * </ul>
    * @param message Jt Message
    */


  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage e = (JtMessage) message;
   Object content;
   Object data;
   JtMessage tmp;
   JtInterface aux, aux1;
   


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


     if (msgid.equals ("JtGET_FLYWEIGHT")) {
       tmp = new JtMessage ("JtGET_CHILD");
       tmp.setMsgData (e.getMsgData ());
       aux = (JtInterface) super.processMessage (tmp);

       if (aux != null)
         return (aux); 

             
       if (factory == null) { 
         handleError ("processMessage: factory attribute needs to be set");
         return (null);
       }

       //handleTrace ("Jt.Flyweight: processMessage creating a new flyweight");

       tmp = new JtMessage ("JtCREATE_FLYWEIGHT");
       tmp.setMsgData (e.getMsgData ());

       aux1 = (JtInterface) sendMessage (factory, tmp);

       tmp = new JtMessage ("JtADD_CHILD");
       tmp.setMsgContent (aux1);
       tmp.setMsgData (e.getMsgData ());
       super.processMessage (tmp);
       return (aux1);


     }

      
     return (super.processMessage (message));
     
      
     //handleError ("JtMediator.processMessage: invalid message id:" + msgid);
     //return (null);

  }

 
  /**
   * Unit tests the messages processed by JtFlyweight.
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();

    JtFlyweight flyweightp;

    // Create an instance of JtColletion

    flyweightp = (JtFlyweight) main.createObject ("Jt.JtFlyweight", "flyweight");

    //main.sendMessage (composite, new JtMessage ("JtTEST"));


    main.removeObject ("flyweight");


  }

}


