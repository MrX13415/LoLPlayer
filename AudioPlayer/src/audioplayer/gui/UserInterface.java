package audioplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mrx13415.searchcircle.event.SearchCircleChangeEvent;
import net.mrx13415.searchcircle.event.SearchCircleKeyEvent;
import net.mrx13415.searchcircle.event.SearchCircleListener;
import net.mrx13415.searchcircle.event.SearchCircleMouseEvent;
import net.mrx13415.searchcircle.swing.JSearchCircle;
import audioplayer.Application;
import audioplayer.desing.Colors;
import audioplayer.gui.components.MenuBar;
import audioplayer.gui.components.PlayerControler.PlayerControlInterface;
import audioplayer.gui.components.frame.TitleFrameResizeHandler.Direction;
import audioplayer.gui.components.playlist.PlaylistInterface;
import audioplayer.gui.components.playlist.PlaylistToggleArea;
import audioplayer.gui.ui.UIFrame;
import audioplayer.player.analyzer.components.JGraph.DrawMode;
import audioplayer.process.components.StatusBar;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public abstract class UserInterface extends UIFrame implements ActionListener,
															  SearchCircleListener,
															  MouseListener,
															  WindowFocusListener,
															  ChangeListener{

    /**
     * 
     */
    private static final long serialVersionUID = 6407423888068079527L;

    private PlayerControlInterface pci;
    private PlaylistToggleArea pta;
    private PlaylistInterface pli;
        
    private MenuBar menu;
    private StatusBar statusbar;

    private boolean ptaStateExtend = false;
    
	public UserInterface() {

		pci = new PlayerControlInterface(this, this, this);

		pli = new PlaylistInterface(this);

		statusbar = new StatusBar();
		statusbar.setName("Statusbar");

		menu = new MenuBar(this) {
			/**
             * 
             */
			private static final long serialVersionUID = -4792554976830903762L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(menu.getBackground());
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		setStatusPane(statusbar);
		setJMenuBar(menu);
		getContentPanePanel().add(pci, BorderLayout.CENTER);
		getContentPanePanel().setPreferredSize(new Dimension(430, 375));
		
		pta = new PlaylistToggleArea(pli, this);
		pta.setName("PlaylistToggleArea");
		pta.getToggleComponent().setName("PlaylistToggleComponent");
		

		getMainPane().add(pta, BorderLayout.SOUTH);
		
		getResizeHandler().addInputComponent(pta.getToggleComponent(), Direction.W, Direction.S, Direction.E);


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(Application.App_Name_Version);
		this.setLocationRelativeTo(null);
		this.pack();

		// Center the frame window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}

		this.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2 - 100);
		
		this.requestFocusInWindow();
		this.setVisible(true);
		
		defineKeyBindings();
	}
	
	public synchronized void setExtendedState(int state)
	{       
	    if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH)
	    {
	    	ptaStateExtend = pta.isShowPlaylist();
	    	pta.showComponente();
	    	
	        Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());         
	        Rectangle screenSize = getGraphicsConfiguration().getBounds();
	        Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x, 
	                                    screenInsets.top + screenSize.y, 
	                                    screenSize.x + screenSize.width - screenInsets.right - screenInsets.left,
	                                    screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);
	        super.setMaximizedBounds(maxBounds);
	    }else{
	    	if (!ptaStateExtend)
	    		pta.hideComponente();
	    }

	    super.setExtendedState(state);
	}
	        
    private void defineKeyBindings(){
    	InputMap imap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    	
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK), "rev");
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK), "playpause");
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "stop");
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK), "frw");
        
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK), "volup");
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK), "voldown");
        
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "screv");
        imap.put(KeyStroke.getKeyStroke(
		        KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), "scfrw");
        
        this.getRootPane().getActionMap().put("rev", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1594888027786430243L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonRev();
			}
		});
        this.getRootPane().getActionMap().put("playpause", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4610839225638958100L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonPlay();
			}
		});
        this.getRootPane().getActionMap().put("stop", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4192441940275972821L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonStop();
			}
		});
        this.getRootPane().getActionMap().put("frw", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3094115933604274973L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonFrw();
			}
		});
        
        this.getRootPane().getActionMap().put("volup", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -9055591316859895624L;

			@Override
			public void actionPerformed(ActionEvent e) {
				double val = getPlayerControlInterface().getVolume().getButtonValue();
				double ksm = 1;
				getPlayerControlInterface().getVolume().setButtonValue(val + ksm);
				onVolumeButtonMove(getPlayerControlInterface().getVolume());
			}
		});
        
        this.getRootPane().getActionMap().put("voldown", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 68238191871929894L;

			@Override
			public void actionPerformed(ActionEvent e) {
				double val = getPlayerControlInterface().getVolume().getButtonValue();
				double ksm = 1;
				getPlayerControlInterface().getVolume().setButtonValue(val - ksm);
				onVolumeButtonMove(getPlayerControlInterface().getVolume());
			}
		});
        
        this.getRootPane().getActionMap().put("screv", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6427718119568525302L;

			@Override
			public void actionPerformed(ActionEvent e) {
				double val = getPlayerControlInterface().getSearchBar().getButtonValue();
				double ksm = getPlayerControlInterface().getSearchBar().getKeyScrollamount();
				getPlayerControlInterface().getSearchBar().setButtonValue(val - ksm);
				onSearchBarButtonMove(getPlayerControlInterface().getSearchBar());
			}
		});
        
        this.getRootPane().getActionMap().put("scfrw", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3615191271327654371L;

			@Override
			public void actionPerformed(ActionEvent e) {
				double val = getPlayerControlInterface().getSearchBar().getButtonValue();
				double ksm = getPlayerControlInterface().getSearchBar().getKeyScrollamount();
				getPlayerControlInterface().getSearchBar().setButtonValue(val + ksm);
				onSearchBarButtonMove(getPlayerControlInterface().getSearchBar());
			}
		});
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object s = ae.getSource();
        if (s.equals(pci.getPlay()))
                onButtonPlay();
        if (s.equals(pci.getStop()))
                onButtonStop();
        if (s.equals(pci.getFrw()))
                onButtonFrw();
        if (s.equals(pci.getRev()))
                onButtonRev();

        if (s.equals(menu.getMenu_file_open()))
                onMenu_file_open();

        if (s.equals(menu.getMenu_file_opendir()))
            onMenu_file_opendir();
        
        if (s.equals(menu.getMenu_file_exit()))
                onMenu_file_exit();

        if (s.equals(menu.getMenu_playlist_add()))
                onMenu_playlist_add();

        if (s.equals(menu.getMenu_playlist_adddir()))
            onMenu_playlist_adddir();
        
        if (s.equals(menu.getMenu_playlist_remove()))
                onMenu_playlist_remove();

        if (s.equals(menu.getMenu_playlist_clear()))
                onMenu_playlist_clear();

        if (s.equals(menu.getMenu_playlist_up()))
                onMenu_playlist_up();

        if (s.equals(menu.getMenu_playlist_down()))
                onMenu_playlist_down();
        
        if (s.equals(menu.getMenu_playlist_shuffle()))
            onMenu_playlist_shuffle();
    
