package net.icelane.amplifire.util.imageloader;

import com.sun.glass.ui.Size;

public class ImageInfo {

	public static final int SIZE_UNDEFINED = -1;

	private String name;
	private Size size;
	
	protected ImageInfo(String name) {
		this(name, SIZE_UNDEFINED, SIZE_UNDEFINED);
	}
	
	protected ImageInfo(String name, int size) {
		this(name, size, size);
	}

	protected ImageInfo(String name, int width, int height) {
		this(name, new Size(width, height));
	}
	
	protected ImageInfo(String name, Size size) {
		super();
		this.name = name;
		this.size = size;
	}
	
	public String getName() {
		return name;
	}
	
	public void get() {

	}

	public int getWidth() {
		return size.width;
	}

	public int getHeight() {
		return size.height;
	}
	
	public Size getSize(){
		return size;
	}
	
	public boolean isWidthUndefined(){
		return size.width == SIZE_UNDEFINED;
	}
	
	public boolean isHightUndefined(){
		return size.height == SIZE_UNDEFINED;
	}
	
	public boolean isSizeUndefined(){
		return  isWidthUndefined() && isHightUndefined();
	}
}
