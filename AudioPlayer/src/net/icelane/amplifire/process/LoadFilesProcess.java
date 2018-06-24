/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icelane.amplifire.process;

import java.io.File;

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.player.AudioFile;
import net.icelane.amplifire.process.api.Process;

/**
 * amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class LoadFilesProcess extends Process {

	private File[] file;

	public LoadFilesProcess(AppCore control, File[] file) {
		super(control);
		this.file = file;
	}

	@Override
	public void process() {

		control.getStatusbar().getBar().setMinimum(0);
		control.getStatusbar().getBar().setMaximum(file.length);
		control.getStatusbar().setMessageText(
				String.format("Loading file ... (%s/%s)", 0, file.length));
		control.getStatusbar().setVisible(true);

		for (int i = 0; i < file.length; i++) {
			control.getStatusbar().setMessageText(String.format("Loading file ... (%s/%s)", i, file.length));

			control.getStatusbar().getBar().setValue(i);

			control.getAudioPlaylist().add(new AudioFile(file[i]));
		}

		control.getStatusbar().setVisible(false);
	}
}
