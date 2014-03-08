package audioplayer;

import javax.swing.UIManager;

import audioplayer.database.sql.LoLPlayerDB;
import audioplayer.desing.Colors;
import audioplayer.font.FontLoader;
import audioplayer.gui.AboutDialog;
import audioplayer.gui.ui.UIFrame;
import audioplayer.images.ImageLoader;
import audioplayer.process.SavePlaylistProcess;

import java.awt.Color;
import java.util.ArrayList;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * @version 0.1.6.9
 */ 
public class Application {

	public static String App_Name = "LoLPlayer II";
	public static String App_Version = "0.1.6.10 beta - Fency Edition";
	public static String App_Name_Version = App_Name + " (" + App_Version + ")";	
	public static String App_Author = "Oliver Daus";	
	public static String App_License = "CC BY-NC-SA 3.0";
    public static String App_License_Link = "http://creativecommons.org/licenses/by-nc-sa/3.0/";
        
    private static boolean debug = false;
	private static boolean waitForExit = false;
	
    private static Application application;
        
	private LoLPlayerDB database;
	
    private PlayerControl control;

    
    private static Colors colors = new Colors();
            
    
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println(App_Name_Version);
	
		proccessCommands(args);
		
		application = new Application();
		application.initialize();

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
	public void initialize() {

//      UIManager.put("TabbedPane.selected", Color.GREEN);
//		UIManager.put("MenuItem.selectionBackground", Color.GREEN);
//		UIManager.put("MenuItem.selectionForeground", Color.BLUE);
//		UIManager.put("Menu.selectionBackground", Color.GREEN);
//		UIManager.put("Menu.selectionForeground", Color.BLUE);
//		UIManager.put("MenuBar.selectionBackground", Color.GREEN);
//		UIManager.put("MenuBar.selectionForeground", Color.BLUE);
               
		try {
			System.out.print("Load Nimubs Look and Feel (L&F) ...\t");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			System.out.println("OK");
		} catch (Exception ex) {
			System.out.println("ERROR");
			System.err.println("Error: " + ex);
		}

		initDB();

		FontLoader.loadFonts();
		ImageLoader.loadImages();

		colors.importData();
		
		AboutDialog.loadAboutText();
		                
		try {
			control = new PlayerControl();
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

	/**
	 * Initialize the database connection informations.
	 */
	private void initDB() {
		database = new LoLPlayerDB();
		database.loadConfig();
		database.getConnection().connectDB();
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

    public PlayerControl getControl() {
        return control;
    }
        
}
