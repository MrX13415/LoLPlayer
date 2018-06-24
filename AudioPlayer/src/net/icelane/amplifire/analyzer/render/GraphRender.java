package net.icelane.amplifire.analyzer.render;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.lwjgl.Version;

import net.icelane.amplifire.analyzer.AudioGraph;
import net.icelane.amplifire.analyzer.Graph;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 1.0
 * 
 * A graph render using OpenGL (Version 1.1)
 */
public abstract class GraphRender extends JPanel implements Graph{

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
	
	public enum DisplayMode{
		NORMAL{
			@Override
			public String toString() {
				return super.toString().substring(0,1).toUpperCase() + super.toString().substring(1).toLowerCase();
			}
		},
		CENTERED{
			@Override
			public String toString() {
				return super.toString().substring(0,1).toUpperCase() + super.toString().substring(1).toLowerCase();
			}
		};
	}
	
	private Thread renderThread = new Thread();
    private boolean active = false;
    
	private ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();

	private GraphicsDevice currentDevice = getDefaultGraphicsDevice();

	private boolean showFPS = false;
	private boolean blurFilter = true;
	private boolean glowEffect = true;

	private volatile DisplayMode displayMode = DisplayMode.NORMAL;
	private volatile DrawMode drawMode = DrawMode.STRAIGHT;

	private float heightLevel = 0.4f;
	private int zoomlLevel = 1;
	
	
	public GraphRender() {
		super();
		
		// Transparent background by default 
        this.setOpaque(false);
	}
	
	public final synchronized void initRenderThread(){
    	if (renderThread.isAlive()) return;
    	
		renderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				active = true;
				
				while(active){
					boolean error = false;
					System.err.println("--------------------------------------- " + Thread.currentThread().getId());
					try {
						renderer();
					}
				    catch (Exception e) {
				    	error = true;
				    	
				    	System.err.printf("Error in Thread \"%s\"!\n", renderThread.getName());
				    	e.printStackTrace();
				    }

					cleanup();
					
					if (error) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {}
					}
					
					System.out.printf("Thread \"%s\" has stopped\n", renderThread.getName());
					System.out.printf("Restarting Thread: \"%s\" ...\n", renderThread.getName());
				}
			}
		});
		renderThread.setName("GraphRenderer");
		renderThread.start();
	}
    
    public final void renderer() {
    	startup();
    	
    	while (rendercondition()) {
    		renderloop();
		}
    	        
        shutdown();
    }

    public boolean rendercondition() {
    	return isActive();
    }

    public abstract void startup();
    
    public abstract void renderloop();
    
    public abstract void shutdown();
    
    public abstract void cleanup();
   	
	public final void dispose() {
		active = false;
	}
	
	public Thread getRenderThread() {
		return renderThread;
	}
		    
	public boolean isActive() {
		return active;
	}

	public boolean isAlive() {
		if (renderThread == null) return false;
		return renderThread.isAlive();
	}

	public final void setActiv(boolean active) {
		this.active = active;
		if (this.active) initRenderThread();
	}
	
	public final void start() {
		setActiv(true);
	}
	
	public final void stop() {
		setActiv(false);
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


	public final boolean isShowFPS() {
		return showFPS;
	}

	public final void setShowFPS(boolean showFPS) {
		this.showFPS = showFPS;
	}

	public final float getHeightLevel() {
		return heightLevel;
	}

	public final void setHeightLevel(float heightLevel) {
		this.heightLevel = heightLevel;
	}

	public final int getZoomlLevel() {
		return zoomlLevel;
	}

	public final void setZoomlLevel(int zoomlLevel) {
		this.zoomlLevel = zoomlLevel;
	}

	public final boolean isBlurFilter() {
		return blurFilter;
	}

	public final void setBlurFilter(boolean blurfilter) {
		this.blurFilter = blurfilter;
	}
	
	public final boolean isGlowEffect() {
		return glowEffect;
	}

	public final void setGlowEffect(boolean glowEffect) {
		this.glowEffect = glowEffect;
	}

	public final DrawMode getDrawMode() {
		return drawMode;
	}

	public final void setDrawMode(DrawMode drawMode) {
		this.drawMode = drawMode;
	}

	public final DisplayMode getDisplayMode() {
		return displayMode;
	}

	public final void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}
}