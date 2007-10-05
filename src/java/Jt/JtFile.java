

package Jt;
import java.util.*;
import java.io.*;

/**
  * Handles files and directories.
  */

public class JtFile extends JtObject {

  private static final long serialVersionUID = 1L;
  String name;
  FileOutputStream ostream = null;
  boolean createdir = true;
  boolean recursive = false;
  Vector filelist = null;
  String filter = null;
  byte[] buffer = null;
  public final static int BUFFER_SIZE = 1024;      // Buffer size (read_file)
  public final static int MAX_LENGTH = 1024 * 50;  // Max length(read_file)
  int buffersize = BUFFER_SIZE;

  public JtFile() {
  }

  /**
   * Specifies the name of the file or directory
   * @param newName file name
   */

  public void setName (String newName) {
     name = newName;
  }

  /**
    * Returns the name of the file or directory.
    */


  public String getName () {
     return (name);
  }

  /**
    * Sets the createDir flag which determines if necessary but nonexisting parent directories
    * should be created.
    * @param newCreatedir create nonexisting parent directories
    */

  public void setCreatedir (boolean newCreatedir) {
     createdir = newCreatedir;
  }

  /**
    * Returns the createDir flag which determines if necessary but nonexisting parent directories
    * should be created.
    */

  public boolean getCreatedir () {
     return (createdir);
  }

  // File list (output)

  /**
    * This attribute is being deprecated.
    */

  public Vector getFilelist () {

     return (filelist);
  }


  /**
    * Sets the filter flag which determines what file extension should be considered.
    */


  public void setFilter (String filter) {
     this.filter = filter;
  }

  /**
    * Returns the filter flag which determines what file extension should be considered.
    */

  public String getFilter () {
     return (filter);
  }

  /**
    * Set the recursive flag which determines if the JtCLEANUP message should recursively
    * remove files under the subdirectories.
    * @param recursive
    *
    */

  public void setRecursive (boolean recursive) {
     this.recursive = recursive;
  }

  /**
    * Returns the recursive flag which determines if the JtCLEANUP message should recursively
    * remove files under the subdirectories.
    *
    */
  public boolean getRecursive () {
     return (recursive);
  }

  // open operation

  void open () {

     try {
	ostream = new FileOutputStream (name);
     } catch (Exception e) {
	handleException (e);
     }
  }

/*
  public byte [] getBuffer ()
  {
    return (buffer);
  }
*/

  // read_file: read the whole file into memory (good only for small files)

  private Object read_file ()
  {
    FileInputStream istream;
    File file;
    int len, offset = 0, i;
    long file_size;
    byte buf [] = new byte [buffersize];

    if (name == null)
      return (null);

    file = new File (name);
    if (!file.exists ())
    {
      handleTrace ("JtFile.readfile: file does not exist:" + name);
      buffer = null;
      return (null); // check
    }

    if (file.length () == 0L)
    {
      handleTrace ("JtFile.readfile: empty file" + name);
      buffer = null;
      return (null);
    }

    // This is to avoid memory problems while handling big files.
    // Huge files should be split.

    file_size = file.length ();

    if (file_size > MAX_LENGTH)
    {
      handleError 
       ("JtFile.readfile: file exceeded maximum length allowed:" + 
       name + ":" + file_size);
      return (null);
    }

    buffer = new byte[(int) file_size];

    try {
      istream = new FileInputStream (name);
      offset = 0;

      while ((len = istream.read (buf, 0, buffersize)) > 0) 
      {
        for (i = 0; i < len; i++)
        {

          if (offset + i >= file_size)
          {
            handleError ("JtFile.readfile: file size mismatch" + 
               name);
            buffer = null;
            istream.close ();
            return (null);

          }
          buffer[offset + i] = buf[i];
        }
        offset += len;
      }

      if (offset != file_size)
        handleWarning ("JtFile.readfile: file size mismatch" + 
               name);  // check

      istream.close ();

    } catch (Exception e) {
      buffer = null;
      handleException (e);
    }

    if (buffer == null)
      return (null);

/*
    handleTrace ("read_file:");
    for (i = 0; i < offset; i++)
      System.out.print (new Character ((char) buffer[i]));
*/
    return (this);
  }

