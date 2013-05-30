package audioplayer.player.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioSystem;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import audioplayer.player.AudioDeviceLayer;
import audioplayer.player.codec.AudioFile.AudioType;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;

/**
 * Audio processing layer for the MPEG 1-2.5 Layer I-III audio file format 
 * 
 * @author Oliver
 * @version 1.1
 * 
 */
public class MPEGAudioProcessingLayer extends AudioProcessingLayer  implements Runnable{
		
	protected Bitstream bitstream;					//The MPEG audio bitstream
	protected Decoder decoder;						//The MPEG audio decoder
	
	protected SampleBuffer output;
	
	public MPEGAudioProcessingLayer() {
		audioDevice = new AudioDeviceLayer();
	}
		
	public Bitstream getBitstream() {
		return bitstream;
	}

	public Decoder getDecoder() {
		return decoder;
	}
	
	public SampleBuffer getOutput() {
		return output;
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
				setState(PlayerState.INIT);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Resets the file bit stream and the audio device
	 */
	public void resetPlayer() throws Exception{	
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
				synchronized (listener) {
					for (int i = 0; i < listener.size(); i++) {
						PlayerListener pl = listener.get(i);
						pl.onPlayerNextSong(new PlayerEvent(this));
					}	
				}				
			}
		}
	}

	/** Stops the current playing file and closes the file stream
	 */
	public void stop() {
		if (closed != true && !isNew()) {
			state = PlayerState.STOPPED;
			if (decoderThread != null) decoderThread.interrupt();
			
			//Listener
			synchronized (listener) {
				for (int i = 0; i < listener.size(); i++) {
					PlayerListener pl = listener.get(i);
					pl.onPlayerStop(new PlayerEvent(this));
				}
			}
						
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
			
	/** Return the length of a given file in milliseconds
	 * <br>
	 * <br>
	 * <code> lenght = file_size * 8 / bitrate </code>
	 * <br>
	 * <br>
	 * @param f The file
	 * @return The length of the given file 'f' in milliseconds
	 * @throws BitstreamException
	 * @throws FileNotFoundException
	 */
	public long calculateStreamLength(File f){
		Bitstream bitstream = null;
		long length = 0; //in ms
		 
		try{
	        bitstream = new Bitstream(new FileInputStream(f));
	        Header header = bitstream.readFrame();
	        
	        long filesize = f.length();
	        if (filesize != AudioSystem.NOT_SPECIFIED) {
	        	length = (long) (((double)filesize * 8d / (double)header.bitrate()) * 1000d);
	        }	
        }catch(BitstreamException bex){
        	System.err.println("[WARNING] Can't determine file length in milliseconds: " + bex);
        }catch(FileNotFoundException fex){
        	System.err.println("[WARNING] Can't determine file length in milliseconds: " + fex);
        }finally{
           if (bitstream != null)
			try {
				bitstream.close();
			} catch (BitstreamException e) {}
        }
		
		return length;
	}

	/** Determines if the given file is supported by this class
	 * 
	 * @param f
	 * @return if the given file is an MPEG file (e.g. MP3)
	 */
	public boolean isSupportedAudioFile(File f) {
		return f.getName().endsWith(".mp1") ||
			   f.getName().endsWith(".mp2") || 
			   f.getName().endsWith(".mp3");
	}

	@Override
	public AudioType getSupportedAudioType() {
		return AudioType.MPEG;
	}
	
}
