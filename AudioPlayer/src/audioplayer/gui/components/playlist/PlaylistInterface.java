/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import net.mrx13415.searchcircle.imageutil.ImageModifier;
import net.mrx13415.searchcircle.imageutil.color.HSB;
import audioplayer.Application;
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
    private JButton searchIcon;
    
    private ImageIcon imgSearch = ImageLoader.image_search;
    private ImageIcon imgSearch_pressed = ImageLoader.image_search_pressed;
    private ImageIcon imgSearch_hover = ImageLoader.setHoverImgHSB(ImageLoader.image_search);
    private ImageIcon imgSearch_pressed_hover = ImageLoader.setPressedHoverImgHSB(ImageLoader.image_search_pressed);
    
    
    public PlaylistInterface(MouseListener ml) {
        
        ptm = new PlaylistTableModel();
        playlistTable = new JTable(ptm);
        playlistTable.addMouseListener(ml);
        playlistTable.getColumnModel().getColumn(0).setMaxWidth(50);
        playlistTable.getColumnModel().getColumn(2).setMaxWidth(80);
        playlistTable.setBackground(Application.getColors().color_playlist_background1);
        playlistTable.setForeground(Application.getColors().color_playlist_forground1);
        playlistTable.setSelectionBackground(Application.getColors().color_playlist_selection_background1);
        playlistTable.setSelectionForeground(Application.getColors().color_playlist_selection_forground1);
        playlistTable.setGridColor(Application.getColors().color_playlist_background2);
        playlistTable.getTableHeader().setBackground(Application.getColors().color_playlist_background3);

        playlistTable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        playlistTable.setDefaultRenderer(Object.class, new PlaylistTableCellRenderer());
        
    	DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
    	headerRenderer.setBackground(Application.getColors().color_playlist_background3);

    	for (int i = 0; i < playlistTable.getModel().getColumnCount(); i++) {
            playlistTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    	}

        playlistTable.setOpaque(false);
        	
        playlistScrollPane = new JScrollPane(playlistTable);

        playlistScrollPane.getViewport().setBackground(Application.getColors().color_playlist_background4);
        playlistScrollPane.setBackground(Application.getColors().color_playlist_background4);

        playlistScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        playlistScrollPane.getVerticalScrollBar().setBackground(Application.getColors().color_playlist_background4);


        searchField = new JTextField();
        
        searchIcon = new JButton("");
        searchIcon.setBorderPainted(false);
        searchIcon.setContentAreaFilled(false);
        searchIcon.setFont(FontLoader.fontSymbola);
        searchIcon.setBackground(new Color(50,50,50));
        searchIcon.setForeground(new Color(255,0,0));
        searchIcon.setPreferredSize(new Dimension(ImageLoader.image_search.getIconWidth(), ImageLoader.image_search.getIconHeight()));
        searchIcon.setIcon(imgSearch);
        searchIcon.setPressedIcon(imgSearch_pressed); 
        searchIcon.setRolloverSelectedIcon(imgSearch_pressed_hover);
        searchIcon.setSelectedIcon(imgSearch_pressed);
        searchIcon.setRolloverIcon(imgSearch_hover);

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
    
    public void setSearchButtonHSB(HSB hsb){
        
		ImageModifier im = new ImageModifier(ImageLoader.image_search.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		imgSearch = new ImageIcon(im.modify());
		imgSearch_pressed = ImageLoader.image_search_pressed;
        imgSearch_hover = ImageLoader.setHoverImgHSB(ImageLoader.image_search);
        imgSearch_pressed_hover = ImageLoader.setPressedHoverImgHSB(ImageLoader.image_search_pressed);
        
        searchIcon.setIcon(imgSearch);
        searchIcon.setPressedIcon(imgSearch_pressed); 
        searchIcon.setRolloverSelectedIcon(imgSearch_pressed_hover);
        searchIcon.setSelectedIcon(imgSearch_pressed);
        searchIcon.setRolloverIcon(imgSearch_hover);

		repaint();
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

	public JButton getPlaylistViewModeButton() {
		return searchIcon;
	}
	
    
}
