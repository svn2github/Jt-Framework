package Jt.examples;

import Jt.*;
import Jt.xml.*;


import java.util.*;
import java.text.*;
import java.io.*;


/**
  * Data Access Object (DAO) example. This subclass of JtDAO contains the 
  * attributes to be mapped to database columns.
  */

public class DAOExample extends JtDAO   {
private String email;
private String name;
private int status;
private Date tstamp;
private boolean booleanv;

  public DAOExample () {
  }

  public int getStatus () {
    return (status);
  }


  public void setStatus (int newStatus) {
    status = newStatus;
  }

  public boolean getBooleanv () {
    return (booleanv);
  }

  public void setBooleanv (boolean booleanv) {
    this.booleanv = booleanv;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail (String newEmail) {
    email = newEmail;
  }

  public void setName (String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }


  public void setTstamp (Date tstamp) {
    this.tstamp = tstamp;
  }

  public Date getTstamp () {
    return tstamp;
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

    handleWarning ("DAOExample: config file (DAOExample.xml) not found.");
    handleWarning ("DAOExample: fallback mappings will be used.");
    setValue (msg, "msgContent", "status");
    setValue (msg, "msgData", "status");
    setValue (msg, "msgId", "JtMAP_ATTRIBUTE");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "email");
    setValue (msg, "msgData", "email");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "name");
    setValue (msg, "msgData", "name");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "tstamp");
    setValue (msg, "msgData", "tstamp");
    sendMessage (this, msg);

    setValue (msg, "msgContent", "booleanv");
    setValue (msg, "msgData", "booleanv");
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
	realize ();
        return (null);
    }

    return (super.processMessage (event));

  }

  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg = new JtMessage ();
    Date date = new Date ();
    DAOExample tmp, member;
    Exception ex;
    Object tmp1;
    File file;

    // Create DAO object

    member = (DAOExample) main.createObject ("Jt.examples.DAOExample", "member");


    // Set database key and table

    main.setValue (member, "key", "email");
    main.setValue (member, "table", "members");

    file = new File ("DAOExample.xml");

    if (file.exists ())
      main.setValue (member, "configFile", "DAOExample.xml");


    // Realize object

    //main.setValue (msg, "msgId", "JtREALIZE");
    main.sendMessage (member, new JtMessage ("JtREALIZE"));

    // Set JDBC driver and url  
 
    member.setValue ("db", "driver", "com.mysql.jdbc.Driver");
    member.setValue ("db", "url", "jdbc:mysql://localhost/test");
    member.setValue ("db", "user", "root");
    member.setValue ("db", "password", "123456");

    // Set Object attributes

    main.setValue (member, "email", "user@freedom.com");
    main.setValue (member, "name", "John Dow");
    main.setValue (member, "tstamp", new Date ());
    main.setValue (member, "status", "2005");
    main.setValue (member, "booleanv", "true");

    // Insert object (object is added to the database)

    tmp = (DAOExample) main.sendMessage (member, new JtMessage ("JtINSERT"));

    if (tmp == null) { 
      System.out.println ("JtINSERT: FAIL");
    } else {
      System.out.println ("JtINSERT: PASS");
    }


    main.setValue (member, "email", "user@freedom.com");
    main.setValue (member, "key", "email");
    main.setValue (member, "table", "members");

    // Find object

    tmp = (DAOExample) main.sendMessage (member, new JtMessage ("JtFIND"));

    if (tmp != null)
      System.out.println ("JtFIND: PASS");
    else
      System.out.println ("JtFIND: FAIL");

    main.sendMessage (member,  new JtMessage ("JtPRINT"));

    main.setValue (member, "status", "2006");
    main.setValue (member, "name", "Jane Dow");
    main.setValue (member, "booleanv", "false");

    // Update object

    tmp = (DAOExample) main.sendMessage (member, new JtMessage ("JtUPDATE"));


    if (tmp == null){ 
      System.out.println ("JtUPDATE: FAIL");
    } else  {
      System.out.println ("JtUPDATE: PASS");
    }

    main.setValue (member, "email", "user@freedom.com");

    main.setValue (msg, "msgId", "JtFIND");
    tmp = (DAOExample) main.sendMessage (member, msg);

    ex = (Exception) main.getValue (member, "objException");

    if (ex != null) {
        System.out.println ("JtFIND: FAIL");
    } else
      if (tmp == null)
        System.out.println ("JtFIND: FAIL");


    main.sendMessage (member,  new JtMessage ("JtPRINT"));

    main.setValue (member, "email", "user@freedom.com");

    // Delete object

    tmp = (DAOExample) main.sendMessage (member, new JtMessage ("JtDELETE"));

    if (tmp == null){ 
      System.out.println ("JtDELETE: FAIL");
    } else {
      System.out.println ("JtDELETE: PASS");
    }

    // Remove the DAO. This closes the database connection

    main.sendMessage (member, new JtMessage ("JtREMOVE"));

  }


} 