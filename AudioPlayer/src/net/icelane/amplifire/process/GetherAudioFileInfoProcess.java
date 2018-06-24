/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icelane.amplifire.process;

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.player.AudioPlaylist;
import net.icelane.amplifire.player.AudioFile.UnsupportedFileFormatException;
import net.icelane.amplifire.process.api.Process;

/**
 * amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class GetherAudioFileInfoProcess extends Process {

	public GetherAudioFileInfoProcess(AppCore control) {
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
	
				control.getPlaylistInterface().getPlaylistTableModel().updateData(i, pl.get(i));
			} catch (UnsupportedFileFormatException e) {
				control.raiseNotSupportedFileFormatError(pl.get(i), e, false);
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
			
			control.getStatusbar().setMessageText(String.format("Gethering file informations ... (%s/%s)", i, pl.size()));
			control.getStatusbar().getBar().setValue(i);
		}

		control.getStatusbar().setVisible(false);
	}
}
