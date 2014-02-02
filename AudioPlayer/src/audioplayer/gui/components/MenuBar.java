package audioplayer.gui.components;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.JMenuItem;

import audioplayer.desing.Colors;
import audioplayer.player.analyzer.components.JGraph;
import audioplayer.player.analyzer.components.JGraph.DrawMode;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1418747989989490805L;

	private JMenu menu_file;
	private JMenuItem menu_file_open;
	private JMenuItem menu_file_opendir;
	private JMenuItem menu_file_exit;

	private JMenu menu_playlist;
	private JMenuItem menu_playlist_add;
	private JMenuItem menu_playlist_adddir;
	private JMenuItem menu_playlist_remove;
	private JMenuItem menu_playlist_clear;
	
	private JMenuItem menu_playlist_up;
	private JMenuItem menu_playlist_down;
	private JMenuItem menu_playlist_shuffle;
	
	private JMenu menu_media;
	private JMenuItem menu_media_library;
	
	private JMenu menu_graph;
	private JMenuItem menu_graph_merge;
	private JMenuItem menu_graph_gfilter;
	private JMenuItem menu_graph_dmode;
	
	private JMenu menu_help;
	private JMenuItem menu_help_about;
	
	public MenuBar(ActionListener actionListener) {
		JSeparator separator = new JSeparator();
		separator.setBackground(Colors.color_menu_background1);
							
		menu_file_open = new MenuItem();
		menu_file_open.setText("Open ...");
		menu_file_open.addActionListener(actionListener);
		menu_file_open.setBackground(Colors.color_menu_background1);
		menu_file_open.setForeground(Colors.color_menu_forground1);
		
		menu_file_open.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_O, 
		        java.awt.Event.CTRL_MASK));
		
		menu_file_opendir = new MenuItem();
		menu_file_opendir.setText("Open Directory ...");
		menu_file_opendir.addActionListener(actionListener);
		menu_file_opendir.setBackground(Colors.color_menu_background1);
		menu_file_opendir.setForeground(Colors.color_menu_forground1);
		
		menu_file_exit = new MenuItem();
		menu_file_exit.setText("Exit");
		menu_file_exit.addActionListener(actionListener);
		menu_file_exit.setBackground(Colors.color_menu_background1);
		menu_file_exit.setForeground(Colors.color_menu_forground1);

		menu_file = new Menu();
		menu_file.setText("File");
		menu_file.setBackground(Colors.color_menu_background1);
		menu_file.setForeground(Colors.color_menu_forground1);
		menu_file.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_file.add(menu_file_open);
		menu_file.add(menu_file_opendir);
		menu_file.add(separator);
		menu_file.add(menu_file_exit);
		menu_file.getPopupMenu().setBackground(Colors.color_menu_background1);
		menu_file.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_playlist_add = new MenuItem();
		menu_playlist_add.setText("Add ...");
		menu_playlist_add.addActionListener(actionListener);
		menu_playlist_add.setBackground(Colors.color_menu_background1);
		menu_playlist_add.setForeground(Colors.color_menu_forground1);
		menu_playlist_add.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_O, 
		        java.awt.Event.ALT_MASK + java.awt.Event.CTRL_MASK));
		
		menu_playlist_adddir = new MenuItem();
		menu_playlist_adddir.setText("Add Directory ...");
		menu_playlist_adddir.addActionListener(actionListener);
		menu_playlist_adddir.setBackground(Colors.color_menu_background1);
		menu_playlist_adddir.setForeground(Colors.color_menu_forground1);
		
		menu_playlist_remove = new MenuItem();
		menu_playlist_remove.setText("Remove");
		menu_playlist_remove.addActionListener(actionListener);
		menu_playlist_remove.setBackground(Colors.color_menu_background1);
		menu_playlist_remove.setForeground(Colors.color_menu_forground1);
		menu_playlist_remove.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_DELETE, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_clear = new MenuItem();
		menu_playlist_clear.setText("Clear");
		menu_playlist_clear.addActionListener(actionListener);
		menu_playlist_clear.setBackground(Colors.color_menu_background1);
		menu_playlist_clear.setForeground(Colors.color_menu_forground1);
				
		menu_playlist_up = new MenuItem();
		menu_playlist_up.setText("Move up");
		menu_playlist_up.addActionListener(actionListener);
		menu_playlist_up.setBackground(Colors.color_menu_background1);
		menu_playlist_up.setForeground(Colors.color_menu_forground1);
		menu_playlist_up.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_UP, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_down = new MenuItem();
		menu_playlist_down.setText("Move down");
		menu_playlist_down.addActionListener(actionListener);
		menu_playlist_down.setBackground(Colors.color_menu_background1);
		menu_playlist_down.setForeground(Colors.color_menu_forground1);
		menu_playlist_down.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_DOWN, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_shuffle = new MenuItem();
		menu_playlist_shuffle.setText("Enable shufflemode");
		menu_playlist_shuffle.addActionListener(actionListener);
		menu_playlist_shuffle.setBackground(Colors.color_menu_background1);
		menu_playlist_shuffle.setForeground(Colors.color_menu_forground1);
		//menu_playlist_shuffle.setAccelerator(KeyStroke.getKeyStroke(
		//        java.awt.event.KeyEvent.VK_DOWN, 
		//        java.awt.Event.ALT_MASK));
		
		menu_playlist = new Menu();
		menu_playlist.setText("Playlist");
		menu_playlist.setBackground(Colors.color_menu_background1);
		menu_playlist.setForeground(Colors.color_menu_forground1);
		menu_playlist.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_playlist.add(menu_playlist_add);
		menu_playlist.add(menu_playlist_adddir);
		menu_playlist.add(menu_playlist_remove);
		menu_playlist.add(menu_playlist_clear);
		menu_playlist.add(separator);
		menu_playlist.add(menu_playlist_up);
		menu_playlist.add(menu_playlist_down);
		menu_playlist.add(separator);
		menu_playlist.add(menu_playlist_shuffle);
		menu_playlist.getPopupMenu().setBackground(Colors.color_menu_background1);
		menu_playlist.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_media_library = new MenuItem();
		menu_media_library.setText("Library");
		menu_media_library.addActionListener(actionListener);
		menu_media_library.setBackground(Colors.color_menu_background1);
		menu_media_library.setForeground(Colors.color_menu_forground1);
		
		menu_media = new Menu();
		menu_media.setText("Media");
		menu_media.setBackground(Colors.color_menu_background1);
		menu_media.setForeground(Colors.color_menu_forground1);
		menu_media.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_media.add(menu_media_library);
		menu_media.getPopupMenu().setBackground(Colors.color_menu_background1);
		menu_media.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_graph_merge = new MenuItem();
		menu_graph_merge.setText("Toggle merged graphs");
		menu_graph_merge.addActionListener(actionListener);
		menu_graph_merge.setBackground(Colors.color_menu_background1);
		menu_graph_merge.setForeground(Colors.color_menu_forground1);
		
		menu_graph_gfilter = new MenuItem();
		menu_graph_gfilter.setText("Toggle Gaussian filter");
		menu_graph_gfilter.addActionListener(actionListener);
		menu_graph_gfilter.setBackground(Colors.color_menu_background1);
		menu_graph_gfilter.setForeground(Colors.color_menu_forground1);
		
		menu_graph_dmode = new Menu();
		menu_graph_dmode.setText("Drawing mode");
		menu_graph_dmode.addActionListener(actionListener);
		menu_graph_dmode.setBackground(Colors.color_menu_background1);
		menu_graph_dmode.setForeground(Colors.color_menu_forground1);
		
		for (DrawMode mode : DrawMode.values()) {
			MenuItem menu_graph_dmode_mode = new MenuItem();
			menu_graph_dmode_mode.setText(mode.toString());
			menu_graph_dmode_mode.addActionListener(actionListener);
			menu_graph_dmode_mode.setBackground(Colors.color_menu_background1);
			menu_graph_dmode_mode.setForeground(Colors.color_menu_forground1);
			menu_graph_dmode_mode.setActionCommand("SetJGraphDrawingMODE:" + mode.name()); 
			menu_graph_dmode.add(menu_graph_dmode_mode);
		}
		
		menu_graph = new Menu();
		menu_graph.setText("Graphs");
		menu_graph.setBackground(Colors.color_menu_background1);
		menu_graph.setForeground(Colors.color_menu_forground1);
		menu_graph.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_graph.add(menu_graph_merge);
		menu_graph.add(menu_graph_gfilter);
		menu_graph.add(separator);
		menu_graph.add(menu_graph_dmode);
		menu_graph.getPopupMenu().setBackground(Colors.color_menu_background1);
		menu_graph.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_help_about = new MenuItem();
		menu_help_about.setText("About");
		menu_help_about.addActionListener(actionListener);
		menu_help_about.setBackground(Colors.color_menu_background1);
		menu_help_about.setForeground(Colors.color_menu_forground1);
		
		menu_help = new Menu();
		menu_help.setText("?");
		menu_help.setBackground(Colors.color_menu_background1);
		menu_help.setForeground(Colors.color_menu_forground1);
		menu_help.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_help.add(menu_help_about);
		menu_help.getPopupMenu().setBackground(Colors.color_menu_background1);
		menu_help.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		add(menu_file);
		add(menu_playlist);
		add(menu_media);
		add(menu_graph);
		add(menu_help);
	}

	public JMenu getMenu_file() {
		return menu_file;
	}

	public JMenuItem getMenu_file_open() {
		return menu_file_open;
	}

	public JMenuItem getMenu_file_exit() {
		return menu_file_exit;
	}

	public JMenuItem getMenu_playlist_add() {
		return menu_playlist_add;
	}

	public JMenuItem getMenu_playlist_remove() {
		return menu_playlist_remove;
	}

	public JMenuItem getMenu_playlist_clear() {
		return menu_playlist_clear;
	}

	public JMenuItem getMenu_help_about() {
		return menu_help_about;
	}

	public JMenuItem getMenu_playlist_up() {
		return menu_playlist_up;
	}

	public JMenuItem getMenu_playlist_down() {
		return menu_playlist_down;
	}
	
	public JMenuItem getMenu_playlist_shuffle() {
		return menu_playlist_shuffle;
	}

	public JMenuItem getMenu_graph_merge() {
		return menu_graph_merge;
	}

	public JMenuItem getMenu_graph_gfilter() {
		return menu_graph_gfilter;
	}
	
	public JMenuItem getMenu_graph_dmode() {
		return menu_graph_dmode;
	}

	public JMenuItem getMenu_file_opendir() {
		return menu_file_opendir;
	}

	public JMenuItem getMenu_playlist_adddir() {
		return menu_playlist_adddir;
	}

	public JMenuItem getMenu_media_library() {
		return menu_media_library;
	}

}
