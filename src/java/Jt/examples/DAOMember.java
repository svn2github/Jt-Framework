// Data Access Object


package Jt.examples;

import Jt.*;

import java.lang.reflect.*;
import java.beans.*;
import java.util.*;


public class DAOMember extends JtDAO   {

private static final long serialVersionUID = 1L;
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
  //String content;
  //String query;
  JtMessage e = (JtMessage) event;


   if (e == null ||  (e.getMsgId() == null))
     return (null);

   if (!initted) {
     initted = true;
     mapAttributes ();
   }

   if (e.getMsgId().equals("UPDATE_RECORD")) {
	updateRecord ();
	return (this);
   }

   
   if (e.getMsgId().equals("INPUT_RECORD")) {
		inputRecord ();
		return (this);
   }
    // Let the superclass handle all the messages

    return (super.processMessage (event));


  }


  private String readValue () {
    JtMessage msg = new JtMessage ("JtACTIVATE");
    JtKeyboard keyboard = new JtKeyboard ();
    String reply;

    reply = (String) sendMessage (keyboard, msg);
    return (reply);
  }

  private void updateRecord () {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   //Class p;
   Method m;
   BeanInfo info = null;
   Object value;
   String input = null;
   //JtMessage msg = new JtMessage ("JtUPDATE");


     try {

       info = Introspector.getBeanInfo(
              this.getClass (), this.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return;
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {
//       System.out.print ("Attribute:" + 
//            prop[i].getName());
       //p = prop[i].getPropertyType();
       
       try {
         m = prop[i].getReadMethod ();
         if (m == null) {
           handleError 
	     ("JtDAO: getReadMethod returned null");
             return;
         }

         value = m.invoke (this, null);

         System.out.print (this.getClass ().getName () + "." +
             prop[i].getName() + "(" + value + "):");
         input = readValue ();
         if (input == null || input.equals ("")) {
           continue;
         }
         setValue (this, prop[i].getName(), input);
        } catch (Exception e) {
         handleException(e);
         return;
        }
      }
      //sendMessage (this, msg);

   }  

  private void inputRecord () {

	   //Object args[];
	   PropertyDescriptor[] prop;
	   int i;
	   //Class p;
	   Method m;
	   BeanInfo info = null;
	   Object value;
	   String input = null;
	   //JtMessage msg = new JtMessage ("JtUPDATE");


	     try {

	       info = Introspector.getBeanInfo(
	              this.getClass (), this.getClass ().getSuperclass());
	     } catch(Exception e) {
	        handleException (e);
	        return;
	     }

	     prop = info.getPropertyDescriptors();
	     for(i = 0; i < prop.length; i++) {
//	       System.out.print ("Attribute:" + 
//	            prop[i].getName());
	       //p = prop[i].getPropertyType();
	       
	       try {
	         m = prop[i].getReadMethod ();
	         if (m == null) {
	           handleError 
		     ("JtDAO: getReadMethod returned null");
	             return;
	         }

	         value = m.invoke (this, null);

	         if (value != null)
	           System.out.print (this.getClass ().getName () + "." +
	                 prop[i].getName() + "(" + value + "):");
	         else
	           System.out.print (this.getClass ().getName () + "." +
	             prop[i].getName()+":");
	         
	         input = readValue ();
	         if (input == null || input.equals ("")) {
	           continue;
	         }
	         setValue (this, prop[i].getName(), input);
	        } catch (Exception e) {
	         handleException(e);
	         return;
	        }
	      }
	      //sendMessage (this, msg);

	   }
  
  
  // Test program

  public static void main(String[] args) {

    JtObject main = new JtFactory ();

    DAOMember tmp;
    Exception ex;
    Object tmp1;


    //main.setObjTrace (1);
    main.createObject ("Jt.examples.DAOMember", "member");
    main.createObject ("Jt.JtMessage", "message");

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

/*
    main.setValue ("message", "msgId", "UPDATE_RECORD");
    main.sendMessage ("member", "message");
*/
    main.setValue ("message", "msgId", "JtUPDATE");
    tmp1 = main.sendMessage ("member", "message");

    main.setValue ("message", "msgId", "JtPRINT");
    main.sendMessage ("member", "message");


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