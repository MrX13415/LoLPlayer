package audioplayer.gui.components;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import javax.swing.JMenuItem;

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
	private JMenuItem menu_file_exit;

	private JMenu menu_playlist;
	private JMenuItem menu_playlist_add;
	private JMenuItem menu_playlist_remove;
	private JMenuItem menu_playlist_clear;
	
	private JMenuItem menu_playlist_up;
	private JMenuItem menu_playlist_down;
	
	private JMenu menu_graph;
	private JMenuItem menu_graph_merge;
	private JMenuItem menu_graph_gfilter;
	
	private JMenu menu_help;
	private JMenuItem menu_help_about;
	
	public MenuBar(ActionListener actionListener) {
		JSeparator separator = new JSeparator();
		separator.setBackground(new Color(50,50,50));
				
		menu_file_open = new MenuItem();
		menu_file_open.setText("Open ...");
		menu_file_open.addActionListener(actionListener);
		menu_file_open.setBackground(new Color(50,50,50));
		menu_file_open.setForeground(new Color(255,255,255));

		menu_file_exit = new MenuItem();
		menu_file_exit.setText("Exit");
		menu_file_exit.addActionListener(actionListener);
		menu_file_exit.setBackground(new Color(50,50,50));
		menu_file_exit.setForeground(new Color(255,255,255));

		menu_file = new Menu();
		menu_file.setText("File");
		menu_file.setBackground(new Color(50,50,50));
		menu_file.setForeground(new Color(255,255,255));
		menu_file.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_file.add(menu_file_open);
		menu_file.add(separator);
		menu_file.add(menu_file_exit);
		menu_file.getPopupMenu().setBackground(new Color(50,50,50));
		menu_file.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_playlist_add = new MenuItem();
		menu_playlist_add.setText("Add ...");
		menu_playlist_add.addActionListener(actionListener);
		menu_playlist_add.setBackground(new Color(50,50,50));
		menu_playlist_add.setForeground(new Color(255,255,255));
		
		menu_playlist_remove = new MenuItem();
		menu_playlist_remove.setText("Remove");
		menu_playlist_remove.addActionListener(actionListener);
		menu_playlist_remove.setBackground(new Color(50,50,50));
		menu_playlist_remove.setForeground(new Color(255,255,255));
		
		menu_playlist_clear = new MenuItem();
		menu_playlist_clear.setText("Clear");
		menu_playlist_clear.addActionListener(actionListener);
		menu_playlist_clear.setBackground(new Color(50,50,50));
		menu_playlist_clear.setForeground(new Color(255,255,255));
		
		menu_playlist_up = new MenuItem();
		menu_playlist_up.setText("Move up");
		menu_playlist_up.addActionListener(actionListener);
		menu_playlist_up.setBackground(new Color(50,50,50));
		menu_playlist_up.setForeground(new Color(255,255,255));
		
		menu_playlist_down = new MenuItem();
		menu_playlist_down.setText("Move down");
		menu_playlist_down.addActionListener(actionListener);
		menu_playlist_down.setBackground(new Color(50,50,50));
		menu_playlist_down.setForeground(new Color(255,255,255));
		
		menu_playlist = new Menu();
		menu_playlist.setText("Playlist");
		menu_playlist.setBackground(new Color(50,50,50));
		menu_playlist.setForeground(new Color(255,255,255));
		menu_playlist.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_playlist.add(menu_playlist_add);
		menu_playlist.add(menu_playlist_remove);
		menu_playlist.add(menu_playlist_clear);
		menu_playlist.add(separator);
		menu_playlist.add(menu_playlist_up);
		menu_playlist.add(menu_playlist_down);
		menu_playlist.getPopupMenu().setBackground(new Color(50,50,50));
		menu_playlist.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_graph_merge = new MenuItem();
		menu_graph_merge.setText("Toggle merged graphs");
		menu_graph_merge.addActionListener(actionListener);
		menu_graph_merge.setBackground(new Color(50,50,50));
		menu_graph_merge.setForeground(new Color(255,255,255));
		
		menu_graph_gfilter = new MenuItem();
		menu_graph_gfilter.setText("Toggle Gaussian filter");
		menu_graph_gfilter.addActionListener(actionListener);
		menu_graph_gfilter.setBackground(new Color(50,50,50));
		menu_graph_gfilter.setForeground(new Color(255,255,255));
		
		menu_graph = new Menu();
		menu_graph.setText("Graphs");
		menu_graph.setBackground(new Color(50,50,50));
		menu_graph.setForeground(new Color(255,255,255));
		menu_graph.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_graph.add(menu_graph_merge);
		menu_graph.add(menu_graph_gfilter);
		menu_graph.getPopupMenu().setBackground(new Color(50,50,50));
		menu_graph.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_help_about = new MenuItem();
		menu_help_about.setText("About");
		menu_help_about.addActionListener(actionListener);
		menu_help_about.setBackground(new Color(50,50,50));
		menu_help_about.setForeground(new Color(255,255,255));
		
		menu_help = new Menu();
		menu_help.setText("?");
		menu_help.setBackground(new Color(50,50,50));
		menu_help.setForeground(new Color(255,255,255));
		menu_help.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_help.add(menu_help_about);
		menu_help.getPopupMenu().setBackground(new Color(50,50,50));
		menu_help.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		add(menu_file);
		add(menu_playlist);
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

	public JMenuItem getMenu_graph_merge() {
		return menu_graph_merge;
	}

	public JMenuItem getMenu_graph_gfilter() {
		return menu_graph_gfilter;
	}

}
