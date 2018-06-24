package net.icelane.lolplayer.player.listener;

import net.icelane.lolplayer.player.AudioFile;
import net.icelane.lolplayer.player.AudioPlaylist;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistEvent {

    protected AudioPlaylist source;
    protected AudioFile audioFile;
    protected int index;

    public PlaylistEvent(AudioPlaylist source, AudioFile audioFile) {
        this.source = source;
        this.audioFile = audioFile;
    }
    
    public PlaylistEvent(AudioPlaylist source, AudioFile audioFile, int index) {
        this.source = source;
        this.audioFile = audioFile;
        this.index = index;
    }
    
    public int getIndex() {
		return index;
	}

	public AudioPlaylist getSource() {
        return source;
    }

	public AudioFile getAudioFile() {
		return audioFile;
	}

}
