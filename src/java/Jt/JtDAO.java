

package Jt;
import java.sql.*;
import java.text.*;
import java.lang.reflect.*;
import java.beans.*;
import java.util.*;


/**
  * Implements the Data Access Object pattern (DAO)
  */

public class JtDAO extends JtObject  {

  private static final long serialVersionUID = 1L;
  //private String user;
  //private String password;
  //private String url;
  //private String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
  private transient Connection connection = null;
  //private String base;
  //private Object reply_to;
  int n = 0;
  JtObject db = null;  
  String insert_query = null;
  //Hashtable attr;
  Hashtable attr;
  private Object key = null;  // key field
  private String table;
  private Hashtable map_table = null;
  private String configFile = null;


  public JtDAO () {

  }

  void map_attribute (String at, String column) {
    if (at == null || column == null)
      return;
    if (map_table == null)
      map_table = new Hashtable ();

    if (!validateAttribute (at)) {
      handleError ("JtDAO.map_attribute: invalid attribute mapping:"
      + at);

      return;
    }

    map_table.put (at, column);    
  }

  String getMapping (String at) {
    String column;

    if (at == null)
      return (null);

    if (map_table == null)
      return (null);
       
    column = (String) map_table.get (at);
/*
    if (column == null)
      return (at);
*/
    return (column);
  }

  private Object closeConnection () {  
    if (db == null) {
        handleWarning ("closeConnection: database object is null");        
    } else
        handleTrace ("closeConnection: closing a db connection");
    return (sendMessage (db, new JtMessage ("JtCLOSE")));      
  }
  

  // When dealing with DataSources, close the connection
 
  private void closeDataSourceConnection () {
    String datasource;

    if (db == null)
      return;
    datasource = (String) getValue (db, "datasource");
    if (datasource != null)
      sendMessage (db, new JtMessage ("JtCLOSE"));
      
  }

  /**
    * Process object messages.
    * <ul>
    * <li> JtINSERT - Insert an object
    * <li> JtFIND - Find an object
    * <li> JtUPDATE - Update an object
    * <li> JtDELETE - Delete an object
    * <li> JtMAP_ATTRIBUTE - Map attribute to database column
    * <li> JtREMOVE - Remove the JtDAO. Release resources including the database connection
    * </ul>
    */

  public Object processMessage (Object event) {
  //String content;
  //String query;
  JtMessage e = (JtMessage) event;
  Object reply;

      if (e == null ||  (e.getMsgId() == null))
          return (null);

      resetExceptions ();

      // establish a connection
      if (e.getMsgId().equals("JtREALIZE")) {
	realize ();
	return (null);
      }

      // insert record
      if (e.getMsgId().equals("JtINSERT")) {
	reply = insert ();

        closeDataSourceConnection ();
        return (reply);
      }

      if (e.getMsgId().equals("JtFIND")) {
        clear ();
	reply = find ();
        // When dealing with DataSources, close the connection after
        // each operation

        closeDataSourceConnection ();
        return (reply);
      }

      if (e.getMsgId().equals("JtUPDATE")) {
        reply = update ();

        closeDataSourceConnection ();
        return (reply);
      }

      if (e.getMsgId().equals("JtCLEAR")) {
        clear ();
	return (null);
      }

      if (e.getMsgId().equals("JtPRINT")) {
        print ();
	return (null);
      }

      if (e.getMsgId().equals("JtMAP_ATTRIBUTE")) {
        map_attribute ((String) e.getMsgContent (),
                       (String) e.getMsgData ());
        return (null);
      }

      if (e.getMsgId().equals("JtDELETE")) {
	reply = delete ();

        closeDataSourceConnection ();
        return (reply);
      }
      
      if (e.getMsgId().equals("JtCALCULATE_KEY")) {
      	reply = calculateKey ();

        closeDataSourceConnection ();
        return (reply);
      }
      
      if (e.getMsgId().equals("JtCLOSE_CONNECTION")) {
        return (closeConnection ());
      }

      if (e.getMsgId().equals("JtFIND_RECORDS")) {
        return (findRecords ((String) e.getMsgContent ()));
      }

      if (e.getMsgId().equals("JtREMOVE")) {
        destroy ();
        return (null);
      }

      handleError 
	("processMessageEvent: invalid message id:"+
		e.getMsgId());
      return (null);
  }


  // build_select_query: build select query

