package audioplayer.player.analyzer;

import java.util.ArrayList;

/** Graph Interface. Basic Graph functions
 * 
 * @author Oliver
 *
 */
public interface Graph {

	public void addGraph(AudioGraph graph);

	public void removeGraph(AudioGraph graph);
	
	public AudioGraph getGraph(int index);
	
	public ArrayList<AudioGraph> getGraphs();

}
