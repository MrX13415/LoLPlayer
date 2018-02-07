package net.icelane.lolplayer.database;

import java.sql.DriverManager;

import net.icelane.lolplayer.database.connetionlayer.DB4OClient;
import net.icelane.lolplayer.database.connetionlayer.DB4OServer;


public class DBConnectionLayer {

	private DB4OServer server;
	private DB4OClient client;
		
	public void connectDB(){
		initServer();
		connectClient();
	}
	
	public boolean initServer(){
        try {
        	System.out.print("Start DataBase Server ...\t\t\t");
            
//        	server = new DB4OServer(DataBase_FileName);
//        	server.startServer();

            System.out.println("OK");
            return true;
        } catch (Exception ex) {
            System.out.println("ERROR");
            System.err.println("\tError: " + ex);
        }
        return false;
    }
	
	public boolean connectClient(){
        try {
        	System.out.print("Connect to DataBase Server ...\t\t");
             
//        	client = new DB4OClient();
//        	client.connectClient();

            System.out.println("OK");
            return true;
        } catch (Exception ex) {
            System.out.println("ERROR");
            System.err.println("\tError: " + ex);
        }
        return false;
    }
	
}
