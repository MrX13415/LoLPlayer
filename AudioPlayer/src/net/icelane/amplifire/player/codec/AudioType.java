package net.icelane.amplifire.player.codec;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

import net.icelane.amplifire.player.codec.MPEG.MPEGAudioType;
import net.icelane.amplifire.player.codec.WAVE.WAVEAudioType;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public abstract class AudioType extends FileFilter{

	protected static AudioType[] types = {new MPEGAudioType(), new WAVEAudioType()};
	
	protected String name;
	protected String description;
	protected String[] extentions = {""};
	protected Class<?> aplClass = AudioProcessingLayer.class;
	
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
			return (AudioProcessingLayer) aplClass.newInstance();
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
//		for (AudioType at : types) {
//			if (at.isSupported(file)) return at;
//		}
		if (file.getName().toLowerCase().endsWith(".mp1") ||
			file.getName().toLowerCase().endsWith(".mp2") ||
			file.getName().toLowerCase().endsWith(".mp3"))
				return new MPEGAudioType();
		
		if (file.getName().toLowerCase().endsWith(".aiff") ||
			file.getName().toLowerCase().endsWith(".wave"))
				return new WAVEAudioType();
		
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
	
	public static FilenameFilter getAllSupportedFilenamesFilter(){
		FilenameFilter ff = new FilenameFilter() {
			
			@Override
			public boolean accept(File f, String s) {
				boolean sup = false;
				for (AudioType at : types) {
					sup = at.accept(new File(f, s));
					if (sup) return true;
				}
				return sup;
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
