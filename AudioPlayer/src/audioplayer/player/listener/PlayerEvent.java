package audioplayer.player.listener;

import audioplayer.player.codec.AudioProcessingLayer;


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
