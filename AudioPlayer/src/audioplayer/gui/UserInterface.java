package audioplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import audioplayer.Applikation;
import audioplayer.gui.components.MenuBar;
import audioplayer.gui.components.PlayerControler.PlayerControlInterface;
import audioplayer.gui.components.playlist.PlaylistInterface;
import audioplayer.gui.components.playlist.PlaylistToggleArea;
import audioplayer.player.codec.AudioFile;



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

    
	public UserInterface() {
                            
        pci = new PlayerControlInterface(this, this, this);
				
        pli = new PlaylistInterface(this);
        
        pta = new PlaylistToggleArea(pli, this);      
        //pta.setSize(new Dimension(400, 50));
        
        //JLayeredPane playlist = new JLayeredPane();
        //playlist.add(pta);
        //playlist.add(pli);
        //playlist.setPreferredSize(new Dimension(400, 50));
        
        mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        mainPane.add(pci, BorderLayout.CENTER);                                      
        mainPane.setBackground(new Color(20, 20, 20));
        mainPane.setBorder(BorderFactory.createRaisedBevelBorder());
        
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
		this.getContentPane().add(mainPane);
		this.getContentPane().add(pta, BorderLayout.SOUTH);
		this.getContentPane().setBackground(new Color(255,10,10));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setTitle(Applikation.App_Name_Version);
		this.pack();
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
		
		if (s.equals(menu.getMenu_file_add()))
			onMenu_file_add();
	}


    @Override
    public void stateChanged(ChangeEvent e) {
    	if (e.getSource().equals(pci.getGraphdetail())){
    		onGraphDetailBarChange(pci.getGraphdetail().getValue());
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

	public abstract void onMenu_file_add();
	
	public abstract void onButtonPlay();

	public abstract void onButtonStop();

	public abstract void onButtonFrw();

	public abstract void onButtonRev();
	
	public abstract void onPlaylistDoubleClick(int index);

	public abstract void onSearchBarButtonMove(SearchCircle s);

	public abstract void onSearchBarMousePressed(SearchCircle s);

	public abstract void onSearchBarMouseReleased(SearchCircle s);

	public abstract void onVolumeButtonMove(SearchCircle v);
	
	public abstract void onGraphDetailBarChange(int value);

    public PlayerControlInterface getPlayerControlInterface() {
            return pci;
	}

    public PlaylistToggleArea getPlayerToggleArea() {
        return pta;
    }

    public PlaylistInterface getPlaylistInterface() {
        return pli;
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
		pta.doComponenteAnimation(false);
	}

}
