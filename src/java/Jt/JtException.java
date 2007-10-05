

package Jt;


/**
  * Represents Jt Exceptions. This type of exception is generated when an error is detected.
  */

public class JtException extends Exception {


   private static final long serialVersionUID = 1L;
// private String trace;

   public JtException () {}


   public JtException (String s) {
	super (s);
   }

}
