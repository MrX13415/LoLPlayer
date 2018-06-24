package net.icelane.amplifire.analyzer.render.opengl;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferSubData;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import net.icelane.amplifire.analyzer.AudioGraph;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 1.3
 * 
 * A simple panel to display Graphs
 */
public class GL45Graph extends GLGraph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
		
	
	private VolatileImage backBuffer;

    
    
	@Override
	public float getPointSize(){
		return getZoomlLevel() / 10f;
	}
	
	@Override
	public void gl_renderLoop() {
		for (AudioGraph graph : getGraphs()) {
			graph.syncBufferSize(GetSize().width);

			gl_renderData(getBackgorundData(graph), 2);
			
			// render data ...
			gl_renderData(getData(graph), graph.size());
		}
	}
	
	public float[] getBackgorundData(AudioGraph g){
		float yOffset = 0;
		
		// determine display mode to set graph position ...
		switch (getDisplayMode()) {
		case CENTERED: break;
		case NORMAL:
		default: //NORMAL
			// set graph positions ...
			if (getGraphs().size() > 1){
				int gindex = getGraphs().indexOf(g);
				if (gindex == 0){
					yOffset = 0.5f;
				}
				if (gindex == 1){
					yOffset = -0.5f;
				}
			}
			break;
		}
		
		float data[] = new float[12];
		
		//x
		data[0] = -1f;
		//y
		data[1] = yOffset;

		//r
		data[2] = Color.darkGray.getRGBComponents(null)[0];
		//g
		data[3] = Color.darkGray.getRGBComponents(null)[1]; 
		//b
		data[4] = Color.darkGray.getRGBComponents(null)[2];
		//a
		data[5] = Color.darkGray.getRGBComponents(null)[3];

		//x
		data[6] = 1f - 0.02f; // 0.02f => right bounds TODO
		//y
		data[7] = yOffset;

		//r
		data[8] = data[2];
		data[9] = data[3]; 
		data[10] = data[4];
		data[11] = data[5];
		
		return data;
	}
	
	
	public float[] getData(AudioGraph g){
		try {
			int samples = g.size();
			int index = 0;
			float data[] = new float[samples * 6];
			
			// Data: [<x><y><rgba>] 
			for(int i = 0; i < samples; i++){
				
				if (index >= data.length) {
					System.out.println("[OpenGL] Warning: Data gathering aborted, due to settings change!");
					break;
				}
				
				
				float yOffset = 0;
				
				// determine display mode to set graph position ...
				switch (getDisplayMode()) {
				case CENTERED: break;
				case NORMAL:
				default: //NORMAL
					// set graph positions ...
					if (getGraphs().size() > 1){
						int gindex = getGraphs().indexOf(g);
						if (gindex == 0){
							yOffset = 0.5f;
						}
						if (gindex == 1){
							yOffset = -0.5f;
						}
					}
					break;
				}
				
				//x
				data[index+0] = (2f/samples) * i - 1f + (1f/samples);
				//y
				data[index+1] = g.getValue(i) * getHeightLevel() + yOffset;

				//r
				data[index+2] = g.getColor().getRGBComponents(null)[0];
				//g
				data[index+3] = g.getColor().getRGBComponents(null)[1]; 
				//b
				data[index+4] = g.getColor().getRGBComponents(null)[2];
				//a
				data[index+5] = g.getColor().getRGBComponents(null)[3];

				index += 6;
			}

			return data;
		} catch (Exception e) {
			throw e;
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
				g.drawString(String.format("%s", (Math.round(getFPS()))), 10, 20);
	        }
			
			// show the back backBuffer on the screen ...
			if (backBuffer != null) g.drawImage(backBuffer, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
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
		
}