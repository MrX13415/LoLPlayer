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
	public static final String font_Webdings = "Webdings.ttf";

	//---------------------------------------------------------------
	
	public static Font fontSymbola = new Font(font_Symbola, Font.PLAIN, 25);
	public static Font fontSymbola_16p = new Font(font_Symbola, Font.PLAIN, 16);
	
	public static Font fontWebdings_16p = new Font(font_Webdings, Font.PLAIN, 16);
	
	public static void loadFonts(){
		System.out.print("Load fonts ...\t\t\t\t");
		
		try{
			InputStream fontStream;

			fontStream = FontLoader.class.getResourceAsStream(font_Symbola);
			Font font;

			font = Font.createFont(Font.TRUETYPE_FONT, fontStream);

			fontSymbola = font.deriveFont(Font.PLAIN, 25);
			fontSymbola_16p = font.deriveFont(Font.PLAIN, 16);
			
			fontStream.close();
			
			fontStream = FontLoader.class.getResourceAsStream(font_Webdings);
			font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			
			fontWebdings_16p = font.deriveFont(Font.PLAIN, 16);
			
			fontStream.close();
			
			System.out.println("OK");
		}catch (Exception e) {
			System.out.println("ERROR");
		}
	}
}
