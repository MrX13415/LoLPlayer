/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.process;

import audioplayer.Application;
import audioplayer.PlayerControl;
import audioplayer.player.AudioFile;
import audioplayer.player.AudioFile.UnsupportedFileFormatException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author dausol
 */
public class LoadPlaylistDBProcess extends Process{

    public LoadPlaylistDBProcess(PlayerControl control) {
        super(control);
    }
        
    @Override
    public void process(){
    	
    	if (!Application.getApplication().getDatabase().getConnection().isConnected()){
    		System.out.println("Load playlist from DataBase ...\t\tSKIPED");
    		return;
    	}
    	System.out.println("Load playlist from DataBase ...\t\t");

    	try {
    		HashMap<Integer, AudioFile> playlist = Application.getApplication().getDatabase().getPlaylistItems();
    		SortedSet<Integer> keys = new TreeSet<Integer>(playlist.keySet());
    		
    		control.getStatusbar().getBar().setMinimum(0);
    		control.getStatusbar().getBar().setMaximum(keys.size());
    		control.getStatusbar().setMessageText(String.format("Loading playlist ... (%s/%s)", 0, keys.size()));
    		control.getStatusbar().setVisible(true);
			
    		int i = 0;
			for (Integer key : keys) {
				control.getStatusbar().setMessageText(
						String.format("Loading playlist ... (%s/%s)", i, keys.size()));
				try {
					AudioFile af = playlist.get(key);
					af.initAudioFile();
					control.getAudioPlaylist().add(af);
				} catch (UnsupportedFileFormatException e) {}
				control.getStatusbar().getBar().setValue(i);
				i++;
				
				if (!running) break;
			}
			
			System.out.println("Load playlist from DataBase ...\t\tOK");
    	} catch (SQLException e) {
    		System.out.println("Load playlist from DataBase ...\t\tERROR");
		}
		
    	control.getStatusbar().setVisible(false);
    }
}
