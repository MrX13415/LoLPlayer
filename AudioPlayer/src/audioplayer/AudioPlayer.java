package audioplayer;

import javax.swing.UIManager;

import audioplayer.database.DataBase;
import audioplayer.font.FontLoader;

/**
 * 
 * @author dausol
 * @version 0.1.1
 */
public class AudioPlayer {

	private DataBase database;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new AudioPlayer();
	}

	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public AudioPlayer() {

		System.out.println(UIManager.getSystemLookAndFeelClassName());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		initDB();

		FontLoader.loadFonts();
		
                /*
		
		DBConnectionLayer dbcl = new DBConnectionLayer(database);
		  
		dbcl.connectDB(); dbcl.select(); dbcl.closeDB();
		 */

		new AudioPlayerControl();
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

}
