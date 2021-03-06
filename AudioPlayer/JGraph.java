package audioplayer.player.analyzer.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

import javax.swing.JPanel;

import audioplayer.player.analyzer.AudioGraph;
import audioplayer.player.analyzer.Graph;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 2.3
 * 
 * A simple panel to display Graphs
 */
public class JGraph extends JPanel implements Graph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
	
	public enum DrawMode{
		STRAIGHT{
			@Override
			public String toString() {
				return super.toString().substring(0,1).toUpperCase() + super.toString().substring(1).toLowerCase();
			}
		},
		DOTS{
			@Override
			public String toString() {
				return super.toString().substring(0,1).toUpperCase() + super.toString().substring(1).toLowerCase();
			}
		},
		LINES{
			@Override
			public String toString() {
				return super.toString().substring(0,1).toUpperCase() + super.toString().substring(1).toLowerCase();
			}
		},
		DOUBLE_LINES{
			@Override
			public String toString() {
				String t = super.toString().replace("_", " ");
				return t.substring(0,1).toUpperCase() + t.substring(1).toLowerCase();
			}
		};
	}
	
	private Thread graphRepaintThread = new Thread();
    
	private ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();

	private boolean enabledDrawing = false;
	
	private VolatileImage backBuffer;
	private VolatileImage screenBuffer;

	private GraphicsDevice currentDevice = getDefaultGraphicsDevice();
	
	private Stroke graphStroke = new BasicStroke(1f);
	private Stroke effetcStroke1 = new BasicStroke(15f);
