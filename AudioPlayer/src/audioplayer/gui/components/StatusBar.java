package audioplayer.gui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class StatusBar extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5935437179835473019L;
	private JProgressBar bar;
	private JLabel message;
	
	public StatusBar() {
		bar = new JProgressBar();
		
		message = new JLabel("Loading files ... (1000/24214)");
		message.setForeground(new Color(255,255,255));
		message.setHorizontalTextPosition(JLabel.LEFT);
		message.setHorizontalAlignment(JLabel.LEFT);

		this.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.5;
		c.insets = new Insets(1,1,1,1);
		
		this.add(message, c);
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.25;
				
		this.add(bar, c);
		
		this.setBackground(new Color(50, 50, 50));
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setVisible(false);
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
	
}
