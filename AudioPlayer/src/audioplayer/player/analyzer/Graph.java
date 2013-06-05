package audioplayer.player.analyzer;

import java.util.ArrayList;

/**
 *  LoLPlayer II - Audio-Player Project
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

}
