package net.icelane.amplifire.analyzer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class PCMData {

	private byte[] pcmData;
	private int offset;
	private int length;
	private AudioFormat format;
	
	//== data holders ======
	private int bits;		// = 
	private int channels;		// = 
	private int frameSize = 1;		// = (sampleSize / 8) * channels
	private boolean bigEndian;
	private Encoding encoding;
	
	public PCMData(AudioFormat format, byte[] bytes) throws IllegalArgumentException {
		this(format, bytes, 0, bytes.length);
	}
	
	public PCMData(AudioFormat audioFormat, byte[] pcmData, int offset, int length) throws IllegalArgumentException {
		super();
		this.format = audioFormat;
		this.pcmData = pcmData;
		this.offset = offset;
		this.length = length;
		
		if (audioFormat == null || pcmData == null)
			throw new IllegalArgumentException("AudioFormat or PCMData can't be null");
		
		/*		
		4
		2
		PCM_SIGNED
		48000.0
		16
		48000.0
		false
*/
		
//		System.out.println(format.getFrameSize());
//		System.out.println();
//		System.out.println(format.getEncoding());
//		System.out.println(format.getSampleRate());
//		System.out.println(format.getSampleSizeInBits());
//		System.out.println(format.getFrameRate());
//		System.out.println(format.isBigEndian());

		bits = audioFormat.getSampleSizeInBits();
		channels = audioFormat.getChannels();
		frameSize = audioFormat.getFrameSize();
		bigEndian = audioFormat.isBigEndian();
		encoding = audioFormat.getEncoding();
		
//		getData(0, 0);
	}

	public static PCMData getEmptyInstance(){
		AudioFormat af = new AudioFormat(48000, 16, 2, true, false);
		
		byte[] data = new byte[1024 * 2];
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
		
		return new PCMData(af, data);
	}

	public byte[] getBytes() {
		return pcmData;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public AudioFormat getFormat() {
		return format;
	}
	
	public boolean isLittleEndian(){
		return !bigEndian;
	}
	
	public boolean isBigEndian() {
		return bigEndian;
	}

	public int getDataLenght(){
		return pcmData.length / frameSize;
	}
	
	public float getData(int dataIndex, int channel){
		
		// byte array example: 16 Bit; 4 Channel 
	    //  c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4     
		//[0][1] [2][3] [4][5] [6][7] | [8][9] [0][1] [2][3] [4][5] | [6][7] [8][9] [0][1] [2][3] | [4][5] [6][7] [8][9] [0][1] | [2][3] [4][5] [6][7] [8][9] |  
		// 0 		                            1 		                             2 		                              3 		                             4
		
		// calculate index of the given data-index and of the given channel
		int index = frameSize * dataIndex + (channel * (frameSize / channels));
		
		int byteCount = bits / 8;
		
		long bytedata = 0L;
		
		if (isLittleEndian()){
			for (int byteIndex = 0; byteIndex < byteCount; byteIndex++) {

				long b = pcmData[index + byteIndex] << (byteIndex * 8);
				
//				//set all bits to 0 except the bits defined by the mask
//				//this will make sure, no data is where it shouldn't be 
//				if (byteIndex == 0) b &= 0xFF;

				//PCM_SIGNED: Sign least significant byte.
				if (encoding == Encoding.PCM_SIGNED && byteIndex == 0)
					b -= 128;
				
				bytedata += b;

//				if (byteIndex == byteCount - 1)
//					System.out.println(getBinary(b) + " => " +getBinary(bytedata));
//				else
//					System.out.println(getBinary(b));
			}
		}
		
		return bytedata / (float) (Math.pow(2, 16) - 1); // 2 ^ [bits] possibilities
	}
	
	public PCMData getQuadMixed(){
		
		byte[] newData = new byte[pcmData.length * 2];
		
		// byte array example: 16 Bit; 4 Channel 
	    //  c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4       c1     c2     c3     c4     
		//[0][1] [2][3] [4][5] [6][7] | [8][9] [0][1] [2][3] [4][5] | [6][7] [8][9] [0][1] [2][3] | [4][5] [6][7] [8][9] [0][1] | [2][3] [4][5] [6][7] [8][9] |  
		// 0 		                            1 		                             2 		                              3 		                             4
		
		for (int di = 0; di < pcmData.length; di += (bits / 8 * channels) ) {
			for (int ci = 0; ci < (bits / 8 * channels); ci += (bits / 8)) {
				for (int bi = 0; bi < (bits / 8); bi++) {
					int index = di + ci + bi;
					//System.out.println(di + " + " + ci + " + " + bi + " = " + index);

					newData[index] = pcmData[index];
					
					newData[frameSize + index] = pcmData[index];
					
				}
			}
		}
		
		AudioFormat af = new AudioFormat(
				format.getSampleRate(),
				format.getSampleSizeInBits(),
				4,
				format.getEncoding() == Encoding.PCM_SIGNED ? true : false,
				bigEndian);
		
		return new PCMData(af, newData);
	}
	
	/** Converts the given data to a binary string with leading zeros in the given length
	 * 
	 * @param data The data to by returned as binary string
	 * @param digits The digit length
	 * @return the given data as binary string with leading zeros in the given length
	 */
	public static String getBinary(long bdata, int digits){
		String data = Long.toBinaryString(bdata);
		String zeros = "";
		for (int i = data.length(); i < digits; i++) {
			zeros += "0";
		}
		return zeros + data;
	}
	
	/** Converts the given data to a binary string with leading zeros in the given length
	 * 
	 * @param data The data to by returned as binary string
	 * @param digits The digit length
	 * @return the given data as binary string with leading zeros in the given length
	 */
	public static String getBinary(int bdata, int digits){
		return getBinary(bdata, digits);
	}
	
	/** Converts the given data to a binary string with leading zeros and a length 64 bits
	 * 
	 * @param data The data to by returned as binary string
	 * @return the given data as binary string with leading zeros and a length 64 bits
	 */
	public static String getBinary(long bdata){
		return getBinary(bdata, 64);
	}
	
	/** Converts the given data to a binary string with leading zeros and a length 32 bits
	 * 
	 * @param data The data to by returned as binary string
	 * @return the given data as binary string with leading zeros and a length 32 bits
	 */
	public static String getBinary(int bdata){
		return getBinary(bdata, 32);
	}
	
}
