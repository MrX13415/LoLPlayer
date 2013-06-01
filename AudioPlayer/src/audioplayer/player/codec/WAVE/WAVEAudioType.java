package audioplayer.player.codec.WAVE;

import audioplayer.player.codec.AudioType;

public class WAVEAudioType extends AudioType{
	{
		name = "Wave";
		description = "Waveform Audio File Format";
		extentions = new String[]{".wav", ".aiff"};
		pplClass = WAVEAudioProcessingLayer.class;
	}
}
