package audioplayer.player.analyzer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import audioplayer.Application;
import audioplayer.player.device.AudioDeviceLayer;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 *         Analyzer class to analyzer audio data ...
 */
public class Analyzer {

	private Thread analyzerThread = new Thread();
	private Thread initNormalizerThread = new Thread();

	private volatile AudioDeviceLayer device;
	private volatile Normalizer normalizer;

	private volatile Graph g;

	private HashMap<Integer, Color> graphSettingsColor = new HashMap<Integer, Color>();
	private Color mergedGraphSettingsColor;

	private HashMap<Integer, Integer> graphSettingsYOffset = new HashMap<Integer, Integer>();
	private int mergedGraphSettingsYOffset;

	private volatile ArrayList<AudioGraph> channelGraphs = new ArrayList<AudioGraph>();

	public static final int initCapacity = 10000;
	private volatile int buffermax = 25;
	private volatile ArrayBlockingQueue<AudioBytesBlock> toAnalyze = new ArrayBlockingQueue<AudioBytesBlock>(initCapacity);
	
	private volatile boolean active;
	private volatile boolean initNormalizerActive;

	private boolean mergedChannels;
	private int detailLevel = 40;

	private volatile float[] channelsValueSum;
	private volatile int[] chennalsDetailIndex;

	private int sleepTime = 20; // (50 FPS) in ms ; must be max 25 ms, otherwise
								// the
								// Graph on the GUI will start lagging

	private long duractionTime; // in ns
	private long speed; // in AudioByteBlock/s

	private boolean DEBUG = false;
	private boolean defaultGraphsSet;

	public Analyzer(Graph g) {
		super();

		// define class defaults ...
		for (int i = 0; i < 6; i++) {
			resetDefaultChannelGraphColor(i);
		}

		resetDefaultMergedChannelGraphColor();

		this.g = g;

		initAnalyzerThread();

		setDefaultGraphs(2);
	}

	public Analyzer(AudioDeviceLayer device, Graph g) {
		this(g);
		init(device);
	}

	public void setDefaultGraphs(int count) {
		for (int i = 0; i < count; i++) {
			Color color = getDefaultChannelGraphColor(count);
			if (color == null)
				color = new Color(new Random().nextInt(256),
						new Random().nextInt(256), new Random().nextInt(256));

			Integer yOffset = getDefaultChannelGraphYOffset(count);

			AudioGraph ag = new AudioGraph(yOffset, color);
			ag.clear();
			ag.addValue(Float.MAX_VALUE);
			ag.setName(getDefaulltGraphName(i, count));
			g.addGraph(ag);
			defaultGraphsSet = true;
		}
	}

	public String getDefaulltGraphName(int index, int count) {
		return count == 1 ? "MONO" : index == 0 ? "L" : index == 1 ? "R" : "";
	}

	/**
	 * Set the default color for each graph by index (usualy there are two
	 * graphs).<br>
	 * The given Color will be used on generating the graphs while initializing
	 * the normalizer for the AudioDevice, usualy defined by the Constructor
	 * 
	 * @param channelIndex
	 *            The index of the graph
	 * @param color
	 *            The color to be set for the graph defined by the index
	 **/
	public void setDefaultChannelGraphColor(int channelIndex, Color color) {
		graphSettingsColor.put(new Integer(channelIndex), color);
	}

	public Color getDefaultChannelGraphColor(int channelIndex) {
		return graphSettingsColor.get(new Integer(channelIndex));
	}

	public Color getMergedGraphSettingsColor() {
		return mergedGraphSettingsColor;
	}

	public void setMergedGraphSettingsColor(Color mergedGraphSettingsColor) {
		this.mergedGraphSettingsColor = mergedGraphSettingsColor;
	}

	public int getMergedGraphSettingsYOffset() {
		return mergedGraphSettingsYOffset;
	}

	public void setMergedGraphSettingsYOffset(int mergedGraphSettingsYOffset) {
		this.mergedGraphSettingsYOffset = mergedGraphSettingsYOffset;
	}

	/**
	 * Set the default Y-offset for each graph by index (usualy there are two
	 * graphs).<br>
	 * The given y-offset will be set on generating the graphs while
	 * initializing the normalizer for the AudioDevice, usualy defined by the
	 * Constructor
	 * 
	 * @param channelIndex
	 *            The index of the graph
	 * @param color
	 *            The y-offset to be set for the graph defined by the index
	 **/
	public void setDefaultChannelGraphYOffset(int channelIndex, int yoffset) {
		graphSettingsYOffset.put(new Integer(channelIndex),
				new Integer(yoffset));
	}

