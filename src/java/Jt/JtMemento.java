

package Jt;


/**
 * Jt implementation of the Memento pattern. This class captures
 * the internal state of an object (originator) so that
 * it can be restored to this state later.
 */

public class JtMemento extends JtObject {


  private static final long serialVersionUID = 1L;
  private Object state = null;


/**
  * Specifies (saves) the originator's internal state.
  * @param state state
  */

  public void setState (Object state) {
     this.state = state;
  }


/**
  * Returns the originator's internal state.
  */
  public Object getState () {
     return (state);
  }

  public JtMemento () {
  }




  /**
    * Process object messages.
    * <ul>
    * <li>JtREMOVE - Performs any housekeeping that may be needed before this 
    * object is removed.
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


     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }


     handleError ("JtMemento.processMessage: invalid message id:" + msgid);
     return (null);

  }

 
  /**
   * Unit tests the messages processed by JtMemento.
   */


  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtMemento memento;
 

    // Create an instance of JtMemento

    memento = (JtMemento) factory.createObject ("Jt.JtMemento", "memento");

    factory.removeObject (memento);


  }


}


