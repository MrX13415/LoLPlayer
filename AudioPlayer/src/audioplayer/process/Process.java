package audioplayer.process;

import audioplayer.Application;
import audioplayer.Control;

/**
 *
 * @author dausol
 */
public abstract class Process implements Runnable{
    
	protected Control control;
    protected boolean running;
    protected boolean reachedEnd;
    protected Thread thread;
    
    public Process(Control control) {
        this.control = control;
        this.control.getStatusbar().addProcess(this);
        thread = new Thread(this);
    }
    
    public void start() {
        if (thread == null) thread = new Thread(this);
        thread.start();
        running = true;
        if (Application.isDebug()) System.out.println("Process '" + this.getClass().getName() + "' started ...");
    }

    public void stop(){
    	running = false;
    	if (Application.isDebug()) System.out.println("Process '" + this.getClass().getName() + "' canceled ...");
    }
    
    public boolean isRunning() {
        return running || !reachedEnd;
    }

    public boolean isReachedEnd(){
        return reachedEnd;
    }
     
    public Thread getThread() {
       return thread;
    }
    
    public abstract void process();
        
    @Override
    public void run(){
    	running = true;
    	 
    	process();
    	
    	running = false;
        reachedEnd = true;
        if (Application.isDebug()) System.out.println("Process '" + this.getClass().getName() + "' ended ...");
        
        control.getStatusbar().prepareNextProcess();
    }
}
