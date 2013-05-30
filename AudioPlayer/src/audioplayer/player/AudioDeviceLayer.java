package audioplayer.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import audioplayer.player.analyzer.Analyzer;


import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

/**
 * Implements an audio device by using the JavaSound API
 */

public class AudioDeviceLayer extends AudioDeviceBase {
	
	private SourceDataLine source = null;
	private AudioFormat fmt = null;
	private byte[] byteBuf = new byte[4096];
	private byte[] currentSamplesBytes;
	private short[] currentSamples;
	private int currentOffs;
	private int currentLen;
	private Analyzer analyzer;
	
	public AudioDeviceLayer() {
            super();
	}
	
	public SourceDataLine getSource() {
		return source;
	}
	
	public int getCurrentOffs() {
		return currentOffs;
	}

	public byte[] getByteBuf() {
		return byteBuf;
	}

	public byte[] getCurrentSamplesBytes() {
		return currentSamplesBytes;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
		this.analyzer.setDevice(this);
	}

	public short[] getCurrentSamples() {
		return currentSamples;
	}

	public void setCurrentOffs(int currentOffs) {
		this.currentOffs = currentOffs;
	}

	public int getCurrentLen() {
		return currentLen;
	}

	public void setCurrentLen(int currentLen) {
		this.currentLen = currentLen;
	}

	public void setVolume(float vol){
	    if (source != null){
                
                FloatControl volControl = null;
                        
                try{
                   volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                }catch (Exception e){
                   volControl = (FloatControl) source.getControl(FloatControl.Type.VOLUME);
                }
	      
	        // 0.0% = -80dB ; 100.0% = 6dB
	        //linear: float newvol = (volControl.getMinimum() + (volControl.getMaximum() - volControl.getMinimum()) / 100f * vol);
	        //log:
	        double vmax =volControl.getMaximum();
	        double vmin = volControl.getMinimum();
	        double lvol = (Math.log ((vol)/100f) * ((vmin - vmax) / Math.log(0.01/100f))) + vmax;
	        if (lvol > vmax) lvol = vmax;
	        if (lvol < vmin) lvol = vmin;
	        
	        volControl.setValue((float) lvol);
	    }
    }
	
	public float getVolume(){
	    if (source != null){
	        FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	        return volControl.getValue();
	    }
	    return 0;
    }
	
	public FloatControl getVolumeControl(){
	    if (source != null){
	        FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	        return volControl;
	    }
	    return null;
    }
	
	public AudioFormat getFmt() {
		return fmt;
	}

	public void setAudioFormat(AudioFormat fmt) {
		this.fmt = fmt;
	}

	protected AudioFormat getAudioFormat() {
		if (fmt == null) {
			Decoder decoder = getDecoder();
			fmt = new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false);
		}
		
		return fmt;
	}

	protected DataLine.Info getSourceLineInfo() {
		AudioFormat fmt = getAudioFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
		return info;
	}

	public void open(AudioFormat fmt){
		if (!isOpen()) {
			setAudioFormat(fmt);
//			openImpl();
			setOpen(true);
		}
	}

	protected void createSource() throws JavaLayerException {
		Throwable t = null;
		try {
			Line line = AudioSystem.getLine(getSourceLineInfo());
			if (line instanceof SourceDataLine) {
				source = (SourceDataLine) line;
				source.open(fmt);
				source.start();		

			}

		} catch (RuntimeException ex) {
			t = ex;
		} catch (LinkageError ex) {
			t = ex;
		} catch (LineUnavailableException ex) {
			t = ex;
		}
		if (source == null)
			throw new JavaLayerException("cannot obtain source audio line", t);
	}

	public int millisecondsToBytes(AudioFormat fmt, int time) {
		return (int) (time * (fmt.getSampleRate() * fmt.getChannels() * fmt.getSampleSizeInBits()) / 8000.0);
	}

	public void closeImpl() {
		if (source != null) {
			analyzer = null;
            try{
                source.close();
            }catch(Exception e){
                System.err.println("WARNING: Can't close the audio device");
            }
		}
	}

	public void writeImpl(short[] samples, final int offs, final int len) throws JavaLayerException {
		this.currentSamples = samples;
		writeImpl(toByteArray(samples, offs, len), offs, len * 2);
	}
	
	public void writeImpl(byte[] samples, final int offs, final int len) throws JavaLayerException {
		if (source == null)
			createSource();

		this.currentOffs = offs;
		this.currentLen = len; 
		this.currentSamplesBytes = samples;
		
		source.write(this.currentSamplesBytes, 0, len);
		
		if (analyzer != null){
			analyzer.addToAnalyze(currentSamplesBytes, offs, len);
		}		
	}

	protected byte[] getByteArray(int length) {
		if (byteBuf.length < length) {
			byteBuf = new byte[length];
		}
		return byteBuf;
	}

	protected byte[] toByteArray(short[] samples, int offs, int len) {
		byte[] b = getByteArray(len * 2);
		int idx = 0;
		short s;
		while (len-- > 0) {
			s = samples[offs++];
			b[idx++] = (byte) s;
			b[idx++] = (byte) (s >>> 8);
		}
		return b;
	}

	protected void flushImpl() {
		if (source != null) {
			source.drain();
		}
	}

	public int getPosition() {
		if (source != null) {
			return (int) (source.getMicrosecondPosition() /1000);
		}
		return 0;
	}
		
	/**
	 * Runs a short test by playing a short silent sound.
	 * @throws JavaLayerException 
	 */
	public void test() throws JavaLayerException{
		try {
			open(new AudioFormat(22050, 16, 1, true, false));
			short[] data = new short[22050 / 10];
			write(data, 0, data.length);
			flush();
			close();
		} catch (RuntimeException ex) {
			throw new JavaLayerException("Device test failed: " + ex);
		}

	}

	public int getLevel()
    { // audioData might be buffered data read from a data line
		byte[] audioData = byteBuf;
		
		for (byte b : audioData) {
			System.out.print(b + " ");
		}
		System.out.println("");
		return 0;
//        long lSum = 0;
//        for(int i=0; i<audioData.length; i++)
//            lSum = lSum + audioData[i];
// 
//        double dAvg = lSum / audioData.length;
// 
//        double sumMeanSquare = 0d;
//        for(int j=0; j<audioData.length; j++)
//            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);
// 
//        double averageMeanSquare = sumMeanSquare / audioData.length;
//        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }

}
