package Jt;


/**
 * Jt Implementation of the Factory Method pattern.
 */

public class JtFactory extends JtObject {


  private static final long serialVersionUID = 1L;


  public JtFactory () {
  }

  // Create an object of a specific class
  
  private Object create (String className) {
    Class jtclass;
    
    if (className == null)
        return (null);      
    try {
        jtclass = Class.forName ((String) className);
        return (jtclass.newInstance ());
    } catch (Exception e) {
        handleException (e);
        return (null);
    }  

  }

  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be required before this object is removed.
    * </ul>
    */

  public Object processMessage (Object event) {

      String msgid = null;
      JtMessage e = (JtMessage) event;
      Object content;
      //Object data;


      if (e == null)
          return null;

      msgid = (String) e.getMsgId ();

      if (msgid == null)
          return null;
      
      if (msgid.equals ("JtCREATE")) {
          content = (String) e.getMsgContent ();
          if (content == null) {
              handleError ("JtCREATE: invalid message. Class name is null.");
              return (null);
          }    
          return (create ((String) e.getMsgContent ()));
      }
      
      //content = e.getMsgContent();
      //data = e.getMsgData ();

      return (super.processMessage (event)); 


  }

 
  /**
   * Unit tests the messages processed by JtFactory
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtObject obj;


    // Create JtFactory

    obj = (JtObject) factory.createObject ("Jt.JtObject", "object");
    System.out.println (obj);



  }

}


