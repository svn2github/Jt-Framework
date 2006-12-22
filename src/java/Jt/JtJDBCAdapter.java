

package Jt;
import java.io.*;
import java.sql.*;
import javax.sql.*;
import java.text.*;
import Jt.jndi.*;

/**
 * Jt Adapter for the JDBC API. This implementation supports Data Sources.
 */

public class JtJDBCAdapter extends JtAdapter  {
  private String user;
  private String password;
  private String url;  
  private String driver;
  private transient Connection connection = null;
  private String base;
  int n = 0;
  private transient Statement myStatement;
  private String datasource = null;
  private transient JtJNDIAdapter jndiAdapter = null;
  private transient DataSource dataSource = null;
  private transient boolean initted = false; 

  private DataSource locateDataSource (String datasource) {
    JtMessage msg = new JtMessage ("JtLOOKUP");
    
      
    if (datasource == null)
      return (null);

    msg.setMsgContent (datasource);
    jndiAdapter = new JtJNDIAdapter ();

    return ((DataSource) jndiAdapter.processMessage (msg));

  }
  
  private void initial () {



    if (datasource != null) {
      dataSource = locateDataSource (datasource);

      if (dataSource == null) {
        handleError ("JtJDBAdapter.connect: unable to locate datasource:" + 
         datasource);
        connection = null;
        return;

      }

      try {
        connection = dataSource.getConnection ();
      } catch (Exception ex) {
        handleException (ex);
      }
    }

  }


  void connect ()
  {
    handleTrace ("JtJDBCAdapter.connect ....");


    if (datasource != null) {

      if (dataSource == null) {
        handleWarning ("JtJDBAdapter.connect: unable to connect to Datasource: " + 
         datasource);
        connection = null;
        return;

      }

      try {
        connection = dataSource.getConnection ();
        handleTrace ("JtJDBCAdapter.connect: using data source ....");

      } catch (Exception ex) {
        handleException (ex);
      }
      return;
    }

    if (connection != null) {
      return;
    }

    if (driver == null) {
      handleError ("connect: null attribute (driver)");
      return;
    }

    if (url == null) {
      handleError ("connect: null attribute (url)");
      return;
    }

    try
    {
      Class.forName(driver);
      if (user == null)
        connection = DriverManager.getConnection(url);
      else
        connection = DriverManager.getConnection(url,
           user, password);
      //System.out.println (connection.getAutoCommit ());
    }
    catch(ClassNotFoundException cnfe)
    {
	    handleException (cnfe);
    }
    catch(SQLException sqle)
    {
	    handleException (sqle);
    }
  }

  // close: close connection

  void close () {

     if (connection == null)
	return;

     try {
     	connection.close ();	
     } 
     catch (SQLException sqle) {
	handleException (sqle);
     } 
     finally {
     	connection = null; 
     }
  }

  // Executes a query

  ResultSet execute_query (String query) {

      if (query == null)
          return (null);

      if (connection == null) {
	 handleError ("execute_query: no database connection");
	 return (null);
      }

      try {
      	myStatement = connection.createStatement ();
      	ResultSet results = myStatement.executeQuery (query);
      	
        if (results == null) // check
          myStatement.close ();

        return (results); // check - close later
              
        //results.close ();
      }
      catch(SQLException sqle)
      {
	handleTrace ("Message: " + sqle.getMessage ());
	handleTrace ("SQL State: " + sqle.getSQLState ());
	handleTrace ("Vendor code: " + sqle.getErrorCode ());
	handleException (sqle);
        return (null);
      } 
/*
      finally {
        if (myStatement != null) {
          try {
            myStatement.close ();
          } catch (SQLException sqle) {
          }
        }
      }
*/

 }

  Object execute_update (String query) {
    int cnt = 0;
    Statement myStatement = null;

      if (query == null)
          return (null);

      if (connection == null) {
	 handleError ("execute_query: no database connection");
	 return (null);
      }

      try {
      	myStatement = connection.createStatement ();
      	cnt = myStatement.executeUpdate (query);
        myStatement.close ();
        return new Integer (cnt);
      }
      catch(SQLException sqle)
      {
	handleTrace ("Message: " + sqle.getMessage ());
	handleTrace ("SQL State: " + sqle.getSQLState ());
	handleTrace ("Vendor code: " + sqle.getErrorCode ());
	handleException (sqle);
	return (null);
      } 

 }

  // show_output: show output of the query 

  int show_output (ResultSet res) {
	
    int ncol, n;
    ResultSetMetaData meta;

    if (res == null)
	return (0);

    try {        
	meta = res.getMetaData ();
    }
    catch (SQLException e) {
	handleException (e);
	return (0);
    }


 
    try {
        while (res.next ()) {
	ncol = meta.getColumnCount ();
        handleTrace ("output:ncol:"+ ncol);
	for (n = 1; n <= ncol; n++) {
		handleTrace ("output:" + 
			meta.getColumnName (n) + ":" +
			res.getString (n));
	}
	}
	return (1);
    }
    catch (SQLException e) {
	handleException (e);
	return (0);
    }
  }

  // map: map database row onto object

