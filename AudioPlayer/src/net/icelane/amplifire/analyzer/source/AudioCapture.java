package net.icelane.amplifire.analyzer.source;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.swing.JFrame;

import net.icelane.amplifire.analyzer.Analyzer;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


public class AudioCapture extends Thread implements AnalyzerSourceDevice{

	private Analyzer analyzer;
	private AudioFormat format;
	
	public AudioCapture(Analyzer analyzer) {
		super();
		this.analyzer = analyzer;
		this.analyzer.registerDevice(this);
	}

	@Override
	public void run() {
		
		
//		Port line = null;
//		Port.Info info = Port.Info.MICROPHONE;
//		
//		if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
//		    try {
//		    	line = (Port) AudioSystem.getLine(info);
//			    line.open(); 
//		    }catch(Exception e){
//		    	System.out.println(e);
//		    }
//		}
		

		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 48000f;
		int channels = 1;
		int frameSize = 4;
		int sampleSize = 16;
		boolean bigEndian = true;

		format = new AudioFormat(encoding, rate, sampleSize,
				channels, (sampleSize / 8) * channels, rate, bigEndian);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Line matching " + info + " not supported.");
			return;
		}

		TargetDataLine line = null;

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
			line.start();
		} catch (Exception e) {
			System.out.println(e);
		}

		boolean hasMoreFrames = false;
		
		while (true){
			if (line != null){
				int btr = 2048;
				byte[] b = new byte[btr];
				int r = line.read(b, 0, btr);
	
				if (r == -1)
					hasMoreFrames = false;
				
				if (r > -1){
					try {	
						analyzer.analyze(this, b, 0, r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public String getDisplayName() {
		return "Default System Microphone";
	}

	@Override
	public String getDescription() {
		return "The default microphone input of this system";
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
