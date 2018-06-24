package net.icelane.amplifire.process.api.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.font.FontLoader;
import net.icelane.amplifire.process.api.Process;

/**
 * amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class StatusBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5935437179835473019L;
	private JLabel cancle;
	private JProgressBar bar;
	private JLabel message;
	private ArrayList<Process> process = new ArrayList<Process>();

	public StatusBar() {
		
		cancle = new JLabel("\u0072");
		cancle.setForeground(new Color(130, 130, 130));
		cancle.setFont(FontLoader.fontMarlett_16p);
		cancle.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0 )); //correct position

		cancle.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				cancle.setForeground(new Color(255, 0, 0));
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				cancle.setForeground(new Color(255, 80, 0));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				cancle.setForeground(new Color(130, 130, 130));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				cancle.setForeground(new Color(255, 0, 0));
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				stopProcess();
			}
		});
		
		bar = new JProgressBar();

		message = new JLabel("Loading files ... (1000/24214)");
		message.setForeground(new Color(255, 255, 255));
		message.setHorizontalTextPosition(JLabel.LEFT);
		message.setHorizontalAlignment(JLabel.LEFT);
		final Dimension size = message.getPreferredSize();
		message.setMinimumSize(size);
		message.setPreferredSize(size);
		
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.5;
		c.insets = new Insets(1, 1, 1, 1);

		this.add(message, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.5;

		this.add(bar, c);

		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(cancle, c);
		
		this.setBackground(new Color(50, 50, 50));
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setPreferredSize(new Dimension(0, 20));
		setVisible(false);
	}

	public void addProcess(Process process) {
		if (Application.isDebug()) System.out.println("Process '" + process.getClass().getName() + "' added to queue ...");
		Process p = getProcess();
		if (p != null) {
			if (!p.isRunning()) {
				if (p.isReachedEnd())
					removeProcess(p);
				this.process.add(process);
				startProcess();
			} else {
				this.process.add(process);
			}
		}else{
			this.process.add(process);
			startProcess();
		}
	}

	public void removeProcess(Process process) {
		if (Application.isDebug()) System.out.println("Process '" + process.getClass().getName() + "' removed from queue ...");
		if (process.isRunning())
			process.stop();
		this.process.remove(process);
	}

	public void startProcess() {
		Process p = getProcess();
		if (p != null)
			p.start();
	}

	public void stopProcess() {
		Process p = getProcess();
		if (p != null)
			p.stop();
	}

	public void stopAllProcess() {
		stopProcess();
		process.clear();
	}

	public Process getProcess() {
		return (process.size() >= 1 ? process.get(0) : null);
	}

	public void prepareNextProcess() {
		Process p = getProcess();
		if (p != null) {
			p.stop();
			process.remove(p);
		}
		startProcess();
	}

	public JProgressBar getBar() {
		return bar;
	}

	public JLabel getMessage() {
		return message;
	}

	public String getMessageText() {
		return message.getText();
	}

	public void setMessageText(String message) {
		this.message.setText(message);
	}

	@Override
	public void setVisible(boolean visible) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component c = this.getComponent(i);
			c.setVisible(visible);
		}
	}

}
