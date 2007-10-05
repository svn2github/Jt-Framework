/*
   DateService - Web service that returns the local date
*/

package Jt.examples;
import java.util.*;
import Jt.*;


public class DateService extends JtObject {

  


  private static final long serialVersionUID = 1L;


  public DateService() {
  }





  // Process object messages

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;
   String dt;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();

     if (msgid.equals ("JtREMOVE")) {
              
         return (this);
     }
     
     // Message1

     if (msgid.equals ("JtGET_DATE")) {

        dt = "" + new Date ();
             
        return (dt);
     }


     if (msgid.equals ("JtGET_TIME")) {
             
        return (new Long ((new Date ()).getTime ()));
     }

          
     handleError ("DateService.processMessage: invalid message id:" + msgid);
     return (null);

  }


  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg1, msg2;
    //Integer count;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");

    msg1 = new JtMessage ();
    msg2 = new JtMessage ();


    msg1.setMsgId ("JtGET_DATE");
    //msg2.setMsgId ("JtMessage2");


    // Create template object

    main.createObject ("Jt.examples.DateService", "template");



    // Send JtMessage1

    System.out.println ((String) main.sendMessage ("template", msg1));


    // Send JtMessage2

    main.sendMessage ("template", msg2);

    main.removeObject ("template");
        

  }

}


