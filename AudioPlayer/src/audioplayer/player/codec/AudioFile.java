/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.player.codec;

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
    
	enum AudioType{
		UNKNOW, MPEG, WAVE;
	}
	
	private AudioType type; 
	private AudioProcessingLayer ppl;
	
    private File file;
    
    private long length = 0;
    
//    private int rating;
//    private int frequency;
    
    
    public AudioFile(File file){
        this.file = file;
        determineAudioType(); 
		length = ppl.calculateStreamLength(file);
    }
    
    private void determineAudioType(){
    	type = AudioType.UNKNOW;
    	
    	String fileExt = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase(); 
    	
    	if (fileExt.equals(".mp3")){
    		type = AudioType.MPEG;
    		ppl = new AudioProcessingLayer();
    	}
    	if (fileExt.equals(".wav")){
    		type = AudioType.WAVE;
    		ppl = new WAVEAudioProcessingLayer();
    	}
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
		return ppl;
	}
	
}
