package audioplayer.gui;

import audioplayer.Application;
import audioplayer.desing.Colors;
import audioplayer.gui.ui.UIFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class AboutDialog extends UIFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6955621556496165323L;
	private static String aboutHTMLPage;
	
    private JLabel text;
//    private JButton okButton;

    public AboutDialog(JFrame parent) {
        super(parent);
        
        text = new JLabel(aboutHTMLPage);
        text.setForeground(Application.getColors().color_aboutPage_forground);
        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        //okButton = new JButton();
         
        this.getContentPanePanel().add(text);

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

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        
        this.setTitle("About " + Application.App_Name_Version);
        this.setVisible(true);
    }
    
    public static String loadAboutText(){
    	System.out.print("Load about text ...\t\t\t");
        String aboutText = "";
        
        BufferedReader br = null;
        try {
            InputStream is = AboutDialog.class.getResourceAsStream("about.html");

            br = new BufferedReader(new InputStreamReader(is));

            while(br.ready()){
                aboutText = String.format("%s%s\n", aboutText, 
                        String.format(br.readLine(),
                        Application.App_Version, 
                        Application.App_Author,
                        Application.App_License,
                        Application.App_License_Link));
            }
            
            System.out.println("OK");
        } catch (IOException ex) {
        	System.out.println("ERROR");
            aboutText = Application.App_Name_Version + "\n" + Application.App_Author + "\n" + Application.App_License;
        }finally{
            if (br != null) try {
                br.close();
            } catch (IOException ex) {}
        }
        
        return aboutHTMLPage = aboutText;
    }

}
