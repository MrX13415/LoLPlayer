package net.icelane.amplifire.analyzer.render;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;


/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 1.0
 * 
 * A graph render using OpenGL (Version 1.1)
 */
public final class RenderComponent extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8370043378690135186L;

	private GraphRender renderer; 
	
	
	public RenderComponent(GraphRender renderer) {
		this.setLayout(new OverlayLayout(this));
		this.setBackground(Color.RED);
		if (renderer == null) return;
		
		renderer.setLayout(null);
		this.add(renderer);
		
		this.renderer = renderer; 
	}


	public void switchRenderer(Class<? extends GraphRender> rendererClass) {
		
		renderer.dispose();
		this.remove(renderer);
		
		long waitTime = System.currentTimeMillis();
		while (renderer.isAlive()) {
			if (System.currentTimeMillis() - waitTime > 200) break;			
		};

		try {
			GraphRender newRenderer = rendererClass.newInstance();
			
			newRenderer.setLayout(null);
			this.add(newRenderer);
			this.renderer = newRenderer; 
			
			this.validate();
			this.repaint();
			this.setVisible(true);
		} catch (InstantiationException | IllegalAccessException ex) {
		}
	}

	public GraphRender getRenderer() {
		return renderer;
	}
	
}