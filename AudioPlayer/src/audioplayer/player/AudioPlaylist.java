package audioplayer.player;

import audioplayer.player.listener.PlayerListener;
import audioplayer.player.listener.PlaylistEvent;
import audioplayer.player.listener.PlaylistIndexChangeEvent;

import java.util.ArrayList;
import java.util.Random;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class AudioPlaylist {
    
    private ArrayList<AudioFile> content = new ArrayList<AudioFile>();
    
    private int index;
    private boolean shuffle;
      
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
        int preIndex = this.index;
         
        this.index = index;
        
        for (PlayerListener l : listener)
            l.onPlaylistIndexSet(new PlaylistIndexChangeEvent(this, preIndex, index));
    }
    
    public int setRandomIndex(){
    	int index = getRandomIndex();
    	setIndex(index);
    	return index;
    }
    
    public void setNextRandomIndex(){
//    	int curIndex = this.index;
    	
//    	AudioFile curFile = content.get(this.index);
	
//    	if(curFile.getNextIndex() >= 0){
//    		setIndex(curFile.getNextIndex());
//    	}else{
    		int nextIndex = setRandomIndex();
//    		AudioFile nextfile = content.get(nextIndex);
    		

//    	}
    }
    
    public void setPrevRandomIndex(){
//    	int curIndex = this.index;
    	
//    	AudioFile curFile = content.get(this.index);
	
//    	if(curFile.getPrevIndex() >= 0){
//    		setIndex(curFile.getPrevIndex());
//    	}else{
    		int nextIndex = setRandomIndex();
//    		AudioFile nextfile = content.get(nextIndex);
    		
//    		curFile.setNextIndex(nextIndex);
//    		nextfile.setPrevIndex(curIndex);
//    	}
    }
    
    public void nextIndex(){
    	if (shuffle)
    		setNextRandomIndex();
    	else
    		incrementIndex();
    }

    public void prevIndex(){
    	if (shuffle)
    		setPrevRandomIndex();
    	else
    		decrementIndex();
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
    
    public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
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
    	if (index + 2 < content.size()) content.remove(index + 2);
    	
    	if (getIndex() == index + 1) this.index -= 1;
    	
    	for (PlayerListener l : listener)
    		l.onPlaylistMoveUp(new PlaylistIndexChangeEvent(this, index + 1, index));
    }
    
    public void moveDown(AudioFile af){
    	int index = content.indexOf(af) + 2;
    	index = (index >= content.size() ? content.size() - 1 : index);
    	content.add(index, af);
    	if (index - 2 >= 0) content.remove(index - 2);
    	
    	if (getIndex() == index - 2) this.index += 1;
    	
    	for (PlayerListener l : listener)
    		l.onPlaylistMoveDown(new PlaylistIndexChangeEvent(this, index - 2, index));
    }
    
    public int getRandomIndex() { 
    	int min = 0;
    	int max = content.size() - 1;
    	
        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
