package net.icelane.lolplayer.player.listener;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public interface PlayerListener {

	public void onPlayerStart(PlayerEvent event);
	
	public void onPlayerStop(PlayerEvent event);
	
	public void onPlayerNextSong(PlayerEvent event);
	
	public void onPlayerVolumeChange(PlayerEvent event);

	public void onPlayerPositionChange(PlayerEvent event);
        
    public void onPlaylistFileAdd(PlaylistEvent event);
    
    public void onPlaylistFileRemove(PlaylistEvent event);
    
    public void onPlaylistIncrement(PlaylistIndexChangeEvent event);

    public void onPlaylistDecrement(PlaylistIndexChangeEvent event);
    
    public void onPlaylistClear(PlaylistEvent event);
    
    public void onPlaylistIndexSet(PlaylistIndexChangeEvent event);
    
    public void onPlaylistMoveUp(PlaylistIndexChangeEvent event);
    
    public void onPlaylistMoveDown(PlaylistIndexChangeEvent event);
}
