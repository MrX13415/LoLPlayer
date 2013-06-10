package audioplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SearchCircle;
import javax.swing.SearchCircle.SearchCircleChangeEvent;
import javax.swing.SearchCircle.SearchCircleKeyEvent;
import javax.swing.SearchCircle.SearchCircleMouseEvent;
import javax.swing.SearchCircle.SearchCricleListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import audioplayer.Application;
import audioplayer.gui.components.MenuBar;
import audioplayer.gui.components.PlayerControler.PlayerControlInterface;
import audioplayer.gui.components.StatusBar;
import audioplayer.gui.components.playlist.PlaylistInterface;
import audioplayer.gui.components.playlist.PlaylistToggleArea;
import java.awt.Toolkit;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public abstract class UserInterface extends JFrame implements ActionListener,
															  SearchCricleListener,
															  MouseListener,
															  WindowFocusListener,
															  ChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 6407423888068079527L;

    private PlayerControlInterface pci;
    private PlaylistToggleArea pta;
    private PlaylistInterface pli;
        
    private MenuBar menu;
    private JPanel mainPane;
    private StatusBar statusbar;

    
    public UserInterface() {
                            
        pci = new PlayerControlInterface(this, this, this);

        pli = new PlaylistInterface(this);
        
        pta = new PlaylistToggleArea(pli, this);      
        
        statusbar = new StatusBar();
                       
        mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        mainPane.add(pci, BorderLayout.CENTER); 
        mainPane.add(statusbar, BorderLayout.SOUTH);
        
        menu = new MenuBar(this){
            /**
             * 
             */
            private static final long serialVersionUID = -4792554976830903762L;

            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(menu.getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        menu.setBackground(new Color(50,50,50));
        menu.setForeground(new Color(255,255,255));
        menu.setBorder(BorderFactory.createRaisedBevelBorder());

        this.setJMenuBar(menu);

        this.addWindowFocusListener(this);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mainPane, BorderLayout.CENTER);
        this.getContentPane().add(pta, BorderLayout.SOUTH);
        this.getContentPane().setBackground(new Color(255,10,10));
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(Application.App_Name_Version);

        this.setPreferredSize(new Dimension(420, 475));
        this.setMinimumSize(new Dimension(430, 470));
        this.pack();
        
        this.setLocationRelativeTo(null);
        
        //Center the frame window
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
        
        this.setVisible(true);	
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

        // Use MouseClicked instad ...
        // if (s.equals(searchBar)) onSearchBarButtonSet((SearchCircle) s);
        // if (s.equals(volume)) onVolumeButtonSet((SearchCircle) s);

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
        
        if (s.equals(menu.getMenu_graph_merge()))
            onMenu_graph_merge();
        
        if (s.equals(menu.getMenu_graph_gfilter()))
            onMenu_graph_gfilter();

        if (s.equals(menu.getMenu_help_about()))
                onMenu_help_about();
    }


    @Override
    public void stateChanged(ChangeEvent e) {
    	if (e.getSource().equals(pci.getGraphdetail())){
    		onGraphDetailBarChange(pci.getGraphdetail());
    	}
    	if (e.getSource().equals(pci.getHeightlevel())){
    		onHeightLevelBarChange(pci.getHeightlevel());
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
                onSearchBarButtonMove((SearchCircle) s);

        if (s.equals(pci.getVolume()))
                onVolumeButtonMove((SearchCircle) s);
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
            if (e.getClickCount() == 2 && !e.isConsumed()) {
                 //handle double click event.
                    mouseDoubleClicked(e);
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
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

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

    public abstract void onMenu_graph_merge();
    public abstract void onMenu_graph_gfilter();
    
    public abstract void onMenu_help_about();

    public abstract void onButtonPlay();
    public abstract void onButtonStop();
    public abstract void onButtonFrw();
    public abstract void onButtonRev();

    public abstract void onPlaylistDoubleClick(int index);

    public abstract void onSearchBarButtonMove(SearchCircle s);
    public abstract void onSearchBarMousePressed(SearchCircle s);
    public abstract void onSearchBarMouseReleased(SearchCircle s);

    public abstract void onVolumeButtonMove(SearchCircle v);

    public abstract void onGraphDetailBarChange(JSlider detailBar);
    public abstract void onHeightLevelBarChange(JSlider heightLevelBar);
	
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

    public JPanel getMainPane() {
        return mainPane;
    }
    
    @Override
    public void windowGainedFocus(WindowEvent arg0) {

    }

    @Override
    public void windowLostFocus(WindowEvent arg0) {
//		pta.doComponenteAnimation(false);
    }

}
