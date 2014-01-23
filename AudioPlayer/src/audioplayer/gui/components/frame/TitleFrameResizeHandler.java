package audioplayer.gui.components.frame;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import audioplayer.gui.components.playlist.PlaylistToggleArea;


public class TitleFrameResizeHandler extends JComponent implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1234906369430567016L;

	public enum Direction{
		NONE,
		N,
		NE,
		E,
		SE,
		S,
		SW,
		W,
		NW;
	}
	
	private boolean debug_printDeltaValues = false;
	private boolean debug_drawDirectionRectangles = true;
	private boolean debug_printComponent = false;
	private boolean debug_printDirection = false;
	
	private JFrame frame;
	private Container contentPane;
	private int actionborderSize = 5;
	
	private ArrayList<DirectionRectangle> rectangles = new ArrayList<DirectionRectangle>();
	private DirectionRectangle activeRectangle;

	private boolean leftMouseButtonPressed = false;
	private boolean mousePressedInRectangle = false;
	private boolean dragging = false;
	private boolean resizing = false;
	private boolean blocked = false;
	
	private Dimension oFrameSize = new Dimension();
	private Point oFrameLoc = new Point();
	private Point oMouseP = new Point();
	
	private Cursor oldCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	private ArrayList<InputComponent> inputComponents = new ArrayList<InputComponent>();
	private InputComponent activeInputComponent = null;
	
	
	public TitleFrameResizeHandler(JFrame f) {
		this.frame = f;
		this.frame.setGlassPane(this);
		
		this.contentPane = frame.getContentPane();
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent ae) {
            	int id = ae.getID();
            	MouseEvent e = (MouseEvent) ae;
            			   e = new MouseEvent((Component) e.getSource(),
            					   e.getID(),
            					   e.getWhen(),
            					   e.getModifiers(),
            					   e.getXOnScreen() - frame.getX(),
            					   e.getYOnScreen() - frame.getY(),
            					   e.getXOnScreen(),
            					   e.getYOnScreen(),
            					   e.getClickCount(),
            					   e.isPopupTrigger(),
            					   e.getButton());

            	if (!frame.isFocused()) return;
            			   
                if (id == MouseEvent.MOUSE_DRAGGED) mouseDragged(e);
                if (id == MouseEvent.MOUSE_MOVED) mouseMoved(e);
                if (id == MouseEvent.MOUSE_PRESSED) mousePressed(e);
                if (id == MouseEvent.MOUSE_RELEASED) mouseReleased(e);
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK);
		
		setVisible(true);
	}
	
	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public int getActionBorderSize() {
		return actionborderSize;
	}

	public void setBorder(int border) {
		this.actionborderSize = border;
	}

	public ArrayList<DirectionRectangle> getRectangles() {
		return rectangles;
	}

	public DirectionRectangle getActiveRectangle() {
		return activeRectangle;
	}
	
	public boolean hasActiveRectangle() {
		return activeRectangle != null;
	}

	public Direction getDirection() {
		return activeRectangle.getDirection();
	}

	public boolean isResizing() {
		return resizing;
	}

	public boolean isDragging() {
		return dragging;
	}

	public boolean isLeftMouseButtonPressed() {
		return leftMouseButtonPressed;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public ArrayList<InputComponent> getInputComponents() {
		return inputComponents;
	}

	public InputComponent getActiveInputComponent() {
		return activeInputComponent;
	}
	
	public boolean hasActiveInputComponent() {
		return activeInputComponent != null;
	}

	public boolean isMousePressedInRectangle() {
		return mousePressedInRectangle;
	}

	public void addInputComponent(Component c){
		inputComponents.add(new InputComponent(c));
	}

	public void addInputComponent(Component c, Direction... directions){
		inputComponents.add(new InputComponent(c, directions));
	}
	
	public InputComponent findInputComponent(Component c){
		for (InputComponent inputComponent : inputComponents) {
			if (inputComponent.getComponent().equals(c))
				return inputComponent;
		}
		return null;
	}
	
	public Direction[] getNeighbourDirections(Direction direction){
		if (direction == Direction.N || direction == Direction.S){
			return new Direction[] {
					Direction.valueOf(direction.name() + "W"),
					Direction.valueOf(direction.name() + "E") };				
		}
		
		if (direction == Direction.W || direction == Direction.E){
			return new Direction[] {
					Direction.valueOf("N" + direction.name()),
					Direction.valueOf("S" + direction.name()) };				
		}
		
		if (direction == Direction.NW || direction == Direction.NE ||
			direction == Direction.SW || direction == Direction.SE){
			return new Direction[] {
					Direction.valueOf(direction.name().substring(0, 1)),
					Direction.valueOf(direction.name().substring(1)) };				
		}
		
		return new Direction[]{null, null};
	}
	
	public Direction[] getMainNeighbourDirections(InputComponent c, Direction d){
		
		Direction[] neighbours = getNeighbourDirections(d);
		
		for (int i = 0; i < neighbours.length; i++) {
			Direction[] nns = getNeighbourDirections(neighbours[i]);
		
			if (neighbours[0] == Direction.N || neighbours[0] == Direction.S){
				nns = neighbours;
				if (!c.getDirectionList().contains(neighbours[0])){
					neighbours[0] = null;
				}
				if (!c.getDirectionList().contains(neighbours[1])){
					neighbours[1] = null;
				}
				break;
			}
			
			if (!c.getDirectionList().contains(nns[0]) &&
					!c.getDirectionList().contains(nns[1]))
				neighbours[i] = null;
			else if (c.getDirectionList().contains(nns[0])){
				neighbours[i] = nns[0];
			}else if (c.getDirectionList().contains(nns[1])){
				neighbours[i] = nns[1];
			}
		}
		
		return neighbours;
	}
	
	public Direction getAlternateMainDirections(InputComponent c, Direction d){
		if (c == null) return d;
		
		Direction[] neighbours = getMainNeighbourDirections(c, d);
		
		return neighbours[0] == null ? neighbours[1] :
			neighbours[1] == null ? neighbours[0] : d;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//clear debug drawing once ...
		if (!dragging) repaint();

		dragging = true;
		
		if (isLeftMouseButtonPressed()
				&& isMousePressedInRectangle()
				&& hasActiveInputComponent()
				&& hasActiveRectangle()
				&& !isBlocked()){
			
			int mx1 = oFrameLoc.x + oMouseP.x;
			int my1 = oFrameLoc.y + oMouseP.y;
			
			int mx2 = e.getXOnScreen();
			int my2 = e.getYOnScreen();
			
			int fx = frame.getX();
			int fy = frame.getY();
			
			int oh = oFrameSize.height;
			int ow = oFrameSize.width;

			Delta delta = new Delta(mx2 - mx1, my2 - my1);
	
			int h = oh;
			int w = ow;
			int x = fx;
			int y = fy;

			Direction direction = activeRectangle.getDirection();			
			
			if(direction != Direction.NONE){
				resizing = true;
				frame.getGlassPane().setVisible(true);
			}
			
			if (direction == Direction.N || direction == Direction.NW || direction == Direction.NE) {
				delta.y.setMin(oh - frame.getMaximumSize().height);
				delta.y.setMax(oh - frame.getMinimumSize().height);
				
				h = oh - delta.y.get();
				y = oFrameLoc.y + delta.y.get();
            }
			
			if (direction == Direction.W || direction == Direction.NW || direction == Direction.SW) {
				delta.x.setMin(ow - frame.getMaximumSize().width);
				delta.x.setMax(ow - frame.getMinimumSize().width);
				
				w = ow - delta.x.get();
				x = oFrameLoc.x + delta.x.get();
            }
			
			if (direction == Direction.S || direction == Direction.SW || direction == Direction.SE) {
				delta.y.setMin(frame.getMinimumSize().height - oh);
				delta.y.setMax(frame.getMaximumSize().height - oh);
				
				h = oh + delta.y.get();
            }
			
			if (direction == Direction.E || direction == Direction.NE || direction == Direction.SE) {
				delta.x.setMin(frame.getMinimumSize().width - ow);
				delta.x.setMax(frame.getMaximumSize().width - ow);
				
				w = ow + delta.x.get();
            }
		
//			System.out.printf("%s | %s %s | %s %s | %s %s | %s %s %s %s | %s %s %s %s \n",
//					origin,
//					mx1, my1,
//					mx2, my2,
//					dx, dy,
//					oh, ow, fx, fy,
//					h, w, x, y);
			
			//pack here ????
			
			//min size
			if (frame.getMinimumSize().width > w){
				w = frame.getMinimumSize().width;
			}
			if (frame.getMinimumSize().height > h){
				h = frame.getMinimumSize().height;
			}
			
			//max size
			if (frame.getMaximumSize().width < w){
				w = frame.getMaximumSize().width;
			}
			if (frame.getMaximumSize().height < h){
				h = frame.getMaximumSize().height;
			}
			
			if (debug_printDeltaValues) System.out.printf("%s\n", delta);

        	frame.setBounds(x, y, w, h);

        	//refresh ...
        	frame.repaint();
        	
        	return;
		}

		findComponent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		findComponent(e);
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
		if(e.getButton() == MouseEvent.BUTTON1){
			leftMouseButtonPressed = true;
			oFrameSize = frame.getSize();
			oFrameLoc = new Point(frame.getX(), frame.getY());
			oMouseP = new Point(e.getX(), e.getY());
		}
		
		findComponent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			leftMouseButtonPressed = false;
			dragging = false;
			resizing = false;
			//frame.getGlassPane().setVisible(false);
		}

		findComponent(e);
	}
	
	private void calculateRectangles(Component c){
		rectangles.clear();
		
		Component pc = c.getParent();
		InputComponent ac = getActiveInputComponent();
		
		int px = 0;
		int py = 0;

		int h = 0;
		int w = 0;
		int x = 0;
		int y = 0;
		Direction d = Direction.NONE;
		
		do{
			if (pc instanceof TitleFramePane) break;
			
			px += pc.getX();
			py += pc.getY();
			
			pc = pc.getParent();
		}while (pc != null);
		
		//top
		h = actionborderSize;
		w = c.getWidth() - actionborderSize * 2;
		x = c.getX() + px + actionborderSize;
		y = c.getY() + py;
		d = Direction.N;
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
		
		//top left
		h = actionborderSize;
		w = actionborderSize;
		x = c.getX() + px;
		d = Direction.NW;
		d = getAlternateMainDirections(ac, d);
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
		
		//top right
		x = c.getX() + px + c.getWidth() - actionborderSize;
		d = Direction.NE;
		d = getAlternateMainDirections(ac, d);
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
				
		//left
		h = c.getHeight() - actionborderSize * 2;
		w = actionborderSize;
		x = c.getX() + px;
		y = c.getY() + py + actionborderSize;
		d = Direction.W;
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));

		//bottom
		h = actionborderSize;
		w = c.getWidth() - actionborderSize * 2;
		x = c.getX() + px + actionborderSize;
		y = c.getY() + py + c.getHeight() - actionborderSize;
		d = Direction.S;
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
		
		//bottom left
		h = actionborderSize;
		w = actionborderSize;
		x = c.getX() + px;
		d = Direction.SW;
		d = getAlternateMainDirections(ac, d);
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
		
		//bottom right
		x = c.getX() + px + c.getWidth() - actionborderSize;
		d = Direction.SE;
		d = getAlternateMainDirections(ac, d);
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
		
		//right
		h = c.getHeight() - actionborderSize * 2;
		w = actionborderSize;
		x = c.getX() + px + c.getWidth() - actionborderSize;
		y = c.getY() + py + actionborderSize;
		d = Direction.E;
		
		if (debug_printDirection)
			System.out.println("Direction: " + d);
		
		rectangles.add(new DirectionRectangle(x, y, w, h, d));
	}
	
	public void setActiveDirectionRectangle(Point p){
		if (dragging || isBlocked()) return;
		
		mousePressedInRectangle = false;
		activeRectangle = null;
		
		for (DirectionRectangle rectangle : rectangles) {
			if (!rectangle.contains(p)) continue;
			
			if (rectangle.contains(oMouseP))
				mousePressedInRectangle = true;
			
			activeRectangle = rectangle;
			
			break;
		}
	}

	private void setComponentCursor(){
		cursor = oldCursor;
		Cursor newCursor = cursor;
				
    	if (activeRectangle != null){;
    		newCursor = activeRectangle.getCursor();
    	}
    	
    	if (cursor.getType() != newCursor.getType()){
    		oldCursor = cursor;
    		cursor = newCursor;
    	}
    	
    	frame.setCursor(cursor);
	}
	
	private void findComponent(MouseEvent e) {
        if (dragging || isBlocked()) return;
        
		Container container = contentPane;
		Point glassPanePoint = e.getPoint();
		 
        Point p = glassPanePoint;
        Component c1 = container;
        Component c2 = c1.getComponentAt(p);
        
        while (c2 != null) {
        	for (InputComponent irc : inputComponents) {
        		Component rc = irc.getComponent();
        		
        		if (!c2.equals(rc)) continue;
        		
	        	//special for PAT
    			if (c2 instanceof PlaylistToggleArea) c2 = ((PlaylistToggleArea)c2).getToggleComponent();
    			
    			activeInputComponent = findInputComponent(c2);
    			if (activeInputComponent == null) break;
    			
    			if (debug_printComponent)
    				System.out.println("CurrentComponent: " + activeInputComponent);
    			
	        	calculateRectangles(activeInputComponent.getComponent());
	        	setActiveDirectionRectangle(glassPanePoint);
	        	setComponentCursor();
	        	
	        	break;
			}
        	
        	p = SwingUtilities.convertPoint(c1, p, c2);
        	Component c3 = c2.getComponentAt(p);
        	
        	if (c3 != null && c3.equals(c2)) break;
        	
        	c1 = c2;
        	c2 = c3;
		}

        repaint();
	}

	protected void paintComponent(Graphics g) {
		//debug!
		if (!debug_drawDirectionRectangles) return;
		
		if (dragging || isBlocked()) return;
		
		for (DirectionRectangle r : rectangles) {

			Direction[] directions = getActiveInputComponent().getDirections();
							
			for (Direction direction : directions) {
				if (direction != r.getDirection()) continue;
				
				float dh = 1f / (float)rectangles.size();
				int i = rectangles.indexOf(r);
				
				g.setColor(Color.getHSBColor(dh * (i + 1), 1f, 1f));
				g.fill3DRect(r.x, r.y, r.width, r.height, false);
				
				if (hasActiveRectangle()){
					DirectionRectangle ar = getActiveRectangle();
					
					if (ar.getDirection() != direction) continue;
					
					g.setColor(Color.white);
					g.fill3DRect(ar.x, ar.y, ar.width, ar.height, false);
				}
			}
		}
	}	
	
	
	public class InputComponent{
		
		private Component component;
		private ArrayList<Direction> directions;
		
		public InputComponent(Component component) {
			this(component, Direction.values());
		}
		
		public InputComponent(Component component, Direction[] directions) {
			super();
			this.component = component;
			setDirections(directions);
		}

		public ArrayList<Direction> getDirectionList() {
			return directions;
		}
		
		public Direction[] getDirections() {
			return directions.toArray(new Direction[]{});
		}

		public void setDirections(Direction[] directions) {
			ArrayList<Direction> dList = new ArrayList<Direction>(Arrays.asList(directions));
			
			for (Direction direction : directions) {
				if (direction == Direction.N)
					addCornerDirections(dList, direction);
				if (direction == Direction.S)
					addCornerDirections(dList, direction);
				if (direction == Direction.W)
					addCornerDirections(dList, direction);
				if (direction == Direction.E)
					addCornerDirections(dList, direction);					
			}
			
			this.directions = dList;
		}
		
		private void addCornerDirections(ArrayList<Direction> dList, Direction direction){
			if (!dList.contains(direction)) return;
			
			if (direction == Direction.N || direction == Direction.S){
				addCorner(dList, Direction.valueOf(direction.name() + "W"));
				addCorner(dList, Direction.valueOf(direction.name() + "E"));				
			}
			
			if (direction == Direction.W || direction == Direction.E){
				addCorner(dList, Direction.valueOf("N" + direction.name()));
				addCorner(dList, Direction.valueOf("S" + direction.name()));				
			}
		}
		
		private void addCorner(ArrayList<Direction> dList, Direction direction){
			if (!dList.contains(direction))
				dList.add(direction);
		}

		public Component getComponent() {
			return component;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof InputComponent){
				InputComponent ic = (InputComponent) o;
				
				return this.getComponent().equals(ic.getComponent())
						&& getDirections().equals(ic.getDirections());
			}else return super.equals(o);
		}
	}
	
	public class Delta{
		
		public DeltaValue x;
		public DeltaValue y;
		
		public Delta() {
			this(0, 0);
		}
		
		public Delta(int dx, int dy) {
			x = new DeltaValue(dx);
			y = new DeltaValue(dy);
		}
		
		@Override
		public String toString() {
			return String.format("%s{x:%s,y:%s}", super.toString(), x, y);
		}
	}
	
	public class DeltaValue{
		
		private int value;
		private int min = Integer.MIN_VALUE;
		private int max = Integer.MAX_VALUE;
		
		public DeltaValue() {
		}
		
		public DeltaValue(int value) {
			this();
			set(value);
		}
		
		@Override
		public String toString() {
			return String.format("[value:%s,min:%s,max:%s]", value, min, max);
		}
		
		public int get() {
			return value;
		}
		public void set(int value) {
			this.value = value;
			if(value < min) value = min;
			if(value > max) value = max;
		}
		
		public int min() {
			return min;
		}
		
		public void setMin(int min) {
			this.min = min;
			if(value < min) value = min;
		}
		
		public int max() {
			return max;
		}
		
		public void setMax(int max) {
			this.max = max;
			if(value > max) value = max;
		}
	}
	
	public class DirectionRectangle extends Rectangle{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4685640805715978415L;
		private Direction direction = Direction.NONE;
		private Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
				
		public DirectionRectangle() {
			super();
		}
		
		public DirectionRectangle(int x, int y, int width, int height) {
			super(x, y, width, height);
		}
		
		public DirectionRectangle(int x, int y, int width, int height, Direction direction) {
			this(x, y, width, height);
			setDirection(direction);
		}
		
		public DirectionRectangle(Point p, Dimension d, Direction direction) {
			this(p.x, p.y, d.width, d.height, direction);
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
			try {
				//set correct resize Cursor ... 
				this.cursor = new Cursor(Cursor.class.getDeclaredField(
						getDirection().toString() + "_RESIZE_CURSOR").getInt(
						Cursor.getDefaultCursor()));
			} catch (Exception e) {}
		}

		public Cursor getCursor() {
			return cursor;
		}

		public void setCursor(Cursor cursor) {
			this.cursor = cursor;
		}	
		
	}
	
}
