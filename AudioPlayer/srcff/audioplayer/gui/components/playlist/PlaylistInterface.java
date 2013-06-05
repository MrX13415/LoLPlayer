/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

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
        playlistTable.setBackground(new Color(150,0,0));
        playlistTable.setForeground(new Color(255,255,255));
        playlistTable.setSelectionBackground(new Color(255, 128 ,0));
        playlistTable.setSelectionForeground(new Color(0,0,0));
        playlistTable.setGridColor(new Color(255, 128 ,0));
        playlistTable.getTableHeader().setBackground(new Color(50,50,50));
        playlistTable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
   
    	DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
    	headerRenderer.setBackground(new Color(50,50,50));

    	for (int i = 0; i < playlistTable.getModel().getColumnCount(); i++) {
            playlistTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    	}

        playlistTable.setOpaque(false);
        	
        playlistScrollPane = new JScrollPane(playlistTable);
        playlistScrollPane.getViewport().setBackground(new Color(150,00,00));
        playlistScrollPane.setBackground(new Color(150,00,00));
        playlistScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        playlistScrollPane.getVerticalScrollBar().setBackground(new Color(150,00,00));

        this.setLayout(new BorderLayout());
        this.add(playlistScrollPane, BorderLayout.CENTER);
        this.setOpaque(false);
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
