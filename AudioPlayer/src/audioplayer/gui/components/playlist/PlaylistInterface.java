/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author dausol
 */
public class PlaylistInterface extends JPanel{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2511850113109246648L;
	private JTable playlistTable;
    private JScrollPane playlistScrollPane;
    private PlaylistTableModel ptm;
    
    public PlaylistInterface(MouseListener ml) {
        
        ptm = new PlaylistTableModel();
        playlistTable = new JTable(ptm);
        playlistTable.addMouseListener(ml);
        playlistTable.getColumnModel().getColumn(0).setMaxWidth(50);
        playlistTable.getColumnModel().getColumn(2).setMaxWidth(80);
        playlistTable.setOpaque(false);

        playlistScrollPane = new JScrollPane(playlistTable);
        playlistScrollPane.setOpaque(false);
        
        this.setLayout(new BorderLayout());
        this.add(playlistScrollPane, BorderLayout.CENTER);
        this.setOpaque(false);
        this.setBackground(new Color(255,50,50));
        this.setPreferredSize(new Dimension(this.getWidth(), 200));
    }  
    
    public PlaylistTableModel getPlaylistTableModel(){
        return ptm;
    }

	public JTable getPlaylistTable() {
		return playlistTable;
	}

	public JScrollPane getPlaylistScrollPane() {
		return playlistScrollPane;
	}
    
    
    
}
