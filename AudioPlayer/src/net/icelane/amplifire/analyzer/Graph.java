package net.icelane.amplifire.analyzer;

import java.util.ArrayList;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 * Graph Interface. Basic Graph functions
 */
public interface Graph {

	public void addGraph(AudioGraph graph);

	public void removeGraph(AudioGraph graph);
	
	public AudioGraph getGraph(int index);
	
	public ArrayList<AudioGraph> getGraphs();

	public void clearGraphs();
	
}
