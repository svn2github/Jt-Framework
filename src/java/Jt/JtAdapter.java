package Jt;


/**
 * Jt Implementation of the Adapter pattern.
 */


abstract public class JtAdapter extends JtObject {


  private Object adaptee;

  public JtAdapter() {
  }

/**
  * Specifies the adaptee.
  *
  * @param adaptee adaptee
  */

  public void setAdaptee (Object adaptee) {
     this.adaptee = adaptee; 

  }

/**
  * Returns the adaptee.
  */

  public Object getAdaptee () {
     return (adaptee);
  }




  /**
    * Process object messages. 

    * @param event Jt Message    
    */

  //abstract public Object processMessage (Object event);//{
/*
   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;

    return (null);
*/
  //}

  /**
    * Unit Tests the messages processed by JtAdapter.   
    */

  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    //JtMessage msg;
    JtAdapter adapter;


    // Create an instance of JtAdapter

    adapter = (JtAdapter)
      factory.createObject ("Jt.JtAdapter", "adapter");
    factory.removeObject(adapter);    

  }

}
