package audioplayer.gui.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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

import audioplayer.Application;
import audioplayer.gui.components.frame.TitleFrameBorder;
import audioplayer.gui.components.frame.TitleFramePane;
import audioplayer.gui.components.frame.TitleFrameResizeHandler;
import audioplayer.gui.components.frame.TitleFrameResizeHandler.Direction;


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

	private boolean translucency;
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

		// If translucent windows aren't supported, exit.
		if (!translucencySupported) {
			System.out.println("NOT SUPPORTED");
		} else
			System.out.println("SUPPORTED");

		contentPane = new JPanel();
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
		
		/* TRANSLUCENCY 
		 *
		 * Alpha has to be 0;
		 * 
		 */
		framePane.setBackground(Application.getColors().color_background4);
		
		framePane.setName("FramePane");

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(framePane);

		this.setResizable(false);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));
		
		if (parent == null) return;

//		JPanel glass = new JPanel(){
//			
////		    public JPane() {
////
////		        setOpaque(false);
////
////		        addMouseListener(new MouseAdapter() {
////		            @Override
////		            public void mouseClicked(MouseEvent e) {
////		                actionAllowed = false;
////		            }
////		        });
////		    }
////
////		    //Draw an cross to indicate glasspane visibility 
////		    public void paintComponent(Graphics g) {  
////		      g.setColor(Color.red);  
////		      g.drawLine(0, 0, getWidth(), getHeight());  
////		      g.drawLine(getWidth(), 0, 0, getHeight());
////		    }
////		};
		
//		parent.setGlassPane(glassPane);
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
