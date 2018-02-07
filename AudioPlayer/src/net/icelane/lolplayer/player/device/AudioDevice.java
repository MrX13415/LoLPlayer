package net.icelane.lolplayer.player.device;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import net.icelane.lolplayer.Application;
import net.icelane.lolplayer.player.analyzer.Analyzer;
import net.icelane.lolplayer.player.analyzer.data.PCMData;
import net.icelane.lolplayer.player.analyzer.device.AnalyzerSourceDevice;
import net.icelane.lolplayer.player.codec.AudioProcessingLayer;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * Audio-Device
 * 
 * @author Oliver Daus
 * 
 * @version 2.0
 * 
 */
public interface AudioDevice{
	
	public boolean DEBUG = false;
	
	public Analyzer getAnalyzer();
	
	public void setAnalyzer(Analyzer analyzer);

	public void setAnalyzerActive();
	
	public boolean isOpen();

	public Object getOwner();

	public boolean isClaimed();
	
	/**
	 * Claims this audio device 
	 * @param owner The new owner of this object
	 * @return if claiming was successful<p>
	 * @return <b>true</b> if claiming was successful<p>
	 *         <b>false</b> if no owner was given or this device is already owned
	 */
	public boolean claim(Object owner);
	
	/**
	 * Releases this audio device if claimed
	 * @param owner The owner which is claiming this device
	 * @return <b>true</b> if releasing this device was successful<p>
	 * 		   <b>false</b> if the given owner is not the current owner
	 */
	public boolean release(Object owner);
	
	/**
	 * Releases this device immediately of any owner
	 */
	public void forceRelease();
	
	public SourceDataLine getDataline();

	public AudioFormat getFormat();

	public void open(AudioFormat format);

	public void close();
	
	public boolean write(Object owner, byte[] b, final int off, final int len) throws LineUnavailableException;
				
	public void flush();

	public FloatControl getVolumeControl();
	
	public void setVolume(float vol);

	public float getVolume();

	/**
	 * Plays a test tone with a frequency of 432 Hz and a duration of 700 ms
	 * 
	 * @throws LineUnavailableException
	 */
	public void test() throws LineUnavailableException;

}
