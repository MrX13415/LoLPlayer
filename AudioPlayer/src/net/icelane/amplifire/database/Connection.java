package net.icelane.lolplayer.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Connection {

	private String dbName;
	
	private Properties dbProperties;

	
	public void Connection(String addressBookName) {
	    this.dbName = addressBookName;
	    setDBSystemDir();
	    dbProperties = loadDBProperties();
	    String driverName = dbProperties.getProperty("derby.driver"); 
	    loadDatabaseDriver(driverName);

	}

	private Properties loadDBProperties() {
	    InputStream dbPropInputStream = null;
	    dbPropInputStream = 
	        Connection.class.getResourceAsStream("Configuration.properties");
	    dbProperties = new Properties();
	    try {
	        dbProperties.load(dbPropInputStream);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return dbProperties;
	}

	private void loadDatabaseDriver(String driverName) {
	    // Load the Java DB driver.
	    try {
	        Class.forName(driverName);
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	}  
	
	private void setDBSystemDir() {
	    // Current jar directory ...
	    String systemDir = new File(".").getAbsolutePath();

	    // Set the db system directory
	    System.setProperty("derby.system.home", systemDir);
	}
	
}
