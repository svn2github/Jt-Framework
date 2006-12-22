package Jt;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Jt Adapter for the JavaMail API.
 */ 

public class JtMail extends JtObject {
private String from;
private String to;
private String message;
private String subject;
private String server;
private String cc;
private String attachment;
private String username;
private String password;
private Session ses = null;


   void activate () {

   if (server == null || from == null || to == null || 
	message == null) {
   	handleTrace ("JtMail: null attribute(s)");
	return; // check subject
   }
   try {
          Authenticator auth = null;

       if (ses == null) {
          if (username != null || password != null)
            auth = new SMTPAuthenticator();
          // Get system properties
          Properties props = System.getProperties(); 
          // Setup mail server 
          props.put("mail.smtp.host", server); 
          if (username != null || password != null)
            props.put("mail.smtp.auth", "true");


           // Get session 
           ses = Session.getDefaultInstance(props, auth); 

           if (this.getObjTrace() == 1)
             ses.setDebug(true);
        }

        // Define message 

        MimeMessage msg = new MimeMessage(ses); 
        msg.setFrom(new InternetAddress(from)); 
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); 
        if (cc != null && !cc.equals (""))
           msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc)); 
           
           msg.setSubject(subject);
           if (attachment == null)
             msg.setText(message);
           else {
             BodyPart messageBodyPart = new MimeBodyPart(); 
             // Fill the message 
             messageBodyPart.setText(message); 
             Multipart multipart = new MimeMultipart(); 
             multipart.addBodyPart(messageBodyPart); // Part two is attachment 
             messageBodyPart = new MimeBodyPart(); 
             DataSource source = new FileDataSource(attachment); 
             messageBodyPart.setDataHandler(new DataHandler(source)); 
             messageBodyPart.setFileName(attachment); 
             multipart.addBodyPart(messageBodyPart); 
             msg.setContent(multipart);

           }

 
           //msg.setText(message); 
           msg.setSentDate(new Date());


           // Send message 
          Transport.send(msg); 
   }
   catch (Exception e) {
     handleException (e);
   }
   }

  /**
    * Process object messages.
    * <ul>
    * <li> JtACTIVATE - Sends an email message using the JavaMail API
    * </ul>
    * @param message message
    */

  public Object processMessage (Object message) {
  String content;
  String query;
  JtMessage e = (JtMessage) message;


      if (e == null ||  (e.getMsgId() == null))
          return (null);

     // Remove this object
     if (e.getMsgId().equals ("JtREMOVE")) {
       return (null);     
     }

      // establish connection
      if (e.getMsgId().equals("JtACTIVATE")) {
	activate ();
	return (null);
      }

      handleError 
	("processMessage: invalid message id:"+
		e.getMsgId());
      return (null);
  }

  /**
    * Sets the "from" attribute (sender).
    */

  public void setFrom (String newFrom) {
    from = newFrom;
  }

  /**
    * Returns the "from" attribute (sender).
    */

  public String getFrom () {
    return (from);
  }


  /**
    * Sets the "to" attribute (recipient).
    */

  public void setTo (String newTo) {
    to = newTo;
  }

  /**
    * Gets the "to" attribute (recipient).
    */

  public String getTo () {
    return (to);
  }

  /**
   * Sets the CC attribute
   */

  public void setCc (String cc) {
    this.cc = cc;
  }

  /**
   * Returns the CC attribute.
   */

  public String getCc () {
    return (cc);
  }

  /**
   * Specifies the content of the email message.
   */

  public void setMessage (String newMessage) {
    message = newMessage;
  }


  /**
    * Returns the content of the email message.
    */

  public String getMessage () {
    return (message);
  }


  /**
    * Specifies the subject of the email message.
    */

  public void setSubject (String newSubject) {
    subject = newSubject;
  }


  /**
    * Returns the subject of the email message.
    */

  public String getSubject () {
    return (subject);
  }

  /**
    * Sets the user name (for authentication).
    */

  public void setUsername (String newUsername) {
    username = newUsername;
  }


  /**
    * Returns the user name (for authentication).
    */

  public String getUsername () {
    return (username);
  }

  /**
    * Sets the password (for authentication).
    */

  public void setPassword (String newPassword) {
    password = newPassword;
  }


  /**
    * Returns the password (for authentication).
    */

  public String getPassword () {
    return (password);
  }

  /**
    * Specifies the SMTP server.
    */

  public void setServer (String newServer) {
    server = newServer;
  }

  /**
    * Returns the SMTP server.
    */

  public String getServer () {
    return (server);
  } 

  /**
    * Specifies the attachment.
    */

  public void setAttachment (String attachment) {
    this.attachment = attachment;
  }


  /**
    * Returns the attachment.
    */

  public String getAttachment () {
    return (attachment);
  } 


  /**
    * Unit tests the messages processed by JtMail.
    */

  public static void main (String[] args) {
    JtObject main;
    JtMail jtmail;

    main = new JtObject ();
    //main.setValue (main, "objTrace", "1");
    main.createObject ("Jt.JtMessage", "message");

    // Create JtMail object

    jtmail = (JtMail) main.createObject ("Jt.JtMail", "jtmail");


    // Set JtMail attributes
    
    main.setValue ("jtmail", "from", "Jt@fsw.com");

    main.setValue ("jtmail", "subject", "Hello World!");
    main.setValue ("jtmail", "message", "Jt message");


    // remove comment from the following line if
    // an attachement is required
    // main.setValue ("jtmail", "attachment", "test.txt");

    main.setValue ("jtmail", "to", "Jt@fsw.com");


    // Activate JtMail (send email)

    main.setValue ("message", "msgId", "JtACTIVATE");
    main.sendMessage ("jtmail", "message");


    // Check for exceptions (if any)

    if (jtmail.getObjException() == null)
 	System.out.println  ("JtMail: GO");  
    else
  	System.out.println  ("JtMail: FAIL");  
   

    //main.handleTrace ("jtmail:Exception:" 
    //  + jtmail.getObjException());


    // Remove JtMail object

    main.removeObject ("jtmail");


  }

/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
private class SMTPAuthenticator extends javax.mail.Authenticator
{

    public PasswordAuthentication getPasswordAuthentication()
    {
        //String username = SMTP_AUTH_USER;
        //String password = SMTP_AUTH_PWD;
        return new PasswordAuthentication(username, password);
    }
}
}