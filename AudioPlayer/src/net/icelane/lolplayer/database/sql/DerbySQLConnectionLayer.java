package audioplayer.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class DerbySQLConnectionLayer {
    
    private DataBase db;
    
    //Connection to the database
    private Connection database;
    //Database command channel
    private Statement statement;
        
    private boolean connected;
    
    public DerbySQLConnectionLayer(DataBase db){
        this.db = db;
    }
    
    public boolean connectDB(){
        try {
        	System.out.print("Connect to DataBase ...\t\t\t");
        	
            //Load an initialize database driver class ...
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            	
            //Connect to db ...
            database = DriverManager.getConnection(db.getDBurl(), db.getDBusername(), db.getDBpassword());

            //Init. command channel ...
            statement = database.createStatement();
            
            connected = true;
            
            System.out.println("OK");
            
            return connected;
        } catch (Exception ex) {
            System.out.println("ERROR");
            System.err.println("\tError: " + ex);
        }
        return connected;
    }
        
    public ResultSet sendSQLQuery(String sql) throws SQLException{
        return statement.executeQuery(sql);
    }
    
    public void sendSQLUpdate(String sql) throws SQLException{
        statement.executeUpdate(sql);
    }
    
    public ResultSet getValues(String view) throws SQLException{
    	String sql = String.format("SELECT * FROM %s;", view);
    	return sendSQLQuery(sql);
    }
    
    public String getValue(String view, String valName, String condValName, String condValue) throws SQLException{
    	String sql = String.format("SELECT %s FROM %s WHERE %s = %s;", valName, view, condValName, condValue);
    	ResultSet rs = sendSQLQuery(sql);
    	rs.next();
    	return rs.getString(valName);
    }
    
    public boolean closeDB(){
        try{
            //Close database connection ...
            if (database != null) database.close();
            //Close command channel ...
            if (statement != null) statement.close();
            
            return true;
        }catch(Exception ex){
            
        }
        return false;
    }

	public boolean isConnected() {
		return connected;
	}
    
}
