package net.icelane.lolplayer.database.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class DataBase {
    
	protected transient static final String dbConfigFile = "db";

	protected String DBname = "";
	protected String DBurl = "";
	protected String DBusername = "";
	protected String DBpassword = "";

    private DerbySQLConnectionLayer dbConnectionLayer;
    
    public DataBase() {
    	dbConnectionLayer = new DerbySQLConnectionLayer(this);
    }
    
    public DataBase(String DBname, String DBurl) {
    	this();
        this.DBname = DBname;
        this.DBurl = DBurl;
    }
    
    public DataBase(String DBname, String DBurl, String DBusername, String DBpassword) {
        this(DBname, DBurl);
        this.DBusername = DBusername;
        this.DBpassword = DBpassword;
    }

    public String getDBname() {
        return DBname;
    }

    public String getDBurl() {
        return DBurl;
    }

    public String getDBusername() {
        return DBusername;
    }

    public String getDBpassword() {
        return DBpassword;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public void setDBurl(String DBurl) {
        this.DBurl = DBurl;
    }

    public void setDBusername(String DBusername) {
        this.DBusername = DBusername;
    }

    public void setDBpassword(String DBpassword) {
        this.DBpassword = DBpassword;
    }

    /** Loads the db settings from an config. file 
     * 
     */
    public void loadConfig(){
         BufferedReader br = null;
         try {
        	 System.out.print("Loading DB configuartion file ...\t");
        	 
             br = new BufferedReader(new FileReader(new File(dbConfigFile)));

             Class<?> c = this.getClass();
             
             if (!c.getName().equals("audioplayer.database.DataBase")) c = c.getSuperclass();
             
             Field[] fields = c.getDeclaredFields();
             
             while(br.ready()){
                String line = br.readLine();
                
                if (line.startsWith("#") || !line.contains(":")) continue;
                
                String key = line.substring(0, line.indexOf(":")).trim();
                String val = line.substring(line.indexOf(":") + 1).trim();
                
                for (Field field : fields) {
					if (field.getModifiers() == Modifier.TRANSIENT) continue;
					if (field.getModifiers() == Modifier.FINAL) continue;
					
					if (field.getName().equals(key)){
						field.set(this, val);						
						break;
					}
				}
             }

             System.out.println("OK");
         
         } catch (Exception ex) {
        	 System.out.println("ERROR");
        	 System.err.println("Error: " + ex);
         }finally{
             if (br != null) try {
                 br.close();
             } catch (IOException ex) {}
         }
         
    }
    
    public DerbySQLConnectionLayer getConnection() {
		return dbConnectionLayer;
	}

	public  void createTables(PostgresSQLConnectionLayer dbcl){
/*
 * -- =============================================
   --
   --  Audio-Player 
   --
   --  Author: Oliver Daus
   --
   --
   --  PostgresSQL "create-table" commands
   --
   -- =============================================

   -- Tabelle Filepath
   CREATE TABLE Filepath (
     ID            serial                     PRIMARY KEY,             -- Song ID
     FilePath      varChar(255)   NOT NULL                             -- The path to the song
   );

   -- Tabelle Title
   CREATE TABLE Title (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Title         varchar(100)                                        -- One or more titles
   );

   -- Tabelle Author
   CREATE TABLE Author (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Author        varchar(100)                                        -- One or more authors
   );


   -- Tabelle Album
   CREATE TABLE Album (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Album         varchar(100)                                        -- One or more albums
   );


   -- Tabelle Genre
   CREATE TABLE Genre (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Genre         varchar(100)                                        -- One or more genres
   );


   -- Tabelle Statistics
   CREATE TABLE Statistics (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Frequency     smallint       NOT NULL                             -- The frequency of the Song
   );


   -- Tabelle Playlist
   CREATE TABLE Playlist (
     No            serial                     PRIMARY KEY,             -- Position in the Playlist
     SongID        int            NOT NULL                             -- ID of the Song
   );


   -- Tabelle Rating
   CREATE TABLE Rating (
     ID            serial                     PRIMARY KEY,             -- Song ID
     Rate          smallint       NOT NULL                             -- Raiting from 0 to 5
   );


 */
    }

}
