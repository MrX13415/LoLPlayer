package net.icelane.amplifire.player.codec.MPEG;

import net.icelane.amplifire.player.codec.AudioType;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class MPEGAudioType extends AudioType{
	{
	name = "MPEG";
	description = "MPEG 1-2.5 Layer I-III";
	extentions = new String[]{".mp1", ".mp2", ".mp3"};
	aplClass = MPEGAudioProcessingLayer.class;
	}
}
