package net.icelane.lolplayer.player.analyzer.render.opengl;

import java.util.LinkedList;

public class MovingAverage {

    private final LinkedList<Double> window = new LinkedList<Double>();
    private final int period;

    public MovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
        
        for(int index = 0; index < period; index++){
    		window.add(0.0);
    	}
    }

    public void put(double num) {
        window.add(num);
        if (window.size() > period) {
            window.remove();
        }
    }

    public double getAverage() {
    	if (window.isEmpty()) return 0.0;
    	
    	double sum = 0.0;
    	for(int index = 0; index < period; index++){
    		sum += (1.0 / period) * window.get(index);
    	}
    	
        return sum;
    }
}
