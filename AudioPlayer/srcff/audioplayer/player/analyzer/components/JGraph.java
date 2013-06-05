package audioplayer.player.analyzer.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import audioplayer.gui.UserInterface;
import audioplayer.player.analyzer.AudioGraph;
import audioplayer.player.analyzer.Graph;

import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.PremultiplyFilter;
import com.jhlabs.image.UnpremultiplyFilter;


/** A simple window to display Graphs
 *   
 * @author Oliver
 * @version 1.5
 * 
 */
public class JGraph extends JPanel implements Graph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;
		
	private Thread graphRepaintThread = new Thread();	
	private volatile ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();
	private BufferedImage image;
	private BufferedImage destimage;

	/* Enable / Disable Gaussian-Blur
	 * NOTE: This needs a lot CPU power and
	 *       will slow down the graph which
	 *       cause in lagging of the graphs
	 */
	private volatile boolean gaussianFilter = false;
	
	private GaussianFilter gf = new GaussianFilter(1.8f);
	private PremultiplyFilter pf = new PremultiplyFilter();
	private UnpremultiplyFilter upf = new UnpremultiplyFilter();
    
	private float heightLevel = 0.4f;
	
    UserInterface ui;

    public void setUi(UserInterface ui) {
        this.ui = ui;
    }
             
	public JGraph() {
		super();

        initGraphRepaintThread();
		
//		test();
	}

    public void paintComponent(Graphics g ) {
    	super.paintComponent(g);
    	
        g.drawImage(destimage, 0, 0, null);
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
	public synchronized AudioGraph getGraph(int index){
		return graphs.get(index);
	}
	
	@Override
	public synchronized ArrayList<AudioGraph> getGraphs() {
		return graphs;
	}
	
	private synchronized void repaintGraphs(){
		if(this.getWidth() > 0 && this.getHeight() > 0)
			image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			
		for (AudioGraph ag : graphs) {
			ag.setShownValues(this.getWidth());
			
			int index = graphs.indexOf(ag);
			if (index == 0) ag.setYOffset( this.getHeight() / 4 );
			if (index == 1) ag.setYOffset( (this.getHeight() / 4) * -1 );
			
			paintGraph(ag);
		}
		
		if(this.getWidth() > 0 && this.getHeight() > 0)
	        destimage = (isGaussianFilter() ? upf.filter(gf.filter(pf.filter(image, null), null), null) : image);
		
		repaint();
	}
	
	private void paintGraph(final AudioGraph graph){	
		Graphics2D g = image.createGraphics();
		
		if(graph.getValues().size() < 1) return;
		
		//define height ...
		int height = Math.round(this.getHeight() + heightLevel);
		int h = height >> 1;
		
		//define max point count
		int pointCount = this.getWidth();
		
		//calculate min index ...
		int minIndex = graph.getValues().size() - (pointCount); 
			minIndex = (minIndex < 0 ? 0 : minIndex);
			
		//define first point ...
		int lastPoint_x = 0;
		int lastPoint_y = Math.round((graph.getValue(minIndex) * (float)(h * heightLevel)) + h) + graph.getYOffset();
		
		//current point 
		int point_x = 0;
		int point_y = 0;
		
		//update minIndex because first point is already defined ... 
		minIndex++;

		for (int i = minIndex; i < graph.getValues().size(); i++) {				

			//calculate the y coordinates of the next point
			point_y = Math.round((graph.getValue(i) * (float)(h * heightLevel)) + h);
						
			//add Y-Offset ...
			point_y += graph.getYOffset();
			
			//calculate x coordinate;
			point_x++; 
				
			//draw a line from the last point to the current one ...
			g.setColor(graph.getColor());
			g.setStroke(new BasicStroke(1f));
			g.drawLine(lastPoint_x, lastPoint_y, point_x, point_y);				

			//Update last point ...
			lastPoint_x = point_x;
			lastPoint_y = point_y;
			
		}
		g.dispose();
	}
		
	private synchronized void initGraphRepaintThread(){
		graphRepaintThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}	
					try{
						repaintGraphs();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		graphRepaintThread.setName("GraphRepaintThread");
		graphRepaintThread.start();
	}

	public float getHeightLevel() {
		return heightLevel;
	}

	public void setHeightLevel(float heightLevel) {
		this.heightLevel = heightLevel;
	}

	public boolean isGaussianFilter() {
		return gaussianFilter;
	}

	public void setGaussianFilter(boolean enableGF) {
		this.gaussianFilter = enableGF;
	}

	
//	private synchronized void initDrawingThread(){
//		new Thread(new Runnable() {
//			
//			int id = 1;
//			
//			@Override
//			public void run() {
//
//				while(true){
//					
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {}
//										
//					if(toDrawY.size() < 1) continue;
//					
//					int lx = 0;
//					int ly = toDrawY.get(0);
//					
//					for (int i = 1; i < toDrawY.size(); i++) {
//						final int flx = lx;
//						final int fly = ly;
//						final int y = toDrawY.get(i);						
//						final int x = i;
//
//						SwingUtilities.invokeLater(new Runnable() {
//							public void run() {
//								z.setzeLinie(id, flx, fly, x, y, Color.red);
////								z.setzePunkt(id, flx, fly, Color.blue);
////								z.setzePunkt(id, x, y, Color.blue);		
//							}
//						});
//								
//						lx = x;
//						ly = y;
//						
//					}
//					SwingUtilities.invokeLater(new Runnable() {
//						public void run() {
//							id *= -1;
//							z.loeschen(id);
//						}
//					});
//				}
//			}
//		}).start();
//	}
	
//	@Override
//	public void addValue(float val){
//		int h = z.getHeight() >> 1;
//		int v = Math.round((val * (float)h) + h);
//		toDrawY.add(v);
//			if (toDrawY.size() > z.getWidth()) toDrawY.remove(0);
//	}
	
//	public void test(){
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(true){
////					synchronized(d){
//						for (int i = 0; i < 315; i++) {
//							addValue((float) (Math.cos(i / 50d) * 0.5f));
//							try {
//								Thread.sleep(0, 50);
//							} catch (InterruptedException e) {}
//						}
////					}
//				
//				}
//			}
//		}).start();
//	}
//	
	
	/*
	 * 
	 * package audioplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Graph extends JFrame{

	/**
	 * 
	private static final long serialVersionUID = -3435425401548849058L;
	
	ZeichenFlaechenPanel4 z;

	public Graph() {
		setLayout(new BorderLayout());;

		z = new ZeichenFlaechenPanel4(800, 400);
		
        add(z);

        this.pack();
        this.setVisible(true);

	}
	
	public ZeichenFlaechenPanel4 getZ() {
		return z;
	}
	
	public synchronized void addValues(float[] values){
		final float[] f = values;
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				drawValues(f);
			}
		});
		th.start();
	}
	
	private void drawValues(float[] f){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				z.loeschen(0);
			}
		});
		
		int lx = -1;
		int ly = z.getHeight() >> 1;
		
		for (int i = 0; i < f.length; i++) {
			int h = z.getHeight() >> 1;
			int v = Math.round((f[i] * (float)h) + h);
		
			final int flx = lx;
			final int fly = ly;
			final int y = v;						
			final int x = i;

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					z.setzeLinie(0, flx, fly, x, y, Color.red);
//						z.setzePunkt(id, flx, fly, Color.blue);
//						z.setzePunkt(id, x, y, Color.blue);		
				}
			});
					
			lx = x;
			ly = y;
		}
	}

		
}

	 * 
	 * 
	 */

}