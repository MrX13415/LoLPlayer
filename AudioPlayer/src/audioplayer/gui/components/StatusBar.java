package audioplayer.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import audioplayer.desing.Colors;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
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
		message.setForeground(Colors.color_statusbar_forground1);
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
		
		this.setBackground(Colors.color_statusbar_background1);
		this.setBorder(BorderFactory.createRaisedBevelBorder());
                this.setPreferredSize(new Dimension(0, 20));
		setVisible(false);
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
        public void setVisible(boolean visible){
            for (int i = 0; i < this.getComponentCount(); i++) {
                Component c = this.getComponent(i);
                c.setVisible(visible);
            }
        }
	
}
