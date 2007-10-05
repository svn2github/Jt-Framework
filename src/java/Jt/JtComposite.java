

package Jt;
import java.util.*;


/**
 * Jt Implementation of the Composite pattern.
 */

public class JtComposite extends JtObject {


  private static final long serialVersionUID = 1L;
  private  Hashtable hashtable = null;


  /**
    * Changes the hastable used to represent this class and store component objects.
    */ 

  public void setHashtable (Hashtable hashtable) {
    this.hashtable = hashtable;
  }


  /**
    * Returns the hastable used to represent this class and store component objects.
    */ 

  public Hashtable getHashtable () {
    return (hashtable);
  }

  public JtComposite () {
  }



  // Broadcast a message

  private void broadcastMessage (JtMessage msg)
  {
    Collection values;
    Iterator it;

    if (msg == null || hashtable == null)
      return;

    values = hashtable.values ();
    if (values == null)
      return;
    
    synchronized (hashtable) {
    it = values.iterator ();

    while (it.hasNext()) {
      sendMessage (it.next (), msg);
    }
    }

  }


  /**
    * Process object messages.
    * <ul>
    * <li> JtADD_CHILD - Adds a component object (msgContent) to this composition. It uses
    * the name (key) specified by msgData. 
    * <li> JtREMOVE_CHILD - Removes a component object (msgData) from this composition
    * <li> JtGET_CHILD - Returns the component object specified by msgData or null if it
    * doesn't exist.
    * <li> JtBROADCAST - Broadcast the message specified by msgContent to all the objects
    * in this composition.
    * </ul>
    * @param message Jt Message
    */

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage e = (JtMessage) message;
   Object content;
   Object data;


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

     if (msgid.equals ("JtADD_CHILD")) {
        // Add an object to the hash table

        if (content == null) {
          handleWarning 
            ("JtComposite.processMessage(JtADD_CHILD):invalid value (null)");
          return (this);

        }
        if (hashtable == null)

          hashtable = new Hashtable ();
        
        hashtable.put (data, content);        
        return (this);
     }     


     if (msgid.equals ("JtREMOVE_CHILD")) {
        // Add an object to the hash table


        if (hashtable == null)
          hashtable = new Hashtable ();
        

        return (hashtable.remove (data));        

     }   

     if (msgid.equals ("JtGET_CHILD")) {


        if (data == null) {
          handleWarning 
            ("JtHashTable.processMessage(JtGET):invalid value (null)");
          return (this);

        }
        if (hashtable == null)
          return (null);
        

        return (hashtable.get (data));        

     }  


     if (msgid.equals ("JtTEST")) {     
       return (test ());
     }

     // Broadcast a message to all the members
     // of the hash table

     if (msgid.equals ("JtBROADCAST")) {
     
       if (hashtable == null) {
         return (this);
       }

       broadcastMessage ((JtMessage) content);

       return (this);
     }

     if (msgid.equals ("JtPRINT_OBJECT")) {     
       return (super.processMessage (message));
     }
          
     handleError ("JtComposite.processMessage: invalid message id:" + msgid);
     return (null);

  }

 /**
   * Unit tests the messages processed by JtComposite and illustrates the use of this class.
   */

  private Object test() {

    JtObject main = new JtFactory ();
    JtMessage msg;
    JtEcho echo1;
    JtEcho echo2;



    echo1 = (JtEcho) createObject ("Jt.JtEcho", "echo1");
    echo2 = (JtEcho) createObject ("Jt.JtEcho", "echo2");


    System.out.println ("JtComposite(JtADD_CHILD): adding a child ...");

    msg = new JtMessage ("JtADD_CHILD");

    setValue (msg, "msgContent", echo1);
    setValue (msg, "msgData", "echo1");

    sendMessage (this, msg);

    System.out.println ("JtComposite(JtADD_CHILD): adding a child ...");

    msg = new JtMessage ("JtADD_CHILD");

    setValue (msg, "msgContent", echo2);
    setValue (msg, "msgData", "echo2");


    sendMessage (this, msg);



    msg = new JtMessage ("JtGET_CHILD");
    main.setValue (msg, "msgData", "echo1");

    System.err.println ("JtComposite(JtGET_CHILD):" + 
      sendMessage (this, msg));

    System.out.println ("Printing the composition (XML format) ...");

    sendMessage (this, new JtMessage ("JtPRINT_OBJECT"));


    msg = new JtMessage ("JtREMOVE_CHILD");

    main.setValue (msg, "msgData", "echo1");

    System.err.println ("JtComposite(JtREMOVE_CHILD):" + 
      sendMessage (this, msg));


    msg = new JtMessage ("JtBROADCAST");
    msg.setMsgContent (new JtMessage ("JtPRINT_OBJECT"));

    System.out.println 
    ("JtComposite(JtBROADCAST): broadcasting a message to the composition ...");

    sendMessage (this, msg);


    return (this);


  }
 
  /**
   * Unit tests the messages processed by JtComposite
   */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    //JtMessage msg, msg1;
    //Integer count;
    JtComposite composite;

    // Create an instance of JtColletion

    composite = (JtComposite) main.createObject ("Jt.JtComposite", "composite");

    main.sendMessage (composite, new JtMessage ("JtTEST"));


    main.removeObject ("composite");


  }

}


