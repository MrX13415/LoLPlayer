package net.icelane.lolplayer.util.imageloader;

import java.lang.reflect.Field;

public class ImageLoader {

	private static boolean isloaded;
	
	public static boolean isLoaded(){
		return isloaded;
	}
	
	public static void loadImages(Class<?> imageClass){
		if (!isImageDataClass(imageClass)) return;
		
		for (Field f : imageClass.getDeclaredFields()) {
			try {
				if (!f.getType().equals(ImageInfo.class)) continue;
				
				ImageInfo i = (ImageInfo)f.get(null);
				System.out.println(f.getName() + " : " + i.getName() + " " + i.getHeight());
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isImageDataClass(Class<?> imageClass){
		Class<?> superClass = imageClass.getSuperclass();
		
		if (superClass.equals(ImageData.class)) return true;
		return false;
	}
	
}
