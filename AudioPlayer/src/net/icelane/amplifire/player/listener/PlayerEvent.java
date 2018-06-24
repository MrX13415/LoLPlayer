package net.icelane.lolplayer.player.listener;

import net.icelane.lolplayer.player.codec.AudioProcessingLayer;

/**
 *  LoLPlayer II - Audio-Player Project
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