  private String build_select_query () {
    StringBuffer query = new StringBuffer ();
    String value;
    //Object tmp;
    Enumeration keys; 
    String att, tmp;
    String key_column;

    if (map_table == null)
      return (null);

    if (key == null ||  table == null)
      return (null);

    if (attr == null)
      return (null);

    value = (String) attr.get (key);

    if (value == null) {
      handleError ("build_select_query: invalid key (null)");
      return (null);
    }

    key_column = getMapping ((String) key);

    if (key_column == null) {
      handleError ("build_select_query: invalid mapping for "
       + key);
      return (null);      
    }
//    query.append ("Select * from "
//     + table + " where "); //check


//    if (map_table == null)
//      keys = attr.keys ();
//    else
    keys = map_table.keys ();

    if (!keys.hasMoreElements ())
      return (null);

    query.append ("Select ");
 
    while (keys.hasMoreElements ()) {
      att = (String) keys.nextElement ();
      tmp = getMapping (att);

      if (tmp == null) {
       handleError ("build_select_query: invalid mapping for "
       + att);
       return (null);
      }

      query.append (tmp);        

      if (keys.hasMoreElements ())
        query.append (",");   

    }

    query.append (" from " + table + " where ");

    query.append (key_column);
    query.append ("=");
    query.append (value);
    
    handleTrace ("query:" + query);

    return (query.toString ());

  }

  // build_delete_query: build delete query

  private String build_delete_query () {
    StringBuffer query = new StringBuffer ();
    Object value;
    //Object tmp;
    String key_column;

    if (attr == null)
      return (null);

    if (key == null ||  table == null)
      return (null);

    if (map_table == null)
      return (null);

    value = attr.get (key);
    //value = (Object) getValue (this, key);

    if (value == null) {
      handleError ("build_delete_query: invalid key (null)");
      return (null);
    }

    key_column = getMapping ((String) key);

    if (key_column == null) {
      handleError ("build_delete_query: invalid mapping for "
       + key);
      return (null);      
    }

    query.append ("Delete from "
     + table + " where "); //check

    query.append (key_column);
    //query.append (key);
    query.append ("=");
    query.append (value);
    
    handleTrace ("query:" + query);

    return (query.toString ());

  }

  // build_update_query: build update query

  private String build_update_query () {
    StringBuffer query = new StringBuffer ();
    Enumeration keys; 
    String att;
    String value;
    Object tmp;
    String column;
    String key_column;
    boolean first = true;

    if (attr == null)
      return (null);
    if (key == null || table == null)
      return (null);

    if (map_table == null)
      return (null);

    keys = map_table.keys ();

    if (!keys.hasMoreElements ())
      return (null);

    query.append ("Update " + table + " Set ");

    while (keys.hasMoreElements ()) {
      att = (String) keys.nextElement ();
      
      if (att.equals (key))
        continue; // check

      tmp = (Object) attr.get (att);

      if (!((tmp == null) || tmp instanceof String || 
            tmp instanceof java.util.Date)) {
        handleError ("build_update_query:invalid type for "
        + att);
        return (null);
      }

      column = getMapping (att);

      if (column == null) {
        handleError ("build_update_query: invalid mapping for " +
          att);
        return (null);
      }      

      if (first) {
        first = false;
      } else 
        query.append (", ");

      query.append (column);

      if (tmp == null) {
        query.append ("="+null);
      } else if (tmp instanceof String) {
        value = (String) attr.get (att);
        query.append ("="+value);
      } else if (tmp instanceof java.util.Date) {
        query.append ("= ?");
      } 

/*      
      if (keys.hasMoreElements ())
       query.append (","); 
*/

      //value = (String) attr.get (key);

    }

    query.append (" where ");

    value = (String) attr.get (key);

    if (value == null) {
      handleError ("build_update_query: invalid key (null)");
      return (null);
    }

    key_column = getMapping ((String) key);

    if (key_column == null) {
      handleError ("build_update_query: invalid mapping for "
       + key);
      return (null);      
    }

    query.append (key_column + "=" + value);
    
    handleTrace ("query:" + query);

    return (query.toString ());

  }


  void setParameters (PreparedStatement pst)
  {

    Enumeration keys; 
    String att;
    //String value;
    Object tmp;
    int i;
    java.sql.Date tmp1;

    if (pst == null)
      return;

    if (map_table == null)
      return;

    if (attr == null)
      return;

//  keys = attr.keys ();
    keys = map_table.keys ();

    i = 1;
    while (keys.hasMoreElements ()) {
      att = (String) keys.nextElement ();
      
      if (att.equals (key))
        continue;

      tmp = (Object) attr.get (att);

      if (tmp instanceof java.util.Date) {

        tmp1 = new 
        java.sql.Date (((java.util.Date)tmp).getTime ());
        try {
          pst.setDate (i, tmp1);
        } catch (Exception ex) {
          handleException (ex);
        }
        i++;
      } 

    }    

  }


