
package Jt;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

/**
  * Handles input streams.
  */


public class JtInputStream extends JtObject {


  InputStream inputStream = null;
  //boolean createdir = true;
  byte[] buffer;
  public final static int BUFFER_SIZE = 1024;  // Buffer size (read_file)
  public final static int MAX_LENGTH = 1024 * 50;  // Max length(read_file)
  int bufferSize = BUFFER_SIZE;

  public JtInputStream() {
  }

   /**
    * Specifies the input stream.
    * @param inputStream input stream
    */

   public void setInputStream(Object inputStream) {
    this.inputStream = (InputStream) inputStream;
   }

   /**
    * Returns the input stream.
    */

   public Object getInputStream() {
    return inputStream;
   }

  /**
    * Return the buffer size.
    */

  public int getBufferSize () {
    return bufferSize;
  }

  /**
    * Sets the buffer size. The default value for this attribute is 1024.
    * @param bufferSize buffer size
    */

  public void setBufferSize (int bufferSize) {
    this.bufferSize = bufferSize;
  }


  // open operation

/*
  public void open () {

     try {
	ostream = new FileOutputStream (name);
     } catch (Exception e) {
	handleException (e);
     }
  }
*/

  byte [] getBuffer ()
  {
    return (buffer);
  }


  // read_file: read from input stream

  private void read_stream (Object reply_to)
  {
    InputStream istream;
    File file;
    int len, i;
    JtMessage msg;
    JtBuffer buff = new JtBuffer ();

    if (inputStream == null)
      return;

    istream = inputStream;

    byte buf [] = new byte [bufferSize];

    byte[] buffer1;

    try {
      //istream = new FileInputStream (name);

      handleTrace ("read_stream:available:" +
        istream.available ());

      while ((len = istream.read (buf, 0, bufferSize)) > 0) 
      {
 
        handleTrace ("read_stream:" + len);
        buffer1 = new byte [len];

        i = 0;
	while (i < len) {
          buffer1[i] = buf[i];
	  i++;
	}

	buff.setBuffer (buffer1);
       
        msg = new JtMessage ();
        msg.setMsgId ("JtDATA_BUFFER");
        msg.setMsgContent (buff);
        
        // send messages to the reply_to object

        if (reply_to != null)
          this.sendMessage (reply_to, msg);
      }

      // istream.close (); check

    } catch (Exception e) {
      handleException (e);
    }


/*
    handleTrace ("read_file:");
    for (i = 0; i < offset; i++)
      System.out.print (new Character ((char) buffer[i]));
*/
    return;
  }

  // write operation

/*
  void write (byte buffer[], int len) {

     if (ostream == null)
        return;

     try {
        ostream.write (buffer, 0, len);
     } catch (Exception e) {
        handleException (e);
     }

  }
*/

  // Destroy operation

/*
  public void destroy ()  {
     if (ostream != null) 
	close ();

  }
*/
  
  // close operation

  void closeStream () {

     if (inputStream == null)
       return;

     try {
       inputStream.close ();
     } catch (Exception e) {
       handleException (e);
     }
  }


  /**
    * Process object messages.
   * <ul>
   * <li> JtREAD - Reads from the input stream, one buffer of data at a time. 
   * Each buffer is sent to the object specified by msgReplyTo (JtMessage).
   * <li> JtCLOSE - Closes the input stream. 
   * </ul>
   * @param message Jt Message
   */


  public Object processMessage (Object message) {

   String msgid = null;
   byte buffer[];
   //JtBuffer buf;
   File file;
   JtMessage e = (JtMessage) message;
   Object reply_to;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;


     if (msgid.equals ("JtREAD")) {
        reply_to = (Object) e.getMsgReplyTo ();
	read_stream (reply_to);
        return (null);
     }

     if (msgid.equals ("JtCLOSE") || msgid.equals ("JtREMOVE")) {
	closeStream ();
        return (null);
     }

     handleError ("JtInputStream.processMessage: invalid message id:" + msgid);
     return (null);

  }


  /**
    * Unit tests the messages processed by JtInputStream.
    */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;
    File tmp;
    JtFile jfile;
    FileInputStream istream = null;
    JtObject f;

    try {
      istream = new FileInputStream ("test.txt");
    } catch (Exception e) {
      e.printStackTrace ();
    }

    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    // Create JtInputStream using test.txt

    main.createObject ("Jt.JtInputStream", "istream");
    main.setValue ("istream", "inputStream", istream);
    main.setValue ("istream", "bufferSize", "2048");


    // Create output file (output)

    f = (JtObject) main.createObject ("Jt.JtFile", "file");
    main.setValue ("file", "name", "output");

    main.sendMessage ("file", new JtMessage ("JtOPEN"));

    // Read input stream, one buffer at a time. Send buffers to a file 

    msg = new JtMessage ("JtREAD");
    msg.setMsgReplyTo (f);

    main.sendMessage ("istream", msg);
    //main.setValue ("message", "msgId", "JtCLOSE");
    main.sendMessage ("file", new JtMessage ("JtCLOSE"));

    main.sendMessage ("istream", new JtMessage ("JtCLOSE"));
    main.removeObject ("istream");



  }

}


