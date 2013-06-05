package audioplayer.player.codec;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import audioplayer.player.codec.MPEG.MPEGAudioType;
import audioplayer.player.codec.WAVE.WAVEAudioType;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public abstract class AudioType extends FileFilter{

	protected static AudioType[] types = {new MPEGAudioType(), new WAVEAudioType()};
	
	protected String name;
	protected String description;
	protected String[] extentions = {""};
	protected Class<?> pplClass = AudioProcessingLayer.class;
	
	public  String getName() {
		return name;
	}
	
	public String getDescription() {
		String ffext = ""; 
		for (String ext : extentions) {
			ffext = String.format("%s*%s; ", ffext, ext);
		}
		ffext = ffext.substring(0, ffext.length() - 2);
		
		return String.format("%s (%s)", description, ffext);
	}

	public String getDescriptionOnly() {
		return description;
	}
	
	public String[] getExtentions() {
		return extentions;
	}

	public AudioProcessingLayer getAudioProcessingLayerInstance() {
		try {
			return (AudioProcessingLayer) pplClass.newInstance();
		} catch (Exception e) {
			return AudioProcessingLayer.getEmptyInstance();
		}
	}

	public static AudioType[] getTypes() {
		return types;
	}
	
	public boolean isSupported(File file){
		return getAudioProcessingLayerInstance().isSupportedAudioFile(file);
	}
	
	public static AudioType getAudioType(File file){
		for (AudioType at : types) {
			if (at.isSupported(file)) return at;
		}
		return new UNKNOWAudioType();
	}
	
	public static FileFilter getAllSupportedFilesFilter(){
		FileFilter ff = new FileFilter() {
			
			@Override
			public String getDescription() {
				String ffext = ""; 
				
				for (AudioType at : types) {
					for (String ext : at.getExtentions()) {
						ffext = String.format("%s*%s; ", ffext, ext);
					}
				}
				ffext = ffext.substring(0, ffext.length() - 2);
				
				return String.format("All Supported files (%s)", ffext);
			}
			
			@Override
			public boolean accept(File f) {
				for (AudioType at : types) {
					if (at.accept(f)) return true;
				}
				return f.isDirectory();
			}
		};
		
		return ff;
	}
	
	@Override
	public boolean accept(File f) {
		for (String ext : extentions) {
			if (f.getName().toLowerCase().endsWith(ext)){
				return true;
			}
		}
		return f.isDirectory();
	}
}
