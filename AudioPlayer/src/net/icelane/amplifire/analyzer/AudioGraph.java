package net.icelane.amplifire.analyzer;

import java.awt.Color;
import java.awt.image.VolatileImage;
import java.util.List;
import java.util.Random;

import org.magicwerk.brownies.collections.GapList;


/**
 *  amplifier - Audio-Player Project
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
	private volatile GapList<Float> buffer = new GapList<Float>();
	private volatile int id = (int) System.currentTimeMillis() + new Random().nextInt(500);
	
	private volatile int shownValues = 1000;
	private volatile Color color = Color.red;
	private volatile int yOffset = 0;
	private volatile String name = "";
	private volatile int changesCounter;
	
	public AudioGraph() {
		for (int i = 0; i < shownValues; i++) {
			buffer.add(0f);
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
    	buffer.clear();
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

	public int getChangesCountSincSnapshot() {
		return changesCounter;
	}

	public void createSnapShot(VolatileImage img) {
		this.changesCounter = 0;
	}

	@Deprecated
	public int getShownValues() {
		return shownValues;
	}

	public int size(){
		return shownValues > buffer.size() ? buffer.size() : shownValues; 
	}
	
	@Deprecated
	public void setShownValues(int shownValues) {
		this.shownValues = shownValues;
	}

	public void addValue(float val){
		synchronized (buffer) {
			if (buffer.size() >= shownValues) {
	            buffer.removeFirst();    
	        }
			
            buffer.add(val);			
            changesCounter ++;
		}
	}
	
	public void syncBufferSize(int size){
		if (size < 1) return;
		
		shownValues = size;
		while (buffer.size() > shownValues) {
			buffer.removeFirst();
			changesCounter ++;
		}
	}
	
	public float getValue(int index){
		synchronized (buffer) {
			if (index >= buffer.size()) return 0.0f;
			return buffer.get(index);
		}
	}

	@Deprecated
	public List<Float> getBuffer() {
		return buffer;
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