  // create operation

  void create_directory () {

     create_dir (name);

  }

  // write operation

  void write (byte buffer[], int len) {

     if (ostream == null)
        return;

     try {
        ostream.write (buffer, 0, len);
     } catch (Exception e) {
        handleException (e);
     }

  }

  // Destroy operation

  void destroy ()  {
     if (ostream != null) 
	close ();

  }

  
  // close operation

  void close () {

     if (ostream == null)
       return;

     try {
       ostream.close ();
     } catch (Exception e) {
       System.err.println (e);
     }
     ostream = null; // check
  }

  void create_dir (String name) {
   File file;
   //String parentpath; 

        if (name == null)
           return;

        file = new File (name);
        if (file == null) {
           handleError ("JtFile: unable to create File object:" + name);
	   return;
        }

        if (!file.exists ())
             if (!file.mkdirs ())
                     handleError ("JtFile: unable to create directories:" +
                                name);
  }


  void create_parent_dir (String name) {
   File file;
   String parentpath; 

        if (name == null)
           return;

        file = new File (name);
        if (file == null) {
           handleError ("JtFile: unable to create File object:" + name);
	   return;
        }

        parentpath = file.getParent ();
	if (parentpath == null)
	   return;

        //file = file.getParentFile ();
        file = new File (parentpath);
        if (file == null) {
           handleError ("JtFile: unable to get parent File:" + name);
	   return;
        }

        if (!file.exists ())
             if (!file.mkdirs ())
                     handleError ("JtFile: unable to create directories:" +
                                parentpath);
  }


  // dir: create a list with the content of this directory

  void dir () {
    int i;
    File Dir;
    String files[];
    String ext = filter; // file extension

    if (name == null)
      return;

    // define a list of files

    if (filelist == null)
        filelist = new Vector ();

    if (filelist == null)
        return;
    else
        filelist.removeAllElements ();

    Dir = new File (name);

    if (!Dir.isDirectory ()) {
        handleError ("File.dir: invalid directory:" +
                 name);
        return;
    }

    files = Dir.list ();

    i = 0;
    while (i < files.length) {
      
      if (ext == null || (ext != null && files[i].endsWith (ext))) {
        handleTrace ("JtFile.dir:" + files[i]);
        filelist.addElement (files[i]);
      }
      i++;
    }
  }

  // ls: create a list with the content of this directory. The directory
  // name is added to the path.

  void ls () {
    int i;
    File Dir;
    String files[];
    String ext = filter; // file extension
    String tmp;

    if (name == null)
      return;

    // define a list of files

    if (filelist == null)
        filelist = new Vector ();

    if (filelist == null)
        return;
    else
        filelist.removeAllElements ();

    Dir = new File (name);

    if (!Dir.isDirectory ()) {
        handleError ("File.ls: invalid directory:" +
                 name);
        return;
    }

    files = Dir.list ();

    i = 0;

    while (i < files.length) {
      
      tmp = name + "/" + files[i];
      if (ext == null || (ext != null && files[i].endsWith (ext))) {
        handleTrace ("JtFile.ls:" + tmp);
        filelist.addElement (tmp);
      }
      i++;
    }
  }

  // find_extension: find a file under this directory. The file uses 
  // the specified extension

  void find_extension (String dir, String extension) {


   File Dir, file;
   String tmp;
   String files[];
   int i;


    if (dir == null || extension == null) // check find
      return;

    // define a list of files
    if (filelist == null)
	filelist = new Vector ();

    if (filelist == null)
	return;

    Dir = new File (dir);

    if (!Dir.isDirectory ()) {
        handleError ("File.find_extension: invalid directory:" +
                 dir);
        return;
    }

    files = Dir.list ();

    i = 0;
    while (i < files.length) {
	
	tmp = dir + "/" + files[i];
        //handleTrace ("JtFile.find_prefix:checking ..." + tmp);
        if (files[i].endsWith (extension)) {
           handleTrace ("JtFile.find_extension:" + tmp);
	   //add it
	   filelist.addElement (tmp);
	}

	i++;
        file = new File (tmp);
        if (file.isDirectory ())
            find_extension (tmp, extension);  // check


    }


  }

