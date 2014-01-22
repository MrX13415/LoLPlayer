package audioplayer.gui.components.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
	
	private boolean toggleState = false;
	private boolean lastState = toggleState;
	private boolean runAnimation = false;
	
	private PlaylistInterface playlistInterface;
	private JFrame frame;
	private int frameHeight;
	private int thisHeight;
	private Insets insets = new Insets(10, 15, 25, 15);

	private Thread animationThread;
	private boolean cancleAnimation = false;

	// Animation speeds: has to be > 0; > 1 is faster
	private int showAnimationSpeed = 15;
	private int hideAnimationSpeed = 15;

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame) {
		this(pli, frame, false);
	}

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame, boolean defaultState) {
		this.playlistInterface = pli;
		this.frame = frame;
		this.toggleState = lastState = defaultState;

		// ** init componentes **

		toggleButton = new JButton("Playlist");
		toggleButton.setForeground(new Color(130, 38, 38));
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
		toggleComponent.setBackground(new Color(235, 65, 65));
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
						int h = c.getPreferredSize().height;
						int w = p.getWidth() - (insets.left + insets.right);
						int x = insets.left;
						int y = insets.top * 2;
						
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
			toggleState = !toggleState;
			onToggle();
		}
	}

	private void onToggle() {
		doComponenteAnimation(toggleState);
	}

	public void hideComponente() {
		final int targetHeightOffset = (playlistInterface.getSize().height + insets.top) * -1;

		final int sizeToremove = Math.abs(targetHeightOffset);
		
		frame.setMinimumSize(new Dimension(frame.getWidth(), frameHeight - sizeToremove));
		frame.setSize(frame.getWidth(), frameHeight - sizeToremove);
		setPreferredSize(new Dimension(getPreferredSize().width, insets.bottom));
		
		frame.repaint();
		frame.validate();
	}

	public void showComponente() {
		final int targetHeightOffset = (playlistInterface.getSize().height + insets.top) * -1;
		
		final int sizeToadd = Math.abs(targetHeightOffset);
		final int size = playlistInterface.getPreferredSize().height + insets.top + insets.bottom;

		setPreferredSize(new Dimension(getPreferredSize().width, size));
		frame.setSize(frame.getWidth(), frameHeight + sizeToadd);
		frame.setMinimumSize(new Dimension(frame.getWidth(), frameHeight + sizeToadd));
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
          
                                    if (cancleAnimation) {
                                            cancleAnimation = false;
                                            return;
                                    }

                                    final int sizeToadd = yIndex + Math.abs(targetHeightOffset);
                                    final int sizeToremove = Math.abs(yIndex);

                                    SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                    if (show) {
                                                    	setPreferredSize(new Dimension(thisWidth, thisHeight + sizeToadd));
                                                        frame.setSize(frame.getWidth(), frameHeight + sizeToadd);
                                                        frame.setMinimumSize(new Dimension(frame.getWidth(), frameHeight + sizeToadd));
                                                    } else {
                                                    	frame.setMinimumSize(new Dimension(frame.getWidth(), frameHeight - sizeToremove));
                                                        frame.setSize(frame.getWidth(), frameHeight - sizeToremove);
                                                        setPreferredSize(new Dimension(thisWidth, thisHeight - sizeToremove));
                                                    }
                                                    
                                                    frame.validate();
//                                                  frame.repaint();
                                                                                                   
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
