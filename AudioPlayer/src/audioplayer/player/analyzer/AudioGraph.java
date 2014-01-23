package audioplayer.player.analyzer;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import org.magicwerk.brownies.collections.GapList;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 * Represents a graph (a curve)
 */
public class AudioGraph{

	/*
	 * Faster Version of an ArrayList:
	 * The remove() method of an ArrayList is too slow.
	 */
	private volatile GapList<Float> values = new GapList<Float>();
	private volatile int id = (int) System.currentTimeMillis() + new Random().nextInt(500);
	private volatile int shownValues = 1000;
	private volatile Color color = Color.red;
	private volatile int yOffset = 0;
	private volatile String name = "";
	
	public AudioGraph() {
		for (int i = 0; i < shownValues; i++) {
			values.add(0f);
		}
	}
	
	public AudioGraph(Color color) {
		this();
		this.color = color;
	}
	
	public AudioGraph(Color color, int id) {
		this(color);
		this.id = id;
	}
        
        public AudioGraph(int yOffset, Color color, int id) {
		this(color, id);
                this.yOffset = yOffset;
	}
        
        public AudioGraph(int yOffset, Color color) {
		this(color);
                this.yOffset = yOffset;
	}
        
    public void clear(){
    	values.clear();
    }
       
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getShownValues() {
		return shownValues;
	}

	public void setShownValues(int shownValues) {
		this.shownValues = shownValues;
	}

	public void addValue(float val){
		synchronized (values) {
			if (values.size() < shownValues) {
	            values.add(val);
	        } else {
	            values.removeFirst();
	            values.add(val);
	        }
		}
	}
	
	public float getValue(int index){
		synchronized (values) {
			return values.get(index);
		}
	}

	public List<Float> getValues() {
		return values;
	}

	public int getID() {
		return id;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getYOffset() {
		return yOffset;
	}

	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
		
}
