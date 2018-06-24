package net.icelane.amplifire.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.process.api.Process;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class SavePlaylistProcess extends Process{
	
	private String packageName = "./data/";
	private String resource = "playlist.dat";
	private String header = "LoLPlayer_Playlist_ObjectDataFile#1.0.0.0";
	
	public SavePlaylistProcess(AppCore control) {
		super(control);
	}

	@Override
	public void process() {

    	System.out.println("Save playlist ...\t\t\t");

    	try {
    		File f = new File(packageName + resource);
    		f.delete();
    		f.getParentFile().mkdir();
    		f.createNewFile();
    		
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
    		
    		int count = control.getAudioPlaylist().size();
    		
    		control.getStatusbar().getBar().setMinimum(0);
    		control.getStatusbar().getBar().setMaximum(count);
    		control.getStatusbar().setMessageText(String.format("Saving playlist ... (%s/%s)", 0, count));
    		control.getStatusbar().setVisible(true);
    		
    		oos.writeObject(this.header);
    		oos.writeInt(count);
    		
    		for (int i = 0; i < count; i++) {
    			if (!running) break;
    			
				try {
					oos.writeObject(control.getAudioPlaylist().get(i).getFile().getAbsolutePath());
					
					control.getStatusbar().setMessageText(String.format("Saving playlist ... (%s/%s)", i, count));
					control.getStatusbar().getBar().setValue(i);
					
				} catch (Exception e) {
					System.err.println("Error: While saving Playlist ... ABORTING");
					break;
				}
			}
			
			oos.close();
			
			System.out.println("Save playlist ...\t\t\tOK");
    	} catch (Exception e) {
    		System.out.println("Save playlist ...\t\t\tERROR: "+ e);
		}
	}
	
	
}
