package audioplayer.player.codec.WAVE;

import audioplayer.player.codec.AudioType;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class WAVEAudioType extends AudioType{
	{
		name = "Wave";
		description = "Waveform Audio File Format";
		extentions = new String[]{".wav", ".aiff"};
		aplClass = WAVEAudioProcessingLayer.class;
	}
}
