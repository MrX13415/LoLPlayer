package net.icelane.lolplayer;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.UIManager;

import net.icelane.lolplayer.database.sql.LoLPlayerDB;
import net.icelane.lolplayer.design.Colors;
import net.icelane.lolplayer.font.FontLoader;
import net.icelane.lolplayer.gui.AboutDialog;
import net.icelane.lolplayer.gui.console.JConsole;
import net.icelane.lolplayer.images.ImageLoader;
import net.icelane.lolplayer.images.Images;
import net.icelane.lolplayer.process.SavePlaylistProcess;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 0.1.8.2
 */ 
public class Application {

	public static String App_Name = "LoLPlayer II";
	public static String App_Version = "0.1.8.2";
	public static String App_Name_Version = App_Name + " (" + App_Version + ")";	
	public static String App_Author = "Oliver Daus";	
	public static String App_License = "CC BY-NC-SA 3.0";
    public static String App_License_Link = "http://creativecommons.org/licenses/by-nc-sa/3.0/";
        
    private static boolean debug = false;
	private static boolean waitForExit = false;
	
    private static Application application;
        
	private LoLPlayerDB database;
	
    private AppCore control;
    private JConsole console;
    
    private static Colors colors = new Colors();
            
    
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		application = new Application();
		application.initialize();
		application.proccessCommands(args);
		application.registerDEBUGclasses();
		application.run();
		
//		loadClasses(net.icelane.lolplayer.gui.UserInterface.class);
    }
	
	
