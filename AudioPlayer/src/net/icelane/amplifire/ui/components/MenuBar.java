package net.icelane.amplifire.ui.components;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.render.GraphRender.DisplayMode;
import net.icelane.amplifire.analyzer.render.GraphRender.DrawMode;
import net.icelane.amplifire.analyzer.render.opengl.GL11Graph;
import net.icelane.amplifire.design.Colors;

import javax.swing.JMenuItem;


/**
 *  amplifier - Audio-Player Project
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
	
	private JMenu menu_desing;
	private JMenuItem menu_desing_color;
	
	private JMenu menu_graph;
	private JMenuItem menu_graph_analyzerSettings;
	private JMenuItem menu_graph_enabled;
	private JMenuItem menu_graph_fps;
	private JMenuItem menu_graph_merge;
	private JMenuItem menu_graph_displaymode;
	private JMenuItem menu_graph_bfilter;
	private JMenuItem menu_graph_geffect;
	private JMenuItem menu_graph_drawmode;
	
	private JMenu menu_help;
	private JMenuItem menu_help_about;
	private JMenuItem menu_help_console;
	
	public MenuBar(ActionListener actionListener) {
		JSeparator separator = new JSeparator();

		separator.setBackground(Application.getColors().color_menu_background1);

							
		menu_file_open = new MenuItem();
		menu_file_open.setText("Open ...");
		menu_file_open.addActionListener(actionListener);

		menu_file_open.setBackground(Application.getColors().color_menu_background1);
		menu_file_open.setForeground(Application.getColors().color_menu_forground1);

		
		menu_file_open.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_O, 
		        java.awt.Event.CTRL_MASK));
		
		menu_file_opendir = new MenuItem();
		menu_file_opendir.setText("Open Directory ...");
		menu_file_opendir.addActionListener(actionListener);

		menu_file_opendir.setBackground(Application.getColors().color_menu_background1);
		menu_file_opendir.setForeground(Application.getColors().color_menu_forground1);

		
		menu_file_exit = new MenuItem();
		menu_file_exit.setText("Exit");
		menu_file_exit.addActionListener(actionListener);

		menu_file_exit.setBackground(Application.getColors().color_menu_background1);
		menu_file_exit.setForeground(Application.getColors().color_menu_forground1);


		menu_file = new Menu();
		menu_file.setText("File");

		menu_file.setBackground(Application.getColors().color_menu_background1);
		menu_file.setForeground(Application.getColors().color_menu_forground1);

		menu_file.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_file.add(menu_file_open);
		menu_file.add(menu_file_opendir);
		menu_file.add(separator);
		menu_file.add(menu_file_exit);

		menu_file.getPopupMenu().setBackground(Application.getColors().color_menu_background1);

		menu_file.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		menu_playlist_add = new MenuItem();
		menu_playlist_add.setText("Add ...");
		menu_playlist_add.addActionListener(actionListener);

		menu_playlist_add.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_add.setForeground(Application.getColors().color_menu_forground1);

		menu_playlist_add.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_O, 
		        java.awt.Event.ALT_MASK + java.awt.Event.CTRL_MASK));
		
		menu_playlist_adddir = new MenuItem();
		menu_playlist_adddir.setText("Add Directory ...");
		menu_playlist_adddir.addActionListener(actionListener);

		menu_playlist_adddir.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_adddir.setForeground(Application.getColors().color_menu_forground1);

		menu_playlist_remove = new MenuItem();
		menu_playlist_remove.setText("Remove");
		menu_playlist_remove.addActionListener(actionListener);

		menu_playlist_remove.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_remove.setForeground(Application.getColors().color_menu_forground1);

		menu_playlist_remove.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_DELETE, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_clear = new MenuItem();
		menu_playlist_clear.setText("Clear");
		menu_playlist_clear.addActionListener(actionListener);

		menu_playlist_clear.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_clear.setForeground(Application.getColors().color_menu_forground1);

				
		menu_playlist_up = new MenuItem();
		menu_playlist_up.setText("Move up");
		menu_playlist_up.addActionListener(actionListener);

		menu_playlist_up.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_up.setForeground(Application.getColors().color_menu_forground1);

		menu_playlist_up.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_UP, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_down = new MenuItem();
		menu_playlist_down.setText("Move down");
		menu_playlist_down.addActionListener(actionListener);

		menu_playlist_down.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_down.setForeground(Application.getColors().color_menu_forground1);

		menu_playlist_down.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_DOWN, 
		        java.awt.Event.ALT_MASK));
		
		menu_playlist_shuffle = new MenuItem();
		menu_playlist_shuffle.setText("Enable shufflemode");
		menu_playlist_shuffle.addActionListener(actionListener);

		menu_playlist_shuffle.setBackground(Application.getColors().color_menu_background1);
		menu_playlist_shuffle.setForeground(Application.getColors().color_menu_forground1);

		//menu_playlist_shuffle.setAccelerator(KeyStroke.getKeyStroke(
		//        java.awt.event.KeyEvent.VK_DOWN, 
		//        java.awt.Event.ALT_MASK));
		
		menu_playlist = new Menu();
		menu_playlist.setText("Playlist");

		menu_playlist.setBackground(Application.getColors().color_menu_background1);
		menu_playlist.setForeground(Application.getColors().color_menu_forground1);

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

		menu_playlist.getPopupMenu().setBackground(Application.getColors().color_menu_background1);
		menu_playlist.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
//		menu_media_library = new MenuItem();
//		menu_media_library.setText("Library");
//		menu_media_library.addActionListener(actionListener);
//		menu_media_library.setBackground(Application.getColors().color_menu_background1);
//		menu_media_library.setForeground(Application.getColors().color_menu_forground1);
//		
//		menu_media = new Menu();
//		menu_media.setText("Media");
//		menu_media.setBackground(Application.getColors().color_menu_background1);
//		menu_media.setForeground(Application.getColors().color_menu_forground1);
//		menu_media.getPopupMenu().setBackground(Application.getColors().color_menu_background1);
//		menu_media.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
//		
		menu_desing_color = new MenuItem();
		menu_desing_color .setText("Color");
		menu_desing_color .addActionListener(actionListener);
		menu_desing_color.setBackground(Application.getColors().color_menu_background1);
		menu_desing_color.setForeground(Application.getColors().color_menu_forground1);

		menu_desing = new Menu();
		menu_desing.setText("Design");
		menu_desing.setBackground(Application.getColors().color_menu_background1);
		menu_desing.setForeground(Application.getColors().color_menu_forground1);
		menu_desing.getPopupMenu().setBackground(Application.getColors().color_menu_background1);
		menu_desing.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		menu_desing.add(menu_desing_color);

		menu_graph_analyzerSettings = new MenuItem();
		menu_graph_analyzerSettings.setText("Analyzer settings ...");
		menu_graph_analyzerSettings.addActionListener(actionListener);
		menu_graph_analyzerSettings.setBackground(Application.getColors().color_menu_background1);
		menu_graph_analyzerSettings.setForeground(Application.getColors().color_menu_forground1);

		menu_graph_enabled = new MenuItem();
		menu_graph_enabled.setText("Enable/Disable graph");
		menu_graph_enabled.addActionListener(actionListener);
		menu_graph_enabled.setBackground(Application.getColors().color_menu_background1);
		menu_graph_enabled.setForeground(Application.getColors().color_menu_forground1);

		menu_graph_fps = new MenuItem();
		menu_graph_fps.setText("Toggle FPS");
		menu_graph_fps.addActionListener(actionListener);
		menu_graph_fps.setBackground(Application.getColors().color_menu_background1);
		menu_graph_fps.setForeground(Application.getColors().color_menu_forground1);

		menu_graph_merge = new MenuItem();
		menu_graph_merge.setText("Toggle merged graphs");
		menu_graph_merge.addActionListener(actionListener);
		menu_graph_merge.setBackground(Application.getColors().color_menu_background1);
		menu_graph_merge.setForeground(Application.getColors().color_menu_forground1);

		menu_graph_displaymode = new Menu();
		menu_graph_displaymode.setText("Display mode");
		menu_graph_displaymode.addActionListener(actionListener);
		menu_graph_displaymode.setBackground(Application.getColors().color_menu_background1);
		menu_graph_displaymode.setForeground(Application.getColors().color_menu_forground1);

		for (DisplayMode mode : DisplayMode.values()) {
			MenuItem menu_graph_dmode_mode = new MenuItem();
			menu_graph_dmode_mode.setText(mode.toString());
			menu_graph_dmode_mode.addActionListener(actionListener);

			menu_graph_dmode_mode.setBackground(Application.getColors().color_menu_background1);
			menu_graph_dmode_mode.setForeground(Application.getColors().color_menu_forground1);

			menu_graph_dmode_mode.setActionCommand("SetGraphDisplayMode:" + mode.name()); 
			menu_graph_displaymode.add(menu_graph_dmode_mode);
		}
		
		
		menu_graph_bfilter = new MenuItem();
		menu_graph_bfilter.setText("Toggle blur filter");
		menu_graph_bfilter.addActionListener(actionListener);
		menu_graph_bfilter.setBackground(Application.getColors().color_menu_background1);
		menu_graph_bfilter.setForeground(Application.getColors().color_menu_forground1);

		menu_graph_geffect = new MenuItem();
		menu_graph_geffect.setText("Toggle glow effect");
		menu_graph_geffect.addActionListener(actionListener);
		menu_graph_geffect.setBackground(Application.getColors().color_menu_background1);
		menu_graph_geffect.setForeground(Application.getColors().color_menu_forground1);
		
		menu_graph_drawmode = new Menu();
		menu_graph_drawmode.setText("Drawing mode");
		menu_graph_drawmode.addActionListener(actionListener);
		menu_graph_drawmode.setBackground(Application.getColors().color_menu_background1);
		menu_graph_drawmode.setForeground(Application.getColors().color_menu_forground1);

		for (DrawMode mode : DrawMode.values()) {
			MenuItem menu_graph_dmode_mode = new MenuItem();
			menu_graph_dmode_mode.setText(mode.toString());
			menu_graph_dmode_mode.addActionListener(actionListener);

			menu_graph_dmode_mode.setBackground(Application.getColors().color_menu_background1);
			menu_graph_dmode_mode.setForeground(Application.getColors().color_menu_forground1);

			menu_graph_dmode_mode.setActionCommand("SetGraphDrawMode:" + mode.name()); 
			menu_graph_drawmode.add(menu_graph_dmode_mode);
		}
		
		menu_graph = new Menu();
		menu_graph.setText("Graphs");

		menu_graph.setBackground(Application.getColors().color_menu_background1);
		menu_graph.setForeground(Application.getColors().color_menu_forground1);

		menu_graph.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_graph.add(menu_graph_analyzerSettings);
		menu_graph.add(separator);
		menu_graph.add(menu_graph_enabled);
		menu_graph.add(menu_graph_fps);
		menu_graph.add(separator);
		menu_graph.add(menu_graph_merge);
		menu_graph.add(menu_graph_displaymode);
		menu_graph.add(separator);
		menu_graph.add(menu_graph_bfilter);
		menu_graph.add(menu_graph_geffect);
		menu_graph.add(separator);
		menu_graph.add(menu_graph_drawmode);

		menu_graph.getPopupMenu().setBackground(Application.getColors().color_menu_background1);
		menu_graph.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());

		menu_help_console = new MenuItem();
		menu_help_console.setText("View Console");
		menu_help_console.addActionListener(actionListener);
		menu_help_console.setBackground(Application.getColors().color_menu_background1);
		menu_help_console.setForeground(Application.getColors().color_menu_forground1);
		
		menu_help_about = new MenuItem();
		menu_help_about.setText("About");
		menu_help_about.addActionListener(actionListener);
		menu_help_about.setBackground(Application.getColors().color_menu_background1);
		menu_help_about.setForeground(Application.getColors().color_menu_forground1);

		menu_help = new Menu();
		menu_help.setText("?");
		menu_help.setBackground(Application.getColors().color_menu_background1);
		menu_help.setForeground(Application.getColors().color_menu_forground1);
		menu_help.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_help.add(menu_help_console);
		menu_help.add(menu_help_about);

		menu_help.getPopupMenu().setBackground(Application.getColors().color_menu_background1);
		menu_help.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		add(menu_file);
		add(menu_playlist);
//		add(menu_media);
		add(menu_desing);
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

	public JMenuItem getMenu_graph_analyzerSettings() {
		return menu_graph_analyzerSettings;
	}

	public JMenuItem getMenu_graph_enabled() {
		return menu_graph_enabled;
	}

	public JMenuItem getMenu_graph_fps() {
		return menu_graph_fps;
	}

	public JMenuItem getMenu_graph_merge() {
		return menu_graph_merge;
	}

	public JMenuItem getMenu_graph_displaymode() {
		return menu_graph_displaymode;
	}
	
	public JMenuItem getMenu_graph_bfilter() {
		return menu_graph_bfilter;
	}
	
	public JMenuItem getMenu_graph_geffect() {
		return menu_graph_geffect;
	}

	public JMenuItem getMenu_help_console() {
		return menu_help_console;
	}

	public JMenuItem getMenu_graph_drawmode() {
		return menu_graph_drawmode;
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

	public JMenu getMenu_desing() {
		return menu_desing;
	}

	public JMenuItem getMenu_desing_color() {
		return menu_desing_color;
	}

}