  // find_prefix: find a file under this directory. The file starts with
  // the specified prefix

  void find_prefix (String dir, String prefix) {


   File Dir, file;
   String tmp;
   String files[];
   int i;


    if (dir == null || prefix == null) // check find
      return;

    // define a list of files
    if (filelist == null)
	filelist = new Vector ();

    if (filelist == null)
	return;

    Dir = new File (dir);

    if (!Dir.isDirectory ()) {
        handleError ("File.find_prefix: invalid directory:" +
                 dir);
        return;
    }

    files = Dir.list ();

    i = 0;
    while (i < files.length) {
	
	tmp = dir + "/" + files[i];
        //handleTrace ("JtFile.find_prefix:checking ..." + tmp);
        if (files[i].startsWith (prefix)) {
           handleTrace ("JtFile.find_prefix:" + tmp);
	   //add it
	   filelist.addElement (tmp);
	}

	i++;
        file = new File (tmp);
        if (file.isDirectory ())
            find_prefix (tmp, prefix);  // check


    }


  }

  // find: look for a file under a directory

  void find (String dir, String fname) {


   File Dir, file;
   String tmp;
   String files[];
   int i;


    if (name == null || fname == null)
      return;

    // define a list of files
    if (filelist == null)
	filelist = new Vector ();

    if (filelist == null)
	return;

    Dir = new File (dir);

    if (!Dir.isDirectory ()) {
        handleError ("File.find: invalid directory:" +
                 name);
        return;
    }

    files = Dir.list ();

    i = 0;
    while (i < files.length) {
	
	tmp = dir + "/" + files[i];
        if (fname.equals (files[i])) {
           handleTrace ("JtFile.find:" + tmp);
	   filelist.addElement (tmp);
	   //add it
	}

	i++;
        file = new File (tmp);
        if (file.isDirectory ())
            find (tmp, fname);


    }


  }

  // Read_lines: read file line by line

  private void read_lines (JtMessage ev) { 
    String line;
    JtMessage e;

    if (name == null)
      return;

    try {
      BufferedReader d = new BufferedReader 
		(new FileReader (name));

      while ((line  = d.readLine ()) != null) {
        //System.out.println ("JtFile.read_lines:" + line);
        e = new JtMessage ();
        e.setMsgId("JtMESSAGE");
        e.setMsgSubject (ev.getMsgId ());
        e.setMsgContent (line);
        if (ev.getMsgReplyTo () != null)
 	  this.sendMessage (ev.getMsgReplyTo (), e);
      }

    } catch (Exception ex) {
      handleException (ex);
    }
  }

  /**
    * Process object messages.
    * <ul>
    * <li> JtOPEN - Opens a file
    * <li> JtCLOSE - Closes a file
    * <li> JtREAD_LINES - Reads input lines from the file, one line at a time. 
    * Each line is sent to the object specified by msgReplyTo (JtMessage object). 
    * <li> JtDELETE - Deletes a file or directory
    * <li> JtCREATE_DIRECTORY - Creates a directory
    * <li> JtCLEANUP - Removes all the files under a directory
    * <li> JtCONVERT_TO_STRING - Converts the content of the file String
    * <li> JtFIND - Looks for the file specified by msgContent
    * </ul>
    */

