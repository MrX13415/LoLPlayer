package audioplayer.gui.components.PlayerControler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SearchCircle;
import javax.swing.SearchCircle.SearchCricleListener;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import audioplayer.font.FontLoader;
import audioplayer.player.analyzer.components.JGraph;


public class PlayerControlInterface extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2926358910478096155L;

	private JGraph playerInterfaceGraph;
        
	private JPanel playerControls;
	private JPanel playerButtons;

	private Display display;

	private JButton play;
	private JButton stop;
	private JButton frw;
	private JButton rev;

	private SearchCircle searchBar;
	private SearchCircle volume;

        
	public PlayerControlInterface(ActionListener actionListener,
			SearchCricleListener searchCricleListener) {

		
		display = new Display();

		play = new JButton("\u25BA\u2759\u2759");
		play.setFont(FontLoader.fontGUIPlayerButtons);
		play.addActionListener(actionListener);
		play.setBackground(new Color(50,50,50));
		play.setContentAreaFilled(false);
		play.setForeground(new Color(255,0,0));
		
		//play.setIcon(new ImageIcon(
		//		"C:/Users/Oliver/Pictures/drawing skills/Play_0.png", "PLAY"));
		//play.setBorderPainted(false);
		//play.setPressedIcon(new ImageIcon(
		//		"C:/Users/Oliver/Pictures/drawing skills/Play_1.png", "PLAY"));

		stop = new JButton("\u25FC");
		stop.setFont(FontLoader.fontGUIPlayerButtons);
		stop.addActionListener(actionListener);
		stop.setBackground(new Color(50,50,50));
		stop.setContentAreaFilled(false);
		stop.setForeground(new Color(255,0,0));
		
		frw = new JButton("\u23ed");
		frw.setFont(FontLoader.fontGUIPlayerButtons);
		frw.addActionListener(actionListener);
		frw.setBackground(new Color(50,50,50));
		frw.setContentAreaFilled(false);
		frw.setForeground(new Color(255,0,0));
		
		rev = new JButton("\u23ee");
		rev.setFont(FontLoader.fontGUIPlayerButtons);
		rev.addActionListener(actionListener);
		rev.setBackground(new Color(50,50,50));
		rev.setContentAreaFilled(false);
		rev.setForeground(new Color(255,0,0));
		
		searchBar = new SearchCircle();
		searchBar.setName("searchBar");
		searchBar.setKeyScrollamount(1000);
		searchBar.addSearchCricleListener(searchCricleListener);
		searchBar.addActionListener(actionListener);
		searchBar.setLayout(null);
		searchBar.setFocusPainted(false);
		searchBar.setOpaque(false);
		
		volume = new SearchCircle();
		volume.setName("volume");
		volume.setBarThickness(10);
		volume.setDirection(SearchCircle.BAR_DIRECTION_LEFT);
		volume.setStartAngle(270 - 45);
		volume.setViewAngle(90);
		volume.setMinimum(0d);
		volume.setMaximum(100d);
		volume.addSearchCricleListener(searchCricleListener);
		volume.addActionListener(actionListener);
		volume.setButtonValue(25);
		volume.setBarValue(25);
		volume.setMaximumSize(new Dimension(100, 100));
		volume.addParentMouseListener(searchBar);
		volume.setLayout(null);
		volume.setKeyScrollamount(0.1d);
		volume.setFocusPainted(false);
		volume.setOpaque(false);
		
//		ImageModifier im = new ImageModifier(volume.getBarImage());
//		im.setHue(0.5f);
//		volume.setBarImage(im.modify());
		
		searchBar.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = volume;
				int h = searchBar.getHeight() - 80;
				int w = h;// searchBar.getWidth();
				int x = (searchBar.getWidth() - w) / 2;
				int y = (searchBar.getHeight() - h) / 2;

				Dimension d = new Dimension(w, h);

				c.setSize(d);
				c.setPreferredSize(d);
				c.setLocation(x, y);
			}
		});

		searchBar.add(volume);

		playerButtons = new JPanel();
		playerButtons.setBackground(new Color(50,50,50));
		playerButtons.setOpaque(true);
		playerButtons.setLayout(new GridLayout(1, 4));
		playerButtons.setBorder(BorderFactory.createLoweredBevelBorder());
		playerButtons.add(rev);
		playerButtons.add(play);
		playerButtons.add(stop);
		playerButtons.add(frw);

		playerControls = new JPanel();
		playerControls.setOpaque(true);
		playerControls.setBorder(BorderFactory.createRaisedBevelBorder());
		playerControls.setLayout(new GridLayout(2, 1));
		playerControls.add(display);
		playerControls.add(playerButtons);
		playerControls.setBackground(new Color(50,50,50));

		
		volume.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = playerControls;
				int h = volume.getHeight();
				int w = (int) Math.round((h / 100d) * 80);// searchBar.getWidth();
				h = (int) Math.round((w / 100d) * 50); // (h/100d) * 50);
				int x = (volume.getWidth() - w) / 2;
				int y = (int) Math.round((volume.getHeight() / 100d) * 25);

				/*
				 * int h = volume.getHeight(); int w = (int) Math.round((h/100d)
				 * * 50);//searchBar.getWidth(); h = (int) Math.round((w/100d) *
				 * 100); //(h/100d) * 50); int x = (volume.getWidth() - w) / 2;
				 * int y = (volume.getHeight() - h) / 2;
				 */

				Dimension d = new Dimension(w, h);

				c.setSize(d);
				c.setPreferredSize(d);
				c.setLocation(x, y);
			}
		});

		volume.add(playerControls);

		playerInterfaceGraph = new JGraph();
        playerInterfaceGraph.setGaussianFilter(false);
		playerInterfaceGraph.setOpaque(false);
		playerInterfaceGraph.setLayout(new GridLayout(0, 1, 5, 5));
		playerInterfaceGraph.add(searchBar);

		this.setOpaque(false);
        this.setLayout(new BorderLayout());
		this.add(playerInterfaceGraph, BorderLayout.CENTER);
                
        this.setPreferredSize(new Dimension(400, 400));
	}

	public JGraph getPlayerInterfaceGraph() {
		return playerInterfaceGraph;
	}

	public JPanel getPlayerControls() {
		return playerControls;
	}

	public JButton getPlay() {
		return play;
	}

	public JButton getStop() {
		return stop;
	}

	public JButton getFrw() {
		return frw;
	}

	public JButton getRev() {
		return rev;
	}

	public SearchCircle getSearchBar() {
		return searchBar;
	}

	public SearchCircle getVolume() {
		return volume;
	}

	public Display getDisplay() {
		return display;
	}

}
