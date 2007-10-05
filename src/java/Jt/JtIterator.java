

package Jt;
import java.util.*;


/**
  * Iterator over the elements of a Jt collection.
  */


public class JtIterator extends JtObject {

  private static final long serialVersionUID = 1L;
  //private  Hashtable col = null;// Object table
  //private HashMap col = null;
  private transient Iterator iterator;
  //JtCollection col;


  public JtIterator() {
  }



  /**
    * Void operation.
    */

  public void setIterator (Iterator iterator) {
     this.iterator = iterator; 
  }


  /**
   * Returns the Java Iterator.
   */

  public Iterator getIterator () {
     return (iterator);
  }


  // next: next element

  private Object next () {
    if (iterator == null) // check
      return (null);
    if (iterator.hasNext()) {
      return (iterator.next ());
    }

    return (null);
  }

  /**
    * Process object messages.
    * <ul>
    * <li>JtNEXT - returns the next element in the iteration
    * </ul> 
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;
   //Object data;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	   return null;

     //content = e.getMsgContent();
     //data = e.getMsgData ();

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtNEXT")) {
       return (next ());
     }     

     return (super.processMessage (event));          
     

  }

 
  /**
    * Unit tests the messages processed by JtIterator.
    */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;

    JtIterator it;
    Object obj;

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create a JtColletion

    main.createObject ("Jt.JtCollection", "collection");


    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");
    main.setValue (msg, "msgId", "JtADD");
    main.setValue (msg, "msgContent", new Integer (1));

    // Add objects to the collection

    main.sendMessage ("collection", msg);

    // Add another object to the collection

    main.setValue (msg, "msgId", "JtADD");
    main.setValue (msg, "msgContent", new Integer (2));
    main.sendMessage ("collection", msg);
   
    it = (JtIterator) main.getValue ("collection", "iterator");


    main.setValue (msg, "msgId", "JtNEXT");

    while ((obj = main.sendMessage (it, msg)) != null)
      System.out.println ("Object=" + obj);

    main.removeObject ("collection");



  }

}