	public int getDefaultChannelGraphYOffset(int channelIndex) {
		Integer i = graphSettingsYOffset.get(new Integer(channelIndex));
		return (i == null ? 0 : i);
	}

	/**
	 * Resets the color settings used on graph generating to the class defaults. <br>
	 * <br>
	 * The class defaults are:<br>
	 * (index : color)<br>
	 * 0 : red<br>
	 * 1 : blue<br>
	 * 2 : yellow<br>
	 * 3 : green<br>
	 * 4 : magenta<br>
	 * 5 : orange<br>
	 * 
	 * @param channelIndex
	 *            the Graph defined by Index
	 **/
	public void resetDefaultChannelGraphColor(int channelIndex) {
		switch (channelIndex) {
		case 0:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor1);
			return;
		case 1:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor2);
			return;
		case 2:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor3);
			return;
		case 3:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor4);

			return;
		case 4:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor5);
			return;
		case 5:
			setDefaultChannelGraphColor(
					channelIndex,
					Application.getColors().color_graph_defaultChannelGraphColor6);
			return;
		}
	}

	/**
	 * Resets the color settings used on graph generating to the class defaults.
	 * 
	 */
	public void resetDefaultMergedChannelGraphColor() {
		mergedGraphSettingsColor = Color.red;
	}

	/**
	 * Resets the Y-Offset to the class default setting, which is "0.0".
	 * 
	 * @param channelIndex
	 *            the Graph defined by Index
	 **/
	public void resetDefaultChannelGraphYOffset(int channelIndex) {
		graphSettingsYOffset.remove(new Integer(channelIndex));
	}

	/**
	 * Resets the Y-Offset to the class default setting, which is "0.0".
	 * 
	 **/
	public void resetDefaultMergedChannelGraphYOffset() {
		mergedGraphSettingsYOffset = 0;
	}

	public Normalizer getNormalizer() {
		return normalizer;
	}

	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean isDEBUG() {
		return DEBUG;
	}

	public void setDebug(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	public long getDuractionTime() {
		return duractionTime;
	}

	public long getSpeed() {
		return speed;
	}

	public Graph getGraph() {
		return g;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}

	public ArrayList<AudioGraph> getChannelGraphs() {
		synchronized (channelGraphs) {
			return channelGraphs;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setDevice(AudioDeviceLayer device) {
		this.device = device;
	}

	public int getBufferMax() {
		return buffermax;
	}
	
	public int getBufferSize(){
		return initCapacity - toAnalyze.remainingCapacity();
	}

	protected void stop() {
		active = false;
	}

	public boolean isMergedChannels() {
		return mergedChannels;
	}

	public void setMergedChannels(boolean mergedChannels) {
		if (this.mergedChannels != mergedChannels) {
			clearGraphs();
			this.mergedChannels = mergedChannels;
		}
	}

	public void clearGraphs() {
		synchronized (channelGraphs) {
			g.clearGraphs();
			channelGraphs.clear();
			normalizer = null;
			defaultGraphsSet = false;
		}
	}

	public void init(AudioDeviceLayer device) {
		clearGraphs();
		setDefaultGraphs(2);

		this.device = device;
		device.setAnalyzer(this);

		initNormalizer();
	}

	public void initNormalizer() {
		if (!initNormalizerActive) {
			initNormalizerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					initNormalizerActive = true;
					while (normalizer == null && initNormalizerActive) {

						try {
							Thread.sleep(16);
						} catch (InterruptedException e1) {
						}

						try {
							normalizer = new Normalizer(device.getSource()
									.getFormat());
							channelsValueSum = new float[device.getFmt()
									.getChannels()];
							chennalsDetailIndex = new int[device.getFmt()
									.getChannels()];

							if (mergedChannels) {
								initMergedChannelGraph();
							} else {
								initChannelGraphs();
							}
						} catch (Exception e) {
						}
					}
					initNormalizerActive = false;
				}
			});
			initNormalizerThread.setName("initNormalizerThread");
			initNormalizerThread.start();
		}
	}

	public void addToAnalyze(byte[] b, int off, int len) {
		addToAnalyze(new AudioBytesBlock(b.clone(), off, len));
	}

	public void addToAnalyze(AudioBytesBlock abb) {
		try {
			if (getBufferSize() < getBufferMax()){
				toAnalyze.put(abb);
			}else{
				if(DEBUG) System.err.println("WARNING: AnalyzerThread: Can't keep up!");
			}
		} catch (InterruptedException e) {
		}
	}

	public void clearData() {
		toAnalyze.clear();
	}

	private void initMergedChannelGraph() {
		synchronized (channelGraphs) {
			g.clearGraphs();
			AudioGraph ag = new AudioGraph(mergedGraphSettingsColor,
					mergedGraphSettingsYOffset);
			channelGraphs.add(ag);
			g.addGraph(ag);
		}
	}

	public int getDetailLevel() {
		return detailLevel;
	}

	public void setDetailLevel(int detailLevel) {
		this.detailLevel = detailLevel;
	}

	private void initChannelGraphs() {
		synchronized (channelGraphs) {
			if (defaultGraphsSet)
				g.clearGraphs();

			int channels = device.getFmt().getChannels();
			while (channelGraphs.size() < channels) {

				int index = channelGraphs.size();
				
				resetDefaultChannelGraphColor(index);
				
				Color color = getDefaultChannelGraphColor(index);
				if (color == null)
					color = new Color(new Random().nextInt(256),
							new Random().nextInt(256),
							new Random().nextInt(256));

				Integer yOffset = getDefaultChannelGraphYOffset(index);

				AudioGraph ag = new AudioGraph(yOffset, color);
				ag.setName(getDefaulltGraphName(index, channels));

				channelGraphs.add(ag);
				g.addGraph(ag);
			}
		}
	}

	public void initAnalyzerThread() {
		if (this.isActive() || analyzerThread.isAlive())
			this.stop();

		analyzerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				active = true;
				while (active) {

					long s = System.nanoTime();

					try {
						try {
							// System.out.println("wait: " + sleepTime + "s");
							Thread.sleep(sleepTime);
						} catch (InterruptedException e1) {
						}

						int bx = toAnalyze.size();
						
						//can't keep up ...
						if (bx > buffermax){
							if(DEBUG) System.err.println("WARNING: AnalyzerThread: Can't keep up!");
							buffermax = bx;
							continue;
						}
						
						if (normalizer == null) {
							initNormalizer();
							continue;
						}

						if (toAnalyze.size() <= 0)
							continue;

						AudioBytesBlock abb = toAnalyze.take();
						byte[] byteBlock = abb.getBytes();// .clone();

						int offset = abb.getOffset();
						int length = abb.getLength();

						float[][] channelData = normalizer.normalize(byteBlock,
								offset, length);

						if (mergedChannels) {
							float[] mergedChannelData = channelMerge(channelData);
							channelData = new float[1][mergedChannelData.length];
							channelData[0] = mergedChannelData;
						}

						int samples = channelData[0].length;
						int channels = channelData.length;

						for (int i = 0; i < samples; i++) {

							if (channelGraphs.size() <= 0)
								break;

							try {

								for (int j = 0; j < channels; j++) {

									// get current sample form the current
									// channel ...
									float sample = channelData[j][i];

									if (channelGraphs.size() <= 0)
										break;

									AudioGraph graph = null;

									synchronized (channelGraphs) {
										graph = channelGraphs.get(j);
									}

									channelsValueSum[j] += sample;
									chennalsDetailIndex[j]++;

									if (chennalsDetailIndex[j] >= detailLevel) {

										float valueSum = channelsValueSum[j];
										int detailIndex = chennalsDetailIndex[j];

										float averageValue = (valueSum / (float) detailIndex);

										// send value to the graph
										graph.addValue(averageValue);

										chennalsDetailIndex[j] = 0;
										channelsValueSum[j] = 0;
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					duractionTime = System.nanoTime() - s;
					speed = 1000000000 / duractionTime; // 1000000000ns == 1s

					if (DEBUG)
						System.out
								.printf("Duraction: %9s ns  Buffer: %4s /%4s  Speed: %4s AudioByteBlocks/s\n",
										duractionTime,
										getBufferSize(), 
										buffermax,
										speed);
				}
				System.err
						.println("WARNING: The thread \"AnalyzerThread\" has stopped!");
			}
		});
		analyzerThread.setName ("AnalyzerThread");
		analyzerThread.start();
	}

	/**
	 * Merges two audio channels into one.
	 * 
	 * @param pLeft
	 *            Left channel data.
	 * @param pRight
	 *            Right channel data.
	 * 
	 * @return Merged results of the supplied left and right channel data.
	 */
	protected float[] channelMerge(float[][] pChannels) {
		for (int a = 0; a < pChannels[0].length; a++) {

			float wMcd = 0;

			for (int b = 0; b < pChannels.length; b++) {
				wMcd += pChannels[b][a];
			}

			pChannels[0][a] = wMcd / (float) pChannels.length;

		}

		return pChannels[0];
	}

	class AudioBytesBlock {

		private byte[] bytes;
		private int offset;
		private int length;

		public AudioBytesBlock(byte[] bytes, int offset, int length) {
			super();
			this.bytes = bytes;
			this.offset = offset;
			this.length = length;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

	}
}
