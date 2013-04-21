package audioplayer.player.codec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.activity.InvalidActivityException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import audioplayer.player.AudioDeviceLayer;
import audioplayer.player.AudioFile;
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
public class AudioProcessingLayer implements Runnable{
	
	public enum PlayerState{
		NEW, INIT, PLAYING, STOPPED, PAUSED;
	}
	
	protected Bitstream bitstream;					//The MPEG audio bitstream
	protected Decoder decoder;						//The MPEG audio decoder
	
	protected AudioDeviceLayer audioDevice;			
	protected SampleBuffer output;
	
	protected AudioFile file;								
	protected Thread decoderThread;					
	protected ArrayList<PlayerListener> listener = new ArrayList<PlayerListener>();			
	
	protected PlayerState state = PlayerState.NEW; 	
	
	protected boolean closed;							

	protected long internaltimePosition;						//in milliseconds
    protected long timePosition;
	protected long newTimePosition;					//in milliseconds
	protected boolean skipFrames;					
	protected double skipedFrames;				
	protected double timePerFrame;					//in milliseconds
	
	protected float volume = 25f; 					//default: 80%
	
	protected long timePerLoop = 0;
	
	public AudioProcessingLayer() {
            audioDevice = new AudioDeviceLayer();
	}
		
	public AudioDeviceLayer getAudioDevice() {
		return audioDevice;
	}

	public void setAudioDevice(AudioDeviceLayer audioDevice) {
		this.audioDevice = audioDevice;
	}

	public ArrayList<PlayerListener> getPlayerListener() {
		return listener;
	}
	
	public void addPlayerListener(PlayerListener playerListener) {
		listener.add(playerListener);
	}
	
	public void removePlayerListener(PlayerListener playerListener) {
		listener.remove(playerListener);
	} 

	public Bitstream getBitstream() {
		return bitstream;
	}

	public Decoder getDecoder() {
		return decoder;
	}

	public Thread getDecoderThread() {
		return decoderThread;
	}

	public PlayerState getState() {
		return state;
	}

	public long getTimePerLoop() {
		return timePerLoop;
	}

	public boolean isSkipFrames() {
		return skipFrames;
	}

	public boolean isClosed() {
		return closed;
	}
		
	public boolean isStopped(){
		return state == PlayerState.STOPPED;
	}

	public boolean isPlaying(){
		return state == PlayerState.PLAYING;
	}
	
	public boolean isPaused(){
		return state == PlayerState.PAUSED;
	}
	
	public boolean isInitialized(){
		return state == PlayerState.INIT;
	}
	
	public boolean isNew(){
		return state == PlayerState.NEW;
	}
	
	public SampleBuffer getOutput() {
		return output;
	}

	/** Set the volume of the player
	 * 
	 * @param vol The volume in range from 0.0 till 100.0
	 */
	public void setVolume(float vol){
		this.volume = vol;
	}
	
	/** Get the volume of the player
	 * 
	 * @return The volume in range from 0.0 till 100.0
	 */
	public float getVolume(){
		return this.volume;
	}
	
	/** Get the current playing file
	 * 
	 * @return The file
	 */
	public AudioFile getAudioFile() {
		return file;
	}

	/** Get the current position of the current playing file
	 * 
	 * @return The position in milliseconds
	 */
	public long getTimePosition() {
		return timePosition;
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
	public void resetPlayer() throws Exception{	
		bitstream = new Bitstream(new FileInputStream(file.getFile()));
		audioDevice = new AudioDeviceLayer();
		audioDevice.open(decoder = new Decoder());

		closed = false;
		internaltimePosition = 0;
	}
	
	/** Toggle pause for the current song
	 * 
	 */
	public void togglePause(){
		setPause(!isPaused());
	}
	
	/** Pause the current song
	 * 
	 * @param pause <code>true</code> to pause the song
	 */
	public void setPause(boolean pause){
		if (pause) state = PlayerState.PAUSED;
		else state = PlayerState.PLAYING;
	}
	
	
	public void togglePlayPause(){
		if (isPlaying()) togglePause();
		else try { play(); } catch (InvalidActivityException e) {}
	}
	
	/** Start playing the current song or resume it if paused
	 * @throws InvalidActivityException 
	 * 
	 */
	public void play() throws InvalidActivityException{
		if (state == PlayerState.INIT){
			createDecoderThread();
			
		}else if (state == PlayerState.PAUSED) {
			togglePause();
			
		}else if (state == PlayerState.STOPPED || closed) {
			if (file != null){
				initialzePlayer(file);
				createDecoderThread();
			}
		}else if (state == PlayerState.NEW) {
			throw new InvalidActivityException("player not initalized");
		}
	}
	
	/**
	 * Creates the audio file decoder thread 
	 */
	public void createDecoderThread(){
		decoderThread = new Thread(this){
			/*
			 * Interrupt logic for decoder thread ...
			 */
			protected boolean isInterrupted;
			
			public boolean isInterrupted(){
				return isInterrupted || super.isInterrupted();
			}
			
			public void interrupt(){
				isInterrupted = true;
				super.interrupt();
			}
		};
		
		decoderThread.setName("Stream Decoder");
		decoderThread.start();
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
							audioDevice.write(output.getBuffer(), 0, output.getBufferLength() * 2);
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
                                            timePosition = internaltimePosition;
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
	
	public boolean reachedEnd(){
		return getStreamLength() - timePosition < 10000;
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
	
	/** Returns the length of the current file 
	 * <br>
	 * <br>
	 * <code> lenght = file_size * 8 / bitrate </code>
	 * <br>
	 * <br>
	 * @return The length of the given file in milliseconds
	 * @throws BitstreamException
	 * @throws FileNotFoundException
	 */
	public long getStreamLength(){
		try{
			if (file != null) return AudioFile.calculateStreamLength(this.file.getFile());
        }catch(Exception ex){}
		return 0;
	}
}
