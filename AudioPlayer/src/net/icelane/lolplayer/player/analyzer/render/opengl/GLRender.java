package net.icelane.lolplayer.player.analyzer.render.opengl;

public interface GLRender {

	public void startup();
	
	public void render(double time);
	
	public void shutdown();
	
	public double GetFPS();
}
