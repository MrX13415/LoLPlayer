package audioplayer.player.listener;

import audioplayer.player.AudioFile;
import audioplayer.player.AudioPlaylist;

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