  int map (ResultSetMetaData meta, 
	ResultSet res, String obj_name) {
	
    int ncol, n;
    DateFormat df = DateFormat.getDateInstance();

    if ((meta == null) || (res == null) ||
	(obj_name == null))
	return (0);
 
    try {
	ncol = meta.getColumnCount ();
	for (n = 1; n <= ncol; n++) {
          //handleTrace ("JtJDBCAdapter.map:" + meta.getColumnTypeName(n));
	  if (meta.getColumnTypeName(n).equals ("DATE") ||
              meta.getColumnTypeName(n).equals ("DATETIME")) {
            Date date = res.getDate (n);
            if (date == null || date.equals (""))
              continue; // check
            String sdate = df.format (date);
            this.setValue (obj_name, 
			meta.getColumnName (n),
			sdate);
            continue;
          }
	  this.setValue (obj_name, 
			meta.getColumnName (n),
			res.getString (n));
/*
	  handleTrace ("JtJDBCAdapter.map:" + 
                 meta.getColumnName (n) + ":" +
                 res.getString (n));
*/
	}
	
	return (1);
    }
    catch (SQLException e) {
	handleException (e);
	return (0);
    }
  }

  // map: map query onto object list. This method is being deprecated (see JtDAO)

  void map_query (String query, JtMessage me) {
	ResultSetMetaData mdata;
	ResultSet res;
	//int n = 0; - check memory leak
	String name;
	JtMessage event;
        JtObject tmp;

        //if (table == null)
	  //return;

        handleTrace ("JtJDBCAdapter.map_query ....");

	if (query == null)
	   return;

	if (base == null)
	   return;

        //query = "select * from " + table; 

        res = execute_query (query);

	if (res == null) {
           handleTrace ("map_query:res:null");
           //myStatement.close ();
	   return;
        }

	try {        
	   mdata = res.getMetaData ();
	}
	catch (SQLException e) {
	   handleException (e);
	   return;
	}

        try {
	  if (mdata == null) {
            myStatement.close ();
            //res.close (); // check
	    return;
          }
	} catch (SQLException e) {
	   handleException (e);
	   return;
	}

	try {
	while (res.next ()) {
	   n++;
	   name = "" + n;
           handleTrace ("creating object ..." + name);
           tmp = (JtObject) this.createObject (base, name);	
	   if (map (mdata, res, name) == 0)
		continue;
	   event = new JtMessage ();
           //event.setMsgContent (this.getObjName()+"."+name);
           event.setMsgContent (tmp);
           event.setMsgId ("JtOBJECT");
           event.setMsgSubject (me.getMsgSubject ());
	   //event.setMsgSubject("JtMAP");


           // send a reply
	   if (me.getMsgReplyTo () != null) {
		// fireMessageEvent(event);
	   //      else { 
		//event.setMsgIsReply (true);
	     this.sendMessage (me.getMsgReplyTo (), event);
	   }
	
	}
        //	res.close (); // check
	myStatement.close ();
        }
  	catch (SQLException e) {
	   handleException (e);
	}
}  

  private PreparedStatement prepareStatement (String query)
  {
    PreparedStatement pst = null;

    if (connection == null) {
      handleError ("JtJDBCAdapter.prepareStatement: invalid connection (null)");
      return (null);
    }

    if (query == null)
     return (null);

    try {
      pst = connection.prepareStatement (query);
    } 
    catch (Exception ex) {
      handleException (ex);
    }
    return (pst);    
  }


  private Object executePreparedUpdate (PreparedStatement pst )
  {
    int cnt = 0;
    
    if (pst == null) {
      return (null);
    }

    if (connection == null) {
      handleError ("JtJDBCAdapter.prepareStatement: invalid connection (null)");
      return (null);
    }
    try {
      cnt = pst.executeUpdate ();
      return (new Integer (cnt));
    } catch (Exception ex) {
      handleException (ex);
      return (ex);
    }

  }
  
  /**
    * Process object messages.
    * <ul>
    * <li> JtCONNECT - Establishes a database connection using the appropriate object attributes 
    * (database driver, url, user/password if any).
    * <li> JtEXECUTE_QUERY - Executes an SQL statement. Typically this is an SQL SELECT statement.
    * Returns a ResultSet object. The message contains the query to be executed (msgContent).
    * <li> JtEXECUTE_UPDATE - Executes an SQL INSERT, UPDATE, DELETE or DDL statement. Returns
    * the row count (Integer) for INSERT, UPDATE  or DELETE statements, or 0 for SQL statements 
    * that return nothing. The message contains the query to be executed (msgContent).
    * <li> JtPREPARE_STATEMENT - Returns a prepared statement. The message contains the query (msgContent).
    * <li> JtEXECUTE_PREPARED_UPDATE - Executes a prepared statement. The message contains the prepared statement
    * to be executed. Returns the row count (Integer) for INSERT, UPDATE  or DELETE statements, or 0 for SQL statements 
    * that return nothing.
    * <li> JtCLOSE - Closes the database connection.
    * </ul>
    */

