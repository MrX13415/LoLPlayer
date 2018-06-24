package net.icelane.amplifire.player.codec.WAVE;

import net.icelane.amplifire.player.codec.AudioType;

/**
 *  amplifier - Audio-Player Project
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
