package Jt.examples;

import Jt.*;


import java.util.*;
import java.text.*;

public class Member extends JtObject   {

private static final long serialVersionUID = 1L;
private String email;
private String firstname;
private String lastname;
private int status;
private String subject;
private String comments;
private Date tstamp;

  public Member() {
  }

  public int getStatus () {
    return (status);
  }

  public void setStatus (int newStatus) {
    status = newStatus;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail (String newEmail) {
    email = newEmail;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject (String newSubject) {
    subject = newSubject;
  }

  public String getComments() {
    return comments;
  }

  public void setComments (String newComments) {
    comments = newComments;
  }

  public void setFirstname (String newFirstname) {
    firstname = newFirstname;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setLastname (String newLastname) {
    lastname = newLastname;
  }

  public String getLastname() {
    return lastname;
  }


  public void setTstamp (Date tstamp) {
    this.tstamp = tstamp;
  }

  public Date getTstamp () {
    return tstamp;
  }

  /**
    * Process object messages.
    * <ul>
    * <li> JtACTIVATE - Activates this object to locate a service.
    * <li> JtTEST - Tests the messages processes by JtServiceLocator
    * </ul>
    * @param event message
    */

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;
   JtValueObject valueObj;
   JtMessage msg;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     //content = e.getMsgContent();

     if (msgid.equals ("JtREMOVE")) {
             
        return (null);

     }

     if (msgid.equals ("JtPRINT_OBJECT")) {
         
         return (super.processMessage(event));

     }
     
     if (msgid.equals ("JtVALUE_OBJECT")) { 
        valueObj = new JtValueObject ();
        setValue (valueObj, "subject", this);
        msg = new JtMessage ("JtACTIVATE");
        //msg.setMsgContent (this);

        sendMessage (valueObj, msg);   
        return (valueObj);

     }
          
     handleError ("processMessage: invalid message id:" + msgid);
     return (null);

  }

  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    //JtMessage msg, msg1;
    //Integer count;
    Date date = new Date ();
    DateFormat df = DateFormat.getDateInstance();


    main.setObjTrace (1);
    main.createObject ("Jt.examples.Member", "member");
    main.createObject ("Jt.JtMessage", "message");
    main.setValue ("message", "msgId", "JtPRINT_OBJECT");
    //main.setValue ("member", "tstamp", "December 12, 2004");
    main.setValue ("member", "tstamp", df.format (date));

    main.sendMessage ("member", "message");
  

  }


} 