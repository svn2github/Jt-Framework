package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;


/**
 * Jt Implementation of the Template Method pattern.
 */


abstract public class JtTemplateMethod extends JtObject {



  public JtTemplateMethod() {
  }


  /**
    * Define the skeleton of the processMessage algorithm, deferring some steps 
    * to the subclasses. 
    *
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
     data = e.getMsgData ();

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

   
          
     handleError ("JtTemplateMethod.processMessage: invalid message id:" + msgid);
     return (null);

  }


}
