package audioplayer.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
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
	private JMenuItem menu_file_add;

	public MenuBar(ActionListener actionListener) {

		menu_file_open = new JMenuItem("Open ..."){
			/**
			 * 
			 */
			private static final long serialVersionUID = -4792554976830903762L;

			@Override
		    protected void paintComponent(Graphics g)
		    {
		        Graphics2D g2d = (Graphics2D) g;
		        g2d.setColor(menu_file_open.getBackground());
		        g2d.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		menu_file_open.addActionListener(actionListener);
		menu_file_open.setBackground(new Color(50,50,50));
		menu_file_open.setForeground(new Color(255,255,255));

		menu_file_add = new JMenuItem("Add ..."){
			/**
			 * 
			 */
			private static final long serialVersionUID = -4792554976830903762L;

			@Override
		    protected void paintComponent(Graphics g)
		    {
		        Graphics2D g2d = (Graphics2D) g;
		        g2d.setColor(menu_file_add.getBackground());
		        g2d.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		menu_file_add.addActionListener(actionListener);
		menu_file_add.setBackground(new Color(50,50,50));
		menu_file_add.setForeground(new Color(255,255,255));
	
		menu_file = new JMenu("Datei"){
			/**
			 * 
			 */
			private static final long serialVersionUID = -4792554976830903762L;

			@Override
		    protected void paintComponent(Graphics g)
		    {
		        Graphics2D g2d = (Graphics2D) g;
		        g2d.setColor(menu_file.getBackground());
		        g2d.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		menu_file.setBackground(new Color(50,50,50));
		menu_file.setForeground(new Color(255,255,255));
		menu_file.setBorder(BorderFactory.createRaisedBevelBorder());
		menu_file.add(menu_file_open);
		menu_file.add(menu_file_add);
		menu_file.getPopupMenu().setBackground(new Color(50,50,50));
		menu_file.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
		
		add(menu_file);
	}

	public JMenu getMenu_file() {
		return menu_file;
	}

	public JMenuItem getMenu_file_open() {
		return menu_file_open;
	}
	
	public JMenuItem getMenu_file_add() {
		return menu_file_add;
	}

}
