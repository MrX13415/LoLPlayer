/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.process;

import audioplayer.Application;
import audioplayer.Control;
import audioplayer.player.codec.AudioFile;
import java.sql.SQLException;

/**
 *
 * @author dausol
 */
public class SavePlaylistDBProcess extends Process{

    public SavePlaylistDBProcess(Control control) {
        super(control);
    }
        
    @Override
    public void process(){
    	if (!Application.getApplication().getDatabase().getConnection().isConnected()){
    		System.out.println("Saveing playlist to DataBase ...\tSKIPED");
    		return;
    	}
    	System.out.println("Saveing playlist to DataBase ...\t");

    	try {
    		control.getStatusbar().getBar().setMinimum(0);
    		control.getStatusbar().getBar().setMaximum(control.getAudioPlaylist().size());
    		control.getStatusbar().setMessageText(String.format("Saveing playlist ... (%s/%s)", 0, control.getAudioPlaylist().size()));
    		control.getStatusbar().setVisible(true);
						    	
			Application.getApplication().getDatabase().clearPlaylist();
			
			for (int i = 0; i < control.getAudioPlaylist().size(); i++) {
				control.getStatusbar().setMessageText(
						String.format("Saveing playlist ... (%s/%s)", i,control.getAudioPlaylist().size()));
				AudioFile audioFile = control.getAudioPlaylist().get(i);	
				Application.getApplication().getDatabase().addPlaylistItem(audioFile);
				control.getStatusbar().getBar().setValue(i);
			}
			
			System.out.println("Saveing playlist to DataBase ...\tOK");
    	} catch (SQLException e) {
    		System.out.println("Saveing playlist to DataBase ...\tERROR");
		}
		
		control.getStatusbar().setVisible(false);
    }
}
