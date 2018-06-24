package net.icelane.lolplayer.gui.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class JConsolePanel extends JTextPane implements FocusListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4119143649822884097L;
	
	//*** Style Attributes *******************************************************************************
	public static final Font DEFAULT_STDOUT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	public static final Color DEFAULT_STDOUT_FORECOLOR = new Color(177, 177, 177); //new Color(34, 76, 177); //pastel dark blue
	public static final Color DEFAULT_STDOUT_BACKCOLOR = Color.white;

	public static final Font DEFAULT_STDERR_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	public static final Color DEFAULT_STDERR_FORECOLOR = new Color(177, 34, 34); //pastel red
	public static final Color DEFAULT_STDERR_BACKCOLOR = Color.white;
	
	//*** Style ******************************************************************************************
	public static final AttributeSet DEFAULT_STDOUT_STYLE = _getDefaultOutputStyle();
	public static final AttributeSet DEFAULT_STDERR_STYLE = _getDefaultErrorStyle();
	
	private HashMap<String, AttributeSet> wordStyles = new HashMap<>();
	
	private PrintStream stdout;
	private PrintStream stderr;
	
	public JConsolePanel() {
		//add default word styles ...
		wordStyles.clear();
		setDefaultWordStyles();
		this.setEditable(false);
		this.addFocusListener(this);
	}
	
	public JConsolePanel(StyledDocument consoleDoc) {
		this();
	}
	
	//*** Defaults ***************************************************************************************
	public static AttributeSet _getDefaultOutputStyle(){
		return getStyle(DEFAULT_STDOUT_FORECOLOR, DEFAULT_STDOUT_FONT);
	}
	public static AttributeSet _getDefaultErrorStyle(){
		return getStyle(DEFAULT_STDERR_FORECOLOR, DEFAULT_STDERR_FONT);
	}
	public void setDefaultWordStyles(){
		addStyleFor("OK", getStyle(Color.orange));
		addStyleFor("ERROR", getStyle(Color.red));;
		addStyleFor("SKIPED", getStyle(new Color(0, 192, 232)));
		addStyleFor("SUPPORTED", getStyle(new Color(159, 217, 53)));
		
		addStyleFor("ERROR", getStyle(Color.red));;
		addStyleFor("STDOUT", getStyle(Color.gray));
		addStyleFor("STDERR", getStyle(Color.red));
	}

	public void initOutputRedirection() throws IOException{
		stdout = System.out;
		stderr = System.err;

		PrintListener out = (t, n) ->{
			//print to the native STDOUT
			if (n) t = new StringBuilder("STDOUT: ").append(t).toString();
			stdout.print(t);
			
			appendWordStyled(t);
		}; 
		
		PrintListener err = (t, n) ->{
			//print to the native STDERR
			if (n) t = new StringBuilder("STDERR: ").append(t).toString();
			stderr.print(t);	
			appendWordStyled(t, DEFAULT_STDERR_STYLE);
		}; 
		
		ConsoleOutputStream co = new ConsoleOutputStream(out);
		ConsoleOutputStream ce = new ConsoleOutputStream(err);

		System.setOut(new PrintStream(co,true));
		System.setErr(new PrintStream(ce,true));
	}
	
	public static AttributeSet getStyle(Color fc){
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, fc);
		return set;
	}
	
	public static AttributeSet getStyle(Color fc, Color bc){
		SimpleAttributeSet set = (SimpleAttributeSet) getStyle(fc);
		StyleConstants.setForeground(set, fc);
		return set;
	}
	
	public static AttributeSet getStyle(Color fc, Font f){
		SimpleAttributeSet set = (SimpleAttributeSet) getStyle(fc);
		StyleConstants.setFontFamily(set, f.getFamily());
		StyleConstants.setFontSize(set, f.getSize());
		return set;
	}
	
	public static AttributeSet getStyle(Color fc, Color bc, Font f){
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, fc);
		StyleConstants.setBackground(set, bc);
		StyleConstants.setFontFamily(set, f.getFamily());
		StyleConstants.setFontSize(set, f.getSize());
		return set;
	}
	
	public void addStyleFor(String text, AttributeSet set){
		wordStyles.put(text.toLowerCase(), set);
	}
	
	public void clearStyleFor(){
		wordStyles.clear();
	}
	
	public void removeStyleFor(String text){
		wordStyles.remove(text.toLowerCase());
	}
	
	public AttributeSet getStyleFor(String text){
		return wordStyles.get(text.toLowerCase());
	}

	public HashMap<String, AttributeSet> getWordStyles() {
		return wordStyles;
	}

	public void appendWordStyled(String t){
		appendWordStyled(t, DEFAULT_STDOUT_STYLE);
	}
	
	public void appendWordStyled(String t, AttributeSet defSet){
//		try {
//			String x = getText();
//			
//			StyledDocument doc = getStyledDocument();
//			Matcher m = Pattern.compile("e").matcher(x);
//
//			while (m.find()) {
//				String str = m.group();
//				AttributeSet style = getStyle(Color.orange);
//				style = style == null ? DEFAULT_STDOUT_STYLE : style;
//				int o = m.start();
//				int l = m.end() - m.start();
//				doc.setCharacterAttributes(o, l, style, true);
//			}
//		} catch (Exception e) {
//			e.printStackTrace(stderr);
//		}
		
		
		String splitRegx = "[\\w]+|[\\W]+";
		String wordRegx = "[\\w]+";
		String notWordRegx = "[\\W]+";
				
		Matcher m = Pattern.compile(splitRegx).matcher(t);
		boolean lastIsWord = false;
		AttributeSet lastStyle = defSet;
		
		while (m.find()) {
			String str = m.group();
			AttributeSet style = getStyleFor(str);
			
			//is not a word ...
			if (Pattern.matches(notWordRegx, str)){
				if (lastIsWord){
					style = lastStyle;
				}
			}
			
			//add text ...
			append(str, style == null ? defSet : style);
			
			lastStyle = style;
			lastIsWord = Pattern.matches(wordRegx, str); //is this a word ?
		}
	}

	public void append(String t){
		append(t, DEFAULT_STDOUT_STYLE);
	}
	
	public void append(String t, Color c){
		if (c == null)
			append(t);
		else
			append(t, getStyle(c));
	}
	
	public void append(final String t, final AttributeSet a){
		SwingUtilities.invokeLater(() -> {
			AttributeSet _a = a;
			if (_a == null) _a = DEFAULT_STDOUT_STYLE;
			
			try {
				getDocument().insertString(getDocument().getLength(), t, _a);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {
		this.setCaretPosition(getDocument().getLength());
	}
	
	
}