//        if (s.equals(menu.getMenu_media_library()))
//            onMenu_media_library();
        
        if (s.equals(menu.getMenu_desing_color()))
            onMenu_desing_color();
        
        if (s.equals(menu.getMenu_graph_merge()))
            onMenu_graph_merge();
        
        if (s.equals(menu.getMenu_graph_gfilter()))
            onMenu_graph_gfilter();

        if (s.equals(menu.getMenu_help_about()))
                onMenu_help_about();
        
        if (ae.getActionCommand().startsWith("SetJGraphDrawingMODE:")){
        	String mode = ae.getActionCommand().split(":")[1];
        	onMenu_graph_dmode_change(DrawMode.valueOf(mode));
        }
    }


    @Override
    public void stateChanged(ChangeEvent e) {
    	if (e.getSource().equals(pci.getGraphdetail())){
    		onGraphDetailBarChange(pci.getGraphdetail());
    	}
    	if (e.getSource().equals(pci.getHeightlevel())){
    		onHeightLevelBarChange(pci.getHeightlevel());
    	}
    	if (e.getSource().equals(pci.getZoomlevel())){
    		onZoomLevelBarChange(pci.getZoomlevel());
    	}
    }
	
    @Override
    public void onMouseDragged(SearchCircleMouseEvent event) {
        if (event.getSearchCircle().equals(pci.getSearchBar()))
                onSearchBarButtonMove(event.getSearchCircle());

        if (event.getSearchCircle().equals(pci.getVolume()))
                onVolumeButtonMove(event.getSearchCircle());
    }

    @Override
    public void onMouseClicked(SearchCircleMouseEvent event) {
        Object s = event.getSearchCircle();

        if (s.equals(pci.getSearchBar()))
                onSearchBarButtonMove((JSearchCircle) s);

        if (s.equals(pci.getVolume()))
                onVolumeButtonMove((JSearchCircle) s);
    }

    @Override
    public void onMouseEntered(SearchCircleMouseEvent event) {
    }

    @Override
    public void onMouseExited(SearchCircleMouseEvent event) {
    }

    @Override
    public void onMousePressed(SearchCircleMouseEvent event) {
            if (event.getSearchCircle().equals(pci.getSearchBar()))
                    onSearchBarMousePressed(event.getSearchCircle());
    }

    @Override
    public void onMouseReleased(SearchCircleMouseEvent event) {
            if (event.getSearchCircle().equals(pci.getSearchBar()))
                    onSearchBarMouseReleased(event.getSearchCircle());
    }

    @Override
    public void onMouseMoved(SearchCircleMouseEvent event) {
    	
    }

    @Override
    public void onButtonChange(SearchCircleChangeEvent event) {
    }

    @Override
    public void onBarChange(SearchCircleChangeEvent event) {
    }

    @Override
    public void onKeyHold(SearchCircleKeyEvent event) {
            if (event.getSearchCircle().equals(pci.getVolume()))
                    onVolumeButtonMove(event.getSearchCircle());
    }

    @Override
    public void onKeyPressed(SearchCircleKeyEvent event) {
            if (event.getSearchCircle().equals(pci.getSearchBar()))
                    onSearchBarMousePressed(event.getSearchCircle());
    }

    @Override
    public void onKeyReleased(SearchCircleKeyEvent event) {
            if (event.getSearchCircle().equals(pci.getSearchBar())) {
                    onSearchBarMouseReleased(event.getSearchCircle());
                    onSearchBarButtonMove(event.getSearchCircle());
            }
    }

    @Override
    public void onKeyTyped(SearchCircleKeyEvent event) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed() && e.getButton() == MouseEvent.BUTTON1) {
            //handle double click event.
        	mouseDoubleClicked(e);
        }
        System.out.println(e.getButton());
        if (e.getSource().equals(pli.getPlaylistTable()) && e.getButton() == MouseEvent.BUTTON3) {
        	//handle right click event
        	mouseRightClicked(e);
        }
    }

    public void mouseRightClicked(MouseEvent e) {
    	 if (e.getSource().equals(pli.getPlaylistTable())) {
             onPlaylistRightClick(pli.getPlaylistTable().getSelectedRow());
         }
    }
    
    public void mouseDoubleClicked(MouseEvent e) {
            if (e.getSource().equals(pli.getPlaylistTable())) {
                    onPlaylistDoubleClick(pli.getPlaylistTable().getSelectedRow());
            }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }
    
    public abstract void onMenu_file_open();
    public abstract void onMenu_file_opendir();
    public abstract void onMenu_file_exit();

    public abstract void onMenu_playlist_add();
    public abstract void onMenu_playlist_adddir();
    public abstract void onMenu_playlist_remove();
    public abstract void onMenu_playlist_clear();

    public abstract void onMenu_playlist_up();
    public abstract void onMenu_playlist_down();
    public abstract void onMenu_playlist_shuffle();
    
    public abstract void onMenu_media_library();
    
    public abstract void onMenu_desing_color();
    
    public abstract void onMenu_graph_merge();
    public abstract void onMenu_graph_gfilter();
    public abstract void onMenu_graph_dmode_change(DrawMode mode);
    
    public abstract void onMenu_help_about();

    public abstract void onButtonPlay();
    public abstract void onButtonStop();
    public abstract void onButtonFrw();
    public abstract void onButtonRev();

    public abstract void onPlaylistDoubleClick(int index);
    public abstract void onPlaylistRightClick(int index);
    
    public abstract void onSearchBarButtonMove(JSearchCircle s);
    public abstract void onSearchBarMousePressed(JSearchCircle s);
    public abstract void onSearchBarMouseReleased(JSearchCircle s);

    public abstract void onVolumeButtonMove(JSearchCircle v);

    public abstract void onGraphDetailBarChange(JSlider detailBar);
    public abstract void onHeightLevelBarChange(JSlider heightLevelBar);
    public abstract void onZoomLevelBarChange(JSlider zoomLevelBar);
	
    public PlayerControlInterface getPlayerControlInterface() {
            return pci;
	}

    public PlaylistToggleArea getPlayerToggleArea() {
        return pta;
    }

    public PlaylistInterface getPlaylistInterface() {
        return pli;
    }

    public StatusBar getStatusbar() {
        return statusbar;
    }

    public MenuBar getMenu() {
        return menu;
    }

//    public JPanel getMainPane() {
//        return contentPane;
//    }
    
//    public TitleFrameBorder getTitleFrame() {
//		return titleFrame;
//	}

//	public TitleFramePane getFramePane() {
//		return framePane;
//	}

//	public JPanel getContentPanePanel() {
//		return contentPane;
//	}

	@Override
    public void windowGainedFocus(WindowEvent arg0) {

    }

    @Override
    public void windowLostFocus(WindowEvent arg0) {
//		pta.doComponenteAnimation(false);
    }

}
