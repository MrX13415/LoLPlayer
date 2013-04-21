package audioplayer.gui.components;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1418747989989490805L;

	private JMenu menu_file;
	private JMenuItem menu_file_open;

	public MenuBar(ActionListener actionListener) {

		menu_file_open = new JMenuItem("Öffnen ...");
		menu_file_open.addActionListener(actionListener);

		menu_file = new JMenu("Datei");
		menu_file.add(menu_file_open);

		add(menu_file);
	}

	public JMenu getMenu_file() {
		return menu_file;
	}

	public JMenuItem getMenu_file_open() {
		return menu_file_open;
	}

}
