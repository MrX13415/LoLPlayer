package audioplayer.gui.components.PlayerControler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SearchCircle;
import javax.swing.SearchCircle.SearchCricleListener;
import javax.swing.event.ChangeListener;

import audioplayer.font.FontLoader;
import audioplayer.gui.components.StatusBar;
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

	private JSlider graphdetail;
	private JSlider heightlevel;
		
	
	public PlayerControlInterface(ActionListener actionListener,
			SearchCricleListener searchCricleListener, ChangeListener changeListener) {

		
            display = new Display();

            play = new JButton("\u25BA\u2759\u2759");
            play.setFont(FontLoader.fontGUIPlayerButtons);
            play.addActionListener(actionListener);
            play.setBackground(new Color(50,50,50));
            play.setContentAreaFilled(false);
            play.setForeground(new Color(255,0,0));

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
            volume.setLayout(new LayoutManager() {

                    @Override
                    public void addLayoutComponent(String s, Component p) {}

                    @Override
                    public void removeLayoutComponent(Component p) {}

                    @Override
                    public Dimension preferredLayoutSize(Container p) {
                            return p.getSize();
                    }

                    @Override
                    public Dimension minimumLayoutSize(Container p) {
                            return new Dimension(50, 50);
                    }

                    @Override
                    public void layoutContainer(Container p) {
                            for (int i = 0; i < p.getComponentCount(); i++) {
                                    Component c = p.getComponent(i);

                                    int oH = p.getSize().height;
                                    int oW = p.getSize().width;

                                    int h = oH;
                                    int w = (int) Math.round((h / 100d) * 80);	//80% of the parents width	
                                        h = (int) Math.round((w / 100d) * 50);	//50% of the parents height
                                    int x = (oW - w) / 2;						//center horizontally
                                    int y = (oH - h) / 2;						//center vertically

                                    c.setBounds(x, y, w, h);
                            }
                    }
            });
            volume.setKeyScrollamount(0.1d);
            volume.setFocusPainted(false);
            volume.setOpaque(false);
            volume.add(playerControls);

//		ImageModifier im = new ImageModifier(volume.getBarImage());
//		im.setHue(0.5f);
//		volume.setBarImage(im.modify());

            searchBar = new SearchCircle();
            searchBar.setName("searchBar");
            searchBar.setKeyScrollamount(1000);
            searchBar.addSearchCricleListener(searchCricleListener);
            searchBar.addActionListener(actionListener);
            searchBar.setLayout(new LayoutManager() {
			
                    @Override
                    public void addLayoutComponent(String s, Component p) {}

                    @Override
                    public void removeLayoutComponent(Component p) {}

                    @Override
                    public Dimension preferredLayoutSize(Container p) {
                            return p.getSize();
                    }

                    @Override
                    public Dimension minimumLayoutSize(Container p) {
                            return new Dimension(50, 50);
                    }

                    @Override
                    public void layoutContainer(Container p) {
                            for (int i = 0; i < p.getComponentCount(); i++) {
                                    Component c = p.getComponent(i);

                                    int oH = p.getSize().height;
                                    int oW = p.getSize().width;

                                    oH = (oH > oW ? oW : oH);	//make sure the component is not to big ...

                                    int h = oH - 2 * 40;		//contract 40px from the top and the bottom
                                    int w = h;					//keep it rectangular
                                    int x = (oW - w) / 2;		//center horizontally
                                    int y = (oH - h) / 2;		//center vertically

                                    c.setBounds(x, y, w, h);
                            }
                    }
            });
            searchBar.setFocusPainted(false);
            searchBar.setOpaque(false);
            searchBar.add(volume);

            //register searchBar as mouse event source from volume 
            volume.addParentMouseListener(searchBar);

            playerInterfaceGraph = new JGraph();
            playerInterfaceGraph.setGaussianFilter(false);
            playerInterfaceGraph.setOpaque(false);
            playerInterfaceGraph.setLayout(new GridLayout(0, 1, 5, 5));
            playerInterfaceGraph.add(searchBar);

            graphdetail = new JSlider(0, 1000); 
            graphdetail.setPreferredSize(new Dimension(16, 0));
            graphdetail.setOpaque(false);
            graphdetail.setOrientation(JSlider.VERTICAL);
            graphdetail.setValue(40);
            graphdetail.addChangeListener(changeListener);

            heightlevel = new JSlider(0, 5000); 
            heightlevel.setPreferredSize(new Dimension(16, 0));
            heightlevel.setOpaque(false);
            heightlevel.setOrientation(JSlider.VERTICAL);
            heightlevel.setValue(400);
            heightlevel.addChangeListener(changeListener);

            JPanel sliderPanel = new JPanel(new BorderLayout());
            sliderPanel.add(heightlevel, BorderLayout.WEST);
            sliderPanel.add(graphdetail, BorderLayout.EAST);
            sliderPanel.setOpaque(false);
	
            this.setLayout(new BorderLayout());
            this.add(playerInterfaceGraph, BorderLayout.CENTER);
            this.add(sliderPanel, BorderLayout.WEST);
            this.setPreferredSize(new Dimension(400, 400));
            this.setBackground(new Color(20, 20, 20));
            this.setBorder(BorderFactory.createRaisedBevelBorder());
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

	public JSlider getGraphdetail() {
		return graphdetail;
	}

	public JSlider getHeightlevel() {
		return heightlevel;
	}
	
}
