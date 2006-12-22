
package Jt.axis;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;
import Jt.*;
import Jt.xml.*;
                    
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;


/**
  * Jt Adapter for the Axis API.
  */  

public class JtAxisAdapter extends JtObject {


  private String url = null;
  private Service  service = null;
  private Call     call    = null;
  private String remoteLogFile;


  public JtAxisAdapter() {
  }

  // Attributes



 /**
  * Specifies the service URL.
  * @param url service url
  */

  public void setUrl (String url) {
     this.url = url;
  }

 /**
   * Returns the service URL. 
   */

  public String getUrl () {
     return (url);
  }


 /**
   * Returns the name of the remote log file
   */

  public String getRemoteLogFile () {
    return (remoteLogFile);
  }


 /**
  * Specifies the name of the remote log file
  * @param remoteLogFile remote log file
  */

  public void setRemoteLogFile (String remoteLogFile) {
    this.remoteLogFile = remoteLogFile;
  }


  // Process object messages

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


     if (msgid.equals ("JtSCRIPT")) {

        return (processJtScript ((String) content));

     }

     if (msgid.equals ("JtREMOVE")) {
        return (null);
     }
          
     handleError ("JtAxisAdapter.processMessage: invalid message id:" + msgid);
     return (null);

  }


    private Object processJtScript (String xmlMsg)
    {

      String ret = null;
      JtXMLHelper xmlHelper = null;
      JtMessage msg = new JtMessage ("JtCONVERT_XML_TO_OBJECT");
      String xmlOutput = null;
      Object tmp, tmp1, output = null;
      JtIterator it;

   
        if (xmlMsg == null)
          return (null);

        try { // check xmlMsg

            
            if (service == null) {
                       
              service = new Service();
              //service.setMaintainSession (true);


              call    = (Call) service.createCall();
            
            if (url == null) {
              handleError ("processxmlMessage: invalid attribute value: url (null)");
              return (null);
            }
                  

            call.setTargetEndpointAddress( new java.net.URL(url) );
            call.setMaintainSession (true);

            // check 
            //call.setOperationName( new QName("http://example3.userguide.samples", "processMessage") );
            call.setOperationName("processMessage");

            call.addParameter( "arg1", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType( org.apache.axis.encoding.XMLType.XSD_STRING );
            }
            ret = (String) call.invoke( new Object[] { xmlMsg } );
            
        } catch (Exception e) {
            handleException (e);
            return (null);
        }


        if (xmlHelper == null)
          xmlHelper = new JtXMLHelper ();

        // Convert XML message back to its object
        // representation

        msg.setMsgContent (ret);
        //handleTrace ("JtAxisAdapter returned ...." + ret);


        tmp = sendMessage (xmlHelper, msg);


        // If it is a list, an exception was detected

        if (tmp instanceof JtList) {

          it = (JtIterator) getValue (tmp, "iterator");

          if (it == null)
            return (null);
          
          for (;;) {

            //tmp1 = sendMessage (it, new JtMessage ("JtNEXT"));
            tmp1 =  it.processMessage (new JtMessage ("JtNEXT"));

            if (tmp1 == null)
              break;
            if (tmp1 instanceof JtRemoteException) {
              handleError ("JtAxisAdapter.processJtScript: remote exception detected");

              handleWarning ("<Remote Exception>\n" + 
                ((JtRemoteException) tmp1).getTrace () + "</Remote Exception>\n"); 
              //setValue (this, "objException", tmp1);             
            } else
              output = tmp1; // check size
              
          }
          return (output); // check          
        } else
          return (tmp);
    }


/**
   * Unit tests all the message processed by JtAxisAdapter
   */


  public static void main(String[] args) {

    JtObject main = new JtObject ();
    JtMessage msg;
    JtAxisAdapter adapter;
    String st = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Object>\n" +
                "<classname>Jt.JtList</classname>\n" +  
                "<Object>\n" +
                "<classname>Jt.JtMessage</classname>\n" + 
                "<msgId>JtCREATE_OBJECT</msgId>\n" +
                "<msgContent>Jt.JtObject</msgContent>\n" +
                "<msgData>test</msgData>\n" +
                "</Object>\n";




    //main.setObjTrace (1);
    //main.setLogFile ("log.txt");

    msg = new JtMessage ();


    msg.setMsgId ("JtSCRIPT");
    msg.setMsgContent ("Hello world");


    // Create template object

    adapter = (JtAxisAdapter) main.createObject ("Jt.service.JtAxisAdapter", "adapter");
    main.setValue (adapter, "url",
      "http://localhost:8080/axis/services/MyService");       

    main.sendMessage (adapter, msg);


  }

}


