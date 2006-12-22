package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;


/**
 * Jt Implementation of the Abstract Factory pattern.
 */


public abstract class JtAbstractFactory extends JtObject {


  private Object family;

  public JtAbstractFactory() {
  }

/**
  * Specifies the object family.
  *
  * @param family family
  */

  public void setFamily (Object family) {
     this.family = family; 

  }

/**
  * Returns the object family.
  */

  public Object getFamily () {
     return (family);
  }




  /**
    * Process object messages. 
    * @param event Jt Message    
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   Object content;

 
     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     content = e.getMsgContent();


     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }


     if (family == null) {
       handleError 
        ("processMessage: family attribute needs to be set" + msgid);
       return (null);
     }

     handleError ("processMessage: invalid message id:" + msgid);
     return (null);


  }



}

