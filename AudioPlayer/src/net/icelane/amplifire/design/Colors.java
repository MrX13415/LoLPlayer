package net.icelane.lolplayer.design;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.icelane.lolplayer.Application;
import net.icelane.lolplayer.gui.UserInterface;
import net.icelane.lolplayer.gui.ui.UIFrame;
import net.icelane.lolplayer.player.analyzer.AudioGraph;
import net.mrx13415.searchcircle.imageutil.ImageModifier;
import net.mrx13415.searchcircle.imageutil.color.HSB;


public class Colors {

	/*
	 * Ausgangsfarbe : RGB(        235,         065,         065)
	 *                 HSB(0.00000000f, 0.72340420f, 0.92156863f)
	 *                
	 * Rainbow Effect: add '0.0001f' to the "hue" every 10 ms
	 * 
	 */

	private Thread rainbowThread;
	
	volatile public boolean SETTING_rainbowColor = true;
	volatile public float SETTING_rainbowHue = 0;
	volatile public float SETTING_rainbowSaturation = 0;
	volatile public float SETTING_rainbowBrightness = 0;
	
	public Color SETTING_color_uiframe_title_forground = new Color(130, 38, 38);
	public Color SETTING_color_uiframe_titel = new Color(130, 38, 38);
	public Color SETTING_color_uiframe_buttons = new Color(150, 38, 38);
	public Color SETTING_color_uiframe_buttons_hover = new Color(100, 38, 38);
	public Color SETTING_color_uiframe_buttons_pressed = new Color(30, 30, 30);
	
	public HSB SETTING_color_controls_play = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_pause = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_stop = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_frw = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_rev = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_searchbar_bar = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_volume_bar = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_searchbar_button = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_volume_button = new HSB(0, 0, 0);
	public HSB SETTING_color_controls_search_button = new HSB(0, 0, 0);
	
	public Color SETTING_color_graph_defaultMergedGraphColor = Color.white;
	public Color SETTING_color_graph_defaultChannelGraphColor1 = Color.red;
	public Color SETTING_color_graph_defaultChannelGraphColor2 = new Color(255, 80, 0);
	public Color SETTING_color_graph_defaultChannelGraphColor3 = Color.yellow;
	public Color SETTING_color_graph_defaultChannelGraphColor4 = Color.green;
	public Color SETTING_color_graph_defaultChannelGraphColor5 = Color.blue;
	public Color SETTING_color_graph_defaultChannelGraphColor6 = Color.magenta;
	
	public Color SETTING_color_aboutpage_background1 = new Color(20, 20, 20);
	public Color SETTING_color_aboutPage_forground = new Color(255 ,255, 255);	
	
	public Color SETTING_color_forground1 = new Color(255, 255, 255);
	
	public Color SETTING_color_background1 = new Color(50, 50, 50);
	public Color SETTING_color_background2 = new Color(128, 128, 128, 0);
	public Color SETTING_color_background3 = new Color(235, 65, 65);
	public Color SETTING_color_background4 = new Color(128, 128, 128, 0);
	
	public Color SETTING_color_display_forground1 = new Color(255, 128 , 0);
	public Color SETTING_color_display_forground2 = Color.gray;
	public Color SETTING_color_display_background = new Color(15, 15, 15, 150);
	
	public Color SETTING_color_menu_background1 = new Color(50 ,50, 50);
	public Color SETTING_color_menu_forground1 = new Color(255 ,255, 255);
	
	public Color SETTING_color_statusbar_background1 = new Color(50 ,50, 50);
	public Color SETTING_color_statusbar_forground1 = new Color(255 ,255, 255);
	
	public Color SETTING_color_playlist_background1 = new Color(150, 38, 38);
	public Color SETTING_color_playlist_selection_background1 = new Color(255, 128, 0);
	public Color SETTING_color_playlist_background2 = new Color(255, 128, 0);
	public Color SETTING_color_playlist_background3 = new Color(50, 50, 50);
	public Color SETTING_color_playlist_background4 = new Color(150, 38, 38);
	public Color SETTING_color_playlist_background5 = new Color(150, 38, 38);
	public Color SETTING_color_playlist_background6 = new Color(235, 65, 65); //red
	
