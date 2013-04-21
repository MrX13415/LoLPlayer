package audioplayer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author dausol
 */
public class DBConnectionLayer {
    
    private DataBase db;
    
    //Connection to the database
    private Connection database;
    //Database command channel
    private Statement statement;
        
    public DBConnectionLayer(DataBase db){
        this.db = db;
    }
    
    public boolean connectDB(){
        try {
            //Load driver class ...
            Class.forName("org.postgresql.Driver").newInstance();
            
            //Connect to db ...
            database = DriverManager.getConnection(db.getDBurl(), db.getDBusername(), db.getDBpassword());
           
            //Init. command channel ...
            statement = database.createStatement();
            
            return true;
        } catch (Exception ex) {
            System.out.println("Keine Datenbankverbindung möglich: " + ex);
        }
        return false;
    }
        
    public ResultSet sendQuery(String sql) throws SQLException{
        return statement.executeQuery(sql);
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
    
}
