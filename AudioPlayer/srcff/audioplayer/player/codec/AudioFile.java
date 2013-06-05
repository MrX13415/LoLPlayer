/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.player.codec;

import java.io.File;

/**
 *
 * @author dausol
 */
public class AudioFile {
	
	private AudioType type; 

    private File file;
    
    private long length = 0;
    
//  private int rating;
//  private int frequency;
        
    public AudioFile(File file){
        this.file = file;

        type = AudioType.getAudioType(file);
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
}