  public Object processMessage (Object message) {
  String content;
  String query;
  JtMessage e = (JtMessage) message;
  Object reply;

      if (e == null ||  (e.getMsgId() == null))
          return (null);

      if (!initted) {
        initial ();
        initted = true;        
      }

      // establish connection
      if (e.getMsgId().equals("JtCONNECT")) {
	connect ();
	return (connection);
      }

      // execute a query
      if (e.getMsgId().equals("JtQUERY")) {
        connect ();
	query = (String) e.getMsgContent();
	show_output (execute_query (query));
        //if (dataSource != null)
        //  close ();

	return (null);
      }

      if (e.getMsgId().equals("JtEXECUTE_QUERY")) {
	query = (String) e.getMsgContent();
        connect ();
	reply = execute_query (query);
        // When dealing with DataSources Close the connection after each operation
        //if (dataSource != null)
        //  close ();
        return (reply);

      }


      if (e.getMsgId().equals("JtPREPARE_STATEMENT")) {
	query = (String) e.getMsgContent();
        connect ();
	reply = prepareStatement (query);
        //if (dataSource != null)
        //  close ();
        return (reply);
      }


      if (e.getMsgId().equals("JtEXECUTE_PREPARED_UPDATE")) {
	//query = (String) e.getMsgContent();
        connect ();
	reply = executePreparedUpdate 
         ((PreparedStatement) e.getMsgContent());
        //if (dataSource != null)
        //  close ();
        return (reply);
      }

      // execute an Update
      if (e.getMsgId().equals("JtUPDATE")) {
	query = (String) e.getMsgContent();
        connect ();
	reply = execute_update (query);
        //if (dataSource != null)
        //  close ();
        return (reply);
      }

      // map query onto object list
      if (e.getMsgId().equals("JtMAP")) {
	query = (String) e.getMsgContent();
	if (query == null)
	   return (null);
        connect ();
	map_query (query, e);
        //if (dataSource != null)
        //  close ();
	return (null);
      }

      // close a connection
      if (e.getMsgId().equals("JtCLOSE") || e.getMsgId().equals ("JtREMOVE")) {
	close ();
	return (null);
      }
      handleError 
	("processMessage: invalid message id:"+
		e.getMsgId());
      return (null);
  }

  /**
   * Specifies the database user.
   * @param newUser user
   */

  public void setUser (String newUser) {
    user = newUser;
  }


  /**
   * Returns the database user.
   */

  public String getUser () {
    return (user);
  }


  /**
   * Specifies the database password.
   * @param newPassword password
   */

  public void setPassword (String newPassword) {
    password = newPassword;
  }


  /**
   * Returns the database password.
   */

  public String getPassword () {
    return (password);
  }


  /**
   * Specifies the URL of the database to which to connect.
   * @param newUrl url
   */

  public void setUrl (String newUrl) {
    url = newUrl;
  }

  /**
   * Returns the URL of the database.
   */

  public String getUrl () {
    return (url);
  }


  /**
   * Specifies the Datasource logical name (if any).
   * @param datasource Datasource logical name
   */

  public void setDatasource (String datasource) {
    this.datasource = datasource;
  }

  /**
   * Returns the Datasource logical name (JNDI).
   */

  public String getDatasource () {
    return (datasource);
  }


  /**
   * Specifies the database driver.
   * @param newDriver driver
   */

  public void setDriver (String newDriver) {
    driver = newDriver;
  }

  /**
   * Returns the database driver.
   */
  public String getDriver () {
    return (driver);
  }

  /**
   * Method being deprecated.
   */

  public void setBase (String newBase) {
    base = newBase;
  }

  /**
   * Method being deprecated.
   */

  public String getBase () {
    return (base);
  }


  /**
   * Specifies the database Connection.
   */

  public void setConnection (Connection newConnection) {
    connection = newConnection;
  }

  /**
   * Returns the database Connection.
   */

  public Connection getConnection () {
    return (connection);
  }


  private static void test () {
    JtObject main;
    String query;

    main = new JtObject ();
    //main.setValue (main, "objTrace", "1");
    main.createObject ("Jt.JtMessage", "message");

    main.createObject ("Jt.JtJDBCAdapter", "database");
    main.setValue ("database", "objTrace", "1");
    //main.setValue ("message", "msgId", "JtCONNECT");

    //main.sendMessage ("database", "message");


    query = "select email from member where email='SUPPORT@FSW.COM';";

    main.setValue ("message", "msgId", "JtQUERY");    
    main.setValue ("message", "msgContent", query);
    main.sendMessage ("database", "message");
    main.removeObject ("database");
    main.removeObject ("message");

  }

  /**
   * Unit tests the messages processed by JtJDBCAdapter. The following attributes should be
   * included in the Jt resource file (.Jtrc):
   * <ul>
   * <li>Jt.JtJDBCAdapter.user
   * <li>Jt.JtJDBCAdapter.password
   * <li>Jt.JtJDBCAdapter.driver
   * <li>Jt.JtJDBCAdapter.url
   * <li>Jt.JtJDBCAdapter.datasource (if this attribute is present, the previous
   * attributes are not needed)
   * </ul>
   */

  public static void main (String[] args) {
     test ();
  }
}