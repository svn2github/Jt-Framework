

package Jt;


/**
  * This interface implements the messaging pattern. JtInterface defines a single method
  * to process a message and return a reply.
  * All the Jt Objects need to implement this interface directly or indirectly by subclassing
  * JtOject (or one of its subclasses). JtObject implements the JtInterface.    
  */

public interface JtInterface  {

/**
  * Process a Jt message and return a reply. Message and reply objects are used to pass information
  * to/from the object. 
  */

  Object processMessage (Object msg);  


}


