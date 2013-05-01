package audioplayer.gui.components.playlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
 * 
 * @author dausol
 */
public class PlaylistToggleArea extends JLayeredPane implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6885243785578821275L;

	private JButton toggleButton;

	private boolean toggleState = false;
	private boolean lastState = toggleState;

	private PlaylistInterface pli;
	private JFrame frame;
	private int frameHeight;
	private int thisHeight;
	private int leftRightBorderSize = 30;
	private ComponentAdapter componentResizer;

	private Thread animationThread;
	private boolean cancleAnimation = false;

	// Animation speeds: has to be > 0; > 1 is faster
	private int showAnimationSpeed = 10;
	private int hideAnimationSpeed = 25;

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame) {
		this(pli, frame, false);
	}

	public PlaylistToggleArea(PlaylistInterface pli, JFrame frame, boolean defaultState) {
		this.pli = pli;
		this.frame = frame;
		this.toggleState = lastState = defaultState;

		// ** init componentes **

		toggleButton = new JButton("TOGGLE");
		toggleButton.addActionListener(this);
		toggleButton.setSize(new Dimension(400, 25));
		
		this.setBackground(new Color(255,50,50));
		
		pli.setSize(new Dimension(400, 200));

		initComponentResize(frame, pli);

		if (defaultState) {
			// init pli componente location
			showComponente();

			// init defualt size
			this.setPreferredSize(new Dimension(this.getWidth(), toggleButton.getHeight() + pli.getHeight()));
		} else {
			// init pli componente location
			hideComponente();

			// init defualt size
			this.setPreferredSize(new Dimension(this.getWidth(), toggleButton
					.getHeight()));
		}

		this.add(toggleButton);
		this.add(pli);
	}

	private void initComponentResize(final JFrame fframe,
			final PlaylistInterface fpli) {
		if (componentResizer != null)
			frame.removeComponentListener(componentResizer);

		componentResizer = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				int w = fframe.getContentPane().getWidth() - (leftRightBorderSize * 2);
				
				toggleButton.setSize(w, toggleButton.getSize().height);
				
				fpli.setSize(w, pli.getSize().height);

				toggleButton.setLocation(leftRightBorderSize, toggleButton.getLocation().y);
				
				fpli.setLocation(leftRightBorderSize, fpli.getLocation().y);
			}
		};

		frame.addComponentListener(componentResizer);
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
		int y = pli.getSize().height * -1;
		pli.setLocation(0, y);

		// frame.setSize(frame.getWidth(), frameHeight - pli.getHeight());
		// this.setPreferredSize(new Dimension(this.getWidth(), thisHeight -
		// pli.getHeight()));
	}

	public void showComponente() {
		int y = 0;
		pli.setLocation(0, y);
		//
		// frame.setSize(frame.getWidth(), frameHeight + pli.getHeight());
		// this.setPreferredSize(new Dimension(this.getWidth(), thisHeight +
		// pli.getHeight()));
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

		final int hideY = pli.getSize().height * -1;
		final int showY = 0;

		frameHeight = frame.getHeight();
		thisHeight = this.getPreferredSize().height;
		final int thisW = this.getPreferredSize().width;
		final PlaylistToggleArea pta = this;

		animationThread = new Thread(new Runnable() {
			@Override
			public void run() {

				for (int yIndex = (show ? hideY : showY); (show ? yIndex <= showY
						: yIndex >= hideY); yIndex = (show ? yIndex
						+ showAnimationSpeed : yIndex - hideAnimationSpeed)) {

					if (cancleAnimation) {
						cancleAnimation = false;
						return;
					}

					final int fNewY = yIndex;
					final int sizeToadd = fNewY + Math.abs(hideY);
					final int sizeToremove = Math.abs(fNewY - showY);

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {

							pli.setLocation(pli.getLocation().x, fNewY);

							toggleButton.setLocation(
									toggleButton.getLocation().x,
									fNewY + pli.getSize().height);
							
							if (show) {
								frame.setSize(frame.getWidth(), frameHeight
										+ sizeToadd);
								pta.setPreferredSize(new Dimension(thisW,
										thisHeight + sizeToadd));
								
							} else {
								frame.setSize(frame.getWidth(), frameHeight
										- sizeToremove);
								pta.setPreferredSize(new Dimension(thisW,
										thisHeight - sizeToremove));
								
							}
						}
					});

					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
					}
				}

				if (show)
					showComponente();
				else
					hideComponente();

				
				try {
					Thread.sleep(20);
				} catch (InterruptedException ex) {
				}
				animationThread = null;
			}

		});

		animationThread.start();
	}

	public boolean isToggleStateTrue() {
		return toggleState;
	}

	public JButton getToggleButton() {
		return toggleButton;
	}

	public PlaylistInterface getPli() {
		return pli;
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
