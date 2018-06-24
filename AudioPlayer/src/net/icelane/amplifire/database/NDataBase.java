package net.icelane.amplifire.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.icelane.amplifire.database.sql.PostgresSQLConnectionLayer;
import net.icelane.amplifire.player.AudioFile;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class NDataBase {
    
	public transient static final String dbFileName = "lolp2data.db";
	
    private DBConnectionLayer dbConnectionLayer;
    
    public NDataBase() {
    	dbConnectionLayer = new DBConnectionLayer();
    }
    
    public DBConnectionLayer getConnection(){
    	return dbConnectionLayer;
    }
    
    public void getAudioFile(){
    	
    }
    
    public void addAudioFile(AudioFile af){
    	
    }
    
}
