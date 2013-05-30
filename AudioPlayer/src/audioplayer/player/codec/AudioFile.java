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
    	
	enum AudioType{
		UNKNOW, MPEG, WAVE;
	}
	
	private AudioType type; 
	private AudioProcessingLayer ppl;
	
    private File file;
    
    private long length = 0;
    
//  private int rating;
//  private int frequency;
        
    public AudioFile(File file){
        this.file = file;
        determineAudioType(); 
		length = ppl.calculateStreamLength(file);
    }
    
    /** Determines the Audio file type
     *  
     * @return if the file is supported
     */
    private boolean determineAudioType(){
    	AudioProcessingLayer[] ppls = new AudioProcessingLayer[]{new MPEGAudioProcessingLayer(), new WAVEAudioProcessingLayer()};
    	
    	for (AudioProcessingLayer audioProcessingLayer : ppls) {
    		ppl = audioProcessingLayer;
    		if (ppl.isSupportedAudioFile(file)){
    			type = ppl.getSupportedAudioType();
    			return true;
    		}
		}

    	ppl = AudioProcessingLayer.getEmptyInstance();
    	type = ppl.getSupportedAudioType();
    	
    	return false;
    }
        
    public boolean isSupported(){
    	return ppl.isSupportedAudioFile(file);
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
