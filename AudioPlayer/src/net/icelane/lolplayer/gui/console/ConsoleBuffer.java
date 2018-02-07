package net.icelane.lolplayer.gui.console;

import java.awt.Dimension;

public class ConsoleBuffer {

	private Dimension size = new Dimension(1000, 5000);
	private char[][] buffer = new char[size.width][size.height];
	
	
	public char[][] getBuffer() {
		return buffer;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
		buffer = copyBuffer(buffer, size);
	}	
	
	public void setSizeWidth(int size) {
		setSize(new Dimension(this.size.width, size));
	}
	
	public void setSizeHeight(int size) {
		setSize(new Dimension(size, this.size.height));
	}
	
	public static char[][] copyBuffer(char[][] b, Dimension size){
		int sx = size.width > b.length ? b.length : size.width;
		int sy = size.height > b[0].length ? b[0].length : size.height;
		
		char[][] n = new char[size.width][size.height];
		
		System.arraycopy(b, 0, n, 0, sx);
		for (int i = 0; i < sx; i++) {
		    System.arraycopy(b[i], 0, n[i], 0, sy);
		}
		
		return n;
	}

}