	public Color SETTING_color_playlist_forground1 = new Color(255,255,255);
	public Color SETTING_color_playlist_selection_forground1 = new Color(0,0,0);
	
	//************************************
	
	public Color color_uiframe_title_forground = SETTING_color_uiframe_title_forground;
	public Color color_uiframe_titel = SETTING_color_uiframe_titel;
	public Color color_uiframe_buttons = SETTING_color_uiframe_buttons;
	public Color color_uiframe_buttons_hover = SETTING_color_uiframe_buttons_hover;
	public Color color_uiframe_buttons_pressed = SETTING_color_uiframe_buttons_pressed;

	public HSB color_controls_play = SETTING_color_controls_play;
	public HSB color_controls_pause = SETTING_color_controls_pause;
	public HSB color_controls_stop = SETTING_color_controls_stop;
	public HSB color_controls_frw = SETTING_color_controls_frw;
	public HSB color_controls_rev = SETTING_color_controls_rev;
	public HSB color_controls_searchbar_bar = SETTING_color_controls_searchbar_bar;
	public HSB color_controls_volume_bar = SETTING_color_controls_volume_bar;
	public HSB color_controls_searchbar_button = SETTING_color_controls_searchbar_button;
	public HSB color_controls_volume_button = SETTING_color_controls_volume_button;
	public HSB color_controls_search_button = SETTING_color_controls_search_button;

	public Color color_graph_defaultMergedGraphColor = SETTING_color_graph_defaultMergedGraphColor; 
	public Color color_graph_defaultChannelGraphColor1 = SETTING_color_graph_defaultChannelGraphColor1;
	public Color color_graph_defaultChannelGraphColor2 = SETTING_color_graph_defaultChannelGraphColor2;
	public Color color_graph_defaultChannelGraphColor3 = SETTING_color_graph_defaultChannelGraphColor3;
	public Color color_graph_defaultChannelGraphColor4 = SETTING_color_graph_defaultChannelGraphColor4;
	public Color color_graph_defaultChannelGraphColor5 = SETTING_color_graph_defaultChannelGraphColor5;
	public Color color_graph_defaultChannelGraphColor6 = SETTING_color_graph_defaultChannelGraphColor6;

	public Color color_aboutpage_background1 = SETTING_color_aboutpage_background1;
	public Color color_aboutPage_forground = SETTING_color_aboutPage_forground;

	public Color color_forground1 = SETTING_color_forground1;
	
	public Color color_background1 = SETTING_color_background1;
	public Color color_background2 = SETTING_color_background2;
	public Color color_background3 = SETTING_color_background3;
	public Color color_background4 = SETTING_color_background4;

	public Color color_display_forground1 = SETTING_color_display_forground1;
	public Color color_display_forground2 = SETTING_color_display_forground2;
	public Color color_display_background = SETTING_color_display_background;
	
	public Color color_menu_background1 = SETTING_color_menu_background1;
	public Color color_menu_forground1 = SETTING_color_menu_forground1;

	public Color color_statusbar_background1 = SETTING_color_statusbar_background1;
	public Color color_statusbar_forground1 = SETTING_color_statusbar_forground1;

	public Color color_playlist_background1 = SETTING_color_playlist_background1;
	public Color color_playlist_selection_background1 = SETTING_color_playlist_selection_background1;
	public Color color_playlist_background2 = SETTING_color_playlist_background2;
	public Color color_playlist_background3 = SETTING_color_playlist_background3;
	public Color color_playlist_background4 = SETTING_color_playlist_background4;
	public Color color_playlist_background5 = SETTING_color_playlist_background5;
	public Color color_playlist_background6 = SETTING_color_playlist_background6;

	public Color color_playlist_forground1 = SETTING_color_playlist_forground1;
	public Color color_playlist_selection_forground1 = SETTING_color_playlist_selection_forground1;

