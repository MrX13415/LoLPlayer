package audioplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog{

    private JLabel text;
    private JButton okButton;
    
    public AboutDialog(JFrame parent) {
        super(parent);
        
        String about = "<html>"
                        + "<p style=\"padding:35px 35px 0 35px\">"
                        + "<font size=20>"
                        + "<font color=#ff0000>LoL</font>"
                        + "Player "
                        + "<font color=#ff8000>II</font>"
                        + "</font>"
                        + "</p>"
                        + "<p style=\"padding: 0 40px 35px 35px\">"
                        + "<font size=10>"
                        + "<font color=#ff0000>Version: </font>"
                        + "Player "
                        + "<font color=#ff8000>II</font>"
                        + "</font>"
                        + "</p>"
                        + "</html>";
        
        text = new JLabel(about);
        text.setForeground(new Color(255 ,255, 255));
        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        okButton = new JButton();
         
        this.getContentPane().add(text);
        this.getContentPane().setBackground(new Color(20, 20, 20));
        this.pack();
        
         //Center the frame window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        
        this.setVisible(true);
    }

}
