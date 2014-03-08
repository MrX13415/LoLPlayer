package audioplayer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;

import audioplayer.player.AudioFile;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class AudioFilePropertiesDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 24682161630979195L;
	
	private JLabel title;
	private JLabel author;
	private JLabel album;
	private JLabel genre;
	private JLabel file;
	
	public AudioFilePropertiesDialog(AudioFile af) {
	
		title = new JLabel(af.getTitle());
		title.setForeground(new Color(255 ,255, 255));
		
		author = new JLabel(af.getAuthor());
		author.setForeground(new Color(255 ,255, 255));
		
		album = new JLabel(af.getAlbum());
		album.setForeground(new Color(255 ,255, 255));
		
		genre = new JLabel(af.getGenre());
		genre.setForeground(new Color(255 ,255, 255));
		
		file = new JLabel(af.getFile().getAbsolutePath());
		file.setForeground(new Color(255 ,255, 255));
		
		JLabel titlelbl = new JLabel("Title:");
		titlelbl.setForeground(new Color(255 ,128, 0));
		
		JLabel authorlbl = new JLabel("Author:");
		authorlbl.setForeground(new Color(255 ,128, 0));
		
		JLabel albumlbl = new JLabel("Album:");
		albumlbl.setForeground(new Color(255 ,128, 0));
		
		JLabel genrelbl = new JLabel("Genre:");
		genrelbl.setForeground(new Color(255 ,128, 0));
		
		JLabel filelbl = new JLabel("File:");
		filelbl.setForeground(new Color(255 ,128, 0));
		
		this.getContentPane().setLayout(new GridLayout(0,1));
        this.getContentPane().add(titlelbl);
        this.getContentPane().add(title);
        this.getContentPane().add(authorlbl);
        this.getContentPane().add(author);
        this.getContentPane().add(albumlbl);
        this.getContentPane().add(album);
        this.getContentPane().add(genrelbl);
        this.getContentPane().add(genre);
        this.getContentPane().add(filelbl);
        this.getContentPane().add(file);
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
        
        this.setTitle("Proberties");
        this.setVisible(true);
	}
}
