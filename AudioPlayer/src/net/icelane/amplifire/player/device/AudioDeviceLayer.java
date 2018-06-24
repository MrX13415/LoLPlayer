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
import javax.swing.JFrame;

import net.icelane.lolplayer.Application;
import net.icelane.lolplayer.player.analyzer.Analyzer;
import net.icelane.lolplayer.player.analyzer.data.PCMData;
import net.icelane.lolplayer.player.analyzer.device.AnalyzerSourceDevice;
import net.icelane.lolplayer.player.codec.AudioProcessingLayer;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * Java Sound Audio-Device Layer
 * 
 * @author Oliver Daus
 * 
 * @version 2.0
 * 
 */
public class AudioDeviceLayer implements AudioDevice, AnalyzerSourceDevice{
	
	private static AudioDevice currentAudioDeviceLayer = null;
	
	private boolean open = false;
	private volatile SourceDataLine dataline = null;
	private AudioFormat format = null;
	private Analyzer analyzer;
	private Object owner = null;
	
	public boolean DEBUG = false;
	
	protected AudioDeviceLayer() {
        super();
	}
	
	public static AudioDevice getInstance(){
		if (currentAudioDeviceLayer == null)
			currentAudioDeviceLayer = new AudioDeviceLayer();

		return currentAudioDeviceLayer;
	}
	
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		if (this.analyzer != null && analyzer != this.analyzer)
			this.analyzer.unregisterDevice(this);
		
