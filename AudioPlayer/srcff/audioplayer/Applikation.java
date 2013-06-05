package audioplayer;

import javax.swing.UIManager;

import audioplayer.database.DBConnectionLayer;
import audioplayer.database.DataBase;
import audioplayer.font.FontLoader;


/**
 *  LoLPlayer II
 * 
 * @author dausol
 * @version 0.1.2.3
 */
public class Applikation {

	public static String App_Name = "LoLPlayer II";
	public static String App_Version = "0.1.3 alpha";
	public static String App_Name_Version = App_Name + " (" + App_Version + ")";	
	public static String App_About = "Oliver Daus (c) 2013";	
	
	private static boolean debug = false;
	
	private DataBase database;
        private Control control;
                
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println(App_Name_Version);
		
		new Applikation();
	}

	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public Applikation() {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
		}

		initDB();

		FontLoader.loadFonts();
		

		DBConnectionLayer dbcl = new DBConnectionLayer(database);
		dbcl.connectDB();

		control = new Control();
	}

	
	public static boolean isDebug() {
		return debug;
	}

	/**
	 * Initialize the database connection informations.
	 */
	private void initDB() {
		database = new DataBase();
		database.setDBname("apdb");
		database.setDBurl("jdbc:postgresql://localhost:5432/apdb");
		database.setDBusername("dausol");
		database.setDBpassword("123");
	}

	public static void exit() {
		System.exit(0);
	}

}
