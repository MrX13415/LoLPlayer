package audioplayer.player;

import java.io.File;

import audioplayer.player.codec.AudioProcessingLayer;
import audioplayer.player.codec.AudioType;
import de.vdheide.mp3.MP3File;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus / Luca Madonia
 * 
 */
public class AudioFile {
	
	public static final String defaultText = "Unknow";
	
    private int id = -1;	//-1 means not in the database ...
	
    private AudioType type; 
    private File file;
    private long length = 0;
    
    private String title = defaultText;
    private String author = defaultText;
    private String album = defaultText;
    private String genre = defaultText;
 
    private int rating = 0;
    private int frequency = 0;
        
    
    public AudioFile(File file){
        this.file = file;
        
        type = AudioType.getAudioType(file);
        
        readID3Tags();
    }
    
    public AudioFile(File file, String title, String author) {
		this(file);
		this.title = title;
		this.author = author;
	}

	public void initAudioFile() throws UnsupportedFileFormatException{
    	 try{
         	length = type.getAudioProcessingLayerInstance().calculateStreamLength(file);	
         }catch(Exception e){
         	throw new UnsupportedFileFormatException();
         }
    	 if (!isSupported()) throw new UnsupportedFileFormatException();
    }
	
	public void readID3Tags(){
		//Ließt die ID3 Tags aus  
        MP3File mp3;
		try {
			mp3 = new MP3File(file.getAbsolutePath());
		} catch (Exception e) {
			return;
		}
		
		try {
			String t = mp3.getTitle().getTextContent();
			if (t != null) setTitle(t);
	    } catch (Exception e) {}
		
		try {
			String t = mp3.getArtist().getTextContent();
			if (t != null) setAuthor(t);
	    } catch (Exception e) {}
		
		try {
			String t = mp3.getAlbum().getTextContent();
			if (t != null) setAlbum(t);
	    } catch (Exception e) {}
		
		try {
			String t = mp3.getGenre().getTextContent();
			if (t != null) setGenre(t);
	    } catch (Exception e) {}
	}
            
    public boolean isSupported(){
    	return type.isSupported(file);
    }
    
    public File getFile(){
        return file;
    }

	public long getLength() {
		return length;
	}

	public String getName() {
		return title == null || title.equals(defaultText) ? file.getName() : getTitle();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || title.isEmpty()) title = defaultText;
		this.title = title;
	}
	
	public boolean isTitleEmpty(){
		return getTitle() == null || getTitle().isEmpty() || getTitle().equals(defaultText);
	}

	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		if (author == null || author.isEmpty()) author = defaultText;
		this.author = author;
	}
	
	public boolean isAuthorEmpty(){
		return getAuthor() == null || getAuthor().isEmpty() || getAuthor().equals(defaultText);
	}
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		if (album == null || album.isEmpty()) album = defaultText;
		this.album = album;
	}

	public boolean isAlbumEmpty(){
		return getAlbum() == null || getAlbum().isEmpty() || getAlbum().equals(defaultText);
	}
	
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		if (genre == null || genre.isEmpty()) genre = defaultText;
		this.genre = genre;
	}

	public boolean isGenreEmpty(){
		return getGenre() == null || getGenre().isEmpty() || getGenre().equals(defaultText);
	}
	
	public String getFormatedLength() {
		String flength = String.format("%1$tM:%1$tS", length);
		if (length >= 3600000) flength = String.format("%1$tH:%1$tM:%1$tS", length);
		return flength;
	}

	public AudioType getType() {
		return type;
	}

	public AudioProcessingLayer getAudioProcessingLayer() {
		return type.getAudioProcessingLayerInstance();
	}
	
	public class UnsupportedFileFormatException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4674198784666823006L;

		public UnsupportedFileFormatException() {
			super("file format not supported");
		}
		
		private AudioFile file;

		public AudioFile getAudioFile() {
			return file;
		}
			
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
    
	public void increaseFrequency() {
		this.frequency += frequency;
	}
	
    public int getId() {
		return id;
	}
        
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isNotInDataBase() {
		return id == -1;
	}

    @Override
    public String toString() {
        return "AudioFile{" + "id=" + id + ", type=" + type + ", file=" + file + ", length=" + length + ", title=" + title + ", author=" + author + ", album=" + album + ", genre=" + genre + ", rating=" + rating + ", frequency=" + frequency + '}';
    }
    
}
