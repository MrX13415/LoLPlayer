package net.icelane.amplifire.ui.components.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.font.FontLoader;

public class TitleFrameBorder extends JPanel implements MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5769780941654523397L;

	private Insets frameBorder = new Insets(20,20,0,0);
	
	private JPanel main;
	
	private JLabel ico;
	private JLabel title;
	private JLabel minimizeButton;
	private JLabel resizeButton;
	private JLabel closeButton;
		
	private JFrame frame;

	private Dimension normalSize;
	private Point location;
	
	private int priorFrameState = Frame.NORMAL;
	
	private int dragPointX;
	private int dragPointY;
	private boolean mousePressed;
	
	private boolean mbhover;
	private boolean rbhover;
	private boolean cbhover;
	
	private boolean mbpressed;
	private boolean rbpressed;
	private boolean cbpressed;
	
	
	private TitleFrameResizeHandler resizehandler;
	
	
	public TitleFrameBorder(JFrame f) {
		this.frame = f;
		
		normalSize = frame.getSize();
		location = frame.getLocation();
			
		int h = frameBorder.top;
		int bw = 16;
		int bh = h - 20;
		
		ico = new JLabel();
		ico.setPreferredSize(new Dimension(h, h));
		
		title = new JLabel(frame.getTitle());
		title.setForeground(Application.getColors().color_uiframe_titel);
		title.setBorder(BorderFactory.createEmptyBorder(-3, 0, 0, 0 )); //correct position

		
		MouseListener buttonColorChange = new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (((JLabel)e.getSource()).equals(minimizeButton)) mbpressed = false;
				if (((JLabel)e.getSource()).equals(resizeButton)) rbpressed = false;
				if (((JLabel)e.getSource()).equals(closeButton)) cbpressed = false;
				((JLabel)e.getSource()).setForeground(Application.getColors().color_uiframe_buttons);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (((JLabel)e.getSource()).equals(minimizeButton)) mbpressed = true;
				if (((JLabel)e.getSource()).equals(resizeButton)) rbpressed = true;
				if (((JLabel)e.getSource()).equals(closeButton)) cbpressed = true;
				((JLabel)e.getSource()).setForeground(Application.getColors().color_uiframe_buttons_pressed);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (((JLabel)e.getSource()).equals(minimizeButton)) mbhover = false;
				if (((JLabel)e.getSource()).equals(resizeButton)) rbhover = false;
				if (((JLabel)e.getSource()).equals(closeButton)) cbhover = false;
				((JLabel)e.getSource()).setForeground(Application.getColors().color_uiframe_buttons);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if (((JLabel)e.getSource()).equals(minimizeButton)) mbhover = true;
				if (((JLabel)e.getSource()).equals(resizeButton)) rbhover = true;
				if (((JLabel)e.getSource()).equals(closeButton)) cbhover = true;
				((JLabel)e.getSource()).setForeground(Application.getColors().color_uiframe_buttons_hover);
			}
		};
		
		minimizeButton = new JLabel("\u0030");
		minimizeButton.setForeground(new Color(150, 38, 38));
		minimizeButton.setFont(FontLoader.fontMarlett_16p);
		minimizeButton.setVerticalTextPosition(JLabel.CENTER);
		minimizeButton.setPreferredSize(new Dimension(bw, bh));
		minimizeButton.addMouseListener(buttonColorChange);
		minimizeButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				priorFrameState = frame.getState();
				if (frame.getState() != JFrame.ICONIFIED){
					frame.setState(Frame.ICONIFIED);
				}else{
					frame.setState(priorFrameState);
				}
			}
		});

		resizeButton = new JLabel("\u0031");
		resizeButton.setForeground(new Color(150, 38, 38));
		resizeButton.setFont(FontLoader.fontMarlett_16p);
		resizeButton.setPreferredSize(new Dimension(bw, bh));
		resizeButton.addMouseListener(buttonColorChange);
		resizeButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH){
					
					normalSize = frame.getSize();
					location = frame.getLocation();
					
					frame.setExtendedState(Frame.MAXIMIZED_BOTH);
					frame.setState(Frame.MAXIMIZED_BOTH);
					
					resizeButton.setText("\u0032");
				}else{
					frame.setExtendedState(Frame.NORMAL);
					frame.setState(Frame.NORMAL);
					
//					frame.setPreferredSize(normalSize);
					frame.setSize(normalSize);
					frame.setLocation(location);
					
					resizeButton.setText("\u0031");
				}
			}
		});
		
		closeButton = new JLabel("\u0072");
		closeButton.setForeground(new Color(150, 38, 38));
		closeButton.setFont(FontLoader.fontMarlett_16p);
		closeButton.setPreferredSize(new Dimension(bw, bh));
		closeButton.addMouseListener(buttonColorChange);
		closeButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				int dco = frame.getDefaultCloseOperation();
				if (dco == JFrame.EXIT_ON_CLOSE) Application.exit();
				if (dco == JFrame.DISPOSE_ON_CLOSE) frame.dispose();
			}
		});
		
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.add(minimizeButton, BorderLayout.WEST);
		buttons.add(resizeButton, BorderLayout.CENTER);
		buttons.add(closeButton, BorderLayout.EAST);
		buttons.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0 )); //correct position
		buttons.setOpaque(false);
		
		main = new JPanel(new BorderLayout());
		main.add(ico, BorderLayout.WEST);
		main.add(title, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.EAST);
		main.setOpaque(false);
		
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		this.setName("TitleFrameBorder");
		
		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.NORTH);
	}
	
	public TitleFrameResizeHandler getResizehandler() {
		return resizehandler;
	}

	public void setResizehandler(TitleFrameResizeHandler resizehandler) {
		this.resizehandler = resizehandler;
	}

	@Override
    protected void paintComponent(Graphics g) {
		//TODO
        super.paintComponent(g);
        Application.drawReflectionEffect(this, g);
    }
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!mousePressed) return;
		int xos = e.getX();
		int yos = e.getY();
		
		int x = frame.getLocation().x;
		int y = frame.getLocation().y;
		
		int dx = xos-dragPointX;
		int dy = yos-dragPointY;
	
		if (resizehandler != null && !resizehandler.isResizing()){
			resizehandler.setBlocked(true);
			frame.setLocation(x + dx, y + dy);
		}
			  
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		dragPointX = e.getX(); 
		dragPointY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mousePressed) resizehandler.setBlocked(false);
		mousePressed = false;
	}

	public JLabel getMinimizeButton() {
		return minimizeButton;
	}

	public void setMinimizeButton(JLabel minimizeButton) {
		this.minimizeButton = minimizeButton;
	}

	public JLabel getTitle() {
		return title;
	}

	public JLabel getResizeButton() {
		return resizeButton;
	}

	public void setResizeButton(JLabel resizeButton) {
		this.resizeButton = resizeButton;
	}

	public JLabel getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(JLabel closeButton) {
		this.closeButton = closeButton;
	}

	public void applayColors() {
		title.setForeground(Application.getColors().color_uiframe_titel);
		
		minimizeButton.setForeground(Application.getColors().color_uiframe_buttons);
		if (mbhover) minimizeButton.setForeground(Application.getColors().color_uiframe_buttons_hover);
		if (mbpressed) minimizeButton.setForeground(Application.getColors().color_uiframe_buttons_pressed);
		
		resizeButton.setForeground(Application.getColors().color_uiframe_buttons);
		if (rbhover) resizeButton.setForeground(Application.getColors().color_uiframe_buttons_hover);
		if (rbpressed) resizeButton.setForeground(Application.getColors().color_uiframe_buttons_pressed);
		
		closeButton.setForeground(Application.getColors().color_uiframe_buttons);
		if (cbhover) closeButton.setForeground(Application.getColors().color_uiframe_buttons_hover);
		if (cbpressed) closeButton.setForeground(Application.getColors().color_uiframe_buttons_pressed);
	}
	
}
