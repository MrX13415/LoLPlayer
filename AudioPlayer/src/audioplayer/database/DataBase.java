package audioplayer.database;

/**
 *
 * @author dausol
 */
public class DataBase {
    
    private String DBname = "";
    private String DBurl = "";
    private String DBusername = "";
    private String DBpassword = "";

    public DataBase() {
    }
    
    public DataBase(String DBname, String DBurl) {
        this.DBname = DBname;
        this.DBurl = DBurl;
    }
    
    public DataBase(String DBname, String DBurl, String DBusername, String DBpassword) {
        this.DBname = DBname;
        this.DBurl = DBurl;
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
    
    public void createTables(DBConnectionLayer dbcl){
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
