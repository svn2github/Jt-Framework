

package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
 * Jt Implementation of the Builder pattern.
 */

public class JtBuilder extends JtObject {

  private Object builder;

  public JtBuilder () {
  }

/**
  * Specifies the builder.
  *
  * @param builder builder
  */

  public void setBuilder (Object builder) {
     this.builder = builder; 

  }

/**
  * Returns the builder.
  */

  public Object getBuilder () {
     return (builder);
  }



  /**
    * Process object messages by forwarding them to an object (builder) that
    * takes care of the building functionality. Different representations 
    * can be created.
    * <ul>
    * </ul>
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;
   Object data;


     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();
     //data = e.getMsgData ();


     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     if (builder == null) {
       handleError ("JtBuilder.process: the builder attribute needs to be set");
       return (null);
     }

     // Let the builder process the request

     return (sendMessage (builder, event));


  }

 
  /**
   * Unit tests the messages processed by JtBuilder.
   */


  public static void main(String[] args) {

    JtFactory factory = new JtFactory ();
    JtBuilder builder;


    // Create an instance of JtBuilder

    builder = (JtBuilder) factory.createObject ("Jt.JtBuilder", "builder");

    // Remove the object 

    factory.removeObject ("builder");


  }


}