  public Object processMessage (Object message) {

   String msgid = null;
   byte buffer[];
   JtBuffer buf;
   File file;
   JtMessage e = (JtMessage) message;

     if (e == null)
	return null;

     msgid = (String) e.getMsgId ();

     if (msgid == null)
	return null;

     // Remove this object
     if (msgid.equals ("JtREMOVE")) {
       return (this);     
     }

     if (msgid.equals ("JtOPEN")) {

	// Create the parent directories if needed

	if (name == null)
	   return null;

	if (createdir) {
	   create_parent_dir (name);
	}
	open ();
	return null;
     }

     if (msgid.equals ("JtCREATE_DIRECTORY")) {
	create_directory ();
	return null;
     }

     if (msgid.equals ("JtCREATE_PARENT_DIRECTORY")) {

	if (name == null)
	   return null;

        create_parent_dir (name);
        return null;
     }

     if (msgid.equals ("JtCLOSE")) {
	close ();
	return null;
     }

     if (msgid.equals ("JtREAD_FILE")) {
	return (read_file ());
     }

     if (msgid.equals ("JtCONVERT_TO_STRING")) {
	read_file ();
        if (this.buffer == null) 
          return (null);
        return (new String (this.buffer));
     }

     if (msgid.equals ("JtREAD_LINES")) {
	read_lines (e);
        return (null);
     }

     if (msgid.equals ("JtCLEANUP")) {
	if (name == null)
	   return (null);

	cleanup (name);
	return (null);
     }

/*
     if (msgid.equals ("JtWRITE")) {
        if (e.getMsgContent () == null)
	   return null;

	buf = (RtBuffer) e.getMsgContent ();
	buffer = buf.getBuffer ();
	handleTrace ("RtFile: writing " + buffer.length + " bytes");
	write (buffer, buffer.length);
	return null;
     }
*/

     if (msgid.equals ("JtDATA_BUFFER")) {
        if (e.getMsgContent () == null)
	   return null;

	buf = (JtBuffer) e.getMsgContent ();
	buffer = buf.getBuffer ();
	handleTrace ("JtFile: writing " + buffer.length + " bytes");
	write (buffer, buffer.length);
	return null;
     }

     if (msgid.equals ("JtWRITEBYTE")) {
        if (e.getMsgContent () == null)
           return null;

        buffer = (byte[]) e.getMsgContent ();
        // buffer = buf.getBuffer ();
        handleTrace ("JtFile: writing " + buffer.length + " bytes");
        write (buffer, buffer.length);
        return null;
     }

     if (msgid.equals ("JtDELETE")) {
        if (name == null) {
            handleError ("JtFile: Invalid attribute (name):"
                + name);
            return null;
          }
        file = new File (name);
        file.delete ();
        return null;
     }

     if (msgid.equals ("JtDIR")) {
        dir ();
	return (null);
     }

     // List the directory

     if (msgid.equals ("JtLS")) {
        ls ();
        return (null);
     }


     if (msgid.equals ("JtFIND")) {

        if (filelist != null)
	   filelist.removeAllElements ();

	find (name, (String) e.getMsgContent ());
	return (null);
     }

     if (msgid.equals ("JtFIND_PREFIX")) {

        if (filelist != null)
           filelist.removeAllElements ();

        find_prefix (name, (String) e.getMsgContent ());
        return (null);
     }

     if (msgid.equals ("JtFIND_EXTENSION")) {

        if (filelist != null)
           filelist.removeAllElements ();

        find_extension (name, (String) e.getMsgContent ());
        return (null);
     }


     handleError ("JtFile.processMessage: invalid message id:" + msgid);
     return (null);

  }

  // cleanup: cleanup a directory

  void cleanup (String dname) {
    File dir, file;
    int i;
    String files[];
    String tmp;

    if (dname == null)
      return;

    dir = new File (dname);

    if (!dir.isDirectory ()) {
	handleWarning ("File.cleanup: invalid directory:" +
		 dname);
	return;
    }

    files = dir.list ();

    i = 0;
    while (i < files.length) {
	tmp = dname + "/" + files[i++]; // check
        file = new File (tmp);
        if (file.isDirectory ())
          if (!recursive)
            continue;
          else {
	    handleTrace ("JtFile.cleanup (recursive): cleaning " + tmp);
            cleanup (tmp);
          }
	handleTrace ("JtFile.cleanup: deleting " + tmp);
        file.delete ();
    }
  }



  /**
    * Unit tests the messages processed by JtFile.
    */

