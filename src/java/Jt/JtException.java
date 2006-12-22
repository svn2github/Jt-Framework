

package Jt;


/**
  * Represents Jt Exceptions. This type of exception is generated when an error is detected.
  */

public class JtException extends Exception {

// private String trace;

   public JtException () {}

/*
   public void setTrace (String trace) {
     this.trace = trace;
   }

   public String getTrace () {
     return (trace);
   }
*/

   public JtException (String s) {
	super (s);
   }

}
