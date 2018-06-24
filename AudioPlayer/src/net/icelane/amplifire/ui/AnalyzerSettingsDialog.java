package net.icelane.lolplayer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.icelane.lolplayer.Application;
import net.icelane.lolplayer.gui.ui.UIFrame;
import net.icelane.lolplayer.player.analyzer.Analyzer;
import net.icelane.lolplayer.player.analyzer.device.AnalyzerSourceDevice;

public class AnalyzerSettingsDialog extends UIFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5359730769550465456L;

	private JComboBox<String> sources;
	private JLabel sourceDeviceDesc; 

	private Analyzer analyzer;
	
	public AnalyzerSettingsDialog(JFrame parent, Analyzer analyzer) {
//		super(parent);
	
		this.analyzer = analyzer;
		
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
		
		JPanel mp = new JPanel(new BorderLayout());
		mp.setOpaque(false);
		mp.add(sourceLabel, BorderLayout.NORTH);
		mp.add(sources, BorderLayout.CENTER);
		mp.add(curSelDescPanel, BorderLayout.SOUTH);
		mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
	}

}
