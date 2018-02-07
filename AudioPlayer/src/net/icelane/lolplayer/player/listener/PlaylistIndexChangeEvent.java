package net.icelane.lolplayer.player.listener;

import net.icelane.lolplayer.player.AudioPlaylist;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistIndexChangeEvent extends PlaylistEvent{

    protected int previousIndex;
    protected int newIndex;

    public PlaylistIndexChangeEvent(AudioPlaylist source, int previousIndex, int newIndex) {
        super(source, null);
        this.previousIndex = previousIndex;
        this.newIndex = newIndex;
    }

    public int getPriorIndex() {
        return previousIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }
    
}
