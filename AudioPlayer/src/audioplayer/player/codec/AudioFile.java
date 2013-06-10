package audioplayer.player.codec;

import java.io.File;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class AudioFile {
	
    private int id = -1;	//-1 means not in the database ...
	
    private AudioType type; 
    private File file;
    private long length = 0;
    
    private String title;
    private String author = "Unknow";
    private String album = "Unknow";
    private String genre = "Unknow";
 
    private int rating = 0;
    private int frequency = 0;
        
    
    public AudioFile(File file){
        this.file = file;
        title = file.getName();
        
        type = AudioType.getAudioType(file);
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
            
    public boolean isSupported(){
    	return type.isSupported(file);
    }
    
    public File getFile(){
        return file;
    }

	public long getLength() {
		return length;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
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
