package Jt.examples;

import Jt.*;


public class HelloWorld extends JtObject {


  private static final long serialVersionUID = 1L;

  private String greetingMessage;

  public HelloWorld() {
  }

  // Attributes


  public void setGreetingMessage (String greetingMessage) {
     this.greetingMessage = greetingMessage; 

  }

  public String getGreetingMessage () {
     return (greetingMessage);
  }


  // Process object messages

  public Object processMessage (Object message) {

   String msgid = null;
   JtMessage msg = (JtMessage) message;


     if (msg == null)
	  return null;

     msgid = (String) msg.getMsgId ();

     if (msgid == null)
	  return null;


     // Process JtHello Message

     if (msgid.equals ("JtHello")) {

        if (greetingMessage == null)
            greetingMessage = "Hello World ...";
        
        handleTrace ("HelloWorld returning a greeting message: " +  greetingMessage);
             
        return (greetingMessage);
     }

     if (msgid.equals ("JtREMOVE")) {             
        return (this);
     }
         
     handleError ("HelloWorld.processMessage: invalid message id:" + msgid);
     return (null);

  }


  // Test program

  public static void main(String[] args) {

    JtFactory main = new JtFactory ();  // Jt Factory
    JtMessage msg;
    //String greeting;
    String reply;


    // Create helloWorld (HelloWorld class)

    main.createObject ("Jt.examples.HelloWorld", "helloWorld");

 
    // Set the value of the greetingMessage attribute
    // If the greetingMessage hasn't be set (loaded from .Jtrc)
    // use the default value

    /*
    greeting = (String) main.getValue ("helloWorld", "greetingMessage");

    if (greeting == null || greeting.equals (""))
      main.setValue ("helloWorld", "greetingMessage", "Hello World ...");
    */

    // Create a Message ("JtHello")

    msg = new JtMessage ("JtHello");



    // Send the Message

    main.handleTrace ("main:sending a message (JtHello) to the helloWorld object ...");
    reply = (String) main.sendMessage ("helloWorld", msg);

    // Print the reply message (Greeting)
    System.out.println (reply);    

    // Remove helloWorld

    main.removeObject ("helloWorld");

        

  }

}



