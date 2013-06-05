package audioplayer.player.codec.MPEG;

import audioplayer.player.codec.AudioType;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class MPEGAudioType extends AudioType{
	{
	name = "MPEG";
	description = "MPEG 1-2.5 Layer I-III";
	extentions = new String[]{".mp1", ".mp2", ".mp3"};
	pplClass = MPEGAudioProcessingLayer.class;
	}
}