//	private Stroke effetcStroke2 = new BasicStroke(10f);
//	private Stroke effetcStroke3 = new BasicStroke(6f);
	
	private boolean showFPS = false;
	private boolean blurFilter = true;
	private boolean glowEffect = true;

	private volatile DrawMode drawMode = DrawMode.STRAIGHT;
	private volatile float fps = 0;
	
	private float heightLevel = 0.4f;
	private int zoomlLevel = 1;


	public JGraph() {
		super();
		
		//start DrawingThread
        setEnabledDrawing(true);
        
        this.setOpaque(false);
	}
	
	long fpsUpdateT = System.currentTimeMillis();
	
	//TODO: implement FPS lock
    public void paintComponent(Graphics graphics) {
    	super.paintComponent(graphics);
    	
    	Graphics2D g = (Graphics2D) graphics;
    	
    	if (!enabledDrawing) return;
    	if(!this.isShowing()) return;

    	// render startTime
		long renderStart = System.nanoTime();

		try {
			// render the image ...
			repaintGraphs();
		
			// show the back backBuffer on the screen ...
			if (backBuffer != null) g.drawImage(backBuffer, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Update the current FPS ... 
		if (System.currentTimeMillis() - fpsUpdateT > 250) {
			fpsUpdateT = System.currentTimeMillis();
			long tDelta = System.nanoTime() - renderStart;
			fps = 1000000000f / (float) tDelta; // 1000000000 = 1s
		}
    }
            
	@Override
	public synchronized void addGraph(AudioGraph graph){
		graphs.add(graph);
	}
	
	@Override
	public synchronized void removeGraph(AudioGraph graph){
		graphs.remove(graph);
	}
	
	@Override
	public void clearGraphs() {
		graphs.clear();
		
	}
	
	@Override
	public synchronized AudioGraph getGraph(int index){
		return graphs.get(index);
	}
	
	@Override
	public synchronized ArrayList<AudioGraph> getGraphs() {
		return graphs;
	}
	
	/**
	 * Obtain the default system graphics device
	 * 	 * @return  
	 */
	public GraphicsDevice getDefaultGraphicsDevice(){
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}
	
	/**
	 * Obtain the current system graphical settings
	 * @return Current graphics configuration 
	 */
	public GraphicsDevice getCurrentGraphicsDevice(){
		for (GraphicsDevice graphicsDevice : GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices()){
			
			if (isOnGraphicsDevice(graphicsDevice)) return graphicsDevice;
		}
		return getDefaultGraphicsDevice();
	}
	
	public boolean hasGraphicsDeviceChanged(){
		GraphicsDevice device = getCurrentGraphicsDevice();
		if (currentDevice.equals(device)) return false;	
		currentDevice = device;
		System.out.println("Graphics device changed: " + device.getIDstring());
		return true;
	}
	
	public boolean isOnGraphicsDevice(GraphicsDevice gd){
		if(!this.isShowing()) return false;
		Rectangle r = gd.getDefaultConfiguration().getBounds();
		Point p = this.getLocationOnScreen();
		return r.contains(p);
	}
	
	private VolatileImage createBackBuffer(int width, int height, int transparency){
		// obtain the current system graphical settings
		GraphicsConfiguration gc = getGraphicsConfiguration();
		
		VolatileImage img = gc.createCompatibleVolatileImage(width, height, transparency);
		
		return clearImage(img);
	}
	
	private VolatileImage clearImage(VolatileImage img){
		Graphics2D g = img.createGraphics();
	    g.setComposite(AlphaComposite.DstOut);
	    g.fillRect(0, 0, img.getWidth(), img.getHeight());
	    g.dispose();
	    
	    return img;
	}

	private void repaintGraphs(){
		
		int height = this.getHeight();
		int width = this.getWidth();
				
		if(width <= 0 || height <= 0) return;
		
		try {
			do {
				// Back backBuffer doesn't exist ...
				if (backBuffer == null){
					backBuffer = createBackBuffer(width, height, Transparency.TRANSLUCENT);
				
				// The graphics device has been changed ...
				}else if (hasGraphicsDeviceChanged()){
					backBuffer = createBackBuffer(width, height, Transparency.TRANSLUCENT);
				
				// Back backBuffer doesn't work with new GraphicsConfig ...
				}else if (backBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE){
					backBuffer = createBackBuffer(width, height, Transparency.TRANSLUCENT);
				
				// Back backBuffer size doesn't match anymore ...
				}else if (width != backBuffer.getWidth() || height != backBuffer.getHeight()){
					backBuffer = createBackBuffer(width, height, Transparency.TRANSLUCENT);
				
				// Clear back backBuffer ...
				}else{
					clearImage(backBuffer);
				}

				// Rendering ...
				renderBackBuffer();

			} while (backBuffer.contentsLost());
			
		} catch (Exception e) {
			e.printStackTrace();
		}	

//			if (isGlowEffect()) {				
//				effectsImage = getLinearBlurOp(2, 2, .2f).filter(effectsImage, null);
//			}
//			
//			if(isBlurFilter()){
//				graphImage = getLinearBlurOp(2, 2, .6f).filter(graphImage, null);
//			}			
		
		//show rendered image
//		if (screenBuffer == null) //clearImage(screenBuffer);
//			screenBuffer = backBuffer;
		
//		repaint();
	}
	
	public ConvolveOp getLinearBlurOp(int width, int hight) {
	    float value = 1.0f / (float) (width * hight);
        return getLinearBlurOp(width, hight, value);
    }
	 
	public ConvolveOp getLinearBlurOp(int width, int hight, float value) {
        float[] data = new float[width * hight];
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new ConvolveOp(new Kernel(width, hight, data), ConvolveOp.EDGE_ZERO_FILL, null);
    }
	
	public Kernel getBlurKernel(int width, int hight, float value) {
        float[] data = new float[width * hight];
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new Kernel(width, hight, data);
    }
	
	public void renderBackBuffer(){
		Graphics2D g = (Graphics2D) backBuffer.getGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//*** Mathematics ************************************
		int height = this.getHeight();
		int width = this.getWidth();
		
		int heightCenter = (Math.round(height + heightLevel)) >> 1;
		int maxPointCount = width * zoomlLevel;
		
		for (int i = 0; i < graphs.size(); i++) {
			AudioGraph graph = graphs.get(i);
			
			graph.setShownValues(this.getWidth() * zoomlLevel);
			
			// set graph positions ...
			if (graphs.size() > 1){
				int index = graphs.indexOf(graph);
				if (index == 0){
					graph.setYOffset( height / 4 );
				}
				if (index == 1){
					graph.setYOffset( (height / 4) * -1 );
				}
			}
			
			//*** Mathematics ************************************
			int graphcenterY = heightCenter + graph.getYOffset();

			int minIndex = graph.getValues().size() - (maxPointCount); 
				minIndex = (minIndex < 0 ? 0 : minIndex);

			//*** Background *************************************
			renderBackground(graph, g, graphcenterY);

			//*** Glow effect ************************************
			renderGraph(graph, g, effetcStroke1, 8, maxPointCount, heightCenter, graphcenterY, minIndex);

			//*** Graph ******************************************
			renderGraph(graph, g, graphStroke, 255, maxPointCount, heightCenter, graphcenterY, minIndex);

			//*** FPS ********************************************
	        if (showFPS){
	        	//set rendering hints ...
	        	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	        	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
 	
				//draw label ...
				g.setColor(Color.darkGray);
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
				g.drawString(String.format("%s", (Math.round(fps))), 10, 20);
	        }
		}
	}
	
	private void renderBackground(final AudioGraph graph, Graphics2D g, int graphcenterY){
		//metric of the label text
		LineMetrics metrics = g.getFontMetrics().getLineMetrics(graph.getName(), g);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(graph.getName(), g);

		//draw center line
		g.setColor(Color.darkGray);
		g.setStroke(new BasicStroke(1f));
		g.drawLine(0, graphcenterY, (int) (this.getWidth() - bounds.getWidth() - (graph.getName().length() > 0 ? 10 : 0)), graphcenterY);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		//draw label ...
		g.drawString(graph.getName(), (int) (this.getWidth() - bounds.getWidth() - 5), (int) (graphcenterY + metrics.getAscent() / 2));
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private void renderGraph(final AudioGraph graph, Graphics2D g, Stroke stroke, int alpha, int maxPointCount, int heightCenter, int graphcenterY, int minIndex){	
		if(graph.getValues().size() < 1) return;

		Color c = graph.getColor();
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);	
		
		g.setColor(c);
		g.setStroke(stroke);
	
		//define first point ...
		int lastPoint_x = -1;
		int lastPoint_y = -1;

		//current point 
		int point_x = 0;
		int point_y = 0;

		int detailC = 0;
		
		int lastI = minIndex;
		
		for (int i = minIndex; i < graph.getValues().size(); i++) {				

			//calculate the y coordinates of the next point
			point_y = Math.round((graph.getValue(i) * (float)(heightCenter * heightLevel)) + heightCenter);
						
			//add Y-Offset ...
			point_y += graph.getYOffset();
			
			//calculate x coordinate;
			detailC++;
			
			if (detailC >= zoomlLevel) {
				point_x++;
				detailC = 0;
			}
			
			if (lastPoint_y >= 0){
				//draw a line from the last point to the current one ...
				
				if (drawMode == DrawMode.STRAIGHT){
					g.drawLine(lastPoint_x, lastPoint_y, point_x, point_y);
				}
				
				if (drawMode == DrawMode.DOTS){
					g.drawLine(point_x, point_y, point_x, point_y);
				}
				
				if (drawMode == DrawMode.LINES){
					if (detailC == 0 && (i - zoomlLevel * 3) == lastI){
						lastI = i;
						g.drawLine(point_x, point_y - 16/2, point_x, point_y + 16/2);
					}
				}
				
				if (drawMode == DrawMode.DOUBLE_LINES){
					if (detailC == 0 && (i - zoomlLevel * 3) == lastI){
						lastI = i;
						if(point_y <= graphcenterY){
							g.drawLine(point_x, point_y - 5, point_x, point_y - 16);
						}
						if(point_y >= graphcenterY){
							g.drawLine(point_x, point_y + 5, point_x, point_y + 16);
						}
					}
				}
			}
			
			//Update last point ...
			lastPoint_x = point_x;
			lastPoint_y = point_y;
		}
	}

	private synchronized void initGraphRepaintThread(){
		graphRepaintThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
//				long fpsUpdateT = System.currentTimeMillis();
//				
//				while (enabledDrawing) {					
//					long tStart = System.nanoTime();
//
//					try{
//						repaintGraphs();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//					
////					try {
////						long sleepT = 1 - ((System.nanoTime() - tStart) / 1000000);
////						sleepT = sleepT > 0 ? sleepT : 15;
////						Thread.sleep(sleepT); //max 50 FPS
////					} catch (Exception e) {}	
//					
//					if(System.currentTimeMillis() - fpsUpdateT > 300) {
//						fpsUpdateT = System.currentTimeMillis();
//						long tDelta = System.nanoTime()- tStart;
//						fps = 1000000000f / (float)tDelta; //1000000000 = 1s
//						System.out.println(tDelta);
//					}
//				}
			}
		});
		graphRepaintThread.setName("GraphRepaintThread");
		graphRepaintThread.start();
	}

	public boolean isEnabledDrawing() {
		return enabledDrawing;
	}

	public void setEnabledDrawing(boolean enabledDrawing) {
		if (this.enabledDrawing && enabledDrawing) return;
		 		
		this.enabledDrawing = enabledDrawing;
		
		if (enabledDrawing) initGraphRepaintThread();
	}

	public boolean isShowFPS() {
		return showFPS;
	}

	public void setShowFPS(boolean showFPS) {
		this.showFPS = showFPS;
	}

	public float getHeightLevel() {
		return heightLevel;
	}

	public void setHeightLevel(float heightLevel) {
		this.heightLevel = heightLevel;
	}

	public int getZoomlLevel() {
		return zoomlLevel;
	}

	public void setZoomlLevel(int zoomlLevel) {
		this.zoomlLevel = zoomlLevel;
	}

	public boolean isBlurFilter() {
		return blurFilter;
	}

	public void setBlurFilter(boolean blurfilter) {
		this.blurFilter = blurfilter;
	}
	
	public boolean isGlowEffect() {
		return glowEffect;
	}

	public void setGlowEffect(boolean glowEffect) {
		this.glowEffect = glowEffect;
	}

	public DrawMode getDrawMode() {
		return drawMode;
	}

	public void setDrawMode(DrawMode drawMode) {
		this.drawMode = drawMode;
	}
}