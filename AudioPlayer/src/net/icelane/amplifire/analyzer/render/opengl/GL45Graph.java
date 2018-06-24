package net.icelane.amplifire.analyzer.render.opengl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

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
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.sun.org.apache.bcel.internal.generic.ReturnaddressType;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.AudioGraph;
import net.icelane.amplifire.analyzer.Graph;
import net.icelane.amplifire.analyzer.render.GraphRender;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 2.4
 * 
 * A simple panel to display Graphs
 */
public class GL45Graph extends GLGraph implements Graph{

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
	
	 // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
 

    
    
    
    
    
    
    
    
    
	@Override
	public float getPointSize(){
		return getZoomlLevel() / 10f;
	}
	
	@Override
	public int getNum(){
		try {
			graphs.get(0).syncBufferSize(2000);
			graphs.get(1).syncBufferSize(2000);
			int num = graphs.get(0).size() + graphs.get(1).size();
			return num;	
		} catch (Exception e) {
			return 1;
		}
	}
	
	@Override
	public float[] getData(){
		try {
			int samples = 0;
			for (AudioGraph g : graphs) {
				samples += g.size();
			}
			
			int index = 0;
			float data[] = new float[samples * 6];
			float pre_x = 0 - 1f;
			float pre_y = 0;
			
			// Data: [L:<x><y><rgba>][R:<x><y><rgba>][<CHn>:<x><y><rgba>] 
			for (AudioGraph g : graphs) {
				int num = g.size();
				
				for(int i = 0; i < num; i++){
					
					if (index >= data.length) {
						System.out.println("[OpenGL] Warning: Data gathering aborted, due to settings change!");
						break;
					}
					
					//x (previous point)
//						data[index+0] = pre_x;
//						//y (previous point)
//						data[index+1] = pre_y;						
					//x
					data[index+0] = (2f/num) * i - 1f + (1f/num);
					//y
					data[index+1] = g.getValue(i) * getHeightLevel();
					//r
					data[index+2] = g.getColor().getRGBComponents(null)[0];
					//g
					data[index+3] = g.getColor().getRGBComponents(null)[1]; 
					//b
					data[index+4] = g.getColor().getRGBComponents(null)[2];
					//a
					data[index+5] = g.getColor().getRGBComponents(null)[3];
					
					pre_x = data[index+2];
					pre_y = data[index+3];
					index += 6;
				}
				break;
			}

			return data;
		} catch (Exception e) {
			 
			throw e;
			//return new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
		}
	}

	
	public GL45Graph() {
		super();
		
		start();
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
				g.drawString(String.format("%s", (Math.round(GetFPS()))), 10, 20);
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