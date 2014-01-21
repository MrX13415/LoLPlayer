package audioplayer.player.codec.MPEG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioSystem;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SampleBuffer;
import audioplayer.player.codec.AudioProcessingLayer;
import audioplayer.player.codec.AudioType;
import audioplayer.player.device.AudioDeviceLayer;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;

/**
 *  LoLPlayer II - Audio-Player Project
 *  
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
	
	/** Resets the file bit stream and the audio device
	 */
	public void initializeAudioDevice() throws Exception{
		bitstream = new Bitstream(new FileInputStream(file.getFile()));
		audioDevice = new AudioDeviceLayer();
		audioDevice.open(decoder = new Decoder());
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

			boolean hasMoreFrames = true;
			
			while (hasMoreFrames && !decoderThread.isInterrupted()) {
				long tplStart = System.currentTimeMillis();
				
				boolean notPaused = !isPaused();
				boolean skip = skipFrames;
				
				if (!audioDevice.isOpen()) hasMoreFrames = false;

				Header header = null;
                                
				if (notPaused || skip)
					header = bitstream.readFrame();
                                        
				if (header != null){
					timePerFrame = header.ms_per_frame();

					if (!skip){
						output = (SampleBuffer) decoder.decodeFrame(header, bitstream);

						if (audioDevice.isOpen()) {
							audioDevice.setVolume(volume);
							audioDevice.writeImpl(output.getBuffer(), 0, output.getBufferLength());
						}
					}

				}else if (notPaused) hasMoreFrames = false;
				
				if (notPaused || skip) bitstream.closeFrame();

				if (skip){
					if (newTimePosition < internaltimePosition){
						closeStream();
						initializeAudioDevice();
						internaltimePosition = 0;
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
                    System.err.println("Error while playing Audiofile: " + e);
		}finally{
			boolean nextSong = isPlaying();
			
			if (nextSong && reachedEnd()){
				//Listener
				synchronized (listener) {
					for (int i = 0; i < listener.size(); i++) {
						PlayerListener pl = listener.get(i);
						pl.onPlayerNextSong(new PlayerEvent(this));
					}	
				}				
			}

			stop();
		}
	}

	public synchronized void closeStream() {		
		try {
			if (bitstream != null) bitstream.close();
		} catch (BitstreamException ex) {}
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
	 * @throws StreamLengthException 
	 */
	public long calculateStreamLength(File f) throws StreamLengthException{
		Bitstream bitstream = null;
		long length = 0; //in ms
		 
		try{
	        bitstream = new Bitstream(new FileInputStream(f));
	        	
	        Header header = bitstream.readFrame();

	        long filesize = f.length();
	        if (filesize != AudioSystem.NOT_SPECIFIED) {
	        	length = (long) (((double)filesize * 8d / (double)header.bitrate()) * 1000d);
	        }	
        }catch(Exception bex){
        	throw new StreamLengthException(f);
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
		return f.getName().toLowerCase().endsWith(".mp1") || f.getName().toLowerCase().endsWith(".mp2") || f.getName().toLowerCase().endsWith(".mp3");
	}

	@Override
	public AudioType getSupportedAudioType() {
		return new MPEGAudioType();
	}
	
}
