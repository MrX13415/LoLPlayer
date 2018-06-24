package net.icelane.amplifire.analyzer.render.jgraph.v22;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import net.icelane.amplifire.analyzer.AudioGraph;
import net.icelane.amplifire.analyzer.render.GraphRender;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 2.2
 * 
 * A simple panel to display Graphs
 */
public class JGraph extends GraphRender {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;

	private BufferedImage graphImage;	//the graphs
	private BufferedImage effectsImage;	//the background	
	private BufferedImage backImage;	//the background	
	
	private volatile BufferedImage finalGraphImage;	//the graphs
	private volatile BufferedImage finalEffectsImage;	//the graphs
	private volatile BufferedImage finalBackImage;	//the background	

	private Stroke graphStroke = new BasicStroke(1f);
	private Stroke effetcStroke1 = new BasicStroke(15f);
//	private Stroke effetcStroke2 = new BasicStroke(10f);
//	private Stroke effetcStroke3 = new BasicStroke(6f);

//	private float getHeightLevel() = 0.4f;
//	private int getZoomlLevel() = 1;

	public JGraph() {
		super();
		
		start();
	}
	
    public void paintComponent(Graphics g ) {
    	super.paintComponent(g);
    	
    	if (!isActive()) return;
    	
    	int x = 0;
    	int y = 0;

    	g.drawImage(finalBackImage, x, y, null);
        g.drawImage(finalEffectsImage, x, y, null);
        g.drawImage(finalGraphImage, x, y, null);

        if (isShowFPS()){
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	
			//draw label ...
			g.setColor(Color.darkGray);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			g.drawString(String.format("%s", (Math.round(getFPS()))), 10, 20);
        }
    }
	
	private BufferedImage createBackBuffer(int width, int height, int transparency){
		// obtain the current system graphical settings
		GraphicsConfiguration gc = GraphicsEnvironment
										.getLocalGraphicsEnvironment()
										.getDefaultScreenDevice()
										.getDefaultConfiguration();
		
		return gc.createCompatibleImage(width, height, transparency);
	}

	private synchronized void repaintGraphs(){
		
		if(this.getWidth() > 0 && this.getHeight() > 0){
			backImage = createBackBuffer(this.getWidth(), this.getHeight(), Transparency.TRANSLUCENT);
			effectsImage = createBackBuffer(this.getWidth(), this.getHeight(), Transparency.TRANSLUCENT);
			graphImage = createBackBuffer(this.getWidth(), this.getHeight(), Transparency.TRANSLUCENT);
		}
		
		try {
			for (AudioGraph ag : getGraphs()) {
				ag.syncBufferSize(this.getWidth() * getZoomlLevel());
				
				if (getGraphs().size() > 1){
					int index = getGraphs().indexOf(ag);
					if (index == 0){
						ag.setYOffset( this.getHeight() / 4 );
					}
					if (index == 1){
						ag.setYOffset( (this.getHeight() / 4) * -1 );
					}
				}
				
				paintBackground(ag);
				paintGraph(ag);
				if (isGlowEffect()) paintGlowEffect(ag);
			}
		} catch (Exception e) {}	
				
		if(this.getWidth() > 0 && this.getHeight() > 0){
			
			if (isGlowEffect()) {				
				effectsImage = getLinearBlurOp(2, 2, .2f).filter(effectsImage, null);
			}
			
			if(isBlurFilter()){
				graphImage = getLinearBlurOp(2, 2, .6f).filter(graphImage, null);
			}			
	
			finalBackImage = backImage;
			finalEffectsImage = effectsImage;
			finalGraphImage = graphImage; 

			repaint();
		}
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
		int height = Math.round(this.getHeight() + getHeightLevel());
		int h = height >> 1;
				
		int graphcenterY = Math.round((0 * (float)(h * getHeightLevel())) + h) + graph.getYOffset();;

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
		paintGraph(graph, graphImage, graphStroke, 255);
	}
	
	private void paintGlowEffect(final AudioGraph graph){

//		paintGraph(graph, effectsImage, effetcStroke1, 5);
//		paintGraph(graph, effectsImage, effetcStroke2, 10);
//		paintGraph(graph, effectsImage, effetcStroke3, 15);
		
		paintGraph(graph, effectsImage, effetcStroke1, 8);
	}
	
	private void paintGraph(final AudioGraph graph, BufferedImage image, Stroke stroke, int alpha){	
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(graph.size() < 1) return;
		
		//define height ...
		int height = Math.round(this.getHeight() + getHeightLevel());
		int h = height >> 1;
		
		//define max point count
		int pointCount = this.getWidth() * getZoomlLevel();
		
		//calculate min index ...
		int minIndex = graph.size() - (pointCount); 
			minIndex = (minIndex < 0 ? 0 : minIndex);

		int graphcenterY = Math.round((0 * (float)(h * getHeightLevel())) + h) + graph.getYOffset();;
		
		Color c = graph.getColor();
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);			
		g.setColor(c);
		g.setStroke(stroke);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
		//define first point ...
		int lastPoint_x = 0;
		int lastPoint_y = Math.round((graph.getValue(minIndex) * (float)(h * getHeightLevel())) + h) + graph.getYOffset();

		//current point 
		int point_x = 0;
		int point_y = 0;

		int detailC = 0;
		
		int lastI = minIndex;
		
		//update minIndex because first point is already defined ... 
		minIndex++;

//		GeneralPath path = new GeneralPath();
//		path.moveTo(lastPoint_x, lastPoint_y);
				
		for (int i = minIndex; i < graph.size(); i++) {				

			//calculate the y coordinates of the next point
			point_y = Math.round((graph.getValue(i) * (float)(h * getHeightLevel())) + h);
						
			//add Y-Offset ...
			point_y += graph.getYOffset();
			
			//calculate x coordinate;
			detailC++;
			
			if (detailC >= getZoomlLevel()) {
				point_x++;
				detailC = 0;
			}

			//draw a line from the last point to the current one ...			
			if (getDrawMode() == DrawMode.STRAIGHT){
				g.drawLine(lastPoint_x, lastPoint_y, point_x, point_y);
//				path.lineTo(point_x, point_y);
			}
			
			if (getDrawMode() == DrawMode.DOTS){
				g.drawLine(point_x, point_y, point_x, point_y);
			}
			
			if (getDrawMode() == DrawMode.LINES){
				int offset = 0;

				if (detailC == 0 && (i - getZoomlLevel() * 3) == lastI){
					offset = 16;
					lastI = i;
					g.drawLine(point_x, point_y - offset/2, point_x, point_y + offset/2);
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
			
			//Update last point ...
			lastPoint_x = point_x;
			lastPoint_y = point_y;
		}
		
//		if (getDrawMode() == DrawMode.STRAIGHT){
//			g.draw(path);
//		}

		g.dispose();
	}

	@Override
	public void startup() {
		//long fpsUpdateT = System.currentTimeMillis();
	}

	@Override
	public void renderloop() {
		repaintGraphs();
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}