  // build_insert_query: build insert query

  private String build_insert_query () {
    StringBuffer query = new StringBuffer ();
    Enumeration keys; 
    String key;
    String value;
    Object tmp;
    String attn;

    if (attr == null || table == null)
      return (null);

    if (map_table == null)
      return (null);

    keys = map_table.keys ();

    if (!keys.hasMoreElements ())
      return (null);

    query.append ("Insert into " + table + " (");

    while (keys.hasMoreElements ()) {
      key = (String) keys.nextElement ();
      
      //attn = (String) map_table.get (key);
      attn = getMapping (key);

      if (attn == null) {
        handleError ("build_update_query: invalid mapping for " +
          attn);
        return (null);
      }  
      
      query.append (attn);
        
      if (keys.hasMoreElements ())
       query.append (","); 
      //value = (String) attr.get (key);

    }
    query.append (") values (");

    keys = map_table.keys ();

    while (keys.hasMoreElements ()) {

      key = (String) keys.nextElement ();

      tmp = attr.get (key);

      if (tmp == null) {
        query.append ("null");
      } else if (tmp instanceof String) {
        value = (String) attr.get (key); // check
        query.append (value);
      } else if (tmp instanceof java.util.Date) {
        query.append ("?");
      } 

      //value = (String) attr.get (key);

      //query.append (value);

      if (keys.hasMoreElements ())
       query.append (","); 

    }
    query.append (")");

    handleTrace ("query:" + query);

    return (query.toString ());

  }

  // find: find record

  private JtObject find () {
     JtMessage msg;
     String query;
     Object conn;
     ResultSet res;
     JtObject jout;

     msg = new JtMessage ();

     if (db == null)
       realize ();

     attr = getAttributes (); // check

     query = build_select_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
       if (propagateException (db) != null)
         return (null);
     }

     msg.setMsgId ("JtEXECUTE_QUERY");
     msg.setMsgContent (query);


     res = (ResultSet) this.sendMessage (db, msg);

     if (propagateException (db) != null)
         return (null);


     if (res == null)
       return (null);

     try {
       if (!res.next()) // check
         return (null);
     } catch (Exception e){
       handleException (e);
       return (null);
     } 

     jout = map (res);

     try {
       res.close ();
     }
     catch (Exception ex) {
       handleException (ex);
     }

