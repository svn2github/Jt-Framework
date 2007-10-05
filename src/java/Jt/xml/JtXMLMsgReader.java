

package Jt.xml;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import Jt.*;


/**
  *   Read Jt messages from a XML file. This class is being deprecated. 
  */

public class JtXMLMsgReader extends JtObject 
    implements ContentHandler {

  private static final long serialVersionUID = 1L;
  private XMLReader reader = null;
  protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
  private String uri;
  private String string;
  private JtMessage msg = null;
  private String elemName = null;
  //String elemValue = null;
  Object replyTo = null;
  StringBuffer buffer = null;
  Object output = null;
  JtXMLHelper xmlHelper = null;
  private String parserName = DEFAULT_PARSER_NAME;
  

  public JtXMLMsgReader() {
  }


 /**
  * Specifies the parser.
  * @param parserName parse
  */

  void setParserName (String parserName) {
    this.parserName = parserName;
  
  }


 /**
   * Returns the parser. 
   */

  String getParserName () {
    return (parserName);  
  }


  // URI

  public void setUri (String uri) {
     this.uri = uri; 
  }

  public String getUri () {
     return (uri);
  }

  // Use a String (XML format) instead of a URI

  public void setString (String string) {
     this.string = string; // void operation
  }

  public String getString () {
     return (string);
  }

  /** Start element (SAX API). */
  public void startElement(String uri, String local, String raw,
                             Attributes attrs) throws SAXException {

    if ("".equals (uri)) {
      handleTrace ("Start element: " + raw);

      if ("JtMessage".equals (raw)) {
        msg = new JtMessage ();
        return;    
      }
      
      elemName = raw;
      //elemValue = null;
      buffer = null;

    }else
      handleTrace ("Start element: {" + uri + "}" + local);
    
  } 

  Object sendReply (JtMessage msg ) {
    if (msg != null && replyTo != null )
      return (sendMessage (replyTo, msg));
    return (null);
  } 

  public void endElement(String uri,
                        String name,
                        String qName)
                throws SAXException {


    
//    elemName = null;
//    elemValue = null;
//    buffer = null;

    if ("".equals (uri)) {
      handleTrace ("End element: " + qName);

      if (msg != null && "JtMessage".equals (qName)) {
        output = sendReply (msg);
        msg = null;
        buffer = null;
        return;
      }

      if (msg != null && buffer != null &&
        qName != null) {

        if (!qName.equals ("JtMessage"))
          setValue (msg, qName, buffer.toString ());
        buffer = null;
      } 

    } else
      handleTrace ("End element:   {" + uri + "}" + name);    
  }

  /** Start document. */
  public void startDocument() throws SAXException {

  } 

  public void endDocument()
                 throws SAXException {

  }

  public void characters(char[] ch,
                       int start,
                       int length)
                throws SAXException {
    String tmp;

    //handleTrace ("Characters:    \"");
    if (buffer == null)
      buffer = new StringBuffer ();

    for (int i = start; i < start + length; i++) {
      switch (ch[i]) {
        case '\\':
          handleTrace("\\\\");
          break;
	case '"':
	  handleTrace("\\\"");
	  break;
	case '\n':
	  //handleTrace("\\n");
	  break;
	case '\r':
          handleTrace ("\\r");
          break;
        case '\t':
          handleTrace ("\\t");
          break;
        default:
          buffer.append (ch[i]);
          break;
      }
    }

    //handleTrace("\"\n");
    //elemValue = buffer.toString ();
    tmp = buffer.toString().trim ();

    if (!tmp.equals ("")) {
      handleTrace("JtXMLMsgReader.characters:" + 
        buffer);      
    }    

  }


  public void endPrefixMapping(String prefix)
                      throws SAXException {

  }

  public void ignorableWhitespace(char[] ch,
                                int start,
                                int length)
                         throws SAXException {

  }

  public void processingInstruction(String target,
                                    String data)
                           throws SAXException {

  }

  public void setDocumentLocator(Locator locator) {

  }

  public void skippedEntity(java.lang.String name)
                   throws SAXException {

  }

  public void startPrefixMapping(String prefix,
                                 String uri)
                        throws SAXException
  {

  }

  // realize
  public  void realizeObject () {

     if (reader != null)
       return;

     try{
       reader = XMLReaderFactory.createXMLReader (parserName);

       reader.setContentHandler (this);
       //reader.setErrorHandler (this);
     } catch (Exception ex) {

       handleException (ex);
     }

  }

  private Object parse () {

    ByteArrayInputStream bstream;
    JtMessage msg = new JtMessage ("JtCONVERT_OBJECT_TO_XML");
 
    if (reader == null)
      realizeObject ();

    if (uri == null && string == null) {
      handleError ("JtXMLMsgReader.parse: both uri and string are null");
      return null;
    } 

    if (uri != null && string != null) {
      handleError ("JtXMLMsgReader.parse: set uri or string (only one)");
      return null;
    } 

    try {
      if (uri != null)
        reader.parse (uri);
      else {
        bstream = new ByteArrayInputStream (string.getBytes());     
        reader.parse (new InputSource ((InputStream) bstream));

      }
    } catch (Exception ex) {
      handleException (ex);
    }

    if (xmlHelper == null)
      xmlHelper = new JtXMLHelper ();

    msg.setMsgContent (output);
    return (sendMessage (xmlHelper, msg));
    //System.out.println ("output:" + output);
    //return output;
  }  

  // Process object messages

  public Object processMessage (Object event) {

   String msgid = null;
   JtMessage e = (JtMessage) event;
   //Object content;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

//   content = e.getMsgContent();

     replyTo = e.getMsgReplyTo ();

     if (msgid.equals ("JtPARSE")) {
        return (parse ());
     }

     if (msgid.equals ("JtREMOVE")) {
        return (null);
     }
          
     handleError ("JtXMLMsgReader.processMessage: invalid message id:" + msgid);
     return (null);

  }


  // Test program

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    //JtMessage msg, msg1;
    //Integer count;
    String str;

    main.setObjTrace (1);
    //main.setLogFile ("log.txt");


    if (args.length < 1) {
      System.err.println ("Usage: java JtXMLMsgReader uri");
      System.exit (1);
    }

    // Create message reader

    main.createObject ("Jt.xml.JtXMLMsgReader", "reader");

    main.createObject ("Jt.JtMessage", "message");
    main.setValue ("message", "msgId", "JtPARSE");
    main.setValue ("reader", "uri", args[0]);

    main.sendMessage ("reader", "message");

    main.createObject ("Jt.JtFile", "file");
    main.setValue ("file", "name", args[0]);    
    main.setValue ("message", "msgId", "JtCONVERT_TO_STRING");
 
    str = (String) main.sendMessage ("file", "message");

    main.setValue ("message", "msgId", "JtPARSE");
    main.setValue ("reader", "uri", null);
    main.setValue ("reader", "string", str);

    main.sendMessage ("reader", "message");


  }

}


