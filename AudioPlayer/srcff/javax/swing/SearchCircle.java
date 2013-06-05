package javax.swing;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * SearchCircle Daus/Bellmann (c) 2013
 * 
 * @author Oliver Daus / Jens Bellmann
 * @version 1.9.5
 * 
 *          Description: A round search and progress bar
 * 
 *          License:
 * 
 *             CreativeCommons Attribution-NonCommercial-ShareAlike 3.0 Unported
 *             (CC BY-NC-SA 3.0)
 * 
 *             For more Informations:
 *             http://creativecommons.org/licenses/by-nc-sa/3.0/
 * 
 * 
 *          Version: 1.9.5
 *           - FIX: Button ist not fully dragable at the bar ends
 * 
 *          Version: 1.9.4
 *           - FIX: Button dragging sometimes drags other
 *                  SearchCircles too (only added components)
 *           - ADD: </code>considerInsetsOnMouseDispatch<code> Method 
 *          
 *          Version: 1.9.3
 *           - ADD: Better mouse interacting
 *           - ADD: Components with in the SearchCircle (SearchCircel in SearchCircle)
 *           - FIX: Improved Listener
 *           - ADD: Key function
 * 
 *          Version: 1.9.2
 *           - ADD: Listener
 * 
 **/
public class SearchCircle extends JButton implements MouseListener,
		MouseMotionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Enums
	public static final int BAR_DIRECTION_LEFT = -1;
	public static final int BAR_DIRECTION_RIGHT = 1;

	private static final double buttonPosAngleCorrectionTolerance = 0.1;
	private static final double barPartSpaceAngleFixVaule = 200;

	public boolean debug = false;

	public static enum POS {
		CENTER, LEFT, RIGHT;
	}

	// style
	public static enum STYLE {
		SMOOTH, PARTS;
	}

	// Mouse
	private ArrayList<Component> passMouse = new ArrayList<Component>();
	private boolean considerInsets = false;

	// listener
	private ArrayList<SearchCricleListener> searchCricleListener = new ArrayList<SearchCricleListener>();

	// parts
	public double STYLE_PARTS_barPartSpaceAngleStyleValue = 3.5;
	public int STYLE_PARTS_barPartWidth = 5;

	// smooth
	public double STYLE_SMOOTH_barPartSpaceAngleStyleValue = 1;
	public int STYLE_SMOOTH_barPartWidth = 3;

	// values
	private double maximum = 100; // in %
	private double minimum = 0; // in %
	private double barValue = 0; // in %
	private double buttonValue = 0;
	private boolean mouseEvent = false; // true if a mouse button is pressed
	private boolean keyEvent = false; // true if a key is pressed
	private double keyScrollamount = -1; // -1 == auto
	// pivot
	private int searchCirclePivotX;
	private int searchCirclePivotY;

	// images
	private Image imgBar;
	private Image imgButton;
	private Image imgButtonSelected;
	private Image imgBarBackground;

	// images Backups
	private Image imgOrgBar;
	private Image imgOrgButton;
	private Image imgOrgBarBackground;

	// desing
	private STYLE style = STYLE.SMOOTH;
	private boolean showButton = true;
	private boolean rotateButton = true;
	private double viewAngle = 360;
	private boolean barRotated180 = false;

	private int barDirection = BAR_DIRECTION_RIGHT;
	private double startAngle = 0;
	private double barPartSpaceAngle = 1; // @see recalcBarPartSpaceAngle();
	private int barBoundsX = 10;
	private int barBoundsY = 10;
	private int barPartWidth = 3;
	private int barPartHeight = 0;
	private int buttonHeight = 18;
	private int buttonWidth = 18;
	private int buttonJutOut = 5;
	private POS anchor = POS.CENTER;
	private Alignment alignment = new Alignment(0, 0);

	// current state
	private Image debugScreen;
	private Image currentBar;
	private Image currentButton;

	// vectors
	private Vector2d povit = new Vector2d();
	private Vector2d startPos = new Vector2d();
	private Vector2d mousePos = new Vector2d();
	private Vector2d povitStartPos = new Vector2d(-povit.getX()
			+ startPos.getX(), -povit.getY() + startPos.getY()); // Vector from
																	// Povit to
																	// startpos
	private Vector2d povitMousePos = new Vector2d(-povit.getX()
			+ mousePos.getX(), -povit.getY() + mousePos.getY()); // Vector from
																	// povit to
																	// mousepos
	private Vector2d buttonPosAtAngle = povit.add(povitStartPos).rotate(
			Math.toRadians(0 * barDirection), povit);

	public SearchCircle() {
		// we don't want to see anything from the Button ;)
		// this.setBorder(BorderFactory.createEmptyBorder());
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);

		// init ...
		this.setSize(new Dimension(300, 300));
		this.setPreferredSize(new Dimension(300, 300));

		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// init
		reloadDefaultImages();

		setButtonValue(buttonValue);
		setBarValue(barValue);
	}

	public void reloadDefaultImages() {
		// load Images
		imgBar = new ImageIcon(getClass().getResource(
				"searchcircle/skin/bar.png")).getImage();
		imgBarBackground = new ImageIcon(getClass().getResource(
				"searchcircle/skin/barbackground.png")).getImage();
		imgButton = new ImageIcon(getClass().getResource(
				"searchcircle/skin/button.png")).getImage();
		imgButtonSelected = new ImageIcon(getClass().getResource(
				"searchcircle/skin/buttonSelected.png")).getImage();

		// create Backups
		imgOrgBar = imgBar;
		imgOrgBarBackground = imgBarBackground;
		imgOrgButton = imgButton;

		recalcThickness();
		recalcImageDesing();
		recalcBarPartSpaceAngle();
	}

	public void addSearchCricleListener(SearchCricleListener scl) {
		searchCricleListener.add(scl);
	}

	public void removeSearchCricleListener(SearchCricleListener scl) {
		searchCricleListener.remove(scl);
	}

	public double getValue(double angle) {

		double max = (double) maximum;
		double min = (double) minimum;

		double value = ((max - min) / viewAngle * (angle - startAngle)) + min;

		double gap = ((max - min) / viewAngle * (360 - 300)) + min;

		if (value > maximum + gap / 2) {
			value = minimum;
		} else if (value > maximum) {
			value = maximum;
		}

		return value;
	}

	public double getAngle(double value) {

		double max = (double) maximum;
		double min = (double) minimum;

		double angle = ((viewAngle / (max - min)) * value)
				- ((viewAngle / (max - min)) * minimum);
		angle += (startAngle * barDirection);

		return angle;
	}

	public double recalcMouseAngle() {
		double angle = povitMousePos.angle(povitStartPos); // calculate angle
															// ...

		buttonPosAtAngle = povit.add(povitStartPos).rotate(
				Math.toRadians(angle * barDirection), povit);
		double correctionAngle = povitMousePos.angle(buttonPosAtAngle
				.sub(povit)); // calculate angle ...

		// correct NaN error
		if (Double.doubleToLongBits(correctionAngle) == Double
				.doubleToLongBits(Double.NaN))
			correctionAngle = 0.0;

		if (correctionAngle >= buttonPosAngleCorrectionTolerance) {
			angle = 180 + (180 - angle);
		}

		return angle;
	}

	/**
	 * returns the Value of the Mouse on the circle (minimum - maximum)
	 * 
	 * @param event
	 */
	public double getMouseClickValue(double posX, double posY) {
		// povit = new Vector2d(searchCirclePivotX, searchCirclePivotY); //povit
		// of SearchCircle
		// mousePos = new Vector2d(posX, posY); //mouse click pos from (0,0)
		// startPos = new Vector2d(searchCirclePivotX, barBoundsY); //bar start
		// point from (0,0)
		//
		// startPos = startPos.rotate(Math.toRadians(startAngle), povit);

		recalcVectors(posX, posY);

		// Vector2d povitStartPos = new Vector2d( -povit.getX() +
		// startPos.getX(), -povit.getY() + startPos.getY() ); //Vector from
		// Povit to startpos
		// Vector2d povitMousePos = new Vector2d( -povit.getX() +
		// mousePos.getX(), -povit.getY() + mousePos.getY() ); //Vector from
		// povit to mousepos

		double angle = recalcMouseAngle();

		// povitMousePos.angle(povitStartPos); //calculate angle ...
		//
		// Vector2d buttonPosAtAngle =
		// povit.add(povitStartPos).rotate(Math.toRadians(angle * barDirection),
		// povit);
		// double correctionAngle =
		// povitMousePos.angle(buttonPosAtAngle.sub(povit)); //calculate angle
		// ...
		//
		// //correct NaN error
		// if (Double.doubleToLongBits(correctionAngle) ==
		// Double.doubleToLongBits(Double.NaN)) correctionAngle = 0.0;
		//
		// if (correctionAngle >= buttonPosAngleCorrectionTolerance) {
		// angle = 180 + (180 - angle) ;
		// }

		angle += startAngle; // startpos ...

		if (debug) {
			BufferedImage debug = new BufferedImage(this.getSize().width,
					this.getSize().height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = debug.createGraphics();

			// set start angle
			g.setColor(Color.ORANGE);
			g.drawLine(0, 0, (int) povit.getX(), (int) povit.getY());
			g.setColor(Color.GREEN);
			g.drawLine(0, 0, (int) mousePos.getX(), (int) mousePos.getY());
			g.setColor(Color.BLUE);
			g.drawLine(0, 0, (int) startPos.getX(), (int) startPos.getY());
			g.setColor(Color.RED);
			g.drawLine((int) povit.getX(), (int) povit.getY(),
					(int) povit.add(povitStartPos).getX(),
					(int) povit.add(povitStartPos).getY());
			g.setColor(Color.YELLOW);
			g.drawLine((int) povit.getX(), (int) povit.getY(),
					(int) povit.add(povitMousePos).getX(),
					(int) povit.add(povitMousePos).getY());
			g.setColor(Color.DARK_GRAY);
			g.drawLine((int) povit.getX(), (int) povit.getY(),
					(int) buttonPosAtAngle.getX(),
					(int) buttonPosAtAngle.getY());

			this.debugScreen = debug;

			double a = (getAngle(getValue(angle)) - (startAngle * barDirection))
					* barDirection;
			Vector2d d = startPos.rotate(Math.toRadians(a), povit);
			// System.out.println(d.getX() + " | " + d.getY() + " | " + a);
			g.setColor(Color.PINK);
			g.drawLine((int) povit.getX(), (int) povit.getY(), (int) d.getX(),
					(int) d.getY());

			g.dispose();

		}

		return getValue(angle);
	}

	// public void setSize(Dimension size) {
	// Dimension d = size;
	//
	// super.setSize(d);
	// }
	//
	// public Dimension getSize() {
	// super.setSize(700, 700);
	// return new Dimension(300, 300);
	// }

	private void recalcPovit(Dimension size) {
		if (anchor == POS.CENTER) {
			searchCirclePivotX = this.getSize().width / 2 + alignment.getX();
			searchCirclePivotY = this.getSize().height / 2 + alignment.getY();

		} else if (anchor == POS.LEFT) {
			searchCirclePivotX = this.getSize().height / 2 + alignment.getX();
			searchCirclePivotY = this.getSize().height / 2 + alignment.getY();

		} else if (anchor == POS.RIGHT) {
			searchCirclePivotX = this.getSize().width
					- (this.getSize().height / 2) + alignment.getX();
			searchCirclePivotY = this.getSize().height / 2 + alignment.getY();

		}
	}

	public void recalcPovit() {
		recalcPovit(this.getSize());
	}

	/**
	 * sets the Bar to the specified value
	 * 
	 * value must be in range of minimum and maximum
	 * 
	 * @param value
	 */
	public void setBarValue(double value) {
		double oldValue = barValue;

		// value is not in rage...
		if (value > maximum) {
			value = maximum;
		} else if (value < minimum) {
			value = minimum;
		}

		barValue = value;

		for (SearchCricleListener scl : searchCricleListener) {
			scl.onBarChange(new SearchCircleChangeEvent(this, oldValue,
					barValue));
		}

		recalcPovit();
		recalcBarPartSpaceAngle();

		// create an Image and an Graphics Object from the Image to draw on it
		// ...
		BufferedImage bar = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bar.createGraphics();
		g.setRenderingHints(getRenderingHints());

		// set start angle
		g.rotate(Math.toRadians(startAngle - barPartSpaceAngle * barDirection),
				searchCirclePivotX, searchCirclePivotY); // rotate

		// rotate 180?
		BufferedImage imgBarRotated = null;
		if (barRotated180) {
			imgBarRotated = new BufferedImage(imgBar.getWidth(null),
					imgBar.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gg = imgBarRotated.createGraphics();
			gg.setRenderingHints(getRenderingHints());
			int ix = imgBarRotated.getWidth() / 2;
			int iy = imgBarRotated.getHeight() / 2;
			gg.rotate(Math.toRadians(180), ix, iy);
			gg.drawImage(imgBar, 0, 0, null);
			gg.dispose();
		}

		// draws the Bar to the specified value
		for (double index = getAngle(minimum) - (startAngle * barDirection); index <= (getAngle(value) - (startAngle * barDirection))
				/ barPartSpaceAngle; index++) {
			g.rotate(Math.toRadians(barPartSpaceAngle * barDirection),
					searchCirclePivotX, searchCirclePivotY); // rotate

			if (imgBarRotated != null) {
				g.drawImage(imgBarRotated, searchCirclePivotX, barBoundsY,
						barPartWidth, barPartHeight, this); // draw
			} else {
				g.drawImage(imgBar, searchCirclePivotX, barBoundsY,
						barPartWidth, barPartHeight, this); // draw
			}
		}

		g.dispose();

		currentBar = bar;
	}

	private Image rotateImage180(Image image) {
		if (barRotated180) {
			BufferedImage img = new BufferedImage(image.getWidth(null),
					image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setRenderingHints(getRenderingHints());

			int ix = img.getWidth() / 2;
			int iy = img.getHeight() / 2;
			g.rotate(Math.toRadians(180), ix, iy);
			g.drawImage(image, 0, 0, null);

			g.dispose();
			return img;
		} else {
			return image;
		}
	}

	public void setBarRotated180(boolean rotated) {
		barRotated180 = rotated;
		if (barRotated180) {
			rotateImage180(imgBar);
		}
	}

	public boolean getBarRotated180() {
		return barRotated180;
	}

	/**
	 * sets the Button to the specified value
	 * 
	 * value must be in range of minimum and maximum
	 * 
	 * @param value
	 */
	public synchronized void setButtonValueButEvent(double value) {
		if (!mouseEvent && !keyEvent)
			setButtonValue(value);
	}

	/**
	 * sets the Button to the specified value
	 * 
	 * value must be in range of minimum and maximum
	 * 
	 * @param value
	 */
	public synchronized void setButtonValue(double value) {
		double oldValue = buttonValue;

		// value is not in rage...
		if (value > maximum) {
			value = maximum;
		} else if (value < minimum) {
			value = minimum;
		}

		buttonValue = value;

		for (SearchCricleListener scl : searchCricleListener) {
			scl.onButtonChange(new SearchCircleChangeEvent(this, oldValue,
					barValue));
		}

		recalcPovit();
		recalcBarPartSpaceAngle();

		// create an Image and an Graphics Object from the Image to draw on it
		// ...
		BufferedImage button = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = button.createGraphics();
		g.setRenderingHints(getRenderingHints());

		// draws the Button to the specified value
		g.rotate(Math.toRadians(getAngle(value) * barDirection),
				searchCirclePivotX, searchCirclePivotY); // rotate

		if (rotateButton) {
			int ix = (searchCirclePivotX - buttonWidth / 2) + buttonWidth / 2;
			int iy = (barBoundsY - buttonJutOut) + buttonHeight / 2;
			g.rotate(Math.toRadians((getAngle(value) * barDirection)) * -1, ix,
					iy);
		}

		if (this.hasFocus()){
			g.drawImage(imgButtonSelected, searchCirclePivotX - buttonWidth / 2, barBoundsY
					- buttonJutOut, buttonWidth, buttonHeight, this); // draw	
		}else{
			g.drawImage(imgButton, searchCirclePivotX - buttonWidth / 2, barBoundsY
					- buttonJutOut, buttonWidth, buttonHeight, this); // draw
		}
		
		g.dispose();

		currentButton = button;

	}

	public boolean isMouseChangingButtonValue() {
		return mouseEvent;
	}

	private Image drawBackground() {

		recalcPovit();
		int lowValue = this.getSize().height;
		if (this.getSize().width < this.getSize().height) {
			lowValue = this.getSize().width;
		}
		double backgroundPartSpaceAngle = barPartSpaceAngleFixVaule / lowValue
				* STYLE_SMOOTH_barPartSpaceAngleStyleValue;

		// create an Image and an Graphics Object from the Image to draw on it
		// ...
		BufferedImage bar = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bar.createGraphics();
		g.setRenderingHints(getRenderingHints());

		// set start angle
		g.rotate(
				Math.toRadians(startAngle - backgroundPartSpaceAngle
						* barDirection), searchCirclePivotX, searchCirclePivotY); // rotate

		// draws the Bar to the specified value
		for (double index = getAngle(minimum) - (startAngle * barDirection); index <= (getAngle(maximum) - (startAngle * barDirection))
				/ backgroundPartSpaceAngle; index++) {
			g.rotate(Math.toRadians(backgroundPartSpaceAngle * barDirection),
					searchCirclePivotX, searchCirclePivotY); // rotate
			g.drawImage(imgBarBackground, searchCirclePivotX, barBoundsX,
					STYLE_SMOOTH_barPartWidth, barPartHeight, this); // draw
		}

		g.dispose();
		return bar;
	}

	/**
	 * repaints the search circle image
	 * 
	 * @param image
	 */
	public void repaintImages() {

		setBarValue(barValue);
		setButtonValue(buttonValue);

		// create an Image and an Graphics Object from the Image to draw on it
		// ...
		BufferedImage img = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHints(getRenderingHints());

		recalcPovit();

		// //background
		// g.setColor(Color.LIGHT_GRAY);
		// g.drawOval((searchCirclePivotX - this.getSize().height / 2) +
		// barBoundsX - 1, barBoundsY - 1, this.getSize().height - 2 *
		// barBoundsX + 1, this.getSize().height - 2 * barBoundsY + 1);
		// g.setColor(Color.LIGHT_GRAY);
		// g.drawOval((searchCirclePivotX - this.getSize().height / 2) +
		// barBoundsX + barPartHeight, barBoundsX + barPartHeight,
		// img.getHeight() - 2 * barBoundsX - 2 * barPartHeight - 1,
		// img.getHeight() - 2 * barBoundsX - 2 * barPartHeight - 1);
		//
		g.drawImage(drawBackground(), 0, 0, img.getWidth(), img.getHeight(),
				this); // draw bar

		// bar
		g.drawImage(currentBar, 0, 0, img.getWidth(), img.getHeight(), this); // draw
																				// bar

		if (showButton) {
			// button
			g.drawImage(currentButton, 0, 0, img.getWidth(), img.getHeight(),
					this); // draw button
		}

		if (debug) {
			// debug
			g.drawImage(debugScreen, 0, 0, img.getWidth(), img.getHeight(), this); // draw
																				// debug
																				// ...
		}

		g.dispose();

		// add to JLabel
		this.setIcon(new ImageIcon(img));
	}

	public RenderingHints getRenderingHints() {
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON); // enable Antialiasing ...
		
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY); // set render to HQ ...

		return hints;
	}

	public void recalcVectors(double mousePosX, double mousePosY) {
		povit = new Vector2d(searchCirclePivotX, searchCirclePivotY); // povit
																		// of
																		// SearchCircle
		mousePos = new Vector2d(mousePosX, mousePosY); // mouse click pos from
														// (0,0)
		startPos = new Vector2d(searchCirclePivotX, barBoundsY); // bar start
																	// point
																	// from
																	// (0,0)

		startPos = startPos.rotate(Math.toRadians(startAngle), povit);

		povitStartPos = new Vector2d(-povit.getX() + startPos.getX(),
				-povit.getY() + startPos.getY()); // Vector from Povit to
													// startpos
		povitMousePos = new Vector2d(-povit.getX() + mousePos.getX(),
				-povit.getY() + mousePos.getY()); // Vector from povit to
													// mousepos
	}

	public boolean isMouseOverComponent(int x, int y) {

		recalcVectors(x, y);
                
		double mRad = povitMousePos.length();
		double maxRad = povitStartPos.length() + buttonJutOut;
		double minRad = povitStartPos.length() - barPartHeight - buttonJutOut;

		double angle = recalcMouseAngle();
 
		if (angle <= (viewAngle + buttonJutOut) ||  //<buttonJutOut>px over <viewangle>
                    angle >= (360 - buttonJutOut) &&        //<buttonJutOut>px before startangle
                    mRad < maxRad && mRad > minRad) {       //vectorelength +-<buttonJutOut>px
			return true;
		}

		return false;
	}

	/**
	 * This considers the componets insets on a mouse event dispatch<br>
	 * <br>
	 * Enable this if the dispatched mouse event provides wrong mouse position
	 * values
	 * 
	 * @param arg0
	 *            consider insets
	 */
	public void setConsiderInsetsOnMouseDispatch(boolean arg0) {
		considerInsets = arg0;
	}

	public boolean isConsiderInsetsOnMouseDispatch() {
		return considerInsets;
	}

	private void dispatchMouseEvent(MouseEvent event) {
		for (Component component : passMouse) {
			int offsetX = this.getX();
			int offsetY = this.getY();

			if (considerInsets) {
				offsetX += this.getInsets().left;
				offsetY += this.getInsets().top;
			}

			component.dispatchEvent(setOffset(event, offsetX, offsetY));
		}
	}

	public void addParentMouseListener(Component component) {
		passMouse.add(component);
	}

	public void removeParentMouseListener(Component component) {
		passMouse.remove(component);
	}

	private MouseEvent setOffset(MouseEvent event, int xOffset, int yOffset) {
		return new MouseEvent(this, event.getID(), event.getWhen(),
				event.getModifiers(), event.getX() + xOffset, event.getY()
						+ yOffset, event.getClickCount(),
				event.isPopupTrigger());
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY())) {
			if (this.isEnabled()) {
				mouseEvent = true;
				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseClicked(new SearchCircleMouseEvent(this, event));
				}
				mouseEvent = false;
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY())) {
			if (this.isEnabled()) {
				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseEntered(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY())) {
			if (this.isEnabled()) {
				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseExited(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY())) {
			if (this.isEnabled()) {
				this.requestFocus();

				mouseEvent = true;
				setButtonValue(getMouseClickValue(event.getX(), event.getY()));

				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMousePressed(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY()) || mouseEvent) {
			if (this.isEnabled()) {

				mouseEvent = false;

				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseReleased(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (mouseEvent) {
			if (this.isEnabled()) {
				setButtonValue(getMouseClickValue(event.getX(), event.getY()));

				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseDragged(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (isMouseOverComponent(event.getX(), event.getY())) {
			if (this.isEnabled()) {
				for (SearchCricleListener scl : searchCricleListener) {
					scl.onMouseMoved(new SearchCircleMouseEvent(this, event));
				}
			}
		} else
			dispatchMouseEvent(event);
	}

	@Override
	public void paintComponent(Graphics g) {

		// make sure that the circle is always in the JLabel
		if (getSize().height > getSize().width) {
			setSize(new Dimension(getSize().width, getSize().width));
		}

		super.paintComponent(g);
		repaintImages();
	}

	private void processSCKeyEvent(final KeyEvent e) {

		keyEvent = true;

		Thread ke = new Thread(new Runnable() {
			@Override
			public void run() {
				while (keyEvent) {

					double amount = ((maximum - minimum) / viewAngle) / 3;
					if (amount <= 0)
						amount = 1;

					if (keyScrollamount >= 0)
						amount = keyScrollamount;

					if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						setButtonValue(getButtonValue() + amount);

					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						setButtonValue(getButtonValue() - amount);
					}

					for (int i = 0; i < 250; i++) {
						if (!keyEvent)
							break;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		ke.setName("SearchCircle(" + this.getName() + ")@" + this.hashCode()
				+ "#KeyEventThread");
		ke.start();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (this.isEnabled()) {

			if (!keyEvent) {
				for (SearchCricleListener scl : searchCricleListener) {
					scl.onKeyPressed(new SearchCircleKeyEvent(this, event));
				}
			}

			processSCKeyEvent(event);

			for (SearchCricleListener scl : searchCricleListener) {
				scl.onKeyHold(new SearchCircleKeyEvent(this, event));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (this.isEnabled() || keyEvent) {

			keyEvent = false;

			for (SearchCricleListener scl : searchCricleListener) {
				scl.onKeyReleased(new SearchCircleKeyEvent(this, event));
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (this.isEnabled()) {
			for (SearchCricleListener scl : searchCricleListener) {
				scl.onKeyTyped(new SearchCircleKeyEvent(this, event));
			}
		}
	}

	public double getBarValue() {
		return barValue;
	}

	public double getButtonValue() {
		return buttonValue;
	}

	public double getMaximum() {
		return maximum;
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public boolean isKeyEvent() {
		return keyEvent;
	}

	public boolean isMousePressed() {
		return mouseEvent;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * default image size: 9 x 20
	 * 
	 * @param img
	 */
	public void setBarImage(Image img) {
		imgBar = img;
		imgOrgBar = imgBar;
		recalcThickness();
		recalcImageDesing();
	}

	/**
	 * default image size: 50 x 50
	 * 
	 * @param img
	 */
	public void setButtondImage(Image img) {
		imgButton = img;
		imgOrgButton = imgButton;
		recalcThickness();
		recalcImageDesing();
	}

	/**
	 * default image size: 9 x 20
	 * 
	 * @param img
	 */
	public void setBarBackgroundImage(Image img) {
		imgBarBackground = img;
		imgOrgBarBackground = imgBarBackground;
		recalcThickness();
		recalcImageDesing();
	}

	public Image getBarImage() {
		return imgBar;
	}

	public Image getButtonImage() {
		return imgButton;
	}

	public Image getBarBackgroundImage() {
		return imgBarBackground;
	}

	public Image getOriginalBarImage() {
		return imgOrgBar;
	}

	public Image getOriginalButtonImage() {
		return imgOrgButton;
	}

	public Image getOriginalBarBackgroundImage() {
		return imgOrgBarBackground;
	}

	public void setButtonVisible(boolean showButton) {
		this.showButton = showButton;
	}

	public boolean getButtonVisible() {
		return showButton;
	}

	public void setButtonJutOut(int amount) {
		buttonJutOut = amount;
	}

	public int getButtonJutOut() {
		return buttonJutOut;
	}

	public void setButtonSize(Dimension size) {
		buttonWidth = size.width;
		buttonHeight = size.height;
	}

	public Dimension getButtonSize() {
		return new Dimension(buttonWidth, buttonHeight);
	}

	public void setKeyScrollamount(double keyScrollamount) {
		this.keyScrollamount = keyScrollamount;
	}

	public double getKeyScrollamount() {
		return keyScrollamount;
	}

	public void setRotateButton(boolean rotate) {
		this.rotateButton = rotate;
	}

	public boolean getRotateButton() {
		return rotateButton;
	}

	public void setBarThickness(int thickness) {
		barPartHeight = thickness;
		recalcButtonSize();
	}

	public int getBarThickness() {
		return barPartHeight;
	}

	public void recalcThickness() {
		barPartHeight = imgBar.getHeight(null) / 2; // calculate default
													// thickness from the image
													// height
		recalcButtonSize();
	}

	public void recalcButtonSize() {
		int oldbuttonHeight = buttonHeight;
		buttonHeight = barPartHeight + (2 * buttonJutOut); // calculate default
															// button size ...

		int diff = Math.abs(buttonHeight - oldbuttonHeight);
		if (oldbuttonHeight > buttonHeight)
			diff *= -1;

		buttonWidth += diff;
	}

	public void recalcImageDesing() {
		// set image size ...
		buttonHeight = 10 + barPartHeight;
		buttonWidth = 10 + barPartHeight;
		recalcThickness();
	}

	/**
	 * @see recalcBarPartSpaceAngle(double barPartSpaceAngleStyleValue, int
	 *      barPartWidth)
	 */
	public void recalcBarPartSpaceAngle() {
		double barPartSpaceAngleStyleValue = 1;
		if (style == STYLE.PARTS) {
			barPartSpaceAngleStyleValue = STYLE_PARTS_barPartSpaceAngleStyleValue;
			barPartWidth = STYLE_PARTS_barPartWidth;
		} else if (style == STYLE.SMOOTH) {
			barPartSpaceAngleStyleValue = STYLE_SMOOTH_barPartSpaceAngleStyleValue;
			barPartWidth = STYLE_SMOOTH_barPartWidth;
		}

		recalcBarPartSpaceAngle(barPartSpaceAngleStyleValue);
	}

	/**
	 * At the Size of 300 x 300 (@see barPartSpaceAngleFixVaule) and a
	 * barPartWidth of 3 the barPartSpaceAngle must be equals 1 to make sure it
	 * looks smooth.
	 * 
	 * @param barPartSpaceAngleStyleValue
	 * @param barPartWidth
	 */
	public void recalcBarPartSpaceAngle(double barPartSpaceAngleStyleValue) {
		int lowValue = this.getSize().height;
		if (this.getSize().width < this.getSize().height) {
			lowValue = this.getSize().width;
		}
		barPartSpaceAngle = (barPartSpaceAngleFixVaule) / lowValue
				* barPartSpaceAngleStyleValue;
	}

	public void setStyle(STYLE style) {
		this.style = style;
	}

	public STYLE getStyle() {
		return style;
	}

	public void setStartAngle(double angle) {
		this.startAngle = angle;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setViewAngle(double angle) {
		this.viewAngle = angle;
	}

	public double getViewAngle() {
		return viewAngle;
	}

	public void setAnchor(POS position) {
		anchor = position;
	}

	public POS getAnchor() {
		return anchor;
	}

	public void setAlignment(int alignmentX, int alignmentY) {
		alignment = new Alignment(alignmentX, alignmentY);
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void setDirection(int direction) {
		barDirection = direction;
	}

	public int getDirection() {
		return barDirection;
	}

	private static class Alignment {
		private int alignmentX = 0;
		private int alignmentY = 0;

		public Alignment(int alignmentX, int alignmentY) {
			this.alignmentX = alignmentX;
			this.alignmentY = alignmentY;
		}

		public int getX() {
			return alignmentX;
		}

		public int getY() {
			return alignmentY;
		}
	}

	public void setButtonColor(Color color) {
		int thickness = getBarThickness();

		SearchCircle.ImageModifier im;
		im = new SearchCircle.ImageModifier(this.getOriginalButtonImage());
		im.setHue(SearchCircle.ImageModifier.getHSBfromColor(color).getHue());
		im.setSaturation(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getSaturation());
		im.setBrightness(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getBrightness());
		imgButton = im.modify();

		setBarThickness(thickness);
	}

	public void setBarColor(Color color) {
		int thickness = getBarThickness();

		SearchCircle.ImageModifier im;
		im = new SearchCircle.ImageModifier(this.getOriginalBarImage());
		im.setHue(SearchCircle.ImageModifier.getHSBfromColor(color).getHue());
		im.setSaturation(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getSaturation());
		im.setBrightness(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getBrightness());
		imgBar = im.modify();

		setBarThickness(thickness);
	}

	public void setBackgroundColor(Color color) {
		int thickness = getBarThickness();

		SearchCircle.ImageModifier im;
		im = new SearchCircle.ImageModifier(
				this.getOriginalBarBackgroundImage());
		im.setHue(SearchCircle.ImageModifier.getHSBfromColor(color).getHue());
		im.setSaturation(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getSaturation());
		im.setBrightness(SearchCircle.ImageModifier.getHSBfromColor(color)
				.getBrightness());
		imgBarBackground = im.modify();

		setBarThickness(thickness);
	}

	public static class ImageModifier {

		public static final float HSB_MAX_VALUE = 1.0f;
		public static final float HSB_MIN_VALUE = 0.0f;

		BufferedImage image = null;
		HSB imageHSB = new HSB(0, 0, 0);
		boolean hue = false;
		boolean saturation = false;
		boolean brightness = false;
		boolean modHue = false;
		boolean modSaturation = false;
		boolean modBrightness = false;

		public ImageModifier(Image image) {
			this.image = new BufferedImage(image.getWidth(null),
					image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			this.image.getGraphics().drawImage(image, 0, 0, null);
			this.image.getGraphics().dispose();
		}

		public ImageModifier(BufferedImage image) {
			this.image = image;
		}

		/**
		 * Applies all changes to the Image.
		 * 
		 * @return The modified image as Buffered Image.
		 */
		public BufferedImage modify() {
			BufferedImage newImage = this.image;

			Color averageColor = getAverageColor();

			for (int indexX = 0; indexX < newImage.getWidth(); indexX++) {
				for (int indexY = 0; indexY < newImage.getHeight(); indexY++) {
					// get HSB from each pixel ...
					HSB oldHSB = getHSBfromPixel(newImage, indexX, indexY);
					HSB newHSB = new HSB(oldHSB);

					float averageColorb = (getHSBfromColor(averageColor)
							.getBrightness() * 100f);
					float currentPixlb = (oldHSB.getBrightness() * 100f);
					float difference = Math.abs(averageColorb - currentPixlb);

					// ****************
					// //modify (mod HSB) pixel ...
					if (modHue)
						newHSB.setHue(getMinMax(oldHSB.getHue()
								+ imageHSB.getHue()));
					if (modSaturation)
						newHSB.setSaturation(getMinMax(oldHSB.getSaturation()
								+ imageHSB.getSaturation()));
					if (modBrightness)
						newHSB.setBrightness(oldHSB.getBrightness()
								- difference);

					// modify (set HSB) pixel ...
					if (hue)
						newHSB.setHue(getMinMax(imageHSB.getHue()));
					if (saturation)
						newHSB.setSaturation(getMinMax(imageHSB.getSaturation()));
					if (brightness && (currentPixlb < averageColorb))
						newHSB.setBrightness(getMinMax(imageHSB.getBrightness()
								- (difference / 100f)));
					if (brightness && (currentPixlb > averageColorb))
						newHSB.setBrightness(getMinMax(imageHSB.getBrightness()
								+ (difference / 100f)));
					if (brightness && (currentPixlb == averageColorb))
						newHSB.setBrightness(getMinMax(imageHSB.getBrightness()));
					// ****************

					// get RGB from HSB ...
					RGB newrgb = new RGB(Color.HSBtoRGB(newHSB.getHue(),
							newHSB.getSaturation(), newHSB.getBrightness()));

					// restore alpha ...
					newrgb.setAlpha(new RGB(image.getRGB(indexX, indexY))
							.getAlpha());

					// set pixel ...
					newImage.setRGB(indexX, indexY, newrgb.getRGB());
				}
			}

			return newImage;
		}

		public Color getAverageColor() {
			BufferedImage newImage = this.image;

			int pixelCounts = newImage.getWidth() * newImage.getHeight();

			int red = 0;
			int green = 0;
			int blue = 0;

			for (int indexX = 0; indexX < newImage.getWidth(); indexX++) {
				for (int indexY = 0; indexY < newImage.getHeight(); indexY++) {
					// get RGB from each pixel ...
					RGB rgb = getRGBfromPixel(newImage, indexX, indexY);

					red += rgb.getRed();
					green += rgb.getGreen();
					blue += rgb.getBlue();
				}
			}

			red /= pixelCounts;
			green /= pixelCounts;
			blue /= pixelCounts;

			return new Color(red, green, blue);
		}

		private float getMinMax(float value) {
			if (value > HSB_MAX_VALUE) {
				value = HSB_MAX_VALUE;
			}

			if (value < HSB_MIN_VALUE) {
				value = HSB_MIN_VALUE;
			}
			return value;
		}

		/**
		 * returns the HSB value from the given Color
		 * 
		 * @param The
		 *            color
		 * @return A instance of SearchCircle.ImageModifier.HSB
		 */
		public static HSB getHSBfromColor(Color color) {
			return new HSB((Color.RGBtoHSB(color.getRed(), color.getGreen(),
					color.getBlue(), null)));
		}

		/**
		 * returns the HSB of a Pixel from a given image.
		 * 
		 * @param the
		 *            image
		 * @param position
		 *            x
		 * @param position
		 *            y
		 * @return A instance of SearchCircle.ImageModifier.HSB
		 */
		public static HSB getHSBfromPixel(BufferedImage img, int x, int y) {
			RGB rgb = new RGB(img.getRGB(x, y));

			float[] hsbValues = Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(),
					rgb.getBlue(), null);
			HSB hsb = new HSB(hsbValues);
			return hsb;
		}

		/**
		 * returns the RGB of a Pixel from a given image.
		 * 
		 * @param the
		 *            image
		 * @param position
		 *            x
		 * @param position
		 *            y
		 * @return A instance of SearchCircle.ImageModifier.RGB
		 */
		public static RGB getRGBfromPixel(BufferedImage img, int x, int y) {
			RGB rgb = new RGB(img.getRGB(x, y));
			return rgb;
		}

		/**
		 * set the HSB of this Image. use <code>modify()</code> method the apply
		 * all changes.
		 * 
		 * @param A
		 *            instance of SearchCircle.ImageModifier.HSB
		 */
		public void SetHSB(HSB hsb) {
			this.hue = true;
			this.saturation = true;
			this.brightness = true;
			this.imageHSB = hsb;
		}

		/**
		 * Modify the HSB of this Image. use <code>modify()</code> method the
		 * apply all changes.
		 * 
		 * @param A
		 *            instance of SearchCircle.ImageModifier.HSB
		 */
		public void modifyHSB(HSB hsb) {
			this.modHue = true;
			this.modSaturation = true;
			this.modBrightness = true;
			this.imageHSB = hsb;
		}

		/**
		 * Modify the hue of the Image. use <code>modify()</code> method the
		 * apply all changes.
		 * 
		 * @param Hue
		 *            (range: 0.0f - 1.0f)
		 */
		public void modifyHue(float hue) {
			this.modHue = true;
			this.imageHSB.setHue(hue);
		}

		/**
		 * Modify the saturation of the Image. use <code>modify()</code> method
		 * the apply all changes.
		 * 
		 * @param Saturation
		 *            (range: 0.0f - 1.0f)
		 */
		public void modifySaturation(float saturation) {
			this.modSaturation = true;
			this.imageHSB.setSaturation(saturation);
		}

		/**
		 * Modify the brightness of the Image. use <code>modify()</code> method
		 * the apply all changes.
		 * 
		 * @param Brightness
		 *            (range: 0.0f - 1.0f)
		 */
		public void modifyBrightness(float brightness) {
			this.modBrightness = true;
			this.imageHSB.setBrightness(brightness);
		}

		/**
		 * set the hue of the Image. use <code>modify()</code> method the apply
		 * all changes.
		 * 
		 * @param Hue
		 *            (range: 0.0f - 1.0f)
		 */
		public void setHue(float hue) {
			this.hue = true;
			this.imageHSB.setHue(hue);
		}

		/**
		 * set the saturation of the Image. use <code>modify()</code> method the
		 * apply all changes.
		 * 
		 * @param Saturation
		 *            (range: 0.0f - 1.0f)
		 */
		public void setSaturation(float saturation) {
			this.saturation = true;
			this.imageHSB.setSaturation(saturation);
		}

		/**
		 * set the brightness of the Image. use <code>modify()</code> method the
		 * apply all changes.
		 * 
		 * @param Brightness
		 *            (range: 0.0f - 1.0f)
		 */
		public void setBrightness(float brightness) {
			this.brightness = true;
			this.imageHSB.setBrightness(brightness);
		}

		public static class HSB {

			private float hue = 0x00;
			private float saturation = 0x00;
			private float brightness = 0x00;

			public HSB() {
			}

			public HSB(HSB hsb) {
				this.hue = hsb.getHue();
				this.saturation = hsb.getSaturation();
				this.brightness = hsb.getBrightness();
			}

			public HSB(float hue, float saturation, float brightness) {
				setHSB(hue, saturation, brightness);
			}

			public HSB(float[] hsbValues) {
				setHSB(hsbValues);
			}

			public float[] getHSB() {
				float[] hsb = { hue, saturation, brightness };
				return hsb;
			}

			public float getHue() {
				return hue;
			}

			public float getSaturation() {
				return saturation;
			}

			public float getBrightness() {
				return brightness;
			}

			public void setHSB(float hue, float saturation, float brightness) {
				this.hue = hue;
				this.saturation = saturation;
				this.brightness = brightness;
			}

			public void setHSB(float[] hsbValues) {
				if (hsbValues.length == 3)
					setHSB(hsbValues[0], hsbValues[1], hsbValues[2]);
			}

			public void setHue(float hue) {
				this.hue = hue;
			}

			public void setSaturation(float saturation) {
				this.saturation = saturation;
			}

			public void setBrightness(float brightness) {
				this.brightness = brightness;
			}

			public String toString() {
				return "[h=" + (hue * 360f) + ";s=" + (saturation * 100f)
						+ ";b=" + (brightness * 100f) + "]";
			}
		}

		public static class RGB {

			private short alpha = 0x00;
			private short red = 0x00;
			private short green = 0x00;
			private short blue = 0x00;

			public RGB() {
			}

			public RGB(int rgb) {
				splitRGBValue(rgb);
			}

			private void splitRGBValue(int rgb) {
				alpha = (short) ((rgb >> 24) & 0xFF);
				red = (short) ((rgb >> 16) & 0xFF);
				green = (short) ((rgb >> 8) & 0xFF);
				blue = (short) ((rgb) & 0xFF);
			}

			private int makeRGBValue() {
				return ((alpha) << 24) | (((int) ((red))) << 16)
						| (((int) ((green))) << 8) | ((int) ((blue)));
			}

			public int getRGB() {
				return makeRGBValue();
			}

			public short getAlpha() {
				return alpha;
			}

			public short getRed() {
				return red;
			}

			public short getGreen() {
				return green;
			}

			public short getBlue() {
				return blue;
			}

			public void setRGB(int rgb) {
				splitRGBValue(rgb);
			}

			public void setAlpha(short alpha) {
				this.alpha = alpha;
			}

			public void setRed(int r) {
				this.red = (short) r;
			}

			public void setGreen(int g) {
				this.green = (short) g;
			}

			public void setBlue(int b) {
				this.blue = (short) b;
			}

			public String toString() {
				return "[r=" + red + ";g=" + green + ";b=" + blue + ";a="
						+ alpha + "]";
			}
		}
	}

	public static class Vector2d {

		private double x = 0.0;
		private double y = 0.0;

		public Vector2d() {

		}

		public Vector2d(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		/**
		 * @return The length oft this vector.
		 */
		public double length() {
			return Math.sqrt(this.x * this.x + this.y * this.y);
		}

		public Vector2d getDirection() {
			double abs = this.length();
			return new Vector2d(this.x / abs, this.y / abs);
		}

		public double getDistance(Vector2d vector) {
			double deltaX = this.x - vector.getX();
			double deltaY = this.y - vector.getY();
			return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		}

		/**
		 * @param vector
		 * 
		 * @return The interior angle between this and another vectors in
		 *         degrees.
		 */
		public double angle(Vector2d vector) {
			double radians = Math.acos(this.mul(vector)
					/ (this.length() * vector.length()));
			return Math.toDegrees(radians);
		}

		/**
		 * Calculates the dot product with a vector.
		 * 
		 * @param vector
		 * 
		 * @return Dot product
		 */
		public double mul(Vector2d vec) {
			return this.x * vec.getX() + this.y * vec.getY();
		}

		/**
		 * Subtracts a vector from this vector.
		 * 
		 * @param vector
		 * 
		 * @return difference
		 */
		public Vector2d sub(Vector2d vector) {
			return new Vector2d(this.getX() - vector.getX(), this.getY()
					- vector.getY());
		}

		/**
		 * Adds a vector to this vector.
		 * 
		 * @param vector
		 * 
		 * @return sum total
		 */
		public Vector2d add(Vector2d vector) {
			return new Vector2d(this.getX() + vector.getX(), this.getY()
					+ vector.getY());
		}

		/**
		 * Rotates the the Vector around a given point.
		 * 
		 * @Param phi The angle in radians (rotates clockwise)
		 * 
		 * @Param point The rotation point as a local Vector.
		 * 
		 * @Return The vector rotated by <code> phi </ code> at
		 *         <code> Point </ code>
		 */
		public Vector2d rotate(double phi, Vector2d point) {
			Vector2d vector = this.sub(point);
			vector = vector.rotate(phi);
			return vector.add(point);
		}

		/**
		 * Rotates this vector around the origin. (clockwise)
		 * 
		 * @param phi
		 *            The angle in radians (rotates clockwise)
		 * 
		 * @return The vector rotated by <code> phi </ code>
		 */
		public Vector2d rotate(double phi) {
			return new Vector2d(
					(double) (this.getX() * Math.cos(phi) - this.getY()
							* Math.sin(phi)), (double) (this.getX()
							* Math.sin(phi) + this.getY() * Math.cos(phi)));
		}

	}

	public interface SearchCricleListener {

		public void onButtonChange(SearchCircleChangeEvent event);

		public void onBarChange(SearchCircleChangeEvent event);

		public void onKeyHold(SearchCircleKeyEvent event);

		public void onKeyPressed(SearchCircleKeyEvent event);

		public void onKeyReleased(SearchCircleKeyEvent event);

		public void onKeyTyped(SearchCircleKeyEvent event);

		public void onMouseDragged(SearchCircleMouseEvent event);

		public void onMouseClicked(SearchCircleMouseEvent event);

		public void onMouseEntered(SearchCircleMouseEvent event);

		public void onMouseExited(SearchCircleMouseEvent event);

		public void onMousePressed(SearchCircleMouseEvent event);

		public void onMouseReleased(SearchCircleMouseEvent event);

		public void onMouseMoved(SearchCircleMouseEvent event);

	}

	public class SearchCricleAdapter implements SearchCricleListener {

		@Override
		public void onButtonChange(SearchCircleChangeEvent event) {}

		@Override
		public void onBarChange(SearchCircleChangeEvent event) {}

		@Override
		public void onKeyHold(SearchCircleKeyEvent event) {}

		@Override
		public void onKeyPressed(SearchCircleKeyEvent event) {}

		@Override
		public void onKeyReleased(SearchCircleKeyEvent event) {}

		@Override
		public void onKeyTyped(SearchCircleKeyEvent event) {}

		@Override
		public void onMouseDragged(SearchCircleMouseEvent event) {}

		@Override
		public void onMouseClicked(SearchCircleMouseEvent event) {}

		@Override
		public void onMouseEntered(SearchCircleMouseEvent event) {}

		@Override
		public void onMouseExited(SearchCircleMouseEvent event) {}

		@Override
		public void onMousePressed(SearchCircleMouseEvent event) {}

		@Override
		public void onMouseReleased(SearchCircleMouseEvent event) {}

		@Override
		public void onMouseMoved(SearchCircleMouseEvent event) {}
	}

	public class SearchCircleEvent {

		private SearchCircle searchCircle;

		public SearchCircleEvent(SearchCircle searchCircle) {
			this.searchCircle = searchCircle;
		}

		public SearchCircle getSearchCircle() {
			return searchCircle;
		}
	}

	public class SearchCircleChangeEvent extends SearchCircleEvent {

		private double valueBeforeChange;
		private double value;

		public SearchCircleChangeEvent(SearchCircle searchCircle,
				double oldValue, double barValue) {
			super(searchCircle);
			this.valueBeforeChange = oldValue;
			this.value = barValue;
		}

		public double getValueBeforeChange() {
			return valueBeforeChange;
		}

		public double getValue() {
			return value;
		}
	}

	public class SearchCircleMouseEvent extends SearchCircleEvent {

		private MouseEvent mouseEvent;

		public SearchCircleMouseEvent(SearchCircle searchCircle,
				MouseEvent mouseEvent) {
			super(searchCircle);
			this.mouseEvent = mouseEvent;
		}

		public MouseEvent getMouseEvent() {
			return mouseEvent;
		}
	}

	public class SearchCircleKeyEvent extends SearchCircleEvent {

		private KeyEvent keyEvent;

		public SearchCircleKeyEvent(SearchCircle searchCircle, KeyEvent keyEvent) {
			super(searchCircle);
			this.keyEvent = keyEvent;
		}

		public KeyEvent getKeyEvent() {
			return keyEvent;
		}

	}
}
