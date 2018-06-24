package net.icelane.amplifire.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.icelane.amplifire.AppCore;
import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.Analyzer;
import net.icelane.amplifire.analyzer.render.GraphRender;
import net.icelane.amplifire.analyzer.source.AnalyzerSourceDevice;
import net.icelane.amplifire.ui.ui.UIFrame;

public class AnalyzerSettingsDialog extends UIFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5359730769550465456L;

	private JComboBox<String> renderer;
	private JComboBox<String> sources;
	private JLabel sourceDeviceDesc; 

	private Analyzer analyzer;
	
	public AnalyzerSettingsDialog(JFrame parent, Analyzer analyzer) {
//		super(parent);
	
		this.analyzer = analyzer;
		
		JLabel renderLabel = new JLabel("Renderer: ");
		renderLabel.setForeground(Application.getColors().color_forground1);
		
		renderer = new JComboBox<>();
		renderer.addItem("JGraph v2.2");
		renderer.addItem("JGraph v2.3");
		renderer.addItem("JGraph v2.4");
		renderer.addItem("OpenGL 1.1 Renderer");
		renderer.addItem("OpenGL 4.5 Renderer");
		renderer.addActionListener(this);

		JPanel rr = new JPanel(new BorderLayout());
		rr.setOpaque(false);
		rr.add(renderLabel, BorderLayout.NORTH);
		rr.add(renderer, BorderLayout.CENTER);
		rr.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		JLabel sourceLabel = new JLabel("Analyzer source device: ");
		sourceLabel.setForeground(Application.getColors().color_forground1);
		
		sources = new JComboBox<>();
		sources.addActionListener(this);
		
		JLabel deviceDescLabel = new JLabel("Description: ");
		deviceDescLabel.setForeground(Application.getColors().color_forground1);
		
		sourceDeviceDesc = new JLabel("\r\n\r\n\r\n");
		sourceDeviceDesc.setForeground(Application.getColors().color_forground1);
		
		JPanel curSelDescPanel = new JPanel(new BorderLayout());
		curSelDescPanel.setOpaque(false);
		curSelDescPanel.add(deviceDescLabel, BorderLayout.NORTH);
		curSelDescPanel.add(sourceDeviceDesc, BorderLayout.CENTER);
		
		JPanel asrc = new JPanel(new BorderLayout());
		asrc.setOpaque(false);
		asrc.add(sourceLabel, BorderLayout.NORTH);
		asrc.add(sources, BorderLayout.CENTER);
		asrc.add(curSelDescPanel, BorderLayout.SOUTH);
		asrc.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		JPanel mp = new JPanel(new GridBagLayout());
		mp.setOpaque(false);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		mp.add(rr, c);
		c.gridy = 1;
		mp.add(asrc, c);
		mp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		this.getTitleFrame().getResizeButton().setVisible(false);
		this.getResizeHandler().setBlocked(true);
		this.getContentPanePanel().add(mp);

		this.setPreferredSize(new Dimension(430, this.getPreferredSize().height));
		
		int locX = parent.getLocation().x - this.getPreferredSize().width;
		int locY = parent.getLocation().y; 
		
		this.setLocation(locX < 0 ? 10 : locX, locY < 0 ? 10 : locY);

		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//update gui ...
		reinitDeviceList();
		updateToCurrentDevice();
				
		this.setVisible(true);
	}
	
	public void reinitDeviceList(){
		sources.removeAllItems();
		//add known devices ...
		AnalyzerSourceDevice[] aSDs = Analyzer.geSourceDevices();		
		for (AnalyzerSourceDevice aSD : aSDs) {
			sources.addItem(aSD.getDisplayName());
		}
	}
	
	public void updateToCurrentDevice(){
		AnalyzerSourceDevice asd = analyzer.getActiveDevice();
		if (asd == null) return;
		
		sources.setSelectedIndex(analyzer.getActiveDeviceIndex());
		sourceDeviceDesc.setText(asd.getDescription());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!this.isVisible()) return;
		
		if (e.getSource().equals(sources)){
			int sIndex = sources.getSelectedIndex();
			
			AnalyzerSourceDevice currentASD = analyzer.getActiveDevice();
			
			//WARNING: This can go wrong if a device get removed before this line!
			if (analyzer.setActiveDevice(sIndex)){
				currentASD.CloseSettingsUI();
				analyzer.getActiveDevice().OpenSettingsUI(this);
			}
			
			reinitDeviceList();
			updateToCurrentDevice();
		}
		if (e.getSource().equals(renderer)){
			int sIndex = renderer.getSelectedIndex();
			
			Class<? extends GraphRender> renderClass = null;		
			switch (sIndex) {
			case 0:
				renderClass = net.icelane.amplifire.analyzer.render.jgraph.v22.JGraph.class;
				break;
			case 1:
				renderClass = net.icelane.amplifire.analyzer.render.jgraph.v23.JGraph.class;
				break;
			case 2:
				renderClass = net.icelane.amplifire.analyzer.render.jgraph.JGraph.class;
				break;
			case 3:
				renderClass = net.icelane.amplifire.analyzer.render.opengl.GL11Graph.class;
				break;
			case 4:
				renderClass = net.icelane.amplifire.analyzer.render.opengl.GL45Graph.class;
				break;
			}
			
			if (renderClass != null) {
				analyzer.switchRenderer(renderClass);
			}

		}
	}

}
