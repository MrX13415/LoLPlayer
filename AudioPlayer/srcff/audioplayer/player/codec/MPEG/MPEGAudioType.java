package audioplayer.player.codec.MPEG;

import audioplayer.player.codec.AudioType;

public class MPEGAudioType extends AudioType{
	{
	name = "MPEG";
	description = "MPEG 1-2.5 Layer I-III";
	extentions = new String[]{".mp1", ".mp2", ".mp3"};
	pplClass = MPEGAudioProcessingLayer.class;
	}
}
