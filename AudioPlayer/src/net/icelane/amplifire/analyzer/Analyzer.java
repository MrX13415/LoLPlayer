package net.icelane.amplifire.analyzer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.AudioFormat;

import org.magicwerk.brownies.collections.GapList;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.render.GraphRender;
import net.icelane.amplifire.analyzer.render.RenderComponent;
import net.icelane.amplifire.analyzer.source.AnalyzerSourceDevice;

/**
 * amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 *         Analyzer class to analyzer audio data ...
 */
public class Analyzer {
	
	private Thread analyzerThread = new Thread();
	private Thread initNormalizerThread = new Thread();
	private Object lock = new Object();
	
	private volatile AudioFormat format;

	private static volatile ArrayList<AnalyzerSourceDevice> devices = new ArrayList<>();
	private volatile AnalyzerSourceDevice activeDevice;
	
	private volatile Normalizer normalizer;

	private volatile RenderComponent renderer;
	private volatile GraphRender gr;

	private HashMap<Integer, Color> graphSettingsColor = new HashMap<Integer, Color>();
	private Color mergedGraphSettingsColor;

	private HashMap<Integer, Integer> graphSettingsYOffset = new HashMap<Integer, Integer>();
	private int mergedGraphSettingsYOffset;

	private volatile ArrayList<AudioGraph> channelGraphs = new ArrayList<AudioGraph>();

	public static final int initCapacity = 10000;
	private volatile int buffermax = 25;
	private volatile ArrayBlockingQueue<PCMData> toAnalyze = new ArrayBlockingQueue<PCMData>(initCapacity);
	
	private volatile boolean active;
	private volatile boolean initNormalizerActive;

	private boolean mergedChannels;
	private int detailLevel = 10;

	//private volatile float[] channelsValueSum;
	private volatile GapList<Float>[] channelsValueWindow;
	private volatile int[] chennalsDetailIndex;

	private int sleepTime = 16; // (50 FPS) in ms ; must be max 25 ms, otherwise
								// the
								// Graph on the GUI will start lagging

	private long duractionTime; // in ns
	private long speed; // in AudioByteBlock/s

	private boolean DEBUG = false;
	private boolean defaultGraphsSet;

	public Analyzer(RenderComponent renderComponent) {
		super();

		// define class defaults ...
		for (int i = 0; i < 6; i++) {
			resetDefaultChannelGraphColor(i);
		}

		resetDefaultMergedChannelGraphColor();

		this.renderer = renderComponent;
		this.gr = renderComponent.getRenderer();

		//start analyzer
		start();
		
		init();
	}
	
	public void start() {
		this.active = true;
		initAnalyzerThread();
	}

	public void stop() {
		getGraph().stop();
		this.active = false;		
	}
	
	public void stopWait() {
		getGraph().stopWait();
		this.active = false;
		try {
			synchronized (lock) {
				lock.wait(3000);
			}
		} catch (InterruptedException e) { }
	}
	
	public boolean isActive() {
		return active;
	}

	public boolean isAlive() {
		return analyzerThread.isAlive();
	}
	
	public void switchRenderer(Class<? extends GraphRender> rendererClass) {
		stopWait();
			
		renderer.switchRenderer(rendererClass);
		this.gr = renderer.getRenderer();
		
		init();
		start();
	}
	