	//************************************
	
	
	public void applayColors(){
		ArrayList<UIFrame> uis = UIFrame.getOpendFrames();
		
		for (int index = 0; index < uis.size(); index++) {
			UIFrame uif = uis.get(index);
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			
			
			uif.applayColors();
//			uif.applayMenuBarDesign();
			uif.applayStatusPaneDesign();
		
			if (uif instanceof UserInterface){
				
				UserInterface ui = (UserInterface) uif;
				
				ui.getPlayerControlInterface().setPlayHSB(color_controls_play);
				ui.getPlayerControlInterface().setPauseHSB(color_controls_pause);
				ui.getPlayerControlInterface().setStopHSB(color_controls_stop);
				ui.getPlayerControlInterface().setFrwHSB(color_controls_frw);
				ui.getPlayerControlInterface().setRevHSB(color_controls_rev);
				ui.getPlayerControlInterface().getSearchBar().setButtonHSB(color_controls_searchbar_button);
				ui.getPlayerControlInterface().getVolume().setButtonHSB(color_controls_searchbar_button);
				ui.getPlayerControlInterface().getSearchBar().setBarHSB(color_controls_searchbar_bar);
				ui.getPlayerControlInterface().getVolume().setBarHSB(color_controls_volume_bar);
				ui.getPlaylistInterface().setSearchButtonHSB(color_controls_search_button);
				
//				ui.getMenu().setBackground(Application.getColors().color_menu_background1);
//				ui.getMenu().setForeground(Application.getColors().color_menu_forground1);
		
				ArrayList<AudioGraph> ags = ui.getPlayerControlInterface().getPlayerInterfaceGraph().getGraphs();

				//TODOD: handle merged channels: option audigraph class?... 
				for (int i = 0; i < ags.size(); i++) {
					if (i == 0) ags.get(i).setColor(color_graph_defaultChannelGraphColor1);
					if (i == 1) ags.get(i).setColor(color_graph_defaultChannelGraphColor2);
					if (i == 2) ags.get(i).setColor(color_graph_defaultChannelGraphColor3);
					if (i == 3) ags.get(i).setColor(color_graph_defaultChannelGraphColor4);
					if (i == 4) ags.get(i).setColor(color_graph_defaultChannelGraphColor5);
					if (i == 5) ags.get(i).setColor(color_graph_defaultChannelGraphColor6);
				}

				ui.getPlaylistInterface().getPlaylistTable().setBackground(Application.getColors().color_playlist_background1);
				ui.getPlaylistInterface().getPlaylistTable().setForeground(Application.getColors().color_playlist_forground1);
				ui.getPlaylistInterface().getPlaylistTable().setSelectionBackground(Application.getColors().color_playlist_selection_background1);
				ui.getPlaylistInterface().getPlaylistTable().setSelectionForeground(Application.getColors().color_playlist_selection_forground1);
				ui.getPlaylistInterface().getPlaylistTable().setGridColor(Application.getColors().color_playlist_background2);
				ui.getPlaylistInterface().getPlaylistTable().getTableHeader().setBackground(Application.getColors().color_playlist_background3);
		
				ui.getPlaylistInterface().getPlaylistScrollPane().getViewport().setBackground(Application.getColors().color_playlist_background4);
				ui.getPlaylistInterface().getPlaylistScrollPane().setBackground(Application.getColors().color_playlist_background4);
		        ui.getPlaylistInterface().getPlaylistScrollPane().getVerticalScrollBar().setBackground(Application.getColors().color_playlist_background4);
		
		        ui.getPlayerToggleArea().getToggleButton().setForeground(Application.getColors().color_playlist_background5);
		        ui.getPlayerToggleArea().getToggleComponent().setBackground(Application.getColors().color_playlist_background6);
			}
		}
        
	}
	
	public void initRainbowColorThread(){
		if (SETTING_rainbowColor) initRainbowColorThread(new HSB());
	}
	
