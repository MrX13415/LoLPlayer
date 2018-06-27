package net.icelane.amplifire.util.imageloader;

import java.awt.Dimension;



public abstract class ImageData {

	public static ImageInfo set(String imagename){
		return new ImageInfo(imagename);
	}
	
	public static ImageInfo set(String imagename, int size){
		return new ImageInfo(imagename, size);
	}

	public static ImageInfo set(String imagename, Dimension size){
		return new ImageInfo(imagename, size);
	}
	
	public static ImageInfo set(String imagename, int width, int height){
		return new ImageInfo(imagename, width, height);
	}
	
	public static void get(){
		
	}
	
}
