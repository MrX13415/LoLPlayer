package net.icelane.lolplayer.player.analyzer.render.opengl;

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
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class GLWRender implements GLRender {

	private int fps_avgWindow = 100;
	private MovingAverage fps_avg = new MovingAverage(fps_avgWindow);
	private double fps_tick;
	private volatile double fps;
	
	private long window;
	private String title = "OpenGL Render";
	private Dimension size = new Dimension(1000, 480); //pixel
	
    private int settings_swapInterval = 0;
    
    private Thread renderThread;
    
    
    public GLWRender() {
    	
	}
    
    public void start(){
    	System.out.println("GLWRender: LWJGL " + Version.getVersion());
		
    	try {
	        init();
	        loop();

	        // Free the window callbacks and destroy the window
	        glfwFreeCallbacks(window);
	        glfwDestroyWindow(window);
	    } finally {
	        // Terminate GLFW and free the error callback
	        glfwTerminate();
	        glfwSetErrorCallback(null).free();
	    } 		    
    }
    
    public void startThread()
    {
    	if (renderThread != null) return;

    	renderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				start();
				renderThread = null;
			}
		});
    	renderThread.setName("OpenGL-GLWRender");
		renderThread.start();
    }
    
    private void tick(){
    	this.fps_tick = GLFW.glfwGetTime();
    }
    
    private double tock(){
    	return GLFW.glfwGetTime() - this.fps_tick;
    }
    
    private void init(){
		// Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
 
        // Create the window
        window = glfwCreateWindow(this.size.width, this.size.height, this.title, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");
 
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
            (vidmode.width() - this.size.width) / 2,
            (vidmode.height() - this.size.height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        
        // Enable v-sync
        glfwSwapInterval(settings_swapInterval);
 
        // Make the window visible
        glfwShowWindow(window);
	}
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
 
        startup();
        
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			tick();
			
			render(GLFW.glfwGetTime());

    		glfwSwapBuffers(window);
 
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            
            fps_avg.put(tock());
            fps = 1.0 / fps_avg.getAverage();
        }
		
		shutdown();
		
    }
    
	public void startup(){
		
	}

	public void render(double time){
		
	}
	
	public void shutdown(){
		
	}
	
	public double GetFPS(){
		return this.fps;
	}
	
	public String GetTitle(){
		return this.title;
	}
	
	public void SetTitle(String title){
		this.title = title;
	}
	
	public Thread GetRenderThread(){
		return this.renderThread;
	}

	public boolean IsVSyncEnabled(){
		return this.settings_swapInterval == 1;
	}
	
	public void SetVSyncEnabled(boolean enabled){
		this.settings_swapInterval = enabled ? 1 : 0;
	}
	
	public long GetWindowHandle(){
		return this.window;
	}
}
