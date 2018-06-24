package net.icelane.amplifire.ui.components.PlayerControler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.render.GraphRender;
import net.icelane.amplifire.analyzer.render.RenderComponent;
import net.icelane.amplifire.analyzer.render.jgraph.JGraph;
import net.icelane.amplifire.analyzer.render.opengl.GL11Graph;
import net.icelane.amplifire.analyzer.render.opengl.GL45Graph;
import net.icelane.amplifire.font.FontLoader;
import net.icelane.amplifire.images.ImageLoader;
import net.icelane.amplifire.player.codec.AudioProcessingLayer;
import net.icelane.searchcircle.event.SearchCircleListener;
import net.mrx13415.searchcircle.imageutil.ImageModifier;
import net.mrx13415.searchcircle.imageutil.color.HSB;
import net.mrx13415.searchcircle.swing.JSearchCircle;
import net.mrx13415.searchcircle.swing.JSearchCircle.Anchor;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlayerControlInterface extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2926358910478096155L;

	private RenderComponent renderComponent;
        
	private JPanel playerControls;
	private JPanel playerButtons;

	private Display display;

	private JButton play;
	private JButton stop;
	private JButton frw;
	private JButton rev;

	private JSearchCircle searchBar;
	private JSearchCircle volume;

	private JSlider graphdetail;
	private JSlider heightlevel;
	private JSlider zoomlevel;

	private ImageIcon imgPlay = ImageLoader.image_play;
	private ImageIcon imgPause = ImageLoader.image_pause;
	private ImageIcon imgStop = ImageLoader.image_stop;
	private ImageIcon imgFrw = ImageLoader.image_frw;
	private ImageIcon imgRev = ImageLoader.image_rev;
	
	private ImageIcon imgPlayHover = ImageLoader.setHoverImgHSB(imgPlay);
	private ImageIcon imgPauseHover = ImageLoader.setHoverImgHSB(imgPause);
	private ImageIcon imgStopHover = ImageLoader.setHoverImgHSB(imgStop);
	private ImageIcon imgFrwHover = ImageLoader.setHoverImgHSB(imgFrw);
	private ImageIcon imgRevHover = ImageLoader.setHoverImgHSB(imgRev);
	
	private ImageIcon imgPlayPressedHover = ImageLoader.setPressedHoverImgHSB(imgPlay);
	private ImageIcon imgPausePressedHover = ImageLoader.setPressedHoverImgHSB(imgPause);
	private ImageIcon imgStopPressedHover = ImageLoader.setPressedHoverImgHSB(imgStop);
	private ImageIcon imgFrwPressedHover = ImageLoader.setPressedHoverImgHSB(imgFrw);
	private ImageIcon imgRevPressedHover = ImageLoader.setPressedHoverImgHSB(imgRev);
	
	
	public PlayerControlInterface(ActionListener actionListener,
			SearchCircleListener searchCircleListener, ChangeListener changeListener) {
		
            display = new Display();
            display.setOpaque(false);
            
            play = new JButton(""); // >  \u25BA  ||  \u2759\u2759
            play.setFont(FontLoader.fontSymbola);
            play.addActionListener(actionListener);
            play.setBackground(new Color(50,50,50));
            play.setContentAreaFilled(false);
            play.setForeground(new Color(255,0,0));
            play.setIcon(imgPlay);
            play.setPressedIcon(imgPlayPressedHover);            
            play.setRolloverIcon(imgPlayHover);
            
            stop = new JButton("");	// [ ]  \u25FC
            stop.setFont(FontLoader.fontSymbola);
            stop.addActionListener(actionListener);
            stop.setBackground(new Color(50,50,50));
            stop.setContentAreaFilled(false);
            stop.setForeground(new Color(255,0,0));
            stop.setIcon(imgStop);
            stop.setPressedIcon(imgStopPressedHover);            
            stop.setRolloverIcon(imgStopHover);
            
            frw = new JButton(""); // >>|  \u23ed 
            frw.setFont(FontLoader.fontSymbola);
            frw.addActionListener(actionListener);
            frw.setBackground(new Color(50,50,50));
            frw.setContentAreaFilled(false);
            frw.setForeground(new Color(255,0,0));
            frw.setIcon(imgFrw);
            frw.setPressedIcon(imgFrwPressedHover);            
            frw.setRolloverIcon(imgFrwHover);
            
            rev = new JButton(""); // |<<  \u23ee
            rev.setFont(FontLoader.fontSymbola);
            rev.addActionListener(actionListener);
            rev.setBackground(new Color(50,50,50));
            rev.setContentAreaFilled(false);
            rev.setForeground(new Color(255,0,0));
            rev.setIcon(imgRev);
            rev.setPressedIcon(imgRevPressedHover);            
            rev.setRolloverIcon(imgRevHover);
            
            playerButtons = new JPanel();
            playerButtons.setOpaque(false);
            playerButtons.setLayout(new GridLayout(1, 4));
            //playerButtons.setBorder(BorderFactory.createLoweredBevelBorder());
            playerButtons.add(rev);
            playerButtons.add(play);
            playerButtons.add(stop);
            playerButtons.add(frw);

            playerControls = new JPanel(){
            	@Override
                protected void paintComponent(Graphics g) {
            		//TODO
                    super.paintComponent(g);
                   // Application.drawReflectionEffect(this, g, 0.3f);
                }
            };
            playerControls.setOpaque(true);
            //playerControls.setBorder(BorderFactory.createRaisedBevelBorder());
            playerControls.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
            playerControls.setLayout(new GridLayout(2, 1));
            playerControls.add(display);
            playerControls.add(playerButtons);
            playerControls.setBackground(Application.getColors().color_display_background);
            playerControls.setOpaque(true);
                        
            volume = new JSearchCircle();
            volume.setName("volume");
            volume.setBarThickness(10);
            volume.setDirection(JSearchCircle.BAR_DIRECTION_LEFT);
            volume.setStartAngle(270 - 45);
            volume.setViewAngle(90);
            volume.setMinimum(0f);
            volume.setMaximum(100f);
            volume.addSearchCircleListener(searchCircleListener);
            volume.addActionListener(actionListener);
            volume.setButtonValue(25);
            volume.setBarValue(25);
            volume.setBackgroundHSB(new HSB(0.f, -1.f, -0.16f));
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
            volume.setKeyScrollAmount(0.1d);
            volume.setFocusPainted(false);
            volume.setOpaque(false);
            volume.setBarHSB(Application.getColors().color_controls_volume_bar);
            volume.add(playerControls);

//		ImageModifier im = new ImageModifier(volume.getBarImage());
//		im.setHue(0.5f);
//		volume.setBarImage(im.modify());

            searchBar = new JSearchCircle();
            searchBar.setName("searchBar");
            searchBar.setKeyScrollAmount(100);
            searchBar.addSearchCircleListener(searchCircleListener);
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
                                    
                                    if (searchBar.getAnchor() == Anchor.LEFT)
                                    	x = (oH - w) / 2;
                                    
                                    if (searchBar.getAnchor() == Anchor.RIGHT)
                                    	x = (oH - w) / 2 + (oW - oH);
                                    
                                    int y = (oH - h) / 2;		//center vertically

                                    c.setBounds(x, y, w, h);
                            }
                    }
            });
            searchBar.setFocusPainted(false);
            searchBar.setOpaque(false);
            searchBar.setAnchor(Anchor.LEFT);
            searchBar.setBarHSB(Application.getColors().color_controls_searchbar_bar);
            searchBar.setBackgroundHSB(new HSB(0.f, -1.f, -0.16f));
            searchBar.add(volume);

            //register searchBar as mouse event source from volume 
            volume.addParentMouseListener(searchBar);

            renderComponent = new RenderComponent(new GL45Graph());
