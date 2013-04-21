package audioplayer.player.listener;

import audioplayer.player.AudioPlaylist;


public class PlaylistEvent {

    protected AudioPlaylist source;

    public PlaylistEvent(AudioPlaylist source) {
        this.source = source;
    }

    public AudioPlaylist getSource() {
        return source;
    }

}