  public static void main(String[] args) {

    JtObject main = new JtObject ();
    //JtMessage msg;
    File tmp;
    //JtFile jfile;

    // main.setObjTrace (1);

    // Create a JtFile object

    main.createObject ("Jt.JtFile", "file");
    main.setValue ("file", "name", "JtFile");
    main.setValue ("file", "createdir", "true");
    
    main.createObject ("Jt.JtMessage", "message");
    //main.setValue ("message", "msgId", "JtDELETE");
    //main.sendMessage ("file", "message");


    // JtOPEN

    main.setValue ("message", "msgId", "JtOPEN");

    tmp = new File ("JtFile");

    main.sendMessage ("file", "message");
    main.setValue ("message", "msgId", "JtCLOSE");
    main.sendMessage ("file", "message");

    if (!tmp.exists ()) 
	System.err.println ("JtFile(JtOPEN): FAILED");
    else
	System.err.println ("JtFile(JtOPEN): GO");

    // JtDELETE

    main.setValue ("message", "msgId", "JtDELETE");
    main.sendMessage ("file", "message");

    tmp = new File ("JtFile");
    if (!tmp.exists ())
	System.err.println ("JtFile(JtDELETE): GO");       
    else {
	System.err.println ("JtFile(JtDELETE): FAILED");
    }

    // createdir attribute

    main.setValue ("file", "name", "/tmp/JtFile/JtFile");
    main.setValue ("file", "createdir", "true");
    main.setValue ("message", "msgId", "JtOPEN");
    main.sendMessage ("file", "message");
    main.setValue ("message", "msgId", "JtCLOSE");
    main.sendMessage ("file", "message");

    tmp = new File ("/tmp/JtFile/JtFile");
    if (!tmp.exists ()) 
	main.handleError ("JtFile(JtOPEN/creatdir=true): FAILED");
    else
	System.err.println ("JtFile(JtOPEN/createdir=true): GO");


    main.setValue ("message", "msgId", "JtDELETE");
    main.sendMessage ("file", "message");
    if (!tmp.exists ()) 
	System.err.println ("JtFile(JtDELETE): GO");
    else
	main.handleError ("JtFile(JtDELETE): FAILED");

/*
    main.setValue ("file", "name", "cleanup");
    main.setValue ("file", "recursive", "true");
    main.setValue ("message", "msgId", "JtCLEANUP");
    main.sendMessage ("file", "message");
*/


    //main.setValue ("file", "name", ".");
/*
    main.setValue ("file", "name", "/tmp");
    main.setValue ("message", "msgId", "JtFIND_PREFIX");
    main.setValue ("message", "msgContent", "Jt");
    main.sendMessage ("file", "message");

    main.setValue ("file", "name", "/tmp");
    main.setValue ("message", "msgId", "JtFIND");
    main.setValue ("message", "msgContent", "var");
    main.sendMessage ("file", "message");
*/

    main.setValue ("file", "name", "/tmp/tmpdirectory");
    main.setValue ("message", "msgId", "JtCREATE_DIRECTORY");
    main.sendMessage ("file", "message");

    // Create directory

    tmp = new File ("/tmp/tmpdirectory");
    if (!tmp.exists ()) 
	main.handleError ("JtFile(JtCREATE_DIRECTORY): FAILED");
    else
	System.err.println ("JtFile(JtCREATE_DIRECTORY): GO");

    // Find (JtFIND)

    main.setValue ("file", "name", "/tmp");
    main.setValue ("message", "msgId", "JtFIND");
    main.setValue ("message", "msgContent", "tmpdirectory");
    main.sendMessage ("file", "message");


    main.setValue ("file", "name", "/tmp");
    //main.setValue ("file", "filter", ".txt");
    //main.setValue ("message", "msgId", "JtDIR");
    //main.sendMessage ("file", "message");


    // List (JtLS)

    main.setValue ("message", "msgId", "JtLS");
    main.sendMessage ("file", "message");

    main.removeObject ("file");

/*
    main.setValue ("message", "msgId", "JtREAD_FILE");
    main.setValue ("file", "name", "tmp");
    jfile = (JtFile) main.sendMessage ("file", "message");
    if (jfile != null)
      System.err.println ("buffer:" + jfile.getBuffer ());

    main.setValue ("message", "msgId", "JtREAD_LINES");
    main.setValue ("file", "name", "tmp.txt");
    main.sendMessage ("file", "message");
*/
  }

}


