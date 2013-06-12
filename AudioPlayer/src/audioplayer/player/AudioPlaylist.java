/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.player;

import audioplayer.player.codec.AudioFile;
import audioplayer.player.listener.PlayerListener;
import audioplayer.player.listener.PlaylistEvent;
import audioplayer.player.listener.PlaylistIndexChangeEvent;

import java.util.ArrayList;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class AudioPlaylist {
    
    private ArrayList<AudioFile> content = new ArrayList<AudioFile>();
    private int index;
    
    private ArrayList<PlayerListener> listener = new ArrayList<PlayerListener>();
    
    public ArrayList<PlayerListener> getPlayerListener() {
         return listener;
    }

    public void addPlayerListener(PlayerListener playerListener) {
         listener.add(playerListener);
    }

    public void removePlayerListener(PlayerListener playerListener) {
        listener.remove(playerListener);
    } 
        
    public int getIndex(){
        return index;
    }
    
    public void resetToFirstIndex(){
        index = 0;
    }
    
    public void resetToLastIndex(){
        index = content.size() - 1;
    }
    
    public void setIndex(int index){
        int preIndex = index;
         
        this.index = index;
        
        for (PlayerListener l : listener)
            l.onPlaylistIndexSet(new PlaylistIndexChangeEvent(this, preIndex, index));
    }
    
    public void incrementIndex(){
    	int preIndex = index;
	
    	if (isLastElement()){
    		resetToFirstIndex();
    	}else{
        	if (index < content.size() - 1) this.index++;   
    	}
     	
        for (PlayerListener l : listener)
            l.onPlaylistIncrement(new PlaylistIndexChangeEvent(this, preIndex, index));
    }
    
    public void decrementIndex(){
        int preIndex = index;
        
        if (isFistElement()){
    		resetToLastIndex();
    	}else{
    		 if (index >= 1) this.index--;   
    	}
                
        for (PlayerListener l : listener)
            l.onPlaylistDecrement(new PlaylistIndexChangeEvent(this, preIndex, index));
    }
    
    public AudioFile get(){
    	return content.get(index);
    }
    
    public AudioFile get(int index){
    	return content.get(index);
    }
    
    public int size(){
        return content.size();
    }
    
    public void add(AudioFile af){
        content.add(af);
        for (PlayerListener l : listener)
            l.onPlaylistFileAdd(new PlaylistEvent(this));
    }
    
    public void remove(AudioFile af){
        content.remove(af);
        if(index >= content.size()) resetToFirstIndex();
        
        for (PlayerListener l : listener)
            l.onPlaylistFileRemove(new PlaylistEvent(this));
    }
    
    public void remove(int index){
        content.remove(index);
        for (PlayerListener l : listener)
            l.onPlaylistFileRemove(new PlaylistEvent(this));
    }
    
    public void clear(){
        content.clear();
        for (PlayerListener l : listener)
            l.onPlaylistClear(new PlaylistEvent(this));
    }
    
    public boolean isEmpty(){
        return content.isEmpty();
    }
    
    public boolean isLastElement(){
    	return index == content.size() - 1;
    }
    
    public boolean isFistElement(){
    	return index == 0;
    }
    
    public boolean isLastElement(AudioFile af){
    	return content.lastIndexOf(af) == content.size() - 1;
    }
    
    public boolean isFistElement(AudioFile af){
    	return content.indexOf(af) == 0;
    }
    
    public void moveUp(AudioFile af){
    	int index = content.indexOf(af) - 1;
    	content.add(index, af);
    	content.remove(index + 2);
    	
    	if (getIndex() == index + 1) this.index -= 1;
    	
    	for (PlayerListener l : listener)
    		l.onPlaylistMoveUp(new PlaylistIndexChangeEvent(this, index + 1, index));
    }
    
    public void moveDown(AudioFile af){
    	int index = content.indexOf(af) + 2;
    	index = (index >= content.size() ? content.size() - 1 : index);
    	content.add(index, af);
    	content.remove(index - 2);
    	
    	if (getIndex() == index - 2) this.index += 1;
    	
    	for (PlayerListener l : listener)
    		l.onPlaylistMoveDown(new PlaylistIndexChangeEvent(this, index - 2, index));
    }
}
