package net.icelane.lolplayer.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import net.icelane.lolplayer.AppCore;
import net.icelane.lolplayer.player.AudioFile;
import net.icelane.lolplayer.process.api.Process;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class LoadPlaylistProcess extends Process{
	
	private String packageName = "./data/";
	private String resource = "playlist.dat";
	private String header = "LoLPlayer_Playlist_ObjectDataFile#1.0.0.0";
	
	public LoadPlaylistProcess(AppCore control) {
		super(control);
	}

	@Override
	public void process() {

    	System.out.println("Load playlist ...\t\t\t");

    	try {
    		File f = new File(packageName + resource);
    		
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

    		String header = (String) ois.readObject();
			
    		if (!header.equals(this.header)){
    			System.err.println("Error: Invalied PlaylistObjectData file");
    			System.out.println("Load playlist ...\t\t\tSKIPED: Invalied file");
    			ois.close();
    			return;
    		}
    		
    		int count = (int) ois.readInt();
    		
    		control.getStatusbar().getBar().setMinimum(0);
    		control.getStatusbar().getBar().setMaximum(count);
    		control.getStatusbar().setMessageText(String.format("Loading playlist ... (%s/%s)", 0, count));
    		control.getStatusbar().setVisible(true);
    		
    		for (int i = 0; i < count; i++) {
    			if (!running) break;
    			
				try {
					Object obj = ois.readObject();
					
					if (obj instanceof String){
						File ff = new File((String) obj);
						if (ff.exists()) control.getAudioPlaylist().add(new AudioFile(ff));
						else System.err.println("Error: File not exists");
					}else{
						System.err.println("Error: Unknow Playlist content Object");
					}

					control.getStatusbar().setMessageText(String.format("Loading playlist ... (%s/%s)", i + 1, count));
					control.getStatusbar().getBar().setValue(i);
					
				} catch (Exception e) {
					System.err.println("Error: While loading Playlist ... ABORTING");
					break;
				}
			}
			
			ois.close();
			
			System.out.println("Load playlist ...\t\t\tOK");
    	} catch (Exception e) {
    		System.out.println("Load playlist ...\t\t\tERROR: "+ e);
		}
	}
	
	
}