	public void setDefaultGraphs(int count) {
		gr.clearGraphs();
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
			gr.addGraph(ag);
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
		mergedGraphSettingsColor = Application.getColors().color_graph_defaultMergedGraphColor;
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
	
	public static AnalyzerSourceDevice[] geSourceDevices(){
		AnalyzerSourceDevice[] aT = new AnalyzerSourceDevice[devices.size()];
		return devices.toArray(aT);
	}
	
	public int getActiveDeviceIndex(){
		return devices.indexOf(getActiveDevice());
	}
	
	public AnalyzerSourceDevice getActiveDevice(){
		return activeDevice;
	}
	
	public boolean setActiveDevice(int index){
		try {
			return setActiveDevice(devices.get(index));
		} catch (Exception e) {
			if (devices.size() > 0)
				setActiveDevice(devices.get(0));
			else
				activeDevice = null;
		}
		return false;
	}
	public boolean setActiveDevice(AnalyzerSourceDevice device){
		this.activeDevice = device;
		System.out.println("Analyzer: Active Device changed to: [" + device.getDisplayName() + "]");
		return true;
	}

	public long getDuractionTime() {
		return duractionTime;
	}

	public long getSpeed() {
		return speed;
	}

	public GraphRender getGraph() {
		return gr;
	}

	public void setGraph(GraphRender gr) {
		this.gr = gr;
	}

	public RenderComponent getRenderer() {
		return renderer;
	}
	
	public ArrayList<AudioGraph> getChannelGraphs() {
		synchronized (channelGraphs) {
			return channelGraphs;
		}
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public int getBufferMax() {
		return buffermax;
	}
	
	public int getBufferSize(){
		return initCapacity - toAnalyze.remainingCapacity();
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
			gr.clearGraphs();
			channelGraphs.clear();
			normalizer = null;
			defaultGraphsSet = false;
		}
	}

	public void registerDevice(AnalyzerSourceDevice device){
		if (devices.size() <= 0){
			setActiveDevice(device);
		}
		
		if (!devices.contains(device))
			devices.add(device);
	}

	public void unregisterDevice(AnalyzerSourceDevice device){
		devices.remove(device);
		
		if (devices.size() <= 0){
			activeDevice = null;
		}
	}
	
	public void init() {
		clearGraphs();
		setDefaultGraphs(2);
		
		if (getActiveDevice() == null) return;
		this.format = getActiveDevice().getAudioFormat();
		initNormalizer();
	}

	public void initNormalizer() {
		if (!initNormalizerActive) {
			initNormalizerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					normalizer = null;
					initNormalizerActive = true;
					while (normalizer == null && initNormalizerActive) {

						try {
							Thread.sleep(20);
						} catch (InterruptedException e1) {
						}

						try {
							normalizer = new Normalizer(format);
							//channelsValueSum = new float[format.getChannels()];
							//channelsValueWindow = 
							channelsValueWindow = (GapList<Float>[])new GapList[format.getChannels()];
							for (int i = 0; i < channelsValueWindow.length; i++) {
								channelsValueWindow[i] = new GapList<>();
							}
							chennalsDetailIndex = new int[format.getChannels()];

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

	public void analyze(AnalyzerSourceDevice source, byte[] b, int off, int len) {
		if (b == null || format == null) return;
		analyze(source, new PCMData(format, b.clone(), off, len));
	}

	public void analyze(AnalyzerSourceDevice source, PCMData raw) {
		if (raw == null || format == null) return;
		if (!format.equals(raw.getFormat())) format = raw.getFormat();
		
		try {
			if (getBufferSize() < getBufferMax()){
				if (getActiveDevice() == null) throw new RuntimeException("No devices are known. Register any devices befor calling this method");
				if (getActiveDevice().equals(source)){
					toAnalyze.put(raw);
				}
			}else{
				if(DEBUG) System.err.println("WARNING: AnalyzerThread: Can't keep up!");
			}
		} catch (InterruptedException e) {
		}
	}

	public void clearData() {
		toAnalyze.clear();
		for (AudioGraph g : gr.getGraphs()) {
			g.clear();
		}
	}

	private void initMergedChannelGraph() {
		synchronized (channelGraphs) {
			gr.clearGraphs();
			AudioGraph ag = new AudioGraph(mergedGraphSettingsColor, mergedGraphSettingsYOffset);
			channelGraphs.add(ag);
			gr.addGraph(ag);
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
			if (defaultGraphsSet){
				gr.clearGraphs();
				defaultGraphsSet = false;
			}
			
			int channels = format.getChannels();
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
				gr.addGraph(ag);
			}
		}
	}

	public void initAnalyzerThread() {
		if (analyzerThread.isAlive()) return;

		analyzerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				active = true;
				long fclast = System.currentTimeMillis();
				int fcIndex = 0;
				int fCount = 0;
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

						PCMData pcmdata = toAnalyze.take();
						byte[] byteBlock = pcmdata.getBytes();// .clone();

						int offset = pcmdata.getOffset();
						int length = pcmdata.getLength();
						
						int datalength = pcmdata.getDataLenght();

						//float[][] channelData = normalizer.normalize(byteBlock, offset, length);

						float[][] channelData = new float[ pcmdata.getFormat().getChannels() ][];

						
						for (int ci = 0; ci < pcmdata.getFormat().getChannels(); ci++) {
							channelData[ci] = new float[ datalength ];
							
							for (int di = 0; di < datalength; di++) {
								channelData[ci][di] = pcmdata.getData(di, ci);
							}
						}
						
//						try {
//							abb.getData(0, 0);
//							System.out.println(
//									"                                                                 => " + 
//									PCMData.getBinary(-91L));
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						
						
						
						/* 
						 * byteBlock:
						 * 
						 * 16 bit == 2 byte per channel
						 * 
						 * byteAbfolge:
						 * [channel#1][channel#2][channel#x] [channel#1][channel#2][channel#x] ...
						 * 
						 * 
						 *  
						 */
						
						
						
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

									GapList<Float> channelsValueWindowJ = channelsValueWindow[j];
									
									//channelsValueSum[j] += sample;
									channelsValueWindowJ.add(sample);
									//chennalsDetailIndex[j]++;
									
									while (channelsValueWindowJ.size() > detailLevel) {
										channelsValueWindowJ.remove();
									}
									

									float valueSum = 0; //channelsValueSum[j];
									//int detailIndex = chennalsDetailIndex[j];
									
									for (int k = 0; k < channelsValueWindowJ.size(); k++) {
										valueSum += channelsValueWindowJ.get(k);
									}
										
									float averageValue = (valueSum / (float) detailLevel);

									// send value to the graph
									graph.addValue(averageValue);
									
									if (DEBUG) {
										fcIndex++;
										if((System.currentTimeMillis() - fclast) > 1000) {
											fclast = System.currentTimeMillis();
											fCount = fcIndex;
											fcIndex = 0;
											System.out.println(String.format("Calls/s: %s | Avg Val: %s | Window Size: %s | Buffer: %s/%s", fCount, averageValue, channelsValueWindowJ.size(), bx, getBufferSize()));
										}
									}
																		
									//chennalsDetailIndex[j] = 0;
									//channelsValueSum[j] = 0;
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
						System.out.printf("Duraction: %9s ns  Buffer: %4s /%4s  Speed: %4s AudioByteBlocks/s\n",
										duractionTime,
										getBufferSize(), 
										buffermax,
										speed);
				}
				System.err.println("WARNING: The thread \"AnalyzerThread\" has stopped!");
				
				// notify for stopWait();
				synchronized (lock) {
					lock.notify();
				}
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

}
