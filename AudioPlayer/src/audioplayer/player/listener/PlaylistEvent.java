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

    public PlaylistEvent(AudioPlaylist source, AudioFile audioFile) {
        this.source = source;
        this.audioFile = audioFile;
    }

    public AudioPlaylist getSource() {
        return source;
    }

	public AudioFile getAudioFile() {
		return audioFile;
	}

}
