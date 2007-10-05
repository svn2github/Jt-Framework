

package Jt;
import java.net.*;
import java.io.*;


/**
  * Class used to handle URLs.
  */

public class JtURL extends JtObject {

  private static final long serialVersionUID = 1L;
  private String url;
  InputStream stream;
  BufferedInputStream bstream;
  private URL someurl;

  public JtURL() {
  }

/**
  * Specifies the URL.
  * @param newUrl url
  */

  public void setUrl(String newUrl) {
    url = newUrl;
  }


/**
  * Returns the URL.
  */
  public String getUrl() {
    return url;
  }


  String download () {
  //int available;
  String line;
  BufferedReader d = null;  
  URLConnection connection;
  StringBuffer result = new StringBuffer();
 
       if (url == null)
          return (null);
 
       try {
          someurl = new URL (url);
          connection = someurl.openConnection ();
          connection.connect ();

          //stream = someurl.openStream ();
          stream = connection.getInputStream ();

          d = new BufferedReader (new InputStreamReader (stream));

	  while ((line  = d.readLine ()) != null) {           
            //System.out.println (line);
            result.append (line + "\n"); 
          }

          //System.out.println (result);

	  // close streams
	  if (stream != null)
		stream.close ();
	  if (d != null)
		d.close ();
          return (result.toString ());
        } catch (Exception ex) {
          handleException (ex);
          return (null);
        } 
     } 

  /**
    * Process object messages. 
    * <ul>
    * <li> JtDOWNLOAD - Download the URL and returns its content.
    * </ul>
    * @param message Jt Message    
    */

  public Object processMessage (Object message) {

   String msgid = null;
   //byte buffer[];
   //File file;
   JtMessage e = (JtMessage) message;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;


     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (null);     
     }

     if (msgid.equals ("JtDOWNLOAD")) {
       return (download ());
     }
     handleError ("JtURL.processMessage: invalid message id:" + msgid);
     return (null);
  }


  /**
    * Unit Tests the messages processed by JtURL. Usage: java Jt.JtURL url    
    */

     public static void main (String args[]) {
        JtObject main = new JtObject ();
        JtMessage msg;

        if (args.length < 1) {
	   System.err.println ("Usage: java Jt.JtURL url");
	   System.exit (1);
	}

        main.createObject ("Jt.JtURL", "url");
        main.setValue ("url", "url", args[0]);

        System.err.println ("downloading "+ args[0] + " .....");
        msg = new JtMessage ("JtDOWNLOAD");

        System.err.println (main.sendMessage ("url", msg));
        main.removeObject ("url");
	
     }
}