package net.icelane.amplifire.ui.console;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class ConsoleOutputStream extends OutputStream{

	private HashSet<PrintListener> listeners = new HashSet<PrintListener>();
	private boolean lineBegin = true;
	
	public ConsoleOutputStream() {
		super();
	}
	
	public ConsoleOutputStream(PrintListener listener) {
		this();
		addPrintListener(listener);
	}
	
	public boolean addPrintListener(PrintListener listener){
		return listeners.add(listener);
	}
	
	public boolean removePrintListener(PrintListener listener){
		return listeners.remove(listener);
	}
	
	@Override
	public void write(int b) throws IOException {
		write(new byte[]{(byte) b});
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		String t = new String(b, off, len);
		listeners.forEach(l -> l.print(t, lineBegin));
		lineBegin = t.endsWith("\n");
	}
}
