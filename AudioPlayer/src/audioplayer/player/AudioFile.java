/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioSystem;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

/**
 *
 * @author dausol
 */
public class AudioFile {
    
    private File file;
    
    private long length = 0;
    
//    private int rating;
//    private int frequency;
    
    
    public AudioFile(File file){
        this.file = file;
        

			//length = calculateStreamLength(file);

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
	
	/** Return the length of a given file in milliseconds
	 * <br>
	 * <br>
	 * <code> lenght = file_size * 8 / bitrate </code>
	 * <br>
	 * <br>
	 * @param f The file
	 * @return The length of the given file 'f' in milliseconds
	 * @throws BitstreamException
	 * @throws FileNotFoundException
	 */
	public static long calculateStreamLength(File f){
//		Bitstream bitstream = null;
		long length = 0; //in ms
//		 
//		try{
//	        bitstream = new Bitstream(new FileInputStream(f));
//	        Header header = bitstream.readFrame();
//	        
//	        long filesize = f.length();
//	        if (filesize != AudioSystem.NOT_SPECIFIED) {
//	        	length = (long) (((double)filesize * 8d / (double)header.bitrate()) * 1000d);
//	        }	
//        }catch(BitstreamException bex){
//        	System.err.println("[WARNING] Can't determine file length in milliseconds: " + bex);
//        }catch(FileNotFoundException fex){
//        	System.err.println("[WARNING] Can't determine file length in milliseconds: " + fex);
//        }finally{
//           if (bitstream != null)
//			try {
//				bitstream.close();
//			} catch (BitstreamException e) {}
//        }
		
		return length;
	}
	
}
