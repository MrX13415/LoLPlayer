package audioplayer.player.analyzer.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import javax.swing.JPanel;
import net.mrx13415.searchcircle.imageutil.ImageModifier;
import audioplayer.gui.UserInterface;
import audioplayer.player.analyzer.AudioGraph;
import audioplayer.player.analyzer.Graph;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
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
	private volatile ArrayList<AudioGraph> graphs = new ArrayList<AudioGraph>();
	private BufferedImage graphImage;	//the graphs
	private BufferedImage effectsImage;	//the background	
	private BufferedImage backImage;	//the background	
	
	private BufferedImage finalGraphImage;	//the graphs
	private BufferedImage finalEffectsImage;	//the graphs
	private BufferedImage finalBackImage;	//the background	

	private volatile boolean blurFilter = true;
	private volatile boolean glowEffect = true;
	
	private volatile DrawMode drawMode = DrawMode.STRAIGHT;
//	
//	private GaussianFilter gf = new GaussianFilter(2f);
//	private PremultiplyFilter pf = new PremultiplyFilter();
//	private UnpremultiplyFilter upf = new UnpremultiplyFilter();
//    

			
	private float heightLevel = 0.4f;
	private int zoomlLevel = 1;
	
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
    	
    	int x = -20;
    	int y = -10;
    	int w = this.getWidth() + 20;
    	int h = this.getHeight() + 20;
    	
    	g.drawImage(finalBackImage, x, y, w, h, null);
        g.drawImage(finalEffectsImage, x, y, w, h, null);
        g.drawImage(finalGraphImage, x, y, w, h, null);
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
	
	private synchronized void repaintGraphs(){
		if(this.getWidth() > 0 && this.getHeight() > 0)
			backImage = ImageModifier.createNewCompatibleBufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			effectsImage = ImageModifier.createNewCompatibleBufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			graphImage = ImageModifier.createNewCompatibleBufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		
		try {
			for (AudioGraph ag : graphs) {
				ag.setShownValues(this.getWidth() * zoomlLevel);
				
				if (graphs.size() > 1){
					int index = graphs.indexOf(ag);
					if (index == 0){
						ag.setYOffset( this.getHeight() / 4 );
					}
					if (index == 1){
						ag.setYOffset( (this.getHeight() / 4) * -1 );
					}
				}
				
				paintBackground(ag);
				paintGraph(ag);
			}
		} catch (Exception e) {}	
		
		
		if(this.getWidth() > 0 && this.getHeight() > 0){
			
			if (isGlowEffect()) {
				effectsImage = getLinearBlurOp(15, 2, .03f).filter(graphImage, null);
				effectsImage = getLinearBlurOp(2, 15, .03f).filter(effectsImage, null);
			}
			
			if(isBlurFilter()){
				graphImage = getLinearBlurOp(2, 1, .6f).filter(graphImage, null);
				graphImage = getLinearBlurOp(1, 2, .6f).filter(graphImage, null);
			}
			
			finalEffectsImage = effectsImage;
			finalGraphImage = graphImage; 
			finalBackImage = backImage;
		}
		
		repaint();
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
		
	
	private void paintBackground(final AudioGraph graph){
		Graphics2D g = backImage.createGraphics();
		
		//define height ...
		int height = Math.round(this.getHeight() + heightLevel);
		int h = height >> 1;
				
		int graphcenterY = Math.round((0 * (float)(h * heightLevel)) + h) + graph.getYOffset();;

		//metric of the label text
		LineMetrics metrics = g.getFontMetrics().getLineMetrics(graph.getName(), g);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(graph.getName(), g);

		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//draw center line
		g.setColor(Color.darkGray);
		g.setStroke(new BasicStroke(1f));
		g.drawLine(0, graphcenterY, (int) (this.getWidth() - bounds.getWidth() - (graph.getName().length() > 0 ? 10 : 0)), graphcenterY);
		
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		//draw label ...
		g.drawString(graph.getName(), (int) (this.getWidth() - bounds.getWidth() - 5), (int) (graphcenterY + metrics.getAscent() / 2));
				
		g.dispose();
	}
	
	private void paintGraph(final AudioGraph graph){	
		Graphics2D g = graphImage.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(graph.getValues().size() < 1) return;
		
		//define height ...
		int height = Math.round(this.getHeight() + heightLevel);
		int h = height >> 1;
		
		//define max point count
		int pointCount = this.getWidth() * zoomlLevel;
		
		//calculate min index ...
		int minIndex = graph.getValues().size() - (pointCount); 
			minIndex = (minIndex < 0 ? 0 : minIndex);

		int graphcenterY = Math.round((0 * (float)(h * heightLevel)) + h) + graph.getYOffset();;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
		//define first point ...
		int lastPoint_x = 0;
		int lastPoint_y = Math.round((graph.getValue(minIndex) * (float)(h * heightLevel)) + h) + graph.getYOffset();

		//current point 
		int point_x = 0;
		int point_y = 0;

		int detailC = 0;
		
		int lastI = minIndex;
		
		//update minIndex because first point is already defined ... 
		minIndex++;

		for (int i = minIndex; i < graph.getValues().size(); i++) {				

			//calculate the y coordinates of the next point
			point_y = Math.round((graph.getValue(i) * (float)(h * heightLevel)) + h);
						
			//add Y-Offset ...
			point_y += graph.getYOffset();
			
			//calculate x coordinate;
			detailC++;
			
			if (detailC >= zoomlLevel) {
				point_x++;
				detailC = 0;
			}

			//draw a line from the last point to the current one ...
			g.setColor(graph.getColor());
			g.setStroke(new BasicStroke(1f));
			
			if (drawMode == DrawMode.STRAIGHT){
				g.drawLine(lastPoint_x, lastPoint_y, point_x, point_y);
			}
			
			if (drawMode == DrawMode.DOTS){
				g.drawLine(point_x, point_y, point_x, point_y);
			}
			
			if (drawMode == DrawMode.LINES){
				int offset = 0;

				if (detailC == 0 && (i - zoomlLevel * 3) == lastI){
					offset = 16;
					lastI = i;
					g.drawLine(point_x, point_y - offset/2, point_x, point_y + offset/2);
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
						Thread.sleep(25); //40 FPS
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

	public int getZoomlLevel() {
		return zoomlLevel;
	}

	public void setZoomlLevel(int zoomlLevel) {
		this.zoomlLevel = zoomlLevel;
	}

	public boolean isBlurFilter() {
		return blurFilter;
	}

	public void setBlurFilter(boolean enableGF) {
		this.blurFilter = enableGF;
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
