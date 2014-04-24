package audioplayer.player.analyzer;

import javax.sound.sampled.AudioFormat;

import audioplayer.player.device.AudioDeviceLayer;

public interface AnalyzerSourceDevice {

	public String getDisplayName();	
	
	public String getDescription();
	
	public AudioFormat getAudioFormat();
	
}
