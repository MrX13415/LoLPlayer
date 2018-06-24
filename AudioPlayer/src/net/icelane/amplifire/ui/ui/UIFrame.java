package net.icelane.amplifire.ui.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.ui.components.frame.TitleFrameBorder;
import net.icelane.amplifire.ui.components.frame.TitleFramePane;
import net.icelane.amplifire.ui.components.frame.TitleFrameResizeHandler;
import net.icelane.amplifire.ui.components.frame.TitleFrameResizeHandler.Direction;


public class UIFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8753134594239798828L;
	
	private static ArrayList<UIFrame> frames = new ArrayList<UIFrame>();
	
	private JMenuBar menubar;
	private Container statuspane;

	private TitleFrameResizeHandler resizeHandler;
	private TitleFrameBorder titleFrame;
	private TitleFramePane framePane;
	private JPanel mainPane;
	private JPanel rootPane;
	private JPanel contentPane;

	private boolean translucency = true;
	private boolean translucencySupported;

	private JFrame parent;
	
	
	public UIFrame() {
		this(null);
	}
	
	public UIFrame(JFrame parent) {
		this.parent = parent;
		initUI();
	}

	public void initUI() {
		frames.add(this);
		
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		translucencySupported = gd
				.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);

		System.out.print("Per-pixel translucency ...\t\t");

		if (!translucencySupported) {
			System.out.println("NOT SUPPORTED");
		} else
			System.out.println("SUPPORTED");

		contentPane = new JPanel(){
			@Override
		    protected void paintComponent(Graphics g) {
				//TODO
		        super.paintComponent(g);
		        Application.drawReflectionEffect(this, g, 0.3f);
		    }
		};
		contentPane.setName("ContentPane");
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Application.getColors().color_background1);
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		
		rootPane = new JPanel();
		rootPane.setName("RootPane");
		rootPane.setLayout(new BorderLayout());
		rootPane.add(contentPane, BorderLayout.CENTER);
		rootPane.setBackground(Application.getColors().color_background1);

		mainPane = new JPanel();
		mainPane.setName("MainPane");
		mainPane.setLayout(new BorderLayout());
		mainPane.add(rootPane, BorderLayout.CENTER);
		mainPane.setBackground(Application.getColors().color_background2);

		this.setTitle(Application.App_Name_Version);
		this.setUndecorated(true);

		titleFrame = new TitleFrameBorder(this);
		titleFrame.setName("TitleFrame");
		titleFrame.setBackground(Application.getColors().color_background3);
		titleFrame.setBorder(BorderFactory.createRaisedBevelBorder());

		resizeHandler = new TitleFrameResizeHandler(this);
		resizeHandler.addInputComponent(titleFrame, Direction.N, Direction.W);
		resizeHandler.addInputComponent(rootPane);

		titleFrame.setResizehandler(resizeHandler);
		
		framePane = new TitleFramePane(titleFrame, mainPane);
		framePane.setBackground(Application.getColors().color_background4);
		
		framePane.setName("FramePane");

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(framePane);

		this.setResizable(false);
		this.setUndecorated(true);
		if (translucencySupported && translucency)
			this.setBackground(new Color(0, 0, 0, 0));
		
		if (parent == null) return;

		parent.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
				    @Override
				    public void run() {
				        toFront();
				        repaint();
				    }
				});
			}
		});
		
	}

	public synchronized static ArrayList<UIFrame> getOpendFrames() {
		return frames;
	}

	@Override
	public void setJMenuBar(JMenuBar menubar) {
		if (menubar != null) {
			rootPane.add(menubar, BorderLayout.NORTH);

		} else if (this.menubar != null)
			rootPane.remove(this.menubar);

		this.menubar = menubar;
		applayMenuBarDesign();
	}

	@Override
	public JMenuBar getJMenuBar() {
		return menubar;
	}

	public void applayMenuBarDesign() {
		if (this.menubar == null) return;
		
		this.menubar.setBackground(new Color(50, 50, 50));
		this.menubar.setForeground(new Color(255, 255, 255));
		this.menubar.setBorder(BorderFactory.createRaisedBevelBorder());
	}

	public Container getStatusPane() {
		return statuspane;
	}

	public void setStatusPane(Container statuspane) {
		if (statuspane != null) {
			rootPane.add(statuspane, BorderLayout.SOUTH);

		} else if (this.statuspane != null)
			rootPane.remove(this.statuspane);

		this.statuspane = statuspane;
		
		applayStatusPaneDesign();
	}

	public void applayStatusPaneDesign() {
		if (this.statuspane == null) return;
		
		//TODO: add desing
	}
	
	public void applayColors(){
		try {
			getTitleFrame().applayColors();
			
			getMainPane().setBackground(Application.getColors().color_background2);
			getTitleFrame().setBackground(Application.getColors().color_background3);
			getFramePane().setBackground(Application.getColors().color_background4);	
		} catch (Exception e) {}		
	}
	
	public JPanel getContentPanePanel() {
		return contentPane;
	}

	public JPanel getMainPane() {
		return mainPane;
	}

	public TitleFrameResizeHandler getResizeHandler() {
		return resizeHandler;
	}

	public boolean isTranslucency() {
		return translucency;
	}

	public void setTranslucency(boolean translucency) {
		this.translucency = translucency;
		//TODO: code here
	}

	public TitleFrameBorder getTitleFrame() {
		return titleFrame;
	}

	public TitleFramePane getFramePane() {
		return framePane;
	}

	public boolean isTranslucencySupported() {
		return translucencySupported;
	}
	
}