		if (analyzer != null){
			analyzer.registerDevice(this);
		}
		this.analyzer = analyzer;
	}

	public void setAnalyzerActive(){
		if (analyzer != null) analyzer.setActiveDevice(this);
	}
	
	public boolean isOpen() {
		return open;
	}

	public Object getOwner() {
		return owner;
	}

	public boolean isClaimed() {
		return this.owner != null;
	}
	
	/**
	 * Claims this audio device 
	 * @param owner The new owner of this object
	 * @return if claiming was successful<p>
	 * @return <b>true</b> if claiming was successful<p>
	 *         <b>false</b> if no owner was given or this device is already owned
	 */
	public boolean claim(Object owner){
		if (owner == null) return true;
		if (this.owner != null)
			return this.owner.equals(owner) ? true : false;
		
		
		ccc = true;
		this.owner = owner;
		
		try {
			if (DEBUG) System.out.printf("[ADL] EVENT: CLAIM   SOURCE: %100s CCC : %s\n", ((AudioProcessingLayer) owner).getAudioFile().getName(), ccc);	
		} catch (Exception e) {
			if (DEBUG) System.out.printf("[ADL] EVENT: CLAIM   SOURCE: %100s CCC : %s\n", owner, ccc);
		}
		return true;
	}
	
	boolean ccc = false;
	
	/**
	 * Releases this audio device if claimed
	 * @param owner The owner which is claiming this device
	 * @return <b>true</b> if releasing this device was successful<p>
	 * 		   <b>false</b> if the given owner is not the current owner
	 */
	public boolean release(Object owner){
		if (owner == null || this.owner == null)
			return false;

		if (!this.owner.equals(owner)) return false;
		
		
		ccc = false;
		this.owner = null;
		if (DEBUG) System.out.printf("[ADL] EVENT: RELEASE SOURCE: %100s CCC : %s\n", owner, ccc);
		
		return true;
	}
	
	/**
	 * Releases this device immediately of any owner
	 */
	public void forceRelease(){
		this.owner = null;
	}
	
	public SourceDataLine getDataline() {
		return dataline;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void open(AudioFormat format){
		if (isOpen()) return;
		this.format = format;
		
		System.out.println(format.toString());
		open = true;
	}

	public void close() {
		open = false;
		
		if (dataline == null) return;
		try {
			dataline.close();
		} catch (Exception e) {
			System.err.println("WARNING: Can't close the audio device: " + e);
		}finally{
			dataline = null;
		}
	}
	
	public synchronized boolean write(Object owner, byte[] b, final int off, final int len) throws LineUnavailableException{
		if (owner == null) return false;
		if (this.owner != null && !this.owner.equals(owner)) return false;
		
		// pass data to the analyzer ...
		if (analyzer != null) analyzer.analyze(this, b, off, len);
		
		//PCMData d = new PCMData(format, b, off, len);
		//this.format = d.getFormat();

		// pass data to the audio device
		if (dataline == null) createDataLine();
		this.dataline.write(b, off, len);
		
		return true;
	}
				
	public synchronized void flush(){
		if (dataline != null) dataline.drain();
	}

	protected void createDataLine() throws LineUnavailableException { 
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		
		Line line = AudioSystem.getLine(info);

		if (line instanceof SourceDataLine){
			dataline = (SourceDataLine) line;
			dataline.open(format);
			
			dataline.start();
		}
		
		if (dataline == null) throw new LineUnavailableException("Can't obtain Data line");
	}
	
	public static void printJavaSoundMixerInfo(){
		Mixer.Info[] mi = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mi) {
			System.out.println("info: " + info);
			Mixer m = AudioSystem.getMixer(info);
			System.out.println("mixer " + m);
			Line.Info[] sl = m.getSourceLineInfo();
			for (Line.Info info2 : sl) {
				System.out.println("    info: " + info2);
				try {
					Line line = AudioSystem.getLine(info2);
					if (line instanceof SourceDataLine) {
						SourceDataLine source = (SourceDataLine) line;

						DataLine.Info i = (DataLine.Info) source.getLineInfo();
						for (AudioFormat format : i.getFormats()) {
							System.out.println("    format: " + format);
						}
					}
				} catch (Exception e) {
					System.out.println("    Error: " + e);
				}
			}
		}
	}
	
	public static void printSystemDeviceInfo(){
		System.out.println(Arrays.toString(AudioSystem.getMixerInfo()));
		for (int i = 0; i < AudioSystem.getMixerInfo().length; i++) {
			System.out.println((i < 10 ? " " : "") + i + " : "
					+ AudioSystem.getMixerInfo()[i]);

			Mixer m = AudioSystem.getMixer(AudioSystem.getMixerInfo()[i]);
			System.out.println("     > " + m.getLineInfo());
			System.out.println("     > " + Arrays.toString(m.getControls()));
			System.out.println("     > " + Arrays.toString(m.getSourceLines()));
			for (int j = 0; j < m.getSourceLines().length; j++) {
				System.out.println("          > "
						+ m.getSourceLines()[j].getLineInfo());
			}

			System.out.println("     > " + Arrays.toString(m.getTargetLines()));
			System.out.println("     > "
					+ Arrays.toString(m.getSourceLineInfo()));
			System.out.println("     > "
					+ Arrays.toString(m.getTargetLineInfo()));
		}
	}

	public FloatControl getVolumeControl(){
	    if (dataline == null) return null;
	    	
		Control.Type[] controlTypes = new Control.Type[] {
				FloatControl.Type.MASTER_GAIN,
				FloatControl.Type.VOLUME
				};
		
		for (Control.Type control : controlTypes) {
			if (!dataline.isControlSupported(control)) continue;
			return (FloatControl) dataline.getControl(control);
		}
		return null;
    }
	
	public void setVolume(float vol) {
		if (dataline == null) return;
		    	
		FloatControl control = getVolumeControl();
		
		if (control == null){
		    Application.getApplication().getControl().raiseVolumeControlError();
		    return;
		}
		
//	   //linear:
//	    float newvol = (volControl.getMinimum() + (volControl.getMaximum() - volControl.getMinimum()) / 100f * vol);
		
		//log:
		float vmax = control.getMaximum();
			  vmax = vmax < 3f ? vmax : 3f; // max vol. can't exceed +3 DB due to noise on >+3 DB
		float vmin = control.getMinimum(); //-80 DB
		
		float vdelta = vmin - vmax;
		float vnew = (float) (Math.log(vol/100f) * (vdelta / Math.log(0.01f/100f)) + vmax);
		if (vnew > vmax) vnew = vmax;
		if (vnew < vmin) vnew = vmin;
		
		control.setValue((float) vnew);
	}

	public float getVolume() {
		return getVolumeControl() != null ? getVolumeControl().getValue() : 0;
	}

	  interface IntegerMath {
	        int operation(int a, int b);   
	    }
	  
	  
	/**
	 * Plays a test tone with a frequency of 432 Hz and a duration of 700 ms
	 * 
	 * @throws LineUnavailableException
	 */
	public void test() throws LineUnavailableException {

		Thread th = new Thread(
			(Runnable) () -> {
				claim(this);
				open(new AudioFormat(48000, 16, 2, true, false));
				
				for (int i = 0; i < 1; i++) {
					byte[] b = monoToStereo(generateSineWave(432, 700));
					try {
						write(this, b, 0, b.length);
					} catch (LineUnavailableException e1) {						
						e1.printStackTrace();
					}
				}
				flush();
				close();
				release(this);
			}
		);

		th.start();
	}

	/***
	 * Converts mono PCM data in to stereo PCM data
	 * 
	 * @param A byte array of PCM data
	 * @return A byte array of stereo PCM data
	 */
	public byte[] monoToStereo(byte[] b){
		byte[] nb = new byte[b.length * 2];
		int nbI = 0;
		for (byte c : b) {
			nb[nbI] = c;
			nbI++;
			nb[nbI] = c;
			nbI++;
		}
		return nb;
	}
	
	/**
	 * Returns a byte array of a sinus wave with the given frequency and an duration of 1 second
	 * to be played on an AudioDevice
	 * @param frequency The frequency of the sinus wave
	 * @return A byte array of a sinus wave
	 */
	public static byte[] generateSineWave(int frequency) {
		return generateSineWave(frequency, 1000);
	}
	
	/**
	 * Returns a byte array of a sinus wave with the given frequency and time with a sample rate of 48.0 KHz 
	 * to be played on an AudioDevice
	 * @param frequency The frequency of the sinus wave
	 * @param time The duration in milliseconds
	 * @return A byte array of a sinus wave
	 */
	public static byte[] generateSineWave(int frequency, int time) {
        return generateSineWave(48000, frequency, time);
    }
	
	/**
	 * Returns a byte array of a sinus wave with the given sample rate, frequency and time
	 * to be played on an AudioDevice
	 * @param sampleRate The sample rate in Hz (e.g 44100 for 44.1 KHz or 48000 for 48.0 KHz)
	 * @param frequency The frequency of the sinus wave
	 * @param time The duration in milliseconds
	 * @return A byte array of a sinus wave
	 */
	public static byte[] generateSineWave(int sampleRate, int frequency, int time) {

		//float t = (1f / (float)(frequency)) * 1000;
		//System.out.println(t);
        byte[] sin = new byte[(sampleRate / 1000) * time];
        double samplingInterval = (double) (sampleRate / frequency);

        for (int i = 0; i < (sin.length); i++) {
            double angle = (2.0 * Math.PI * i) / samplingInterval;
            sin[i] = (byte) (Math.sin(angle) * 15);
        }   
        
        return sin;
    }
	
	/**
	 * Returns a byte array of a sinus wave with the given sample rate, frequency and time
	 * to be played on an AudioDevice
	 * @param sampleRate The sample rate in Hz (e.g 44100 for 44.1 KHz or 48000 for 48.0 KHz)
	 * @param frequency The frequency of the sinus wave
	 * @param time The duration in milliseconds
	 * @return A byte array of a sinus wave
	 */
	public static byte[] generateSineWaveOnce(int sampleRate, int frequency) {

		//float t = (1f / (float)(frequency)) * 1000;
		//System.out.println(t);
        byte[] sin = new byte[(sampleRate / 1000) * 1000];
        double samplingInterval = (double) (sampleRate / frequency);

        byte fval = 0;
        byte fvalRi = 0;
        for (int i = 0; i < (sin.length); i++) {
            double angle = (2.0 * Math.PI * i) / samplingInterval;
            sin[i] = (byte) (Math.sin(angle) * 15);
            
        	if (i == 0) fval = sin[i];
        	if (fval == sin[i]) fvalRi += 1;
        	if (fvalRi >= 3){
        		byte[] sin2 = new byte[i + 1];
        		for (int i2 = 0; i2 < (sin2.length); i2++) {
        			sin2[i2] = sin[i2];
        		}
        		sin = sin2;
        		break;
        	}
        }   

        return sin;
    }
	
	@Override
	public String getDisplayName() {
		return "Internal Audio Device";
	}

	@Override
	public String getDescription() {
		return "The internal audio device of the LOLPlayer";
	}

	@Override
	public AudioFormat getAudioFormat() {
		return format;
	}

	@Override
	public boolean hasSettingsUI() {
		return false;
	}

	@Override
	public void OpenSettingsUI(JFrame parent) {

	}
	
	@Override
	public void CloseSettingsUI() {

	}

}
