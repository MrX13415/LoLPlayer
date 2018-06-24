package net.icelane.amplifire.analyzer.render.opengl;

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

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.AudioGraph;
import net.icelane.amplifire.analyzer.render.GraphRender;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 1.0
 * 
 * A graph render using OpenGL (Version 1.1)
 */
public class GL11Graph extends GraphRender{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
	
	private ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();

	private VolatileImage backBuffer;

	private Stroke graphStroke = new BasicStroke(1f);
	private Stroke effetcStroke1 = new BasicStroke(15f);
//	private Stroke effetcStroke2 = new BasicStroke(10f);
//	private Stroke effetcStroke3 = new BasicStroke(6f);
	
	private volatile float fps = 0;
	private long fpsUpdateT = System.currentTimeMillis();
	
//	private float getHeightLevel() = 0.4f;
//	private int getZoomlLevel() = 1;

	MovingAverage avg = new MovingAverage(3);

	
	 // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
 
	
	public GL11Graph() {
		super();
			
		start();
	}

	
	@Override
	public void startup() {
		System.out.println("LWJGL " + Version.getVersion());
		System.out.println("OpenGL 1.1");
		
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
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow(window);
	}
	
	int h = 10;
	int w = 10;
	


	@Override
	public void renderloop() {
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
		while ( !glfwWindowShouldClose(window) && isActive()) {
			
			long fps_time_start = System.nanoTime();
			
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
            
            long fps_delta = System.nanoTime() - fps_time_start;
            avg.put(fps_delta);
            fps = Math.round(1000000000f / avg.getAverage());
        }
	}


	@Override
	public void shutdown() { 
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}


	@Override
	public void cleanup() {
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
	}
	
	
	
		
	public int[] getGLSize(){
		IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, w, h);
        int width = w.get(0);
        int height = h.get(0);
        return new int[]{width, height};
	}
    
	private void gl_drawLine(Color c, double x1, double y1, double x2, double y2) {
		float[] cc = c.getRGBColorComponents(null);
		glColor3f(cc[0], cc[1], cc[2]);
	 
		glBegin(GL_LINES);    
	    glVertex3d(x1, y1, 0f);
        glVertex3d(x2, y2, 0f);
        glEnd();
	}
	
	private void gl_drawRectangle(Color c, double x1, double y1, double x2, double y2) {
		gl_drawLine(c, x1, y1, x2, y1);
		gl_drawLine(c, x1, y2, x2, y2);		
		gl_drawLine(c, x1, y1, x1, y2);
		gl_drawLine(c, x2, y1, x2, y2);
	}
	
	private void gl_drawPoint(Color c, double x1, double y1) {
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
			
	@Override
	public void paintComponent(Graphics graphics) {
    	super.paintComponent(graphics);
    	
    	Graphics2D g = (Graphics2D) graphics;
    
    	if (!isActive()) return;
    	if(!this.isShowing()) return;

    	// render startTime
		long renderStart = System.nanoTime();

		try {
			// render the image ...
			repaintGraphs();
		
			//*** FPS ********************************************
	        if (isShowFPS()){
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
		
		int heightCenter = (Math.round(height + getHeightLevel())) >> 1;
		int maxPointCount = width * getZoomlLevel();
		
		for (int i = 0; i < graphs.size(); i++) {
			AudioGraph graph = graphs.get(i);

			// determine display mode to set graph position ...
			switch (getDisplayMode()) {
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
			graph.syncBufferSize(width == 0 ? 500 : width * getZoomlLevel());
			
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
	        if (isShowFPS()){
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
		gl_drawLine(Color.darkGray,
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
					gl_drawLine(c, lastPoint_x, lastPoint_y, point_x, point_y);
				}
				
				if (getDrawMode() == DrawMode.DOTS){
					gl_drawPoint(c, point_x, point_y);
				}
				
				if (getDrawMode() == DrawMode.LINES){
					if (detailC == 0 && (i - getZoomlLevel() * 3) == lastI){
						lastI = i;
						gl_drawLine(c, point_x, point_y - 16/2, point_x, point_y + 16/2);
					}
				}
				
				if (getDrawMode() == DrawMode.DOUBLE_LINES){
					if (detailC == 0 && (i - getZoomlLevel() * 3) == lastI){
						lastI = i;
						if(point_y <= graphcenterY){
							gl_drawLine(c, point_x, point_y - 5, point_x, point_y - 16);
						}
						if(point_y >= graphcenterY){
							gl_drawLine(c, point_x, point_y + 5, point_x, point_y + 16);
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


}