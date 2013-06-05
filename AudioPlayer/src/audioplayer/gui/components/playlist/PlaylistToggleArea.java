package audioplayer.gui.components.playlist;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

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

	private boolean toggleState = false;
	private boolean lastState = toggleState;
	private boolean runAnimation = false;
	
	private PlaylistInterface playlistInterface;
	private JFrame frame;
	private int frameHeight;
	private int thisHeight;
	private Insets insets = new Insets(0, 25, 25, 25);
	private int heightOffset = 0;

	private Thread animationThread;
	private boolean cancleAnimation = false;

	// Animation speeds: has to be > 0; > 1 is faster
	private int showAnimationSpeed = 13;
	private int hideAnimationSpeed = 23;

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame) {
		this(pli, frame, false);
	}

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame, boolean defaultState) {
		this.playlistInterface = pli;
		this.frame = frame;
		this.toggleState = lastState = defaultState;

		// ** init componentes **

		toggleButton = new JButton("Playlist");
		toggleButton.addActionListener(this);
		toggleButton.setSize(new Dimension(400, 25));
		toggleButton.setContentAreaFilled(false);
				
		pli.setSize(new Dimension(400, 200));

		if (defaultState) {
			heightOffset = 0;
		} else {
			heightOffset = playlistInterface.getPreferredSize().height * -1;
		}
		
		this.setLayout(new LayoutManager() {
			
			@Override
			public void removeLayoutComponent(Component p) {}
			
			@Override
			public void addLayoutComponent(String s, Component p) {}
			
			@Override
			public Dimension preferredLayoutSize(Container p) {
				return new Dimension(0, toggleState
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
				
					if (c.equals(toggleButton)){
						int h = insets.bottom;
						int w = p.getWidth() - (insets.left + insets.right);
						int x = insets.left;
						int y = p.getHeight() - insets.bottom;
						
						c.setBounds(x, y, w, h);
					}
					
					if (c.equals(playlistInterface)){
						int h = c.getPreferredSize().height;
						int w = p.getWidth() - (insets.left + insets.right);
						int x = insets.left;
						int y = insets.top + heightOffset;
						
						c.setBounds(x, y, w, h);
					}
				}
			}
		});
		
		this.add(toggleButton);
		this.add(pli);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(toggleButton)) {
			toggleState = !toggleState;
			onToggle();
		}
	}

	private void onToggle() {
		doComponenteAnimation(toggleState);
	}

	public void hideComponente() {
		final int targetHeightOffset = playlistInterface.getSize().height * -1;

		heightOffset = targetHeightOffset;
		
		final int sizeToremove = Math.abs(targetHeightOffset);
		
		frame.setSize(frame.getWidth(), frameHeight - sizeToremove);
		setPreferredSize(new Dimension(getPreferredSize().width, insets.bottom));
		
		frame.repaint();
		frame.validate();
	}

	public void showComponente() {
		final int targetHeightOffset = playlistInterface.getSize().height * -1;

		heightOffset = 0;
		
		final int sizeToadd = Math.abs(targetHeightOffset);
		final int size = playlistInterface.getPreferredSize().height + insets.top + insets.bottom;

		setPreferredSize(new Dimension(getPreferredSize().width, size));
		frame.setSize(frame.getWidth(), frameHeight + sizeToadd);
		
		frame.repaint();
		frame.validate();
	}

	/**
	 * Shows the show (open) and the hide (close) animation of the componente
	 * 
	 * @param show
	 *            true for show animation, false for hide animation
	 **/
	public synchronized void doComponenteAnimation(final boolean show) {
		if (animationThread != null)
			return;

		// prevent double show / hide animation ...
		if (lastState == show) {
			return;
		}

		lastState = show;
		runAnimation = true;
		
		final int targetHeightOffset = playlistInterface.getSize().height * -1;

		frameHeight = frame.getHeight();
		thisHeight = this.getPreferredSize().height;
		final int thisWidth = this.getPreferredSize().width;

		animationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
                            int yIndex = (show ? targetHeightOffset : 0);
			
                            while(show ? yIndex <= 0: yIndex >= targetHeightOffset){
                                    //(show ? yIndex <= 0: yIndex >= targetHeightOffset);
                                     //yIndex = (show ? yIndex + showAnimationSpeed : yIndex - hideAnimationSpeed)) {

                                    if (cancleAnimation) {
                                            cancleAnimation = false;
                                            return;
                                    }

                                    heightOffset = yIndex;

                                    final int sizeToadd = yIndex + Math.abs(targetHeightOffset);
                                    final int sizeToremove = Math.abs(yIndex);

                                    SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                    if (show) {
                                                            frame.setSize(frame.getWidth(), frameHeight + sizeToadd);
                                                            setPreferredSize(new Dimension(thisWidth, thisHeight + sizeToadd));
                                                    } else {
                                                            setPreferredSize(new Dimension(thisWidth, thisHeight - sizeToremove));
                                                            frame.setSize(frame.getWidth(), frameHeight - sizeToremove);
                                                    }
                                                    
                                                    frame.repaint();
                                                    
                                                    try {
                                                        Thread.sleep(15);
                                                    } catch (InterruptedException ex) {
                                                    }                                                    
                                            }
                                    });

                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ex) {
                                    }
                                                                        
                                 yIndex = (show ? yIndex + showAnimationSpeed : yIndex - hideAnimationSpeed);
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

	public boolean isShowPlaylist() {
		return toggleState;
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
		this.toggleState = toggleState;
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
