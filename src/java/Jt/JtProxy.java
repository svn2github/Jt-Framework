package Jt;



/**
 * Jt Implementation of the Proxy pattern.
 */


public class JtProxy extends JtObject {


  private static final long serialVersionUID = 1L;
  private Object subject;

  public JtProxy() {
  }

/**
  * Specifies the proxy's subject.
  *
  * @param subject subject
  */

  public void setSubject (Object subject) {
     this.subject = subject; 

  }

/**
  * Returns the proxy's subject.
  */

  public Object getSubject () {
     return (subject);
  }




  /**
    * Process object messages by forwarding them to the proxy's subject. 

    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   //String msgid = null;
   //JtMessage e = (JtMessage) event;
   //Object content;

     // Let the subject process the request

     if (subject == null) {
       handleError ("JtProxy.process: the subject attribute needs to be set");
       return (null);
     }

     return (sendMessage (subject, event));


  }

  /**
    * Unit Tests the messages processed by JtProxy.   
    */

  public static void main(String[] args) {

    JtObject main = new JtFactory ();
    JtMessage msg;
    Jt.examples.HelloWorld helloWorld;
    JtProxy proxy;


    // Create an instance of JtProxy

    proxy = (JtProxy)
      main.createObject ("Jt.JtProxy", 
      "proxy");
    helloWorld = (Jt.examples.HelloWorld) main.createObject ("Jt.examples.HelloWorld", 
      "helloWorld");

    main.setValue (proxy, "subject", helloWorld);


    msg = new JtMessage("JtHello");
    System.out.println ("Reply:" + main.sendMessage (proxy, msg));
         

  }

}
