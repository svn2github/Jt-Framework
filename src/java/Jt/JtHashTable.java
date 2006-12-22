

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
  * Handles hash tables.
  */

public class JtHashTable extends JtObject {

  protected HashMap hashmap = null;
  private int size = 0; 


  public JtHashTable() {
  }


  /**
    * Void operation.
    */

  public void setSize (int size) {
     this.size = this.size; // void operation
  }

  /**
    * Returns the number of elements in the hash table.
    */ 

  public int getSize () {
     return (hashmap != null ? hashmap.size (): 0);
  }


  /**
    * Specifies the HashMap object used to represent this object
    */

  public void setHashmap (HashMap hashmap) {
     this.hashmap = hashmap; // void operation
  }

  /**
    * Returns the HashMap.
    */ 

  public HashMap getHashmap () {
     return (hashmap);
  }


  /**
   * Returns a JtIterator.
   */

  public Object getIterator () {
     JtIterator jit;
     Collection values;

     jit = new JtIterator ();
     
     if (hashmap == null)
       return (null);

     values = hashmap.values ();

     if (values == null)
       return (null);
     jit.setIterator(values.iterator ());
     
     return (jit);
  }



  // Broadcast a message

  private void broadcast_message (JtMessage msg)
  {
    Collection values;
    Iterator it;

    if (msg == null || hashmap == null)
      return;

    values = hashmap.values ();
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
    * <li> JtPUT - Adds the object specified by msgContent to this hash table. It associates
    * this object with the key specified by msgData
    * <li> JtCLEAR - Removes all the objects from this hash table
    * <li> JtGET - Returns the value to which the specified key (msgData) is mapped to 
    * <li>JtBROADCAST - Broadcast the message specified by msgContent to all the objects
    * in this hash table
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
       return (null);     
     }

     if (msgid.equals ("JtPUT")) {
        // Add an object to the hash table

        if (content == null) {
          handleWarning 
            ("JtHashTable.processMessage(JtPUT):adding null value");
          //return (this);

        }
        if (hashmap == null)
          hashmap = new HashMap ();
//      col = new Hashtable ();
        
//      size++;
        hashmap.put (data, content);        
        return (this);
     }     


     if (msgid.equals ("JtGET")) {


        if (data == null) {
          handleWarning 
            ("JtHashTable.processMessage(JtGET):invalid value (null)");
          return (this);

        }
        if (hashmap == null)
          return (null);
        

        return (hashmap.get (data));        

     }  


     if (msgid.equals ("JtCLEAR")) {
     
       if (hashmap != null) {
         hashmap.clear ();
       }
       size = 0;

       return (this);
     }

     // Broadcast a message to all the members
     // of the hash table

     if (msgid.equals ("JtBROADCAST")) {
     
       if (hashmap == null) {
         return (this);
       }

       broadcast_message ((JtMessage) content);

       return (this);
     }
          
     handleError ("JtHashTable.processMessage: invalid message id:" + msgid);
     return (null);

  }



  /**
    * Unit tests the messages processed by JtHashTable.
    */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg, msg1;
    Integer count;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create JtColletion

    main.createObject ("Jt.JtHashTable", "hashtable");


    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");
    main.setValue ("message", "msgId", "JtPUT");
    main.setValue ("message", "msgContent", new Integer (1));
    main.setValue ("message", "msgData", "one");


    // Add object to the hashtable


    main.sendMessage ("hashtable", "message");

    main.setValue ("message", "msgId", "JtGET");
    main.setValue ("message", "msgData", "one");

    System.err.println ("JtHashTable(JtGET):" + main.sendMessage ("hashtable", "message"));

    
    count = (Integer) main.getValue ("hashtable", "size");

    if (count.intValue () == 1)
      System.err.println ("JtHashTable(JtPUT):GO");
    else
      System.err.println ("JtHashTable:FAILED");

    // Clear the hashtable

    main.setValue ("message", "msgId", "JtCLEAR");
    main.sendMessage ("hashtable", "message");

    count = (Integer) main.getValue ("hashtable", "size");

    if (count.intValue() == 0)
      System.err.println ("JtHashTable (JtCLEAR):GO");
    else
      System.err.println ("JtHashTable:FAILED");

    msg1 = (JtMessage) main.createObject ("Jt.JtMessage", "message1");
    main.setValue ("message1", "msgId", "JtOBJECT");

    main.setValue ("message", "msgId", "JtBROADCAST");
    main.setValue ("message", "msgContent", msg1);

    main.sendMessage ("hashtable", "message");
    main.removeObject ("hashtable");


  }

}


