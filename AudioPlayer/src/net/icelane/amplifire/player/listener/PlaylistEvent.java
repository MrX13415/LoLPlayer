package net.icelane.amplifire.player.listener;

import net.icelane.amplifire.player.AudioFile;
import net.icelane.amplifire.player.AudioPlaylist;

/**
 *  amplifier - Audio-Player Project
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
