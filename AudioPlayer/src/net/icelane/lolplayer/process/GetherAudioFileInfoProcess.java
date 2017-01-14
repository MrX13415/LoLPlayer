/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.process;

import audioplayer.PlayerControl;
import audioplayer.player.AudioFile.UnsupportedFileFormatException;
import audioplayer.player.AudioPlaylist;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class GetherAudioFileInfoProcess extends Process {

	public GetherAudioFileInfoProcess(PlayerControl control) {
		super(control);
	}

	@Override
	public void process() {

		AudioPlaylist pl = control.getAudioPlaylist();
		
		control.getStatusbar().getBar().setMinimum(0);
		control.getStatusbar().getBar().setMaximum(pl.size());
		control.getStatusbar().setMessageText(String.format("Gethering file informations ... (%s/%s)", 0, pl.size()));
		control.getStatusbar().setVisible(true);

		for (int i = 0; i < pl.size(); i++) {
			if (!running) break;
			
			try {
				pl.get(i).initialize();				
				control.getPlaylistInterface().getPlaylistTableModel().updateData(pl.indexOf(pl.get(i)), pl.get(i));
			} catch (UnsupportedFileFormatException e) {
				control.raiseNotSupportedFileFormatError(pl.get(i), e, false);
			} catch (Exception e) {

			}
			
			control.getStatusbar().setMessageText(String.format("Gethering file informations ... (%s/%s)", i, pl.size()));
			control.getStatusbar().getBar().setValue(i);
		}

		control.getStatusbar().setVisible(false);
	}
}