//	public static <T> void loadClasses(Class<T> _Class){
//
//		String packagePath = _Class.getPackage().getName();
//		while (packagePath.contains(".")) packagePath = packagePath.replace(".", "/"); 
//		
//		String[] acceptedImgFileTypes = { ".class" };
//
//		try {
//			System.out.println("Load ...\t\t\t");
//			CodeSource src = _Class.getProtectionDomain().getCodeSource();
//
//			if (src != null) {
//				URL jar = src.getLocation();
//				File dir = new File(jar.getPath() + packagePath + "/");
//
//				if (dir.canRead() && dir.isDirectory()) {
//
//					for (File file : dir.listFiles()) {
//						for (String type : acceptedImgFileTypes) {
//							if (file.getPath().endsWith(type)) {
//
//								String resourceFileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
//								
//								if (resourceFileName.contains("\\"))
//									resourceFileName = resourceFileName.substring(resourceFileName.lastIndexOf("\\") + 1);
//								
//								String pkg = packagePath + "." + resourceFileName;
//								pkg = pkg.replace("/", ".");
//								pkg = pkg.replace(".class", "");
//								
//								try {
//									Class.forName(pkg);
//									System.out.println("    OK : " + pkg);
//								} catch (Exception e) {
//									System.out.println(" ERROR : " + pkg + " : " + e);
//								}
//							}
//						}
//					}
//
//				} else {
//					ZipInputStream zip = new ZipInputStream(jar.openStream());
//
//					ZipEntry ze = null;
//					while ((ze = zip.getNextEntry()) != null) {
//						if (ze.getName().startsWith(packagePath)) {
//							for (String type : acceptedImgFileTypes) {
//								if (ze.getName().endsWith(type)) {
//									
//									String resourceFileName = ze.getName().substring(ze.getName().lastIndexOf("/") + 1);
//									
//									if (resourceFileName.contains("\\"))
//										resourceFileName = resourceFileName.substring(resourceFileName.lastIndexOf("\\") + 1);
//
//									String pkg = ze.getName(); //packagePath + "." + resourceFileName;
//									pkg = pkg.replace("/", ".");
//									pkg = pkg.replace(".class", "");
//									
//									try {
//										Class.forName(pkg);
//										System.out.println("    OK : " + pkg);
//									} catch (Exception e) {
//										System.out.println(" ERROR : " + pkg + " : " + e);
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			System.out.println("Load...\t\t\tOK\n");
//		} catch (Exception e) {
//			System.out.println("Load...\t\t\tERROR: " + e);
//		}
//	}

	public void initialize(){
		try {
			console = new JConsole();
			console.setVisible(true);			

		} catch (Exception e) {
			System.out.println("ERROR: Can't setup console: " + e);
		}

		System.out.println(App_Name_Version);
	}
	
	public static void drawReflectionEffect(JComponent c, Graphics g){
		drawReflectionEffect(c, g, 0.7f);
	}
	
	public static void drawReflectionEffect(JComponent c, Graphics g, float transp){
		Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = c.getWidth();
        int h = c.getHeight();
        
        Color color1 = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        Color color2 = new Color(1.0f, 1.0f, 1.0f, 0.8f);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, (h/100)*59);
	}
	
	public void proccessCommands(String[] args){
		if (args.length <= 0) return;
		
		for (String cmdarg : args) {
			String arg = cmdarg.toLowerCase();
			
			if (arg.equals("-?") || arg.equals("-h") || arg.equals("--help")){
				System.out.println(getCmdHelp());
				exit();
			}else if (arg.equals("-d") || arg.equals("--debug")){
				debug = true;
				System.out.println("Debug mode ...\t\t\t\tENABLED");
			}else{
				System.out.println(getCmdUsage());
				exit();
			}
		}
	}
	
	public void registerDEBUGclasses(){
//		ClassLoader loader = ClassLoader.getSystemClassLoader();		
//		Class<? extends ClassLoader> clclass = loader.getClass();
//				
//		Vector<?> classes = null;
//        
//		try {
//			Field fldclasses = clclass.getDeclaredField("classes");
//	        fldclasses.setAccessible(true);
//			classes = (Vector<?>) fldclasses.get(loader);        
//		} catch (Exception e) {
//			System.out.println("ERROR: get loaded classes");
//		}
//	
//		if(classes == null) return;
//		
//        for (Iterator<?> iter = classes.iterator(); iter.hasNext();) {
//            System.out.println("   Loaded " + iter.next());
//        }
		
	}
	
	
	public static String getCmdUsage(){
		return "Unknow commandline arguments!\n" +
				"Use \"--help\" for more informations.";
	}
	
	public static String getCmdHelp(){
		return "\n" +
				"usage: java -jar LoLPlayer II.jar [options]\n" +
				"\n" +
				"   option:\t\tdescription:\n" +
				"\n" +
				"   -h,\t--help\t\tThis help message\n" +
				"   -d,\t--debug\t\tEnable the debug mode\n" +
				"\n";
	}
	
	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public void run() {
		
//      UIManager.put("TabbedPane.selected", Color.GREEN);
//		UIManager.put("MenuItem.selectionBackground", Color.GREEN);
//		UIManager.put("MenuItem.selectionForeground", Color.BLUE);
//		UIManager.put("Menu.selectionBackground", Color.GREEN);
//		UIManager.put("Menu.selectionForeground", Color.BLUE);
//		UIManager.put("MenuBar.selectionBackground", Color.GREEN);
//		UIManager.put("MenuBar.selectionForeground", Color.BLUE);
               
		
		try {
			System.out.print("Load Nimubs Look and Feel (L&F) ...\t");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			System.out.println("OK");
		} catch (Exception ex) {
			System.out.println("ERROR");
			System.err.println("Error: " + ex);
		}

		//initDB();

		FontLoader.loadFonts();

		net.icelane.lolplayer.util.imageloader.ImageLoader.loadImages(Images.class);
		
		ImageLoader.loadImageResourcesList();
		ImageLoader.loadImages();

		colors.importData();
		
		AboutDialog.loadAboutText();
		                
		try {
			control = new AppCore();
			colors.initRainbowColorThread();
		} catch (Exception e) {
			System.out.println("Unexpected Error");
			e.printStackTrace();
		}

	}

	public static Colors getColors() {
		return colors;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void exit() {
		if (waitForExit) return;
		
		SavePlaylistProcess spdbp = null;
		
		try {
			spdbp = getApplication().control.savePlaylistToDataFile();
		} catch (Exception e) {}
		
		final SavePlaylistProcess fspdbp = spdbp;
		
        new Thread(new Runnable() {
			@Override
			public void run() {
				waitForExit = true;
				
				colors.exportData();

				System.out.println("Awaiting end of process to exit ...");
		        while(fspdbp != null && !fspdbp.isReachedEnd()){
		        	System.out.print("");
		        }
		        waitForExit = false;
		        
		        System.out.println("Exit ...");
		        System.exit(0);
			}
		}).start();
	}

    public static Application getApplication() {
        return application;
    }

    public LoLPlayerDB getDatabase() {
        return database;
    }

    public AppCore getControl() {
        return control;
    }

	public JConsole getConsole() {
		return console;
	}   
}
