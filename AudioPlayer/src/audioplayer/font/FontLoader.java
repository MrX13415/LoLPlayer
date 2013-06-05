package audioplayer.font;

import java.awt.Font;
import java.io.InputStream;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
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
			
			fontStream.close();
			
			System.out.println("OK");
		}catch (Exception e) {
			System.out.println("ERROR");
		}
	}
}
