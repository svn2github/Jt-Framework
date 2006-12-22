// Data Access Object


package Jt.examples;

import Jt.*;


import java.util.*;
import java.text.*;

public class DAOMember extends JtDAO   {
private String email;
private String firstname;
private String lastname;
private int status;
private String subject;
private String comments;
private Date tstamp;
private int email_flag;
private Date mdate;
private String location;
private boolean initted = false;

  public DAOMember() {
  }

  public void setEmail_flag (int newEmail_flag) {
    email_flag = newEmail_flag;
  }

  public int getEmail_flag () {
    return (email_flag);
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

  public void setMdate (Date mdate) {
    this.mdate = mdate;
  }

  public Date getMdate () {
    return mdate;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation (String location) {
    this.location = location;
  }


  public void mapAttributes () {

    JtMessage msg;

    msg = new JtMessage ();

    // Set attribute/column mappings

    if (getValue (this, "configFile") != null)
      return;

    // Use the following fallback mappings (attribute/column)
    // if the config file is not present

        setValue (msg, "msgContent", "email");
        setValue (msg, "msgData", "email");
        msg.setMsgId ("JtMAP_ATTRIBUTE");
        processMessage (msg);

        setValue (msg, "msgContent", "status");
        setValue (msg, "msgData", "status");
        processMessage (msg);

        setValue (msg, "msgContent", "firstname");
        setValue (msg, "msgData", "firstname");
        processMessage (msg);

        setValue (msg, "msgContent", "lastname");
        setValue (msg, "msgData", "lastname");
        processMessage (msg);


        setValue (msg, "msgContent", "subject");
        setValue (msg, "msgData", "subject");
        processMessage (msg);


        setValue (msg, "msgContent", "comments");
        setValue (msg, "msgData", "comments");
        processMessage (msg);


        setValue (msg, "msgContent", "email_flag");
        setValue (msg, "msgData", "email_flag");
        processMessage (msg);


        setValue (msg, "msgContent", "tstamp");
        setValue (msg, "msgData", "tstamp");
        processMessage (msg);


        setValue (msg, "msgContent", "mdate");
        setValue (msg, "msgData", "mdate");
        processMessage (msg);


        setValue (msg, "msgContent", "location");
        setValue (msg, "msgData", "location");
        processMessage (msg);


  }
  // processMessageEvent: process messages

  public Object processMessage (Object event) {
  String content;
  String query;
  JtMessage e = (JtMessage) event;

   if (!initted) {
     initted = true;
     mapAttributes ();
   }
    // Let the superclass handle all the messages

    return (super.processMessage (event));


  }

  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg, msg1;
    Integer count;
    Date date = new Date ();
    DateFormat df = DateFormat.getDateInstance();
    DAOMember tmp;
    Exception ex;
    Object tmp1;


    //main.setObjTrace (1);
    main.createObject ("Jt.examples.DAOMember", "member");
    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");

    main.setValue ("member", "key", "email");
    main.setValue ("member", "table", "member");

    //main.setValue ("member", "tstamp", "December 12, 2004");
    //main.setValue ("member", "tstamp", df.format (date));

    main.setValue ("member", "tstamp", new Date ());
    main.setValue ("member", "email", "xxx@fsw.com");
    main.setValue ("member", "status", "2005");
    main.setValue ("member", "email_flag", "100");


    //main.sendMessage ("member", "message");

    main.setValue ("message", "msgId", "JtINSERT");
    tmp1 = main.sendMessage ("member", "message");

    if (tmp1 == null){ 
      System.out.println ("JtINSERT: FAIL");
    } else if (tmp1 instanceof Integer) {
      if (((Integer) tmp1).intValue () != 1)
        System.out.println ("JtINSERT: FAIL");
      else
        System.out.println ("JtINSERT: PASS");
    } else {
    ex = (Exception) main.getValue ("member", "objException");
    if (ex == null) {
      main.setValue ("member", "email", "xxx@fsw.com");
      //main.setValue ("member", "key", "email");
      //main.setValue ("member", "table", "members");

      main.setValue ("message", "msgId", "JtFIND");
      tmp = (DAOMember) main.sendMessage ("member", "message");

      if (tmp == null) {
        System.out.println ("JtINSERT: FAIL");
      } else 
        System.out.println ("JtINSERT: PASS");

    } else
      System.out.println ("JtINSERT: FAIL");
    }

    main.setValue ("member", "email", "xxx@fsw.com");
    main.setValue ("member", "key", "email");
    main.setValue ("member", "table", "member");

    main.setValue ("message", "msgId", "JtFIND");
    tmp = (DAOMember) main.sendMessage ("member", "message");


    ex = (Exception) main.getValue ("member", "objException");

    if (ex != null) {
        System.out.println ("JtFIND: FAIL");
    } else
      if (tmp != null)
        System.out.println ("JtFIND: PASS");
      else
        System.out.println ("JtFIND: FAIL");


    main.setValue ("message", "msgId", "JtPRINT");
    main.sendMessage ("member", "message");


    main.setValue ("message", "msgId", "JtCLEAR");
    main.sendMessage ("member", "message");


    main.setValue ("member", "status", "2006");
    main.setValue ("member", "email_flag", "101");

    main.setValue ("message", "msgId", "JtUPDATE");
    tmp1 = main.sendMessage ("member", "message");

    if (tmp1 == null){ 
      System.out.println ("JtUPDATE: FAIL");
    } else if (tmp1 instanceof Integer) {
      if (((Integer) tmp1).intValue () != 1)
        System.out.println ("JtUPDATE: FAIL");
      else
        System.out.println ("JtUPDATE: PASS");
    } else {
    ex = (Exception) main.getValue ("member", "objException");
    if (ex == null)
      System.out.println ("JtUPDATE: PASS");
    else
      System.out.println ("JtUPDATE: FAIL");
    }

    main.setValue ("member", "email", "xxx@fsw.com");
    //main.setValue ("member", "status", "2005");
    //main.setValue ("member", "email_flag", "100");

    //main.sendMessage ("member", "message");

    main.setValue ("message", "msgId", "JtDELETE");
    tmp1 = main.sendMessage ("member", "message");

    if (tmp1 == null){ 
      System.out.println ("JtDELETE: FAIL");
    } else if (tmp1 instanceof Integer) {
      if (((Integer) tmp1).intValue () != 1)
        System.out.println ("JtDELETE: FAIL");
      else
        System.out.println ("JtDELETE: PASS");
    } else {

    ex = (Exception) main.getValue ("member", "objException");
    if (ex == null) {
      main.setValue ("member", "email", "xxx@fsw.com");
      //main.setValue ("member", "key", "email");
      //main.setValue ("member", "table", "members");

      main.setValue ("message", "msgId", "JtFIND");
      tmp = (DAOMember) main.sendMessage ("member", "message");

      if (tmp == null) {
        System.out.println ("JtDELETE: PASS");
      } else 
        System.out.println ("JtDELETE: FAIL");
    } else
      System.out.println ("JtDELETE: FAIL");
    }


  }


} 