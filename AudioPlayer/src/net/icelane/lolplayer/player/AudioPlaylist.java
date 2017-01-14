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

    private History history = new History();
    
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
    
    public int indexOf(AudioFile file){
    	return content.indexOf(file);
    }
    
    public void resetToFirstIndex(){
    	index = 0;
    }
    
    public void resetToLastIndex(){
    	index = content.size() - 1;
    }
    
    public void overrideIndex(int index){
    	this.index = index;
    }
    
    public void setIndex(int index){
    	int preIndex = 0;

		preIndex = this.index;
		overrideIndex(index);
  
        for (PlayerListener l : listener)
            l.onPlaylistIndexSet(new PlaylistIndexChangeEvent(this, preIndex, index));
    }
    
    public void setNextIndex(int index){
    	if (shuffle){
    		history.addNext(index);
    	}
    	setIndex(index);
    }
 
    public int setNextRandomIndex(){
    	int index = getRandomIndex();
    	setIndex(index);
    	return index;
    }
    
    public void nextRandomIndex(){
    	if (history.hasNext()){
    		setIndex(history.getNextPlaylistIndex());
    	}else{
    		int nextIndex = setNextRandomIndex();
    		history.addNext(nextIndex);
    	}
    }
    
    public void priorRandomIndex(){
    	if (history.hasPrior()){
    		setIndex(history.getPriorPlaylistIndex());
    	}else{
    		int priorIndex = setNextRandomIndex();
    		history.addPrior(priorIndex);
    	}
    }
    
    public void nextIndex(){
    	if (shuffle)
    		nextRandomIndex();
    	else
    		incrementIndex();
    }

    public void priorIndex(){
    	if (shuffle)
    		priorRandomIndex();
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
            l.onPlaylistFileAdd(new PlaylistEvent(this, af, content.size() - 1));
    }
    
    public void remove(AudioFile af){
    	int i = content.indexOf(af);
        content.remove(af);
        
        if (index >= i) index--;
        if (index >= content.size()) resetToFirstIndex();        
        
        for (PlayerListener l : listener)
            l.onPlaylistFileRemove(new PlaylistEvent(this, af, i));
    }
    
    public void remove(int index){
    	AudioFile af = content.get(index);
        content.remove(index);
        
        if (this.index >= index) this.index--;
        if (this.index >= content.size()) resetToFirstIndex();        
        
        if (history.contains(index))
        	history.Remove(index);
        
        for (PlayerListener l : listener)
            l.onPlaylistFileRemove(new PlaylistEvent(this, af, index));
    }
    
    public void clear(){
        content.clear();
        for (PlayerListener l : listener)
            l.onPlaylistClear(new PlaylistEvent(this, null));
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
    	if (index >= content.size())
    		content.add(af);
    	else
    		content.add(index, af);
    	
    	if (index - 2 >= 0) content.remove(index - 2);
    	
    	if (getIndex() == index - 2) this.index += 1;
    	
    	for (PlayerListener l : listener)
    		l.onPlaylistMoveDown(new PlaylistIndexChangeEvent(this, index - 2, index - 1));
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
    
    public class History {

        private ArrayList<Integer> history = new ArrayList<Integer>();
        
        private int index;
        
        public boolean contains(int index){
        	return history.contains((Object)index);
        }
        
        public boolean Remove(int index){
        	return history.remove((Object)index);
        }
        
        public int RemoveAt(int index){
        	return history.remove(index);
        }
        
        public boolean hasNext(){
        	return !isEmpty() && index < (history.size()-1);
        }
        
        public boolean hasPrior(){
        	return !isEmpty() && index > 0;
        }
        
        public boolean isEmpty(){
        	return history.isEmpty();
        }
        
        public int getNextPlaylistIndex(){
        	return hasNext() ? history.get(++index) : -1;
        }
        
        public int getPriorPlaylistIndex(){
        	return hasPrior() ? history.get(--index) : -1;
        }
        
        public void addNext(int playlistIndex){
        	if (hasNext())
        		history.subList(index+1, history.size()).clear();
        	
        	history.add(playlistIndex);
    		index = (history.size()-1);
        }
        
        public void addPrior(int playlistIndex){
    		history.add(0, playlistIndex);
    		index = 0;
        }
        
    }
}
