package Jt.jndi;

//import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import java.util.Properties;
import java.util.*;
import Jt.*;


/**
 * Jt JNDI Adapter  
 */

public class JtJNDIAdapter extends JtAdapter {

  private String url = "t3://localhost:7001"; // Default value (Weblogic)
  private String user = null;
  private String password = null;
  private String factory = "weblogic.jndi.WLInitialContextFactory"; // Default value (Weblogic)
  private boolean initted = false;
  private transient Context ctx = null;

 /**
  * Specifies the URL (initial context).
  * @param url url
  */

  public void setUrl (String url) {
     this.url = url; 

  }

 /**
   * Returns the url. 
   */

  public String getUrl () {
     return (url);
  }


 /**
  * Specifies the user (initial context).
  * @param user user
  */

  public void setUser (String user) {
     this.user = user; 
  }

 /**
   * Returns the user. 
   */

  public String getUser () {
     return (user);
  }


 /**
  * Specifies the password (initial context).
  * @param password password
  */

  public void setPassword (String password) {
     this.password = password; 
  }


 /**
   * Returns the password. 
   */

  public String getPassword () {
     return (password);
  }

 /**
  * Specifies the context factory (initial context).
  * @param factory factory
  */

  public void setFactory (String factory) {
     this.factory = factory; 
  }


 /**
   * Returns the context factory. 
   */


  public String getFactory () {
     return (factory);
  }



  // lookup 

  private Object lookup (String jndiName) {

    Object objref = null;

     
    try {

      if (!initted) {
        initted = true;
        ctx = getInitialContext();
      }

      objref = ctx.lookup (jndiName);
      

    } catch (Exception e) {
      handleException (e);
    }
    return (objref);

  }


  private Object test () {

    JtMessage msg = new JtMessage ("JtLOOKUP");

    msg.setMsgContent ("JtSessionFacade");

    return (processMessage (msg));

  }

  /**
    * Process object messages.
    * <ul>
    * <li> JtLOOKUP - retrieves the named object specified by msgContent.
    * </ul>
    * @param event message
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

     // Locate the service (JtACTIVATE)

     if (msgid.equals ("JtLOOKUP")) {
             
        return (lookup ((String) content));

     }


     if (msgid.equals ("JtTEST")) {
             
        return (test ());

     }


     if (msgid.equals ("JtREMOVE")) {
             
        return (null);

     }
          
     handleError ("processMessage: invalid message id:" + msgid);
     return (null);

  }


  /**
   * Gets an initial context.
   *
   */

  private Context getInitialContext() throws Exception {
    Properties p = new Properties();
    p.put(Context.INITIAL_CONTEXT_FACTORY,
          factory);
    p.put(Context.PROVIDER_URL, url);
    if (user != null) {
      //System.out.println ("user: " + user);
      p.put(Context.SECURITY_PRINCIPAL, user);
      if (password == null) 
        password = "";
      p.put(Context.SECURITY_CREDENTIALS, password);
    } 
    return new InitialContext(p);
  }


 /**
   * Unit tests all the messages processed by JtJNDIAdapter. 
   */

  public static void main (String[] args) {
    JtObject main;
    JtJNDIAdapter adapter;
    Object entry;

    main = new JtObject ();


    // Create an instance of JtJNDIAdapter

    adapter = (JtJNDIAdapter) main.createObject ("Jt.jndi.JtJNDIAdapter", "adapter");
    
    // Send a TEST message to the adapter

    entry = (Object) main.sendMessage ("adapter", new JtMessage ("JtTEST"));


    if (entry != null)
 	System.out.println  ("JtJNDIAdapter: GO");  
    else
  	System.out.println  ("JtJNDIAdapter: FAIL");  


    // Destroy the JtJNDIAdapter instance

    main.removeObject ("adapter");


  }
}
