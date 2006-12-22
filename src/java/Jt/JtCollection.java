

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Handles collections of objects.
 */

public class JtCollection extends JtObject {
//  private  Hashtable col = null;// Object table
  private HashMap col = null;
  private int size = 0; 


  public JtCollection() {
  }

  /**
    * Void operation.
    */

  public void setSize (int size) {
     this.size = this.size; // void operation
  }

  /**
    * Returns the number of elements in the collection.
    */ 
  
  public int getSize () {
     return (size);
  }

  /**
   * Returns a JtIterartor over the collection. 
   */

  public Object getIterator () {
     JtIterator jit;
     Collection values;

     jit = new JtIterator ();
     
     if (col == null)
       return (null);

     values = col.values ();

     if (values == null)
       return (null);
     jit.setIterator(values.iterator ());
     
     return (jit);
  }


  /**
    * Broadcasts a message to all the objects in the collection.
    */

  private void broadcast_message (JtMessage msg)
  {
    Collection values;
    Iterator it;

    if (msg == null || col == null)
      return;

    values = col.values ();
    if (values == null)
      return;
    it = values.iterator ();

    while (it.hasNext()) {
      sendMessage (it.next (), msg);
    }

  }

  /**
    * Process object messages.
    * <ul>
    * <li>JtADD - Adds the object specified by msgContent to this collection
    * <li>JtMESSAGE - Adds the object specified by msgContent to this collection
    * <li>JtBROADCAST - Broadcast the message specified by msgContent to all the objects
    * in this collection
    * <li>JtCLEAR - Removes all the objects from this collection
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

     // Destroy this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtCOLLECTION_ADD") || msgid.equals ("JtADD")) {
        // Add object to the collection

        if (content == null) {
          handleWarning 
            ("JtCollection.processMessage(JtCOLLECTION_ADD):invalid content (null)");
          return (this);

        }
        if (col == null)
          col = new HashMap ();
//        col = new Hashtable ();
        
        size++;
        col.put (content, content);        
        return (this);
     }     



     if (msgid.equals ("JtOBJECT") || msgid.equals ("JtMESSAGE")) {
        // Add object to the collection

        if (content == null)
          return (this);
        if (col == null)
          col = new HashMap ();
//        col = new Hashtable ();
        
        size++;
        col.put (content, content);        
        return (this);
     }

     if (msgid.equals ("JtCLEAR")) {
     
       if (col != null) {
         col.clear ();
       }
       size = 0;

       return (this);
     }

     // Broadcasts a message to all the members
     // of the collection

     if (msgid.equals ("JtBROADCAST")) {
     
       if (col == null) {
         return (this);
       }

       broadcast_message ((JtMessage) content);

       return (this);
     }
          
     handleError ("JtCollection.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtCollection
   */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg, msg1;
    Integer count;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create JtColletion

    main.createObject ("Jt.JtCollection", "collection");


    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");
    main.setValue (msg, "msgId", "JtADD");
    main.setValue (msg, "msgContent", "Hello");
    count = (Integer) main.getValue ("collection", "size");

    // Add object to the collection

    main.sendMessage ("collection", msg);

    
    count = (Integer) main.getValue ("collection", "size");

    if (count.intValue () == 1)
      System.err.println ("JtCollection(JtADD):GO");
    else
      System.err.println ("JtCollection:FAILED");

    // Clear the collection

    main.setValue (msg, "msgId", "JtCLEAR");
    main.sendMessage ("collection", msg);

    count = (Integer) main.getValue ("collection", "size");

    if (count.intValue() == 0)
      System.err.println ("JtCollection(JtCLEAR):GO");
    else
      System.err.println ("JtCollection:FAILED");

    msg1 = (JtMessage) main.createObject ("Jt.JtMessage", "message1");
    main.setValue ("message1", "msgId", "JtOBJECT");

    main.setValue ("message", "msgId", "JtBROADCAST");
    main.setValue ("message", "msgContent", msg1);

    main.sendMessage ("collection", "message");

    main.removeObject ("collection");


  }

}