//            renderComponent.getRenderer().setBlurFilter(true);
//            renderComponent.getRenderer().setGlowEffect(true);
            renderComponent.setOpaque(false);
            renderComponent.add(searchBar);
            renderComponent.setBackground(Application.getColors().color_background1);
            
            graphdetail = new JSlider(1, 250); 
            graphdetail.setPreferredSize(new Dimension(16, 0));
            graphdetail.setOpaque(false);
            graphdetail.setOrientation(JSlider.VERTICAL);
            graphdetail.setValue(1);
            graphdetail.addChangeListener(changeListener);

            heightlevel = new JSlider(0, 10000); 
            heightlevel.setPreferredSize(new Dimension(16, 0));
            heightlevel.setOpaque(false);
            heightlevel.setOrientation(JSlider.VERTICAL);
            heightlevel.setValue(1000);
            heightlevel.addChangeListener(changeListener);
            
            zoomlevel = new JSlider(1, 500); 
            zoomlevel.setPreferredSize(new Dimension(16, 0));
            zoomlevel.setOpaque(false);
            zoomlevel.setOrientation(JSlider.VERTICAL);
            zoomlevel.setValue(1);
            zoomlevel.addChangeListener(changeListener);
            
            JPanel sliderPanel = new JPanel(new BorderLayout());
            sliderPanel.add(zoomlevel, BorderLayout.WEST);
            sliderPanel.add(heightlevel, BorderLayout.CENTER);
            sliderPanel.add(graphdetail, BorderLayout.EAST);
            sliderPanel.setOpaque(false);
	
            this.setLayout(new BorderLayout());
            this.add(renderComponent, BorderLayout.CENTER);
            this.add(sliderPanel, BorderLayout.WEST);
            this.setPreferredSize(new Dimension(400, 400));
            this.setBackground(new Color(30, 30, 30));
            this.setOpaque(true);
            
    		//TODO: to method

    		setPlayHSB(Application.getColors().color_controls_play);
    		setPauseHSB(Application.getColors().color_controls_pause);
    		setStopHSB(Application.getColors().color_controls_stop);
    		setFrwHSB(Application.getColors().color_controls_frw);
    		setRevHSB(Application.getColors().color_controls_rev);
    		getSearchBar().setButtonHSB(Application.getColors().color_controls_searchbar_button);
    		getVolume().setButtonHSB(Application.getColors().color_controls_searchbar_button);
    		getSearchBar().setBarHSB(Application.getColors().color_controls_searchbar_bar);
    		getVolume().setBarHSB(Application.getColors().color_controls_volume_bar);
    		
	}
	
	@Override
    protected void paintComponent(Graphics g) {
		//TODO
        super.paintComponent(g);
        Application.drawReflectionEffect(this, g, 0.3f);
    }

	public void setPlayPause(boolean isPlaying) {
		if (isPlaying){
//			 play.setText("\u2759\u2759");
			 play.setIcon(imgPause);
	         play.setPressedIcon(imgPausePressedHover);            
	         play.setRolloverIcon(imgPauseHover);
		}else{
//			 play.setText("\u25BA");
	         play.setIcon(imgPlay);
	         play.setPressedIcon(imgPlayPressedHover);            
	         play.setRolloverIcon(imgPlayHover);
		}
	}
	
	
	
	public void setPlayHSB(HSB hsb){

		ImageModifier im = new ImageModifier(ImageLoader.image_play.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		ImageIcon ni = new ImageIcon(im.modify());
		if (play.getIcon().equals(imgPlay)){
			imgPlay = ni;
			imgPlayPressedHover = ImageLoader.setPressedHoverImgHSB(imgPlay);
			imgPlayHover = ImageLoader.setHoverImgHSB(imgPlay);
			play.setIcon(imgPlay);
            play.setPressedIcon(imgPlayPressedHover);            
            play.setRolloverIcon(imgPlayHover);
		}else{
			imgPlay = ni;
			imgPlayPressedHover = ImageLoader.setPressedHoverImgHSB(imgPlay);
			imgPlayHover = ImageLoader.setHoverImgHSB(imgPlay);
		}
		
		repaint();
	}

	public void setPauseHSB(HSB hsb){

		ImageModifier im = new ImageModifier(ImageLoader.image_pause.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		ImageIcon ni = new ImageIcon(im.modify());
		if (play.getIcon().equals(imgPause)){
			imgPause = ni;
			imgPausePressedHover = ImageLoader.setPressedHoverImgHSB(imgPause);
			imgPauseHover = ImageLoader.setHoverImgHSB(imgPause);
			
			play.setIcon(imgPause);
            play.setPressedIcon(imgPausePressedHover);            
            play.setRolloverIcon(imgPauseHover);
		}else{
			imgPause = ni;
			imgPausePressedHover = ImageLoader.setPressedHoverImgHSB(imgPause);
			imgPauseHover = ImageLoader.setHoverImgHSB(imgPause);
		}
	
		repaint();
	}
	
	public void setStopHSB(HSB hsb){

		ImageModifier im = new ImageModifier(ImageLoader.image_stop.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		imgStop = new ImageIcon(im.modify());
		imgStopPressedHover = ImageLoader.setPressedHoverImgHSB(imgStop);
		imgStopHover = ImageLoader.setHoverImgHSB(imgStop);
		
		stop.setIcon(imgStop);
		stop.setPressedIcon(imgStopPressedHover);            
        stop.setRolloverIcon(imgStopHover);

		repaint();
	}
	
	public void setFrwHSB(HSB hsb){

		ImageModifier im = new ImageModifier(ImageLoader.image_frw.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		imgFrw = new ImageIcon(im.modify());
		imgFrwPressedHover = ImageLoader.setPressedHoverImgHSB(imgFrw);
		imgFrwHover = ImageLoader.setHoverImgHSB(imgFrw);
		
		frw.setIcon(imgFrw);
        frw.setPressedIcon(imgFrwPressedHover);            
        frw.setRolloverIcon(imgFrwHover);

		repaint();
	}
	
	public void setRevHSB(HSB hsb){

		ImageModifier im = new ImageModifier(ImageLoader.image_rev.getImage());
		
		im.setHue(hsb.getHue());
		im.setSaturation(hsb.getSaturation());
		im.setBrightness(hsb.getBrightness());

		imgRev = new ImageIcon(im.modify());
		imgRevPressedHover = ImageLoader.setPressedHoverImgHSB(imgRev);
		imgRevHover = ImageLoader.setHoverImgHSB(imgRev);
				
		rev.setIcon(imgRev);
        rev.setPressedIcon(imgRevPressedHover);            
        rev.setRolloverIcon(imgRevHover);
        
		repaint();
	}
	
	public void setDisplay(AudioProcessingLayer ppl) {
		
		long time = ppl.getTimePosition();
		long lenght = ppl.getStreamLength();
		double posperc = Math.round(100d / (double) lenght * (double) time * 10d) / 10d;
		double volume = Math.round(ppl.getVolume() * 130d) / 130d;
		
		String state = String.format("%s", ppl.getState());

		String volPc = String.format("%6s", String.format("%5.1f%% ", volume, ppl.getAudioDevice().getVolume()));
		String volDb = String.format("%6s", String.format("%5.3f dB", ppl.getAudioDevice().getVolume()));
		String pperc = String.format("%6s", String.format("%5.1f%%", posperc));

		Display d = getDisplay();

		d.setTimeText(ppl.getTimePosition());
		d.setInfo1Text(state);
		d.setInfo2Text(volPc);
		d.setInfo3Text(volDb);
		d.setStatusBar1Text(pperc);
		if (ppl.getAudioFile() != null)d.setStatusBar2Text(ppl.getAudioFile().getName());

	}
	
	public RenderComponent getRenderComponent() {
		return renderComponent;
	}
	
	public GraphRender getGraphRenderer() {
		return renderComponent.getRenderer();
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

	public JSearchCircle getSearchBar() {
		return searchBar;
	}

	public JSearchCircle getVolume() {
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

	public JSlider getZoomlevel() {
		return zoomlevel;
	}
	
}
