package audioplayer.player.listener;

import audioplayer.player.AudioPlaylist;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistEvent {

    protected AudioPlaylist source;

    public PlaylistEvent(AudioPlaylist source) {
        this.source = source;
    }

    public AudioPlaylist getSource() {
        return source;
    }

}