     return (jout);
  }


  // find: find records based on a query

  private JtObject findRecords (String query) {
     JtMessage msg;
     Object conn;
     ResultSet res;
     JtObject obj;
     JtCollection col;

     msg = new JtMessage ();
     col = new JtCollection ();


     if (db == null)
       realize ();

     attr = getAttributes (); // check

     //query = build_select_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
       if (propagateException (db) != null)
         return (null);
     }

     msg.setMsgId ("JtEXECUTE_QUERY");
     msg.setMsgContent (query);


     res = (ResultSet) this.sendMessage (db, msg);

     if (propagateException (db) != null)
         return (null);


     if (res == null)
       return (null);

     msg.setMsgId ("JtADD");

     try {

       while (res.next()) {
        obj = (JtObject) this.getClass().newInstance ();

        ((JtDAO) obj).setKey (key);
        ((JtDAO) obj).setTable (table);

        obj = mapRow (obj, res);
        if (obj == null)
          continue; // check

        msg.setMsgContent (obj);
        sendMessage (col, msg);

       }

     } catch (Exception e) {
       handleException (e);
       return (null);
     } 

     try {
       res.close ();
     }
     catch (Exception ex) {
       handleException (ex);
     }

     return (col);
  }



  // delete: delete record

  private Object delete () {
     JtMessage msg;
     String query;
     Object conn;
     //ResultSet res;
     Object out;

     msg = new JtMessage ();

     if (db == null)
       realize ();

     attr = getAttributes (); // check

     query = build_delete_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
       //return (propagateException (db));
     }

     msg.setMsgId ("JtUPDATE");
     msg.setMsgContent (query);

     out = this.sendMessage (db, msg);
     propagateException (db);

     if (out instanceof Integer) {
       if (((Integer) out).intValue () != 1)
         return (null);
       else
         return (this);
     } else
       return (null);

  }

  private String getRSValue (ResultSet rs, String column)
  {
    String value = null;

    if (rs == null || column == null)
      return (null);

    try {
      value = rs.getString (column);
    } catch (Exception e) {
      handleException (e);
    }
    return (value);
  }  

  // getRSDateValue 

  private String getRSDateValue (ResultSet rs, String column)
  {
    String value = null;
    DateFormat df = DateFormat.getDateInstance();

    if (rs == null || column == null)
      return (null);

    try {
      java.sql.Date date = rs.getDate (column);
      if (date == null)
        return (null); // check
      value = df.format (date); // check
      //value = rs.getString (column);
    } catch (Exception e) {
      handleException (e);
    }
    return (value);
  }  

  private String getRSBooleanValue (ResultSet rs, String column)
  {
    String value = null;

    if (rs == null || column == null)
      return (null);

    try {
      boolean b = rs.getBoolean (column);
      //if (b == null)
      //  return (null); // check
      
      if (b)
        return ("true");
      else
        return ("false");

      //value = b.toString ();
    } catch (Exception e) {
      handleException (e);
    }
    return (value);
  } 


  private JtObject map (ResultSet rs) {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   Class p;
   BeanInfo info = null;
   String value; // check
   String column;

     try {

       info = Introspector.getBeanInfo(
              this.getClass (), this.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return (null);
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {
       //System.out.print ("Attribute:" + 
            //prop[i].getName());
       p = prop[i].getPropertyType();

       column = getMapping (prop[i].getName());

       if (column == null) {
         continue;
       }

       if (p.getName().equals ("java.util.Date") ){
         value = getRSDateValue (rs, column);
       } else if (p.getName().equals ("boolean") ){         
         value = getRSBooleanValue (rs, column);
       } else // check
         value = getRSValue (rs, column);

       setValue (this, prop[i].getName(), value);     
 
     }
     return (this);
  }

  private JtObject mapRow (JtObject obj, ResultSet rs) {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   Class p;
   BeanInfo info = null;
   String value; // check
   String column;

     try {

       info = Introspector.getBeanInfo(
              obj.getClass (), obj.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return (null);
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {
       //System.out.print ("Attribute:" + 
            //prop[i].getName());
       p = prop[i].getPropertyType();

       column = getMapping (prop[i].getName());

       if (column == null) {
         continue;
       }

       if (p.getName().equals ("java.util.Date") ){
         value = getRSDateValue (rs, column);
       } else if (p.getName().equals ("boolean") ){         
         value = getRSBooleanValue (rs, column);
       } else // check
         value = getRSValue (rs, column);

       setValue (obj, prop[i].getName(), value);     
 
     }
     return (obj);
  }

  private void clear () {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   BeanInfo info = null;
   Class p;
   String value = null;

     try {

       info = Introspector.getBeanInfo(
              this.getClass (), this.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return;
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {

       if (prop[i].getName().equals (key))
         continue;


       //handleTrace ("JtDAO.clear:" + prop[i].getName());

       p = prop[i].getPropertyType();
       if (p.getName().equals ("java.lang.String") ){
         value = null;
       } else if (p.getName().equals ("int") ||
                  p.getName().equals ("float") ||
                  p.getName().equals ("double") ||
                  p.getName().equals ("long") ||
                  p.getName().equals ("short") ||
                  p.getName().equals ("byte")) {
         value = "0";
       } else if (p.getName().equals ("java.util.Date")) {
         value = null;
       } else if (p.getName().equals ("boolean")) {
         value = "false";
       } else {
         handleWarning ("JtDAO.clear:unknown type:" + 
           p.getName());
         continue;
       }
       setValue (this, prop[i].getName(), value);  // check   
 
     }

  }


 // update: update record

  private Object update () {
     JtMessage msg;
     String query;
     Object conn;
     Object out;

     msg = new JtMessage ();
     PreparedStatement pst = null;
     
     if (db == null)
       realize ();

     attr = getAttributes ();
     query = build_update_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
     }

/*
     msg.setMsgId ("JtUPDATE");
     msg.setMsgContent (query);

     this.sendMessage (db, msg);
*/
     msg.setMsgId ("JtPREPARE_STATEMENT");
     msg.setMsgContent (query);

     pst = (PreparedStatement) this.sendMessage (db, msg);

     if (propagateException (db) != null)
       return (null);

     if (pst == null)
       return (null);  // check

     setParameters (pst);

     if (propagateException (db) != null)
       return (null);

     msg.setMsgId ("JtEXECUTE_PREPARED_UPDATE");
     msg.setMsgContent (pst);
     out = this.sendMessage (db, msg);

     try {
       pst.close ();
     }
     catch (Exception ex) {
       handleException (ex);
     }

     if (propagateException (db) != null)
       return (null);

     if (out instanceof Integer) {
       if (((Integer) out).intValue () != 1)
         return (null);
       else
         return (this);
     } else
       return (null);

  }


  // propagateException: 

  private Exception propagateException (JtObject obj)
  {
    Exception ex;

    if (obj == null)
      return null;

    ex = (Exception) 
     getValue (obj, "objException");

    if (ex != null)
      setValue (this, "objException", ex);

    return (ex);
  }
 
  // resetExceptions: 

  private void resetExceptions ()
  {

    setValue (this, "objException", null);
    if (db != null)
      setValue (db, "objException", null);

  }
 

  // insert: insert record

  private Object insert () {
     JtMessage msg;
     String query;
     Object conn;
     PreparedStatement pst;
     Object out = null;

     msg = new JtMessage ();

     if (db == null)
       realize ();

     attr = getAttributes ();
     query = build_insert_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
       if (propagateException (db) != null)
         return (null);
     }

     msg.setMsgId ("JtPREPARE_STATEMENT");
     msg.setMsgContent (query);

     pst = (PreparedStatement) this.sendMessage (db, msg);

     if (pst == null)
       return (null);  // check

     setParameters (pst);

     msg.setMsgId ("JtEXECUTE_PREPARED_UPDATE");
     msg.setMsgContent (pst);
     out = this.sendMessage (db, msg);
       

/*
     msg.setMsgId ("JtUPDATE");
     msg.setMsgContent (query);

     this.sendMessage (db, msg);
*/

     try {
       pst.close ();
     }
     catch (Exception ex) {
       handleException (ex);
     }
     
     propagateException (db);

     if (out == null)
       return (null);

     if (out instanceof Integer) {
       if (((Integer) out).intValue () != 1)
         return (null);
       else
         return (this);
     } else
       return (null);


  }

  /**
   * Specifies the Database Connection.
   */

  public void setConnection (Connection newConnection) {
    connection = newConnection;
  }


  /**
   * Returns the Database Connection.
   */

  public Connection getConnection () {
    return (connection);
  }


  /**
   * Specifies the name of the configuration file.
   */

  public void setConfigFile (String configFile) {
    this.configFile = configFile;
  }

  /**
   * Returns the name of the configuration file.
   */

  public String getConfigFile () {
    return (configFile);
  }
  private Hashtable getAttributes () {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   //Class p;
   Method m;
   BeanInfo info = null;
   Object value;
   Hashtable attr;

     attr = new Hashtable ();

     if (map_table == null) {
       handleError 
       ("JtDAO.getAttributes: attributes/database mapping is missing");
       return (null);
     }

     try {
       info = Introspector.getBeanInfo(
              this.getClass (), this.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return (null);
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
             return (null);
         }

         if (getMapping (prop[i].getName()) == null)
           continue;

         value = m.invoke (this, null);
         if (value == null) {
//           attr.put (prop[i].getName(), value); 
           continue;
         }

         if (value instanceof String) {
           attr.put (prop[i].getName(), "'" + value + "'"); 
           //System.out.println ("=" + value);
           continue;
         }

         if (value instanceof Integer ||
             value instanceof Long || 
             value instanceof Float ||
             value instanceof Byte ||
             value instanceof Boolean ||
             value instanceof Short ||
             value instanceof Double) {
           attr.put (prop[i].getName(), value.toString () ); 
           //System.out.println ("=" + value);
           continue;
         }

         if (value instanceof java.util.Date) {
           attr.put (prop[i].getName(), value); 
           continue;
         }

        } catch (Exception e) {
         handleException(e);
         return (null);
        }
      }

      return (attr);
   }

  private boolean validateAttribute (String att_name) {


   PropertyDescriptor[] prop;
   int i;
// Class p;

   BeanInfo info = null;

     if (att_name == null)
       return (false);

     try {

       info = Introspector.getBeanInfo(
              this.getClass (), this.getClass ().getSuperclass());
     } catch(Exception e) {
        handleException (e);
        return (false);
     }

     prop = info.getPropertyDescriptors();
     for(i = 0; i < prop.length; i++) {
//       System.out.print ("Attribute:" + 
//            prop[i].getName());
//       p = prop[i].getPropertyType();
       if ((prop[i].getName()).equals (att_name))
         return (true);
     }
     return (false);
   }

  private void print () {

   //Object args[];
   PropertyDescriptor[] prop;
   int i;
   //Class p;
   Method m;
   BeanInfo info = null;
   Object value;


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

         System.out.println (this.getClass ().getName () + "." +
             prop[i].getName() + ":" + value);


        } catch (Exception e) {
         handleException(e);
        }
      }

   }

  /**
   * Specifies the attribute to be used as the database key.
   */

  public void setKey (Object key) {
    this.key = key;
  }

 /**
   * Returns the attribute to be used as the database key.
   */

  public Object getKey () {
    return (key);
  }


  /**
   * Specifies the name of the database table.
   */

  public void setTable (String table) {
    this.table = table;
  }


  /**
   * Returns the name of the database table.
   */

  public String getTable () {
    return (table);
  }

  private void readConfigFile () {
    JtMessage msg;
    JtObject cf;

    if (configFile == null)
      return;

    msg= new JtMessage ();

    msg.setMsgId ("JtPARSE");
    msg.setMsgReplyTo (this);

    cf = (JtObject) createObject ("Jt.xml.JtXMLMsgReader", "configFile");
    setValue (cf, "uri", configFile);

    sendMessage (cf, msg);

  }
  private String build_key_query () {
      StringBuffer query = new StringBuffer ();
      String value;
      //Object tmp;
      //Enumeration keys; 
      //String att, tmp;
      String key_column;

      if (key == null ||  table == null)
        return (null);

      if (attr == null)
        return (null);

      value = (String) attr.get (key);

      if (value == null) {
        handleError ("build_key_query: invalid key (null)");
        return (null);
      }

      key_column = getMapping ((String) key);

      if (key_column == null) {
        handleError ("build_key_query: invalid mapping for "
         + key);
        return (null);      
      }


      query.append ("Select Max (" + key_column + ") from " + table);   
      
      handleTrace ("query:" + query);

      return (query.toString ());

    }
  
  // calculateKey: calculate a new key

  private Object calculateKey () {
     JtMessage msg;
     String query;
     Object conn;
     ResultSet res;
     //Object out;
     long ltmp = 0L;
     Exception ex1;

     msg = new JtMessage ();

     if (db == null)
       realize ();

     attr = getAttributes (); // check

     query = build_key_query ();

     if (query == null || db == null)
       return (null);

     conn = this.getValue (db, "connection");

     // Connect to the data source

     if (conn == null) {
       msg.setMsgId ("JtCONNECT");
       this.sendMessage (db, msg);
       //return (propagateException (db));
     }

     msg.setMsgId ("JtEXECUTE_QUERY");
     msg.setMsgContent (query);

     res = (ResultSet) this.sendMessage (db, msg);
     ex1 = propagateException (db);
     
     if (ex1 != null)
         return (new Long (-1L));
     
     try
    {
      if (res.next()) {
         ltmp = res.getLong(1);           
      }
    }
    catch (Exception ex)
    {
      handleException (ex);
      return (new Long (-1L));
    }
     return (new Long(ltmp+1));
     
  }
  
  void realize () {
    //JtMessage msg;

    handleTrace ("JtDAO:realize");

    if (db != null)
      return;

    //msg = new JtMessage ();

    db = (JtObject) this.createObject ("Jt.JtJDBCAdapter", "db");
    // this.setValue (db, "objTrace", "1"); // check

    if (configFile != null)
      readConfigFile ();
  }

  void destroy () {
    if (db == null)
      return;
    //sendMessage (db, new JtMessage ("JtCLOSE"));
    closeConnection ();
    db = null;

  }

  private static void test () {
    JtObject main;
    //String query;
    JtMessage msg;

    main = new JtObject ();
//  main.setValue (main, "objTrace", "1");
    main.createObject ("Jt.JtMessage", "message");
    msg = new JtMessage ();

    main.createObject ("Jt.JtDAO", "dao");
//  main.setValue ("dao", "objTrace", "1");

    msg.setMsgId ("JtREALIZE");
    main.sendMessage ("dao", msg);
  }

  /**
    * Unit tests the messages processed by JtDAO.
    */

  public static void main (String[] args) {
     
     test ();
  }
}