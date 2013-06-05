package audioplayer.player.analyzer;

import javax.sound.sampled.AudioFormat;

/** Class to Normalize audio data from byte to float.
 * 
 * @author Oliver
 *
 */
public class Normalizer {

	private AudioFormat audioFormat;
	
	private float[][] channels;

	private int  channelSize;
	private long audioSampleSize;
	
	public Normalizer( AudioFormat pFormat ) {

		audioFormat = pFormat;
		
		channels = new float[ pFormat.getChannels() ][]; 
		
		channelSize     = audioFormat.getFrameSize() / audioFormat.getChannels();
		audioSampleSize = ( 1 << ( audioFormat.getSampleSizeInBits() - 1 ) );
	}

	public float[][] normalize( byte[] pData, int pPosition, int pLength ) {
		
		int wChannels  = audioFormat.getChannels();
		int wSsib      = audioFormat.getSampleSizeInBits();
		int wFrameSize = audioFormat.getFrameSize();
		
		for( int c = 0; c < audioFormat.getChannels(); c++ ) {
			channels[ c ] = new float[ pLength / 2 ];
		}
		
		//audio data.
		for( int sp = 0; sp < pLength / 2; sp++ ) { 
			
			if ( pPosition >= pData.length ) {
				pPosition = 0;
			}
			
			int cdp = 0;
			
			//channels.
			for( int ch = 0; ch < wChannels; ch++ ) {

				//Sign least significant byte. (PCM_SIGNED)
				long sm = ( pData[ pPosition + cdp ] & 0xFF ) - 128;
				
				for( int bt = 8, bp = 1; bt < wSsib; bt += 8 ) {
					sm += pData[ pPosition + cdp + bp ] << bt;
					bp++;
				}
				
				//normalized data.
				channels[ ch ][ sp ] = (float)sm / audioSampleSize;

				cdp += channelSize;
				
			}
				
			pPosition += wFrameSize;
			
		}
		
		return channels;
		
	}
	
}

