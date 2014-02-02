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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import audioplayer.desing.Colors;
import audioplayer.font.FontLoader;
import audioplayer.images.ImageLoader;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistInterface extends JPanel{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2511850113109246648L;
	private JTable playlistTable;
    private JScrollPane playlistScrollPane;
    private PlaylistTableModel ptm;
    private JTextField searchField;
    private JLabel searchIcon;
    
    public PlaylistInterface(MouseListener ml) {
        
        ptm = new PlaylistTableModel();
        playlistTable = new JTable(ptm);
        playlistTable.addMouseListener(ml);
        playlistTable.getColumnModel().getColumn(0).setMaxWidth(50);
        playlistTable.getColumnModel().getColumn(2).setMaxWidth(80);
        playlistTable.setBackground(Colors.color_playlist_background1);
        playlistTable.setForeground(Colors.color_playlist_forground1);
        playlistTable.setSelectionBackground(Colors.color_playlist_selection_background1);
        playlistTable.setSelectionForeground(Colors.color_playlist_selection_forground1);
        playlistTable.setGridColor(Colors.color_playlist_background2);
        playlistTable.getTableHeader().setBackground(Colors.color_playlist_background3);
        playlistTable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        playlistTable.setDefaultRenderer(Object.class, new PlaylistTableCellRenderer());
        
    	DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
    	headerRenderer.setBackground(Colors.color_playlist_background3);

    	for (int i = 0; i < playlistTable.getModel().getColumnCount(); i++) {
            playlistTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    	}

        playlistTable.setOpaque(false);
        	
        playlistScrollPane = new JScrollPane(playlistTable);
        playlistScrollPane.getViewport().setBackground(Colors.color_playlist_background4);
        playlistScrollPane.setBackground(Colors.color_playlist_background4);
        playlistScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        playlistScrollPane.getVerticalScrollBar().setBackground(Colors.color_playlist_background4);

        searchField = new JTextField();
        
        searchIcon = new JLabel("");
        searchIcon.setFont(FontLoader.fontSymbola);
        searchIcon.setBackground(new Color(50,50,50));
//        searchIcon.setBorderPainted(false);
//        searchIcon.setContentAreaFilled(false);
        searchIcon.setForeground(new Color(255,0,0));
        searchIcon.setIcon(ImageLoader.image_search);
//        searchIcon.setPressedIcon(ImageLoader.image_search_pressed_hover);            
//        searchIcon.setRolloverIcon(ImageLoader.image_search_hover);
        
        JPanel searchBox = new JPanel(new BorderLayout(3,0));
        searchBox.add(searchField);
        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.setOpaque(false);
        
        this.setLayout(new BorderLayout(0,5));
        this.add(searchBox, BorderLayout.NORTH);
        this.add(playlistScrollPane, BorderLayout.CENTER);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(this.getWidth(), 200));
    }  
    
    public JTextField getSearchField() {
		return searchField;
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
