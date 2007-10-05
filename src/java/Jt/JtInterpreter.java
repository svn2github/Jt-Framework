package Jt;



/**
 * Jt Implementation of the Interpreter pattern.
 */


public class JtInterpreter extends JtObject {


  private static final long serialVersionUID = 1L;
  private Object context = null;

  public JtInterpreter() {
  }

/**
  * Specifies the interpreter context.
  *
  * @param context context
  */

  public void setContext (Object context) {
     this.context = context; 

  }

/**
  * Returns the interpreter context.
  */

  public Object getContext () {
     return (context);
  }




  /**
    * Process object messages. Subclasses need to override this method and implement
    * the JtINTERPRET message.
    */

  public Object processMessage (Object event) {

      String msgid = null;
      JtMessage e = (JtMessage) event;
      //Object content;


      if (e == null)
          return null;

      msgid = (String) e.getMsgId ();

      if (msgid == null)
          return null;

      //content = e.getMsgContent();


      // Remove this object
      if (msgid.equals ("JtREMOVE")) {
          return (this);     
      }

      if (msgid.equals ("JtINTERPRET")) {
          // subclass needs to implement/process this message
          return (this);     
      }

      handleError ("processMessage: invalid message id:" + msgid);
      return (null);


  }

  /**
    * Unit Tests the messages processed by JtInterpreter.   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    //JtMessage msg;
    JtInterpreter interpreter;



    // Create an instance of JtInterpreter

    interpreter = (JtInterpreter)
      main.createObject ("Jt.JtInterpreter", 
      "interpreter");


    main.sendMessage (interpreter, new JtMessage ("JtINTERPRET"));

    main.removeObject ("interpreter");
         

  }

}