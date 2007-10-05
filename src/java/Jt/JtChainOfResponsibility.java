package Jt;



/**
 * Jt Implementation of the Chain of Responsibility pattern.
 */


public class JtChainOfResponsibility extends JtObject {


  private static final long serialVersionUID = 1L;
  private Object successor;

  public JtChainOfResponsibility() {
  }

/**
  * Specifies the successor in the chain.
  *
  * @param successor successor
  */

  public void setSuccessor (Object successor) {
     this.successor = successor; 

  }

/**
  * Returns the successor in the chain.
  */

  public Object getSuccessor () {
     return (successor);
  }




  /**
    * Process object messages. If unable to process a particular message, forward it to the successor. 
    * @param event Jt Message    
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

      // Unable to handle the request, let the successor process the request

      if (successor == null) {
          handleError 
          ("JtChainOfResposibility.processMessage: last in the chain was unable to process message ID:" + msgid);
          return (null);
      }

      return (sendMessage (successor, event));


  }

  /**
    * Unit Tests the messages processed by JtChainOfResposibilty.   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    Jt.examples.HelloWorld helloWorld;
    JtChainOfResponsibility chain;



    // Create an instance of JtChainOfResponsibilty

    chain = (JtChainOfResponsibility)
      main.createObject ("Jt.JtChainOfResponsibility", 
      "chain");


    helloWorld = (Jt.examples.HelloWorld) main.createObject ("Jt.examples.HelloWorld", 
      "helloWorld");

    main.setValue (chain, "successor", helloWorld);


    msg = new JtMessage("JtHello");

    // Send a message ("JtHello") to the chain

    System.out.println (main.sendMessage (chain, msg));

    main.removeObject ("chain");
         

  }

}
