

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Handles a list of Jt objects.
 */

public class JtList extends JtCollection {
//private  Hashtable col = null;// Object table
  private LinkedList col = null;
 
  public JtList() {
  }


/**
 * Returns the LinkedList used to implement this class. 
 */
  public LinkedList getLinkedList () {
    return (col);
  }



/**
 * Sets the LinkedList used to implement this class. 
 */

  public void setLinkedList (LinkedList col) {
    this.col = col;
  }

  /**
   * Returns a JtIterator.
   */

  public Object getIterator () {
     JtIterator jit;
     Collection values;

     jit = new JtIterator ();
     
     if (col == null)
       return (null);
/*
     values = col.values ();

     if (values == null)
       return (null);
*/
     jit.setIterator(col.iterator ());
     
     return (jit);
  }

  /**
    * Void operation.
    */

  public void setSize (int size) {
    // this.size = this.size; // void operation
  }

  /**
    * Returns the number of elements in this list.
    */ 
  
  public int getSize () {
     return (col != null ? col.size (): 0);
  }


  /**
    * Process object messages.
    * <ul>
    * <li> JtADD - Adds the object specified by msgContent to this list
    * <li> JtCLEAR - Removes all the objects from this list
    * <li> JtFIRST - Returns the first element in the list
    * <li> JtLAST - Returns the last element in the list
    * <li> JtREMOVE_FIRST - Removes and returns the first element in the list
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
     //data = e.getMsgData ();

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtADD")) {
        // Add object to the list

        if (content == null) {
          handleWarning 
            ("JtCollection.processMessage(JtADD):invalid content (null)");
          return (this);

        }
        if (col == null)
          col = new LinkedList ();
//        col = new Hashtable ();
        
//      size++;
        col.add (content);        
        return (this);
     }     


     if (msgid.equals ("JtCLEAR")) {
     
       if (col != null) {
         col.clear ();
       }
//     size = 0;

       return (this);
     }

     if (msgid.equals ("JtFIRST")) {

       if (col == null)
         return (null);
     
       if (col.size () < 1) {
         return (null);
       }

       return (col.getFirst ());
     }


     if (msgid.equals ("JtLAST")) {

       if (col == null)
         return (null);
     
       if (col.size () < 1) {
         return (null);
       }

       return (col.getLast ());
     }


     if (msgid.equals ("JtREMOVE_FIRST")) {

       if (col == null)
         return (null);
     
       if (col.size () < 1) {
         return (null);
       }

       return (col.removeFirst ());
     }
         
     handleError ("JtList.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
    * Unit tests the messages processed by JtList.
    */ 

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg, msg1;
    Integer count;
    JtIterator it;
    Object obj;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create a JtList

    main.createObject ("Jt.JtList", "list");


    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");
    main.setValue (msg, "msgId", "JtADD");
    main.setValue (msg, "msgContent", new Integer (1));

    // Add an object to the list

    main.sendMessage ("list", msg);


    main.setValue (msg, "msgId", "JtADD");
    main.setValue (msg, "msgContent", new Integer (2));

 
    // Add object to the list

    main.sendMessage ("list", msg);

   System.out.println ("Size=" + main.getValue ("list", "size"));


    obj = (Object) main.sendMessage ("list", new JtMessage ("JtFIRST"));

    System.out.println ("First=" + obj);

    obj = (Object) main.sendMessage ("list", new JtMessage ("JtLAST"));

    System.out.println ("Last=" + obj);

    it = (JtIterator) main.getValue ("list", "iterator");

    for (;;) {
      obj = (Object) main.sendMessage (it, new JtMessage ("JtNEXT"));
      if (obj == null)
         break;
      System.out.println (obj);
    }


    // Clear the list

    main.setValue ("message", "msgId", "JtCLEAR");
    main.sendMessage ("list", "message");
    main.removeObject ("list");
  }

}


