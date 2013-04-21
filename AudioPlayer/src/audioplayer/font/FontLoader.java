package audioplayer.font;


import java.awt.Font;
import java.io.InputStream;



public class FontLoader {

	public static final String Altrnative_Fonts_Path = "fonts/"; 

	
	public static final String font_Symbola = "Symbola.ttf";

	//---------------------------------------------------------------
	
	public static Font fontGUIPlayerButtons = new Font(font_Symbola, Font.PLAIN, 20);

	public static void loadFonts(){
		System.out.print("Load fonts ...\t\t\t\t");
		
		try{
			InputStream fontStream;

			fontStream = FontLoader.class.getResourceAsStream(font_Symbola);
			Font font;

			font = Font.createFont(Font.TRUETYPE_FONT, fontStream);

			fontGUIPlayerButtons = font.deriveFont(Font.PLAIN, 25);
			
//			fontPrintviewTitle = font.deriveFont(Font.PLAIN, 28);
//			fontPrintviewInfo = font.deriveFont(Font.BOLD, 17);
//
//			fontPrintviewContent = font.deriveFont(Font.PLAIN, 12);
//			fontPrintviewContent_bold = font.deriveFont(Font.BOLD, 12);
//
//			fontPrintviewContent_small  = font.deriveFont(Font.PLAIN, 10);
//			fontPrintviewContent_small_bold  = font.deriveFont(Font.BOLD, 10);
//
//			fontMonthEndDate  = font.deriveFont(Font.PLAIN, 16);
//			fontMonthEndDate_small  = font.deriveFont(Font.PLAIN, 13);
//			
			
			fontStream.close();
			
			System.out.println("OK");
		}catch (Exception e) {
			System.out.println("ERROR");
			//JOptionPane.showMessageDialog(null, "Fehler beim laden der Schriftarten ...", Launcher.AppName, JOptionPane.ERROR_MESSAGE);
		}
	}
}
