

package Jt;

/**
 * Jt Implementation of the Prototype pattern.
 */

public class JtPrototype extends JtObject {


  private static final long serialVersionUID = 1L;


  public JtPrototype () {
  }

  // Clone the object

  private Object cloneObject () {


      JtMessage msg = new JtMessage ("JtENCODE_OBJECT");
      JtObject tmp = new JtObject();
      Object aux;

      msg.setMsgContent (this);
      aux = tmp.processMessage  (msg);

      if (aux == null) {
          handleError ("cloneObject: Unable to encode the object (XML format"); 
          return (null);
      }

      msg = new JtMessage ("JtDECODE_OBJECT");

      msg.setMsgContent (aux);
      return (tmp.processMessage (msg));    
  }

  private Object test () {

    JtObject tmp;


    tmp = (JtObject) processMessage (new JtMessage ("JtCLONE"));

    if (tmp == null)
      return (null);

    return (tmp.processMessage (new JtMessage ("JtPRINT_OBJECT")));
  }

  /**
    * Process object messages.
    * <ul>
    * <li>JtCLONE - returns a clone of this object.
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

     //return (super.processMessage (event));

     if (msgid.equals ("JtCLONE")) {
       return (cloneObject ());     
     }
 

     if (msgid.equals ("JtPRINT_OBJECT")) {
       return (super.processMessage (event));     
     }


     if (msgid.equals ("JtTEST")) {
       return (test ());     
     }


     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }


     handleError ("JtPrototype.processMessage: invalid message id:" + msgid);
     return (null);


  }

 
  /**
   * Unit tests the messages processed by JtPrototype
   */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtPrototype proto;


    // Create an instance of JtPrototype

    proto = (JtPrototype) factory.createObject ("Jt.JtPrototype", "proto");


    factory.sendMessage (proto, new JtMessage ("JtTEST"));

    // Destroy the object

    factory.removeObject (proto);


  }

}


