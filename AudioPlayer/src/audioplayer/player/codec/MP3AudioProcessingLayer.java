package audioplayer.player.codec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import audioplayer.player.AudioDeviceLayer;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;

/**
 * 
 * @author Oliver
 * @version 1.1
 * 
 * version: 1.1
 *  - Changed File to AudioFile
 */
public class MP3AudioProcessingLayer extends AudioProcessingLayer implements Runnable{
			
	protected Bitstream bitstream;					//The MPEG audio bitstream
	protected Decoder decoder;						//The MPEG audio decoder
	
	public MP3AudioProcessingLayer() {
            audioDevice = new AudioDeviceLayer();
	}
		
	/** Initialize the play with the given file<br>
	 * Always call this first, to play a song or to change the current playing song
	 *   
	 * @param f	The file to be played
	 */
	public void initialzePlayer(AudioFile f){
		try {
			this.file = f;
			
			if (decoderThread != null) decoderThread.interrupt();
			try {
				if (bitstream != null) bitstream.close();
			} catch (BitstreamException ex) {}
			if (audioDevice != null) audioDevice.close();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			
			resetPlayer();
			
			newTimePosition = 0;
			skipFrames = false;
			skipedFrames = 0;

			if (!isPlaying())
				state = PlayerState.INIT;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Resets the file bit stream and the audio device
	 */
	public void resetPlayer() throws JavaLayerException, FileNotFoundException{	
		bitstream = new Bitstream(new FileInputStream(file.getFile()));
		audioDevice = new AudioDeviceLayer();
		audioDevice.open(decoder = new Decoder());

		closed = false;
		internaltimePosition = 0;
	}
	
	/** Frame decoding and audio playing routine
	 *  <br>
	 *  <br>
	 *  NOTE: Do not call this method directly! Use <code>play()</code> instead 
	 */
	@Override
	public void run() {
		try {
			if (!isPaused()) state = PlayerState.PLAYING;
			
			//Listener
			for (PlayerListener pl : listener) pl.onPlayerStart(new PlayerEvent(this));

			boolean hasMoreFrames = true;;
			
			while (hasMoreFrames && !decoderThread.isInterrupted()) {
				long tplStart = System.currentTimeMillis();
				
				boolean notPaused = !isPaused();
								
				if (!audioDevice.isOpen()) hasMoreFrames = false;

				Header h = null; 
				if (notPaused || skipFrames) h = bitstream.readFrame();
								
				if (h != null){
					timePerFrame = h.ms_per_frame();

					if (!skipFrames){
						try {Thread.sleep(1);} catch (Exception e) {}
						
						output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

						if (audioDevice.isOpen()) {
							audioDevice.setVolume(volume);
							audioDevice.write(output.getBuffer(), 0, output.getBufferLength());
						}
					}

				}else if (notPaused) hasMoreFrames = false;
				
				if (notPaused || skipFrames) bitstream.closeFrame();

				if (skipFrames){
					if (newTimePosition < internaltimePosition){
						resetPlayer();
						skipFrames = true;
					}
					
					skipedFrames += timePerFrame;
					if(newTimePosition - skipedFrames <= internaltimePosition) {
						internaltimePosition += skipedFrames;
						skipedFrames = 0;
						skipFrames = false;
					}
				}else{
					if (timePerFrame <= 0) determineTimePerFrame();
					if (notPaused) internaltimePosition += timePerFrame;
                                            timePosition = (long) internaltimePosition;
				}
								
				timePerLoop = System.currentTimeMillis() - tplStart;
			} //loop end
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			boolean nextSong = isPlaying();
			
			stop();
			
			System.out.println(timePosition + " " + internaltimePosition + " " + getStreamLength());
			
			if (nextSong && reachedEnd()){
				//Listener
				for (PlayerListener pl : listener) pl.onPlayerNextSong(new PlayerEvent(this));
			}
		}
	}
		
	/** Set the position of the current file to play from
	 * 
	 * @param ms time to play from in milliseconds
	 */
	public void setPostion(long ms){
		newTimePosition = ms;
		skipFrames = true;
	}
	
	/** Stops the current playing file and closes the file stream
	 */
	public void stop() {
		if (closed != true && !isNew()) {
			state = PlayerState.STOPPED;
			if (decoderThread != null) decoderThread.interrupt();
			
			//Listener
			for (PlayerListener pl : listener) pl.onPlayerStop(new PlayerEvent(this));
			
			try {
				if (audioDevice != null) bitstream.close();
			} catch (BitstreamException ex) {}

			if (audioDevice != null) audioDevice.close();
            
			closed = true;
			internaltimePosition = 0;
			newTimePosition = 0;
			skipFrames = false;
			skipedFrames = 0;
		}
	}
	
	protected void determineTimePerFrame() throws BitstreamException, FileNotFoundException{
		Bitstream bitstream = null;
		try {
			bitstream = new Bitstream(new FileInputStream(file.getFile()));
	        Header header = bitstream.readFrame();
		    timePerFrame = header.ms_per_frame();
		}catch(BitstreamException bex){
	        throw bex;
        }catch(FileNotFoundException fex){
        	throw fex;
		}finally{
			bitstream.close();
		}
		
	}

}