	public void initRainbowColorThread(final HSB hsb){
		if (SETTING_rainbowColor) SETTING_rainbowColor = false;
		SETTING_rainbowColor = true;
		rainbowThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				SETTING_rainbowHue = hsb.getHue();
				
				while (SETTING_rainbowColor) {
					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					
					SETTING_rainbowHue += 0.001f;
					
					changeColor(new HSB(SETTING_rainbowHue, SETTING_rainbowSaturation, SETTING_rainbowBrightness));
					
					applayColors();

					if (SETTING_rainbowHue >= 1f) SETTING_rainbowHue = 0f;
				}
			}
		});
		rainbowThread.setName("UI-RainbowEffect");
		rainbowThread.start();
	}
	
	public void changeColor(){
		changeColor(new HSB(SETTING_rainbowHue, SETTING_rainbowSaturation, SETTING_rainbowBrightness));
	}
	
	public void changeColor(HSB fhsb){
		Field[] f = getClass().getDeclaredFields();
		
		for (Field field : f) {
			if (Modifier.isFinal(field.getModifiers())) continue;
			
			try {
				Field cf = null;
				try {
					cf = getClass().getField("SETTING_" + field.getName());
				} catch (Exception e) {}
				
				if (cf == null) continue;
				 
				if (field.get(this) instanceof Color){
					Color c = (Color) field.get(this);
					Color cc = (Color) cf.get(this);

					HSB hsb = new HSB(c);
					HSB chsb = new HSB(cc);
					
					hsb.setHue(ImageModifier.getMinMax(chsb.getHue() + fhsb.getHue()));
					hsb.setSaturation(ImageModifier.getMinMax(chsb.getSaturation() + fhsb.getSaturation()));
					hsb.setBrightness(ImageModifier.getMinMax(chsb.getBrightness() + fhsb.getBrightness()));
					
					Color nc = hsb.getColor();
					int r = nc.getRed();
					int g = nc.getGreen();
					int b = nc.getBlue();
					
					field.set(this, new Color(r, g, b, c.getAlpha()));
				}else if (field.get(this) instanceof HSB){

					HSB hsb = new HSB(fhsb);
					HSB chsb = (HSB) cf.get(this);
					
					hsb.setHue(ImageModifier.getMinMax(chsb.getHue() + fhsb.getHue()));
					hsb.setSaturation(ImageModifier.getMinMax(chsb.getSaturation() + fhsb.getSaturation()));
					hsb.setBrightness(ImageModifier.getMinMax(chsb.getBrightness() + fhsb.getBrightness()));

					field.set(this, hsb);
				}
			} catch (Exception e) {
				System.out.println("ERROR: " + e);
			}
		}
	}
	
	
	public boolean exportData(){
		Field[] f = getClass().getDeclaredFields();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("./data/colors.dat")));
		
			for (Field field : f) {

				if (!field.getName().startsWith("SETTING_")) continue;
				
				String fname = field.getName().replace("SETTING_", "");
				
				System.out.printf("Exporting colors: field: %-50s\t", fname + " ... ");
				if (field.get(this) instanceof Color){
					Color c = (Color) field.get(this);
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
					int a = c.getAlpha();
					
					bw.write(String.format("%-50s\t= RGBA{%03d, %03d, %03d, %03d}\n", fname, r, g, b, a));
					System.out.println("OK");
					
				}else if (field.get(this) instanceof HSB){
					HSB c = (HSB) field.get(this);
					String h = String.valueOf(c.getHue()).replace(",", ".");
					String s = String.valueOf(c.getSaturation()).replace(",", ".");
					String b = String.valueOf(c.getBrightness()).replace(",", ".");
					
					bw.write(String.format("%-50s\t= HSB{%s, %s, %s}\n", fname, h, s, b));
					System.out.println("OK");
					
				}else{
					Class<?> type = field.getType();
					
					bw.write(String.format("%-50s\t= %s{%s}\n", fname, type.getName(), field.get(this)));
					System.out.println("OK");
				}
			}
			
			bw.close();
			
			return true;
		}catch (Exception e) {
			System.out.println("ERROR: " + e);
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e1) {}
		}
		
		return false;
	}
	
	public boolean importData(){
		File ff = new File("./data/colors.dat");
		
		System.out.print("Loading colors ...\t\t\t");
		
		if (!ff.exists()){
			System.out.println("ERROR: File not found");
		}
		
		System.out.println();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ff));
		
			while (br.ready()) {

				String line = br.readLine();
				
				String[] lp = line.split("=");
				
				if (!lp[1].contains("{") || !lp[1].endsWith("}")) continue;
				
				String settingname = lp[0].trim();
				String name = "SETTING_" + settingname;
				String type = lp[1].trim().split("\\{")[0].trim();
				String data = lp[1].trim().split("\\{")[1].split("\\}")[0].trim();
				
				Field f = null;
				try {
					f = getClass().getField(name);
				} catch (Exception e) {}

				if (f == null) continue;
				if (!f.getName().startsWith("SETTING_")) continue;
				
				if (type.startsWith("RGBA")){
					System.out.printf("    Importing colors: field detected: %-50s\t", settingname + " ... ");
					String[] rgba = lp[1].trim().replace("RGBA{", "").replace("}", "").split(",");
					
					int r = Integer.valueOf(rgba[0].trim());
					int g = Integer.valueOf(rgba[1].trim());
					int b = Integer.valueOf(rgba[2].trim());
					int a = Integer.valueOf(rgba[3].trim());
					
					if (f.get(this) instanceof Color){
						f.set(this, new Color(r, g, b, a));
						
						System.out.println("OK: data set: " + f.get(this));
					}else{
						System.out.println("ERROR: Unknow field object type");
					}
				}else if (type.startsWith("HSB")){
					System.out.printf("    Importing colors: field detected: %-50s\t", settingname + " ... ");
					String[] rgba = lp[1].trim().replace("HSB{", "").replace("}", "").split(",");
					
					float h = Float.valueOf(rgba[0].trim());
					float s = Float.valueOf(rgba[1].trim());
					float b = Float.valueOf(rgba[2].trim());
					
					if (f.get(this) instanceof HSB){
						f.set(this, new HSB(h, s, b));
						
						System.out.println("OK: data set: " + f.get(this));
					}else{
						System.out.println("ERROR: Unknow field object type");
					}
				}else{
					try {
						Class<?> c = null;
						
						try {
							c = Class.forName(type);
						} catch (Exception e) {}
						
						if (c == null) {
							c = Class.forName("java.lang." + type.substring(0, 1).toUpperCase() + type.substring(1));
						}
						
						if (c != null){
							System.out.printf("    Importing " + c.getName() + ": field detected: %-50s\t", settingname + " ... ");
							
							try {
								f.set(this, c.getMethod("valueOf", Class.forName("java.lang.String")).invoke(null, data));
							} catch (NoSuchMethodException e) {
								f.set(this, c.cast(data));
							}

							System.out.println("OK: data set: " + f.get(this));
						}
					} catch (Exception e) {
						System.out.println("ERROR: Unknow field object type or invalid data: " + settingname + " " + type + "{" + data + "}");
					}
				}
				
			}

			br.close();
			
			changeColor();
			applayColors();
						
			System.out.println("Loading colors ...\t\t\tOK");
			
			return true;
		}catch (Exception e) {
			System.out.println("\nLoading colors ...\t\t\tERROR: " + e);
			
			if (br != null)
				try {
					br.close();
				} catch (IOException e1) {}
		}
		
		return false;
	}

	public boolean isRainbowColor() {
		return SETTING_rainbowColor;
	}

	public void setRainbowColor(boolean rainbowColor) {
		this.SETTING_rainbowColor = rainbowColor;
	}

	public float getRainbowHue() {
		return SETTING_rainbowHue;
	}

	public float getRainbowSaturation() {
		return SETTING_rainbowSaturation;
	}

	public float getSetting_rainbowHue() {
		return SETTING_rainbowHue;
	}

	public void setRainbowHue(float SETTING_rainbowHue) {
		this.SETTING_rainbowHue = SETTING_rainbowHue;
	}

	public void setRainbowSaturation(float rainbowSaturation) {
		this.SETTING_rainbowSaturation = rainbowSaturation;
	}

	public float getRainbowBrightness() {
		return SETTING_rainbowBrightness;
	}

	public void setRainbowBrightness(float rainbowBrightness) {
		this.SETTING_rainbowBrightness = rainbowBrightness;
	}
	
}
