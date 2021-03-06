package net.icelane.amplifire.analyzer.render.jgraph;

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
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;

import net.icelane.amplifire.analyzer.AudioGraph;
import net.icelane.amplifire.analyzer.render.GraphRender;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 2.4
 * 
 * A simple panel to display Graphs
 */
public class JGraph extends GraphRender {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
	
	private VolatileImage backBuffer;
	private VolatileImage screenBuffer;

	private GraphicsDevice currentDevice = getDefaultGraphicsDevice();
	
	private Stroke graphStroke = new BasicStroke(1f);
	private Stroke effetcStroke1 = new BasicStroke(15f);
//	private Stroke effetcStroke2 = new BasicStroke(10f);
//	private Stroke effetcStroke3 = new BasicStroke(6f);
	
//	private float heightLevel = 0.4f;
//	private int getZoomlLevel() = 1;
//	
	public JGraph() {
		super();
		

        start();
        

	}
	
	//TODO: implement FPS lock
    public void paintComponent(Graphics graphics) {
    	super.paintComponent(graphics);
    	
    	Graphics2D g = (Graphics2D) graphics;
    	
    	if (!isActive()) return;
    	if(!this.isShowing()) return;

		try {
			// show the back backBuffer on the screen ...
			if (screenBuffer != null) g.drawImage(screenBuffer, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		img.setAccelerationPriority(1f);
		
		return clearImage(img);
	}
	
	private VolatileImage clearImage(VolatileImage img){
		Graphics2D g = img.createGraphics();
	    g.setComposite(AlphaComposite.DstOut);
	    g.fillRect(0, 0, img.getWidth(), img.getHeight());
	    g.dispose();
	    
	    return img;
	}

	private void switchBuffers() {
		VolatileImage buffer = screenBuffer;
		screenBuffer = backBuffer;
		backBuffer = buffer;
	}
	
	private void render(){
		
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
		
		int heightCenter = (Math.round(height + getHeightLevel())) >> 1;
		int maxPointCount = width * getZoomlLevel();
		
		for (int i = 0; i < getGraphs().size(); i++) {
			AudioGraph graph = getGraphs().get(i);
			
			// set graph positions ...
			if (getGraphs().size() > 1){
				int index = getGraphs().indexOf(graph);
				if (index == 0){
					graph.setYOffset( height / 4 );
				}
				if (index == 1){
					graph.setYOffset( (height / 4) * -1 );
				}
			}
			
			//sync graph buffer size ... 
			graph.syncBufferSize(this.getWidth() * getZoomlLevel());
			
			//*** Mathematics ************************************
			int graphcenterY = heightCenter + graph.getYOffset();

			int minIndex = graph.size() - (maxPointCount); 
				minIndex = (minIndex < 0 ? 0 : minIndex);

			//*** Background *************************************
			renderBackground(graph, g, graphcenterY);
			//TODO: only once ..
			
			//*** Glow effect ************************************
			if (isGlowEffect()) renderGraph(graph, g, effetcStroke1, 8, maxPointCount, heightCenter, graphcenterY, minIndex);

			//*** Graph ******************************************
			renderGraph(graph, g, graphStroke, 255, maxPointCount, heightCenter, graphcenterY, minIndex);

			//*** FPS ********************************************
	        if (isShowFPS()){
	        	//set rendering hints ...
	        	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	        	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        	
				//draw label ...
				g.setColor(Color.darkGray);
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
				g.drawString(String.format("%s", (Math.round(getFPS()))), 10, 20);
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
		if(graph.size() < 1) return;

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
		
		int s = graph.size();

		for (int i = minIndex; i < s; i++) {				

			//calculate the y coordinates of the next point
			point_y = Math.round((graph.getValue(i) * (float)(heightCenter * getHeightLevel())) + heightCenter);
						
			//add Y-Offset ...
			point_y += graph.getYOffset();
			
			//calculate x coordinate;
			detailC++;
			
			if (detailC >= getZoomlLevel()) {
				point_x++;
				detailC = 0;
			}
			
			if (lastPoint_y >= 0){
				//draw a line from the last point to the current one ...
				
				if (getDrawMode() == DrawMode.STRAIGHT){
					g.drawLine(lastPoint_x, lastPoint_y, point_x, point_y);
				}
				
				if (getDrawMode() == DrawMode.DOTS){
					g.drawLine(point_x, point_y, point_x, point_y);
				}
				
				if (getDrawMode() == DrawMode.LINES){
					if (detailC == 0 && (i - getZoomlLevel() * 3) == lastI){
						lastI = i;
						g.drawLine(point_x, point_y - 16/2, point_x, point_y + 16/2);
					}
				}
				
				if (getDrawMode() == DrawMode.DOUBLE_LINES){
					if (detailC == 0 && (i - getZoomlLevel() * 3) == lastI){
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

	@Override
	public void startup() {

	}

	@Override
	public void renderloop() {
		render();
		switchBuffers();
		repaint();
		//TODO: 
	}

	@Override
	public void shutdown() {

	}

	@Override
	public void cleanup() {

	}

}