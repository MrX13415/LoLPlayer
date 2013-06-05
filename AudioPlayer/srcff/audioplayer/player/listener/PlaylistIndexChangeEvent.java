package audioplayer.player.listener;

import audioplayer.player.AudioPlaylist;


public class PlaylistIndexChangeEvent extends PlaylistEvent{

    protected int previousIndex;
    protected int newIndex;

    public PlaylistIndexChangeEvent(AudioPlaylist source, int previousIndex, int newIndex) {
        super(source);
        this.previousIndex = previousIndex;
        this.newIndex = newIndex;
    }

    public int getPreviousIndex() {
        return previousIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }
    
}
