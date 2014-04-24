package audioplayer.player.device;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;

import audioplayer.Application;
import audioplayer.player.analyzer.Analyzer;
import audioplayer.player.analyzer.AnalyzerSourceDevice;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * Audio-Device Layer
 * 
 * @author Oliver Daus
 * 
 * @version 2.0
 * 
 * TODO: marker for 0 DB 100%
 * 
 */
public class AudioDeviceLayer implements AnalyzerSourceDevice{
	
	private static AudioDeviceLayer currentAudioDeviceLayer = null;
	
	private boolean open = false;
	private volatile SourceDataLine dataline = null;
	private AudioFormat format = null;
	private Analyzer analyzer;
	
	private AudioDeviceLayer() {
        super();
	}
	
	public static AudioDeviceLayer getInstance(){
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
		
		if (analyzer != null) analyzer.registerDevice(this);
		this.analyzer = analyzer;
	}

	public boolean isOpen() {
		return open;
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
	
	public void write(byte[] b, final int off, final int len) throws LineUnavailableException{
		if (dataline == null) createDataLine();
		
		dataline.write(b, off, len);
		
		if (analyzer != null) analyzer.analyze(this, b, off, len);
	}
				
	public void flush(){
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
		
		System.out.println(Arrays.toString(AudioSystem.getMixerInfo()));
		for (int i = 0; i < AudioSystem.getMixerInfo().length; i++) {
			System.out.println((i < 10? " " : "") + i + " : " + AudioSystem.getMixerInfo()[i]);
			
			Mixer m = AudioSystem.getMixer(AudioSystem.getMixerInfo()[i]);
			System.out.println("     > " + m.getLineInfo());
			System.out.println("     > " + Arrays.toString(m.getControls()));
			System.out.println("     > " + Arrays.toString(m.getSourceLines()));
			for (int j = 0; j < m.getSourceLines().length; j++) {
				System.out.println("          > " + m.getSourceLines()[j].getLineInfo());
			}
			
			System.out.println("     > " + Arrays.toString(m.getTargetLines()));
			System.out.println("     > " + Arrays.toString(m.getSourceLineInfo()));
			System.out.println("     > " + Arrays.toString(m.getTargetLineInfo()));
		}
		
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER)) {
			
		    try {
		        line = (Port) AudioSystem.getLine(Port.Info.MICROPHONE);
		    }catch(Exception e){
		    	
		    }
		}
		

		if (dataline == null) throw new LineUnavailableException("Can't obtain Data line");
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

	/**
	 * Plays a test tone with a frequency of 440 Hz and a duration of 700 ms
	 * @throws LineUnavailableException
	 */
	public void test() throws LineUnavailableException {
		open(new AudioFormat(44100, 16, 1, true, false));

		byte[] b = generateSineWave(440, 700);
		write(b, 0, b.length);

		flush();
		close();
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
	 * Returns a byte array of a sinus wave with the given frequency and time
	 * to be played on an AudioDevice
	 * @param frequency The frequency of the sinus wave
	 * @param time The duration in milliseconds
	 * @return A byte array of a sinus wave
	 */
	public static byte[] generateSineWave(int frequency, int time) {
		int sampleRate = 44100;
        byte[] sin = new byte[(sampleRate / 1000) * time];
        double samplingInterval = (double) (sampleRate / frequency);

        for (int i = 0; i < (sin.length); i++) {
            double angle = (2.0 * Math.PI * i) / samplingInterval;
            sin[i] = (byte) (Math.sin(angle) * 15);
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

}
