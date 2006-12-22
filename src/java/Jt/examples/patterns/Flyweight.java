

package Jt.examples.patterns;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;


/**
 * Demonstrates the use of Flyweight, Abstract Factory and State.
 */

public class Flyweight extends JtFlyweight {



  /**
    * Process object messages.
    * <ul>
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

      
     return (super.processMessage (message));
     
  }

 
  /**
   * Main program. It 
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    SwitchFactory switchFactory;
    JtMessage msg;
    Vector switches;
    int i;
    JtState myswitch;


    JtFlyweight flyweightp;

    // Create instances of JtFlyweight and SwitchFactory

    flyweightp = (JtFlyweight) main.createObject ("Jt.JtFlyweight", "flyweight");
    switchFactory = (SwitchFactory) main.createObject 
      ("Jt.examples.patterns.SwitchFactory", "switchFactory");
    main.setValue (flyweightp, "factory", switchFactory);


    // Manages and shares 100 flyweights (switches). Actually only two State 
    // instances are created.

    switches = new Vector ();

    msg = new JtMessage ("JtGET_FLYWEIGHT");


    System.out.println ("Requesting and sharing  100 flyweights (switches) ...");

    for (i = 0; i <= 100; i++) {   
      if (i % 2 == 0) 
        msg.setMsgData ("On");  
      else
        msg.setMsgData ("Off");     
      myswitch = (JtState) main.sendMessage (flyweightp, msg); 
      switches.addElement (myswitch);
    }

    // Display the switches


    System.out.println ("Displaying the state of the 100 switches ...");

    for (i = 0; i <= 100; i++) {   
      myswitch = (JtState) switches.elementAt (i); 
      System.out.println ("Switch" + i + ":"
       + main.sendMessage (myswitch, new JtMessage ("JtSWITCH_VALUE")));
    }


    main.removeObject ("flyweight");


  }

}


