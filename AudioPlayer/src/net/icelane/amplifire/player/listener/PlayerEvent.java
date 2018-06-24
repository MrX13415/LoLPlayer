package net.icelane.amplifire.player.listener;

import net.icelane.amplifire.player.codec.AudioProcessingLayer;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlayerEvent {

    protected  AudioProcessingLayer source;

    public PlayerEvent(AudioProcessingLayer audioProcessingLayer) {
            super();
            this.source = audioProcessingLayer;
    }

    public AudioProcessingLayer getSource() {
            return source;
    }
	
}
