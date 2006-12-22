

// Illustrates the use of Jt Data Access Objects

  
package Jt.examples;

import Jt.*;
import Jt.xml.*;


import java.util.*;
import java.text.*;
import java.io.*;

public class DAOMembers extends JtDAO   {
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
private long lval;
private float fval;
private double dval;
private byte bval;
private boolean booleanv;

  public DAOMembers() {
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

  public byte getBval () {
    return (bval);
  }

  public void setBval (byte bval) {
    this.bval = bval;
  }

  public long getLval () {
    return (lval);
  }

  public void setLval (long lval) {
    this.lval = lval;
  }

  public boolean getBooleanv () {
    return (booleanv);
  }

  public void setBooleanv (boolean booleanv) {
    this.booleanv = booleanv;
  }

  public float getfval () {
    return (fval);
  }


  public void setDval (double dval) {
    this.dval = dval;
  }

  public double getdval () {
    return (dval);
  }


  public void setFval (float fval) {
    this.fval = fval;
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

  // ConfigFileExist - Verify if the configuration
  //                   file exists

  private boolean configFileExists (String configFile) {

    String fname;
    File file;
 
    //fname = (String) getValue (this, configFile);
    fname = configFile;

    if (fname == null)
      return false;

    file = new File (fname);
    return (file.exists ());
  }

  public void realize () {

    JtMessage msg;

    msg = new JtMessage ();

    // Set attribute/column mappings

    if (getValue (this, "configFile") != null)
      return;

    // Use the following fallback mappings (attribute/column)
    // if the config file is not present

    handleWarning ("DAOMembers: config file (DAOMembers.xml) not found.");
    handleWarning ("DAOMembers: fallback mappings will be used.");
    setValue (msg, "msgContent", "status");
    setValue (msg, "msgData", "status");
    setValue (msg, "msgId", "JtMAP_ATTRIBUTE");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "email");
    setValue (msg, "msgData", "email");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "mdate");
    setValue (msg, "msgData", "mdate");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "tstamp");
    setValue (msg, "msgData", "tstamp");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "email_flag");
    setValue (msg, "msgData", "email_flag");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "booleanv");
    setValue (msg, "msgData", "booleanv");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "fval");
    setValue (msg, "msgData", "fval");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "lval");
    setValue (msg, "msgData", "lval");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "dval");
    setValue (msg, "msgData", "dval");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "bval");
    setValue (msg, "msgData", "bval");
    sendMessage (this, msg);

  }

  // processMessageEvent: process messages

  public Object processMessage (Object event) {
  String content;
  String query;
  JtMessage e = (JtMessage) event;


    if (e == null ||  (e.getMsgId() == null)) {
      handleError ("processMessage:invalid message:" +
        e); 
      return (null);
    }
    // Let the superclass handle all the messages

    if (e.getMsgId().equals("JtREALIZE")) {
        super.processMessage (new JtMessage ("JtREALIZE"));
        //super.realize ();
	realize ();
        return (null);
    }

    return (super.processMessage (event));

  }

  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg, msg1;
    Integer count;
    Date date = new Date ();
    DateFormat df = DateFormat.getDateInstance();
    DAOMembers tmp, member;
    Exception ex;
    Object tmp1;
    File file;

    //main.setObjTrace (1);

    // Create DAO object

    member = (DAOMembers) main.createObject ("Jt.examples.DAOMembers", "member");
    msg = (JtMessage) main.createObject ("Jt.JtMessage", "message");


    // Set database key and table

    main.setValue (member, "key", "email");
    main.setValue (member, "table", "members");

    file = new File ("DAOMembers.xml");

    if (file.exists ())
      main.setValue (member, "configFile", "DAOMembers.xml");


    // Realize object

    //main.setValue (msg, "msgId", "JtREALIZE");
    main.sendMessage (member, new JtMessage ("JtREALIZE"));

    // Set JDBC driver and url  
 
    member.setValue ("db", "driver", "com.mysql.jdbc.Driver");
    member.setValue ("db", "url", "jdbc:mysql://localhost/test");
    member.setValue ("db", "user", "root");
    member.setValue ("db", "password", "123456");



    //main.setValue ("member", "tstamp", "December 12, 2004");
    //main.setValue ("member", "tstamp", df.format (date));

    // Set attribute/column mappings


    //main.setValue (member, "status", "2005");
    //main.setValue (member, "fval", "3.1");


    main.setValue (member, "email", "user@freedom.com");
    main.setValue (member, "tstamp", new Date ());
    main.setValue (member, "status", "2005");
    main.setValue (member, "email_flag", "100");
    main.setValue (member, "lval", "300000");
    main.setValue (member, "fval", "3.1");
    main.setValue (member, "dval", "4.1");
    main.setValue (member, "bval", "1");
    main.setValue (member, "booleanv", "true");

    // Insert object

    //main.setValue (msg, "msgId", "JtINSERT");
    tmp = (DAOMembers) main.sendMessage (member, new JtMessage ("JtINSERT"));

    if (tmp == null) { 
      System.out.println ("JtINSERT: FAIL");
    } else {
      System.out.println ("JtINSERT: PASS");
    }


    main.setValue (msg, "msgId", "JtCLEAR");
    main.sendMessage (member, msg);

    main.setValue (member, "email", "user@freedom.com");
    main.setValue (member, "key", "email");
    main.setValue (member, "table", "members");

    // Find object

    tmp = (DAOMembers) main.sendMessage (member, new JtMessage ("JtFIND"));

    if (tmp != null)
      System.out.println ("JtFIND: PASS");
    else
      System.out.println ("JtFIND: FAIL");

    main.sendMessage (member,  new JtMessage ("JtPRINT"));

    main.setValue (member, "status", "2006");
    main.setValue (member, "email_flag", "101");

    // Update object

    tmp = (DAOMembers) main.sendMessage (member, new JtMessage ("JtUPDATE"));


    if (tmp == null){ 
      System.out.println ("JtUPDATE: FAIL");
    } else  {
      System.out.println ("JtUPDATE: PASS");
    }

    main.setValue (msg, "msgId", "JtCLEAR");
    main.sendMessage (member, msg);

    main.setValue (member, "email", "user@freedom.com");
    main.setValue (member, "key", "email");
    main.setValue (member, "table", "members");

    main.setValue (msg, "msgId", "JtFIND");
    tmp = (DAOMembers) main.sendMessage (member, msg);

    ex = (Exception) main.getValue (member, "objException");

    if (ex != null) {
        System.out.println ("JtFIND: FAIL");
    } else
      if (tmp == null)
        System.out.println ("JtFIND: FAIL");


    main.sendMessage (member,  new JtMessage ("JtPRINT"));

    main.setValue (member, "email", "user@freedom.com");
    //main.setValue ("member", "status", "2005");
    //main.setValue ("member", "email_flag", "100");

    //main.sendMessage ("member", "message");

    // Delete object

    tmp = (DAOMembers) main.sendMessage (member, new JtMessage ("JtDELETE"));

    if (tmp == null){ 
      System.out.println ("JtDELETE: FAIL");
    } else {
      System.out.println ("JtDELETE: PASS");
    }

    main.sendMessage (member, new JtMessage ("JtREMOVE"));

  }


} 