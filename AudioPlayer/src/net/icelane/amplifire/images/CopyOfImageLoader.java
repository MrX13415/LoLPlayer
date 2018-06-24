package net.icelane.amplifire.images;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import net.icelane.amplifire.Application;
import net.mrx13415.searchcircle.imageutil.ImageModifier;

/**
 *  amplifier - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class CopyOfImageLoader {
	
	public static final String Altrnative_Image_Path = "images/"; 

	public static String resName_image_play = "play.png";
	public static String resName_image_play_hover = "play_hover.png";
	public static String resName_image_play_pressed_hover = "play_pressed_hover.png";
	
	public static String resName_image_pause = "pause.png";
	public static String resName_image_pause_hover = "pause_hover.png";
	public static String resName_image_pause_pressed_hover = "pause_pressed_hover.png";
	
	public static String resName_image_stop = "stop.png";
	public static String resName_image_stop_hover = "stop_hover.png";
	public static String resName_image_stop_pressed_hover = "stop_pressed_hover.png";
	
	public static String resName_image_frw = "frw.png";
	public static String resName_image_frw_hover = "frw_hover.png";
	public static String resName_image_frw_pressed_hover = "frw_pressed_hover.png";
	
	public static String resName_image_rev = "rev.png";
	public static String resName_image_rev_hover = "rev_hover.png";
	public static String resName_image_rev_pressed_hover = "rev_pressed_hover.png";
	
	public static String resName_image_search = "search.png";
	public static String resName_image_search_pressed = "search_pressed.png";
	
	//--------------------------------------------------------------------------
	//-- image holder fields ---------------------------------------------------
	//--------------------------------------------------------------------------
	
	public static ImageIcon image_play = new ImageIcon(Altrnative_Image_Path + resName_image_play);
	public static ImageIcon image_play_hover = new ImageIcon(Altrnative_Image_Path + resName_image_play_hover);
	public static ImageIcon image_play_pressed_hover = new ImageIcon(Altrnative_Image_Path + resName_image_play_pressed_hover);
	
	public static ImageIcon image_pause = new ImageIcon(Altrnative_Image_Path + resName_image_pause);
	public static ImageIcon image_pause_hover = new ImageIcon(Altrnative_Image_Path + resName_image_pause_hover);
	public static ImageIcon image_pause_pressed_hover = new ImageIcon(Altrnative_Image_Path + resName_image_pause_pressed_hover);
	
	public static ImageIcon image_stop = new ImageIcon(Altrnative_Image_Path + resName_image_stop);
	public static ImageIcon image_stop_hover = new ImageIcon(Altrnative_Image_Path + resName_image_stop_hover);
	public static ImageIcon image_stop_pressed_hover = new ImageIcon(Altrnative_Image_Path + resName_image_stop_pressed_hover);
	
	public static ImageIcon image_frw = new ImageIcon(Altrnative_Image_Path + resName_image_frw);
	public static ImageIcon image_frw_hover = new ImageIcon(Altrnative_Image_Path + resName_image_frw_hover);
	public static ImageIcon image_frw_pressed_hover = new ImageIcon(Altrnative_Image_Path + resName_image_frw_pressed_hover);
	
	public static ImageIcon image_rev = new ImageIcon(Altrnative_Image_Path + resName_image_rev);
	public static ImageIcon image_rev_hover = new ImageIcon(Altrnative_Image_Path + resName_image_rev_hover);
	public static ImageIcon image_rev_pressed_hover = new ImageIcon(Altrnative_Image_Path + resName_image_rev_pressed_hover);
	
	public static ImageIcon image_search = new ImageIcon(Altrnative_Image_Path + resName_image_search);
	public static ImageIcon image_search_pressed = new ImageIcon(Altrnative_Image_Path + resName_image_search_pressed);
	
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	private static HashMap<String, Exception> errors = new HashMap<String, Exception>();
	
	private static ArrayList<ImageLoaderData> images = new ArrayList<CopyOfImageLoader.ImageLoaderData>();
	
	
	public static void initImageArray(){

		int size = 40;
		images.add(new ImageLoaderData("image_play", resName_image_play, size, size));
		images.add(new ImageLoaderData("image_play_hover", resName_image_play_hover, size, size));
		images.add(new ImageLoaderData("image_play_pressed_hover", resName_image_play_pressed_hover, size, size));
		
		images.add(new ImageLoaderData("image_pause", resName_image_pause, size, size));
		images.add(new ImageLoaderData("image_pause_hover", resName_image_pause_hover, size, size));
		images.add(new ImageLoaderData("image_pause_pressed_hover", resName_image_pause_pressed_hover, size, size));
		
		images.add(new ImageLoaderData("image_stop", resName_image_stop, size, size));
		images.add(new ImageLoaderData("image_stop_hover", resName_image_stop_hover, size, size));
		images.add(new ImageLoaderData("image_stop_pressed_hover", resName_image_stop_pressed_hover, size, size));
		
		images.add(new ImageLoaderData("image_frw", resName_image_frw, size, size));
		images.add(new ImageLoaderData("image_frw_hover", resName_image_frw_hover, size, size));
		images.add(new ImageLoaderData("image_frw_pressed_hover", resName_image_frw_pressed_hover, size, size));
		
		images.add(new ImageLoaderData("image_rev", resName_image_rev, size, size));
		images.add(new ImageLoaderData("image_rev_hover", resName_image_rev_hover, size, size));
		images.add(new ImageLoaderData("image_rev_pressed_hover", resName_image_rev_pressed_hover, size, size));
		
		size = 28;
		images.add(new ImageLoaderData("image_search", resName_image_search, size, size));
		images.add(new ImageLoaderData("image_search_pressed", resName_image_search_pressed, size, size));
	}
	
	public static ImageIcon setPressedHoverImgHSB(ImageIcon img){
		//Make pressed a little darker and hover lighter ... 
		ImageModifier im = new ImageModifier(img.getImage());
		im.setBrightness(-0.2f);
		return new ImageIcon(im.modify());	
	}
	
	public static ImageIcon setHoverImgHSB(ImageIcon img){
		//Make pressed a little darker and hover lighter ... 
		ImageModifier im = new ImageModifier(img.getImage());
		im.setBrightness(0.3f);
		return new ImageIcon(im.modify());	
	}
	
	public static void loadImages() {
		loadImages(null);
	}
	
	public static void loadImages(JProgressBar bar) {
		System.out.println("Load images ...\t\t\t\t");
		
		if (bar != null){
			bar.setIndeterminate(false);
			bar.setValue(10);
		}
		
		initImageArray();
	
		if (bar != null){
			bar.setValue(15);
		}

		int i = 0;
		for (ImageLoaderData img : images) {
			
			try {
				
				Field imageField = CopyOfImageLoader.class.getField(img.getField());
			
				ImageIcon image = (img.scaleImage() ? 
						loadScaledImage(img.getResourceName(), img.getWidth(), img.getHeight()) :
						loadImage(img.getResourceName()));

				imageField.set(null, image);
			} catch (Exception e) {
				errors.put(img.getResourceName(), e);
			}

			i++;
			if (bar != null) bar.setValue((int) ( (100d / (double) images.size()) * i) + 15);
		}

		if (bar != null) bar.setValue(115);
		
		if (images.isEmpty()) errors.put("Keine Bilder gefunden", new Exception());
		
		if (errors.size() > 0){
			System.out.println("Load images ...\t\t\t\tERROR");
			
			String errorlist = "<html>";
			
			for (String image : errors.keySet()) {
				errorlist += "<b>" + image + "</b>  " + (errors.get(image).getMessage() != null ? errors.get(image).getMessage() : "") + "<p>";
			}
			
			errors.clear();
			
			JOptionPane.showMessageDialog(null, "Fehler beim laden der Bilder ...\n" +
					"Die folgenden Bilder konnten nicht geladen werden:\n\n" +
					errorlist + "</html>",
					Application.App_Name, JOptionPane.ERROR_MESSAGE);
		}else System.out.println("Load images ...\t\t\t\tOK");
	}
	
	
		
	private static ImageIcon loadImage(String resourceName){
		ImageIcon re = null;
		
		try {
			String displayName = resourceName.split("/")[resourceName.split("/").length - 1];
			System.out.print("    " + displayName + "  ...");
			System.out.print(displayName.length() <= 6 ? "\t" : "");
			System.out.print(displayName.length() <= 14 ? "\t" : "");
			System.out.print(displayName.length() <= 22 ? "\t\t" : "\t");
									
			re = new ImageIcon(CopyOfImageLoader.class.getResource(resourceName));
			
			System.out.println("OK");
		} catch (Exception e) {
			System.out.println("ERROR");
			errors.put(resourceName, e);
		}
		
		return re;
	}
		
	private static ImageIcon loadScaledImage(String resourceName, int width, int height){
		ImageIcon re = null;
		
		try {
			String displayName = resourceName.split("/")[resourceName.split("/").length - 1];
			System.out.print("    " + displayName + "  ...");
			System.out.print(displayName.length() <= 6 ? "\t" : "");
			System.out.print(displayName.length() <= 14 ? "\t" : "");
			System.out.print(displayName.length() <= 22 ? "\t\t" : "\t");
					
			
			
			re = new ImageIcon(new ImageIcon(
					CopyOfImageLoader.class.getResource(resourceName)).getImage()
					.getScaledInstance(width, height, Image.SCALE_SMOOTH));
		
			System.out.println("OK");
		} catch (Exception e) {
			System.out.println("ERROR");
			errors.put(resourceName, e);
		}
		
		return re;
	}
	
	public static ArrayList<String> loadImageResourcesList(){
		String packagePath = CopyOfImageLoader.class.getPackage().getName();
		while (packagePath.contains(".")) packagePath = packagePath.replace(".", "/"); 
		
		String[] acceptedImgFileTypes = { ".png" };

		ArrayList<String> imageResFiles = new ArrayList<String>();

		try {
			System.out.print("Load Images ...\t\t\t");
			CodeSource src = CopyOfImageLoader.class.getProtectionDomain().getCodeSource();

			if (src != null) {
				URL jar = src.getLocation();
				File dir = new File(jar.getPath() + packagePath + "/");

				if (dir.canRead() && dir.isDirectory()) {

					for (File file : dir.listFiles()) {
						for (String type : acceptedImgFileTypes) {
							if (file.getPath().endsWith(type)) {

								String resourceFileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
								
								if (resourceFileName.contains("\\"))
									resourceFileName = resourceFileName.substring(resourceFileName.lastIndexOf("\\") + 1);

								imageResFiles.add(resourceFileName);
							}
						}
					}

				} else {
					ZipInputStream zip = new ZipInputStream(jar.openStream());

					ZipEntry ze = null;
					while ((ze = zip.getNextEntry()) != null) {
						if (ze.getName().startsWith(packagePath)) {
							for (String type : acceptedImgFileTypes) {
								if (ze.getName().endsWith(type)) {
									
									String resourceFileName = ze.getName().substring(ze.getName().lastIndexOf("/") + 1);
									
									if (resourceFileName.contains("\\"))
										resourceFileName = resourceFileName.substring(resourceFileName.lastIndexOf("\\") + 1);

									imageResFiles.add(resourceFileName);
								}
							}
						}
					}
				}
			}
			System.out.println("OK\n");
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}
		return imageResFiles;
	}
	
	public static class ImageLoaderData{
		
		public String field;
		public String resourceName;
		public int width;
		public int height;
		
		public ImageLoaderData(String field, String resourceName, int width, int height) {
			super();
			this.field = field;
			this.resourceName = resourceName;
			this.width = width;
			this.height = height;
		}
		
		public ImageLoaderData(String field, String resourceName) {
			this(field, resourceName, 0, 0);
		}	
	
		public boolean scaleImage(){
			if (width == 0 && height == 0) return false;
			return true;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getResourceName() {
			return resourceName;
		}

		public void setResourceName(String resourceName) {
			this.resourceName = resourceName;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}
	
}
