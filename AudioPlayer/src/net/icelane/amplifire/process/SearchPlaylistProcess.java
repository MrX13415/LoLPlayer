/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icelane.amplifire.process;

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.player.AudioFile;
import net.icelane.amplifire.player.AudioPlaylist;
import net.icelane.amplifire.process.api.Process;
import net.icelane.amplifire.ui.components.playlist.PlaylistTableModel;

/**
 * amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class SearchPlaylistProcess extends Process {

	public SearchPlaylistProcess(AppCore control) {
		super(control);
	}

	@Override
	public void process() {

		AudioPlaylist pl = control.getAudioPlaylist();
		
		control.getStatusbar().getBar().setMinimum(0);
		control.getStatusbar().getBar().setMaximum(pl.size());
		control.getStatusbar().setMessageText(String.format("Searching in playlist ... (%s/%s)", 0, pl.size()));

		//clear search results ...
		control.getSearchPlaylist().clear();
		
		String text = control.getPlaylistInterface().getSearchField().getText();
		if (text.isEmpty()){
			control.switchPlaylist(false);
			control.getStatusbar().setVisible(false);
			return;
		}
		
		control.switchPlaylist(true);
		control.getStatusbar().setVisible(true);

		for (int i = 0; i < pl.size(); i++) {
			AudioFile af = pl.get(i);
			
			String displayname = (String) PlaylistTableModel.createDataObject(i, af)[1];
			
			if (af.getTitle().toLowerCase().contains(text.toLowerCase())  ||
			    af.getAuthor().toLowerCase().contains(text.toLowerCase()) ||
			    af.getAlbum().toLowerCase().contains(text.toLowerCase())  ||
			    af.getGenre().toLowerCase().contains(text.toLowerCase())  ||
			    af.getName().toLowerCase().contains(text.toLowerCase())   ||
			    displayname.toLowerCase().contains(text.toLowerCase())	  ||
			    af.getFile().getName().toLowerCase().contains(text.toLowerCase())){
				
				//found!
				control.getSearchPlaylist().add(af);
				//update gui
				if (control.isSearchPlaylistActive()) control.getPlaylistInterface().getPlaylistTableModel().insertData(af);
				
				//prevent the list to be completely selected but only do this on the firts item
				if (i == 0)	control.getPlaylistInterface().getPlaylistTable().changeSelection(0, 0, false, false);
				
				control.getStatusbar().setMessageText(String.format("Searching in playlist ... (%s/%s)", i, pl.size()));
				control.getStatusbar().getBar().setValue(i);
			}
		}

		control.getStatusbar().setVisible(false);
	}
}
