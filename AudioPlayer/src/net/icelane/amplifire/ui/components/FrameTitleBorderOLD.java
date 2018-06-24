package net.icelane.amplifire.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class FrameTitleBorderOLD extends AbstractBorder{

	    /**
	 * 
	 */
	private static final long serialVersionUID = -577005564660195705L;
	
		private int thickness;

		public FrameTitleBorderOLD(int thick) {
		    this.thickness = thick;
	    }

		public Insets getBorderInsets(Component c) {
		    // return the top, bottom, left, right spacing in pixels the border will occupy
	        return new Insets(thickness, thickness, thickness, thickness);
	    }

	    public Insets getBorderInsets(Component c, Insets insets) {
	        insets.left = insets.top = insets.right = insets.bottom = thickness;
	        return insets;
	    }

	    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    	super.paintBorder(c, g, x, y, width, height);
	    	
	    	Graphics2D g2d = (Graphics2D) g;
	    	g2d.setColor(Color.red);
	    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    	g2d.setStroke( new BasicStroke(thickness));
	    	g2d.drawRect(x, y, width, height);
	    }
	    
		@Override
		public boolean isBorderOpaque() {
			return false;
		}

}
