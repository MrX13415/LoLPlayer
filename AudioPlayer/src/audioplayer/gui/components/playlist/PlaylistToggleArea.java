package audioplayer.gui.components.playlist;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import audioplayer.Application;

import audioplayer.desing.Colors;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistToggleArea extends JLayeredPane implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6885243785578821275L;

	private JButton toggleButton;
	private JPanel toggleComponent;
	
	private boolean shownState = false;
//	private boolean lastState = shownState;
	private boolean runAnimation = false;
	
	private PlaylistInterface playlistInterface;
	private JFrame frame;
	private Component resizeTarget;
	
	Dimension fSize = new Dimension();
    Dimension fMSize = new Dimension();
    Dimension thisSize = new Dimension();

	private Insets insets = new Insets(5, 15, 25, 15);

	private Thread animationThread;
	private boolean cancleAnimation = false;

	// Animation speeds: has to be > 0; > 1 is faster
	private int showAnimationSpeed = 15;
	private int hideAnimationSpeed = 15;

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame) {
		this(null, pli, frame, false);
	}

	public PlaylistToggleArea(Component resizeTargetComp, PlaylistInterface pli, JFrame frame) {
		this(resizeTargetComp, pli, frame, false);
	}
	
	public PlaylistToggleArea(Component resizeTargetComp, PlaylistInterface pli, JFrame frame, boolean defaultState) {
		this.playlistInterface = pli;
		this.frame = frame;
//		this.shownState = lastState = defaultState;
		this.resizeTarget = resizeTargetComp;
		
		// ** init componentes **

		toggleButton = new JButton("Playlist");
		toggleButton.setForeground(Application.getColors().color_playlist_background5);
		toggleButton.addActionListener(this);
		toggleButton.setSize(new Dimension(400, insets.bottom));
		toggleButton.setPreferredSize(new Dimension(400, insets.bottom));
		toggleButton.setContentAreaFilled(false);
						
		pli.setSize(new Dimension(400, 200));

		this.setLayout(new LayoutManager() {
			
			@Override
			public void removeLayoutComponent(Component p) {}
			
			@Override
			public void addLayoutComponent(String s, Component p) {}
			
			@Override
			public Dimension preferredLayoutSize(Container p) {
				return new Dimension(0, shownState
						? playlistInterface.getPreferredSize().height + insets.top + insets.bottom
				        : insets.bottom);
			}
			
			@Override
			public Dimension minimumLayoutSize(Container p) {
				return new Dimension(0, insets.bottom);
			}
			
			@Override
			public void layoutContainer(Container p) {
				for (int i = 0; i < p.getComponentCount(); i++) {
					Component c = p.getComponent(i);
				
					int h = c.getPreferredSize().height;
					int w = p.getWidth();
					
					double d = w / 100d * 20d;
					w -= d;
					
					int x = 20;
					int y = (h - p.getHeight()) * -1;
					
					c.setBounds(x, y, w, h);
				}
			}
		});
		
		toggleComponent = new JPanel();
		toggleComponent.add(toggleButton);
		toggleComponent.add(pli);
		toggleComponent.setBackground(Application.getColors().color_playlist_background6);
		toggleComponent.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		toggleComponent.setLayout(new LayoutManager() {
			
			@Override
			public void removeLayoutComponent(Component p) {}
			
			@Override
			public void addLayoutComponent(String s, Component p) {}
			
			@Override
			public Dimension preferredLayoutSize(Container p) {
				return minimumLayoutSize(p);
			}
			
			@Override
			public Dimension minimumLayoutSize(Container p) {
				int h = insets.top * 2;
				for (int i = 0; i < p.getComponentCount(); i++) {
					Component c = p.getComponent(i);
					h += c.getPreferredSize().height;
				}
				return new Dimension(0, h);
			}
			
			@Override
			public void layoutContainer(Container p) {
				for (int i = 0; i < p.getComponentCount(); i++) {
					Component c = p.getComponent(i);
				
					if (c.equals(toggleButton)){
						int h = insets.bottom;
						int w = p.getWidth() - (insets.left + insets.right);
						int x = insets.left;
						int y = p.getHeight() - insets.bottom;
						
						c.setBounds(x, y, w, h);
					}
					
					if (c.equals(playlistInterface)){
						//TODO: buggy ... x y loc wrong!
						int h = resizeTarget != null ? resizeTarget.getSize().height / 2 : c.getPreferredSize().height;
						int w = p.getWidth() - (insets.left + insets.right);
						int x = insets.left;
						int y = insets.top * 2;
						
						c.setPreferredSize(new Dimension(c.getPreferredSize().width, h));
						c.setBounds(x, y, w, h);
					}
				}
			}
		});
		
		this.add(toggleComponent);
	}

	public JPanel getToggleComponent() {
		return toggleComponent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(toggleButton)) {
			if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) return;
			shownState = !shownState;
			onToggle();
		}
	}

	private void onToggle() {
		doComponenteAnimation(shownState);
	}

	public void hideComponente() {
		int cDelta = getPreferredSize().height - insets.bottom;
		
		int delta = cDelta * -1;
		
		fSize = frame.getSize();
	    fMSize = frame.getMinimumSize();
	    thisSize = getPreferredSize();
	    
		setDelta(delta);
		shownState = false;
	}

	public void showComponente() {
		int targetHeightOffset = (playlistInterface.getSize().height + insets.top);
		
		int cDelta = getPreferredSize().height - insets.bottom;
		
		int delta = targetHeightOffset - cDelta;
		
		fSize = frame.getSize();
	    fMSize = frame.getMinimumSize();
	    thisSize = getPreferredSize();
	    
		setDelta(delta);
		shownState = true;
	}

	/**
	 * Shows the show (open) and the hide (close) animation of the componente
	 * 
	 * @param show
	 *            true for show animation, false for hide animation
	 **/
	private synchronized void doComponenteAnimation(final boolean show) {		
		if (animationThread != null) return;

		// prevent double show / hide animation ...
//		if (lastState == show)  return;

//		lastState = show;
		runAnimation = true;

		animationThread = new Thread(new Runnable() {
			@Override
			public void run() {

				final int targetHeightOffset = playlistInterface.getSize().height * -1;
				
				fSize = frame.getSize();
			    fMSize = frame.getMinimumSize();
			    thisSize = getPreferredSize();
			    
                int yIndex = (show ? targetHeightOffset : 0);
                int showYMax = insets.top;
                int hideYMin = targetHeightOffset - insets.top;
                int delta = 0;
                
				while (show ? yIndex <= showYMax
						    : yIndex >= hideYMin)
				{

	                if (cancleAnimation) {
                        cancleAnimation = false;
                        return;
	                }
	
	                delta = show ? yIndex + Math.abs(targetHeightOffset)
	                		: Math.abs(yIndex) * -1;

	                setDelta(delta);
	                
	                try {
	                    Thread.sleep(33); //30 FPS
	                } catch (InterruptedException ex) {
	                }

                    if (show && yIndex >= showYMax || !show && yIndex <= hideYMin) break;
                    
	                int nextYIndex = (show ? yIndex + showAnimationSpeed : yIndex - hideAnimationSpeed);
					yIndex = show ?
							 	nextYIndex > showYMax ?
							 		yIndex + Math.abs(showYMax - yIndex)
									: nextYIndex
							 : nextYIndex < hideYMin ?
									yIndex - Math.abs(yIndex - hideYMin)
									: nextYIndex;
                }
								
				SwingUtilities.invokeLater(new Runnable() {
	                @Override
	                public void run() {
	                	if (show)
	                		showComponente();
	                	else
	                		hideComponente();
	                }
		        });
		        
		        runAnimation = false;
		        animationThread = null;
			}

		});

		animationThread.start();
	}
	
	/**
	 * set delta value
	 * 
	 * @param delta
	 */
	private synchronized void setDelta(int delta){
				
		final Dimension nfSize = new Dimension(fSize.width, fSize.height + delta);
		final Dimension nfMSize = new Dimension(fMSize.width, fMSize.height + delta);
		final Dimension nthisSize = new Dimension(thisSize.width, thisSize.height + delta);
		
		if (SwingUtilities.isEventDispatchThread()){
			frame.setMinimumSize(new Dimension(nfMSize));
			frame.setSize(nfSize);
			setPreferredSize(nthisSize);

			frame.validate();
		}else{
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
//						if (frame.getState() != Frame.MAXIMIZED_BOTH){
							frame.setMinimumSize(new Dimension(nfMSize));
							frame.setSize(nfSize);
//						}

						setPreferredSize(nthisSize);

						frame.validate();
					}
				});
			} catch (Exception e) {
				//TODO:
			}
		}
	}
	
	public boolean isShowPlaylist() {
		return shownState;
	}

	public JButton getToggleButton() {
		return toggleButton;
	}

	public PlaylistInterface getPli() {
		return playlistInterface;
	}

	public JFrame getFrame() {
		return frame;
	}

	public Thread getAnimationThread() {
		return animationThread;
	}

	public boolean isCancleAnimation() {
		return cancleAnimation;
	}
	
	public boolean isRunAnimation() {
		return runAnimation;
	}

	public int getShowAnimationSpeed() {
		return showAnimationSpeed;
	}

	public int getHideAnimationSpeed() {
		return hideAnimationSpeed;
	}

	public void setToggleState(boolean toggleState) {
		this.shownState = toggleState;
		onToggle();

	}

	public void setCancleAnimation(boolean cancleAnimation) {
		this.cancleAnimation = cancleAnimation;
	}

	public void setShowAnimationSpeed(int showAnimationSpeed) {
		this.showAnimationSpeed = showAnimationSpeed;
	}

	public void setHideAnimationSpeed(int hideAnimationSpeed) {
		this.hideAnimationSpeed = hideAnimationSpeed;
	}

}
