package net.icelane.lolplayer.player.analyzer.render.opengl;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.swing.JPanel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import net.icelane.lolplayer.AppCore;
import net.icelane.lolplayer.Application;
import net.icelane.lolplayer.player.analyzer.AudioGraph;
import net.icelane.lolplayer.player.analyzer.Graph;
import net.icelane.lolplayer.player.analyzer.render.GraphRender;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 1.0
 * 
 * A graph render using OpenGL (Version 1.1)
 */
public class GL11Graph extends GraphRender implements Graph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
	
	private Thread graphRepaintThread = new Thread();
    
	private ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();

	private boolean enabledDrawing = false;
	
	private VolatileImage backgroundBuffer;
	private VolatileImage graphBuffer;
	
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

	private volatile DisplayMode displayMode = DisplayMode.NORMAL;
	private volatile DrawMode drawMode = DrawMode.STRAIGHT;
	private volatile float fps = 0;
	private long fpsUpdateT = System.currentTimeMillis();
	
	private float heightLevel = 0.4f;
	private int zoomlLevel = 1;
	
	 // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
 
	
	public GL11Graph() {
		super();
			
		start();
	}
	
	// OpenGL
	public void start() {
		initGraphRepaintThreadGL();
			
		//start DrawingThread
        setEnabledDrawing(true);
        
        this.setOpaque(false);
    }

	private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
 
        int WIDTH = 600;
        int HEIGHT = 500;
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, Application.App_Name_Version, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
        });
 
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - WIDTH) / 2,
            (vidmode.height() - HEIGHT) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(0);
 
        // Make the window visible
        glfwShowWindow(window);
    }
		
	MovingAverage avg = new MovingAverage(3);

	public int[] getGLSize(){
		IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, w, h);
        int width = w.get(0);
        int height = h.get(0);
        return new int[]{width, height};
	}
	
	int h = 10;
	int w = 10;
	
    private void loop() {
    	// This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
 
        float[] cc = Application.getColors().SETTING_color_aboutpage_background1.getRGBColorComponents(null);
        // Set the clear color
        glClearColor(cc[0], cc[1], cc[2], 0.0f);
        
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			
			long fps_time_start = System.currentTimeMillis();
			
			Dimension size = GetSize();
			int height = size.height;
			int width = size.width;
  	
			if ( w != width || h != height ){
				glViewport(0, 0, width, height);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				glOrtho(0, width, height, 0, 1, -1);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
			}
			
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

    		renderBackBufferGL(); // Rendering ...

    		glfwSwapBuffers(window); // show on display ...
 
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            
            long fps_delta = System.currentTimeMillis() - fps_time_start;
            avg.put(fps_delta);
            fps = Math.round(1000.0 / avg.getAverage());
        }
    }
    
    public class MovingAverage {

        private final LinkedList<Double> window = new LinkedList<Double>();
        private final int period;

        public MovingAverage(int period) {
            assert period > 0 : "Period must be a positive integer";
            this.period = period;
            
            for(int index = 0; index < period; index++){
        		window.add(0.0);
        	}
        }

        public void put(double num) {
            window.add(num);
            if (window.size() > period) {
                window.remove(); //);
            }
        }

        public double getAverage() {
        	if (window.isEmpty()) return 0.0;
        	
        	double sum = 0.0;
        	for(int index = 0; index < period; index++){
        		sum += (1.0 / period) * window.get(index);
        	}
        	
            return sum;
        }
    }

   
    /*

     * */
    
	private void drawLine(Color c, double x1, double y1, double x2, double y2) {
		float[] cc = c.getRGBColorComponents(null);
		glColor3f(cc[0], cc[1], cc[2]);
	 
		glBegin(GL_LINES);    
	    glVertex3d(x1, y1, 0f);
        glVertex3d(x2, y2, 0f);
        glEnd();
	}
	
	private void drawRectangle(Color c, double x1, double y1, double x2, double y2) {
		drawLine(c, x1, y1, x2, y1);
		drawLine(c, x1, y2, x2, y2);		
		drawLine(c, x1, y1, x1, y2);
		drawLine(c, x2, y1, x2, y2);
	}
	
	private void drawPoint(Color c, double x1, double y1) {
		float[] cc = c.getRGBColorComponents(null);
		glColor3d(cc[0], cc[1], cc[2]);
		glBegin(GL_POINTS);
	    glVertex3d(x1, y1, 0f);
	    glEnd();
	}
	
	private Dimension GetSize(){	
		IntBuffer glww = BufferUtils.createIntBuffer(1);
		IntBuffer glwh = BufferUtils.createIntBuffer(1);
		glfwGetFramebufferSize(window, glww, glwh);
		return new Dimension(glww.get(0), glwh.get(0));
	}
		
    private synchronized void initGraphRepaintThreadGL(){
		graphRepaintThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(true){
					System.out.println("LWJGL " + Version.getVersion());
					System.out.println("OpenGL 1.1");
					
					try {
				        init();
				        loop();
	
				        // Free the window callbacks and destroy the window
				        glfwFreeCallbacks(window);
				        glfwDestroyWindow(window);
					}
				    catch (Exception e) {
				    	System.err.println("Error in Thread \"UI-JGraph-Repainter\"!");
				    	e.printStackTrace();
				    } finally {
				        // Terminate GLFW and free the error callback
				        glfwTerminate();
				        glfwSetErrorCallback(null).free();
				    }
					
					System.out.println("Thread has \"UI-JGraph-Repainter\" stopped");
					System.out.println("Restarting Thread: \"UI-JGraph-Repainter\" ...");
				}
			}
		});
		graphRepaintThread.setName("UI-JGraph-Repainter");
		graphRepaintThread.start();
	}
    
	public void renderBackBufferGL(){
		Graphics2D g = (Graphics2D) backBuffer.getGraphics();
//
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//*** Mathematics ************************************
		Dimension size = GetSize();
		int height = size.height;
		int width = size.width;

		//drawRectangle(Color.CYAN, 0, 0, width, height);
		
		int heightCenter = (Math.round(height + heightLevel)) >> 1;
		int maxPointCount = width * zoomlLevel;
		
		for (int i = 0; i < graphs.size(); i++) {
			AudioGraph graph = graphs.get(i);

			// determine display mode to set graph position ...
			switch (displayMode) {
			case CENTERED:
				graph.setYOffset(0);
				break;
				
			case NORMAL:
				
			default: //NORMAL
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
				
				break;
			}
			
			//sync graph buffer size ... 
			graph.syncBufferSize(width == 0 ? 500 : width * zoomlLevel);
			
			//*** Mathematics ************************************
			int graphcenterY = heightCenter + graph.getYOffset();

			int minIndex = graph.size() - (maxPointCount); 
				minIndex = (minIndex < 0 ? 0 : minIndex);

			//*** Background *************************************
			renderBackgroundGL(graph, g, graphcenterY);
			//TODO: only once ..
			
			//*** Glow effect ************************************
			if (isGlowEffect()) renderGraphGL(graph, g, effetcStroke1, 8, maxPointCount, heightCenter, graphcenterY, minIndex);

			//*** Graph ******************************************
			renderGraphGL(graph, g, graphStroke, 255, maxPointCount, heightCenter, graphcenterY, minIndex);

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

	private void renderBackgroundGL(final AudioGraph graph, Graphics2D g, int graphcenterY){
		//metric of the label text
//		LineMetrics metrics = g.getFontMetrics().getLineMetrics(graph.getName(), g);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(graph.getName(), g);

		//draw center line
//		g.setColor();
//		g.setStroke(new BasicStroke(1f));
		drawLine(Color.darkGray,
				0,
				graphcenterY,
				(int) (GetSize().width - bounds.getWidth() - (graph.getName().length() > 0 ? 10 : 0)),
				graphcenterY);

//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		//draw label ...
		//drawString(graph.getName(), (int) (this.getWidth() - bounds.getWidth() - 5), (int) (graphcenterY + metrics.getAscent() / 2));
		
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private void renderGraphGL(final AudioGraph graph, Graphics2D g, Stroke stroke, int alpha, int maxPointCount, int heightCenter, int graphcenterY, int minIndex){	
		if(graph.size() < 1) return;

		Color c = graph.getColor();
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);	
		
//		g.setColor(c);
//		g.setStroke(stroke);
	
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
					drawLine(c, lastPoint_x, lastPoint_y, point_x, point_y);
				}
				
				if (drawMode == DrawMode.DOTS){
					drawPoint(c, point_x, point_y);
				}
				
				if (drawMode == DrawMode.LINES){
					if (detailC == 0 && (i - zoomlLevel * 3) == lastI){
						lastI = i;
						drawLine(c, point_x, point_y - 16/2, point_x, point_y + 16/2);
					}
				}
				
				if (drawMode == DrawMode.DOUBLE_LINES){
					if (detailC == 0 && (i - zoomlLevel * 3) == lastI){
						lastI = i;
						if(point_y <= graphcenterY){
							drawLine(c, point_x, point_y - 5, point_x, point_y - 16);
						}
						if(point_y >= graphcenterY){
							drawLine(c, point_x, point_y + 5, point_x, point_y + 16);
						}
					}
				}
			}
			
			//Update last point ...
			lastPoint_x = point_x;
			lastPoint_y = point_y;
		}
	}
    
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
			
			// show the back backBuffer on the screen ...
			if (backBuffer != null) g.drawImage(backBuffer, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Update the current FPS ... 
		if (System.currentTimeMillis() - fpsUpdateT > 250) {
			//fpsUpdateT = System.currentTimeMillis();
			//long tDelta = System.nanoTime() - renderStart;
			//fps = 1000000000f / (float) tDelta; // 1000000000 = 1s
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
				//renderBackBuffer();

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
			
			switch (displayMode) {
			case CENTERED:
				graph.setYOffset(height / 4);
				break;
				
			case NORMAL:
				
			default: //NORMAL
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
				
				break;
			}
			
			//sync graph buffer size ... 
			graph.syncBufferSize(this.getWidth() * zoomlLevel);
			
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
				
				while(enabledDrawing){
					try {
						Thread.sleep(10); //lock to 100 FPS
					} catch (InterruptedException e) {}
					
					repaint();
				}
				
			}
		});
		graphRepaintThread.setName("UI-JGraph-Repainter");
		graphRepaintThread.start();
	}

	public boolean isEnabledDrawing() {
		return enabledDrawing;
	}

	public void setEnabledDrawing(boolean enabledDrawing) {
		if (this.enabledDrawing && enabledDrawing) return;
		 		
		this.enabledDrawing = enabledDrawing;
		
		//if (enabledDrawing) initGraphRepaintThreadGL();
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

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}
}