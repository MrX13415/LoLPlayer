package net.icelane.amplifire;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.icelane.amplifire.database.sql.LoLPlayerDB;
import net.icelane.amplifire.design.Colors;
import net.icelane.amplifire.font.FontLoader;
import net.icelane.amplifire.images.ImageLoader;
import net.icelane.amplifire.images.Images;
import net.icelane.amplifire.process.SavePlaylistProcess;
import net.icelane.amplifire.ui.AboutDialog;
import net.icelane.amplifire.ui.console.JConsole;

/**
 * -- amplifire 
 *
 * <p>Basic application handling, like console agruments and logger.</p>
 *
 * @version 
 * @author MrX13415
 */
public final class Application {

	public static final String App_Name = "amplifier";
	public static final String App_Version = "0.1.9";
	public static final String App_Name_Version = String.format("%s (%s)", App_Name, App_Version);	
	public static final String App_Author = "Oliver Daus";	
	public static final String App_License = "MIT License";
    public static final String App_License_Link = "http://creativecommons.org/licenses/by-nc-sa/3.0/";
    
    private static Logger logger; 
    
    private static String[] arguments;  
    private static boolean debug = false;
	private static boolean exit = false;
	
	private static JConsole console;
	private static AppCore control;
	
    private static Colors colors = new Colors();
	private static LoLPlayerDB database;
	     
	
    public static void launch(String[] args){
    	arguments = args;
    	
    	logger = Logger.getLogger(Application.class.getName());    	  

    	//TODO: console
    	console = new JConsole();
		console.setVisible(true);

		logger.info(App_Name_Version);
		
		initialize();
	}

    private static void initialize() {
		
		proccessCommands(arguments);
		
		try {
			System.out.print("Load Nimubs Look and Feel (L&F) ...\t");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			System.out.println("OK");
		} catch (Exception ex) {
			System.out.println("ERROR");
			System.err.println("Error: " + ex);
		}

		//initDB();

		FontLoader.loadFonts();

		net.icelane.amplifire.util.imageloader.ImageLoader.loadImages(Images.class);
		
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
	
	public static void proccessCommands(String[] args){
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
	

	public static void exit() {
		if (exit) return;
	
		control.getAnalyzer().stopWait();

		SavePlaylistProcess spdbp = null;
		
		try {
			spdbp = control.savePlaylistToDataFile();
		} catch (Exception e) {}
		
		final SavePlaylistProcess fspdbp = spdbp;
		
        new Thread(new Runnable() {
			@Override
			public void run() {
				exit = true;
				
				colors.exportData();

				System.out.println("Awaiting end of process to exit ...");
		        while(fspdbp != null && !fspdbp.isReachedEnd()){
		        	System.out.print("");
		        }
		        exit = false;
		        
		        System.out.println("Exit ...");
		        System.exit(0);
			}
		}).start();
	}
	
	public static String[] getArguments() {
		return arguments;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static AppCore control() {
		return control;
	}

	public static JConsole console() {
		return console;
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
				"usage: java -jar amplifier.jar [options]\n" +
				"\n" +
				"   option:\t\tdescription:\n" +
				"\n" +
				"   -h,\t--help\t\tThis help message\n" +
				"   -d,\t--debug\t\tEnable the debug mode\n" +
				"\n";
	}

	public static Colors getColors() {
		return colors;
	}

}
