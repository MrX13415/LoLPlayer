package net.icelane.lolplayer.gui.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * JConsole 
 * 
 * @version 2.0
 * @author Oliver Daus
 * 
 * (c) 2011 
 */
public class JConsole extends JFrame implements KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5215332537219700809L;	
	
	private JConsolePanel console;
	private StyleContext styleContext;
	private StyledDocument consoleDoc;
	private Style style;
	private JScrollPane scrollPane ;
	
	private int bufferSizeX = 800;
	private int bufferSizeY = 2500;
	private int cartePosX = 0;
	private char content[][] = new char[bufferSizeX][bufferSizeY];
	
	/** generates and shows the Console Window
	 * 
	 */
	public JConsole(){	

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		styleContext = new StyleContext();
		consoleDoc = new DefaultStyledDocument( styleContext );        

		//TextPane
		console = new JConsolePanel(consoleDoc){
		    /**
			 * 
			 */
			private static final long serialVersionUID = -4394833403104206875L;

			public boolean getScrollableTracksViewportWidth()
		    {
		        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
		    }
		};
		console.setOpaque(true);
		console.setForeground(Color.BLACK);
		console.setBackground(Color.WHITE);
		console.setFont(new Font(Font.MONOSPACED, console.getFont().getStyle(), 11));
		console.setBackground(new Color(25, 25, 25));
		console.setCaret(new DosBoxCaret());
		console.addKeyListener(this);
		try {
			console.initOutputRedirection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    //Scroll-bar
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(console);
		
		//Frame
		this.getContentPane().add(scrollPane);
		this.setPreferredSize(new Dimension(1000, 500));
//		this.setMinimumSize(this.getPreferredSize());
//		this.setMaximumSize(this.getPreferredSize());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.pack();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		int c = e.getKeyChar();
		
		// 8 => Backspace
		if (c == 8){
			
		}else{
			//con.append(String.valueOf(e.getKeyChar()));
		}
	}
	
	/** prints some Text at the Console
	 * 
	 * @param text
	 */
	public void print(String text){
		print(text, new Color(34, 76, 177));
		cartePosX++;
	}
	
	/** prints some Text at the Console
	 * 
	 * @param text
	 * @param textcolor
	 */
	public void print(String text, Color textcolor){
		try {
			style = styleContext.addStyle(null, null);
			StyleConstants.setForeground(style, textcolor);	
			consoleDoc.addStyle(textcolor.toString(), style);
			
			consoleDoc.insertString(consoleDoc.getLength(), text, consoleDoc.getStyle(textcolor.toString()));
			
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** prints a line of Text at the Console
	 * 
	 * @param text
	 * @param textcolor
	 */
	public void println(String text, Color textcolor){
		print((text + "\n"), textcolor);
	}
	
	/** prints a line of Text at the Console
	 * 
	 * @param text
	 * @param textcolor
	 */
	public void println(String text){
		print((text + "\n"));
	}
	
	/** prints some error Text at the Console
	 * 
	 * @param text
	 */
	public void errprint(String text){
		try {			
			consoleDoc.insertString(consoleDoc.getLength(), text, consoleDoc.getStyle("red"));
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** prints a line of error Text at the Console
	 * 
	 * @param text
	 */
	public void errprintln(String text){
		errprint(text + "\n");
	}
	
	/** reads Text from the Console
	 * 
	 * @return String
	 */
	public String read(){
		console.setEditable(true);
		try {
			print(" ", Color.green);
			
			int caretPos = consoleDoc.getLength();
			
			//get last char informations 
			Style lastStyle = style; 
			String lastChar = "";
			if (consoleDoc.getLength() > 2) {
				lastChar = console.getText(consoleDoc.getLength() - 2, 2);
			}
			
			String input = "";
			
			do {
				//lock caret Pos
				if (console.getCaretPosition() < caretPos){
					consoleDoc.insertString(consoleDoc.getLength(), lastChar.substring((caretPos - console.getCaretPosition() - 2) * -1), lastStyle);
					console.setCaretPosition(caretPos);
				}		
				
				if((consoleDoc.getLength() - caretPos) >= 0){
					input = consoleDoc.getText(caretPos, consoleDoc.getLength() - caretPos );
				}
				
				Thread.sleep(10);
				
			} while (! (input.contains( String.valueOf( (char)10 ) )));
			
			if((consoleDoc.getLength() - caretPos) >= 0){
				input = input.substring(0,input.length() - 1);
			}
			
			return input;
		} catch (Exception e) {
			return "";
		}finally{ 
			console.setEditable(false);
		}
	}
	
	/** reads numbers from the Console
	 * 
	 * @return integer
	 */
	public int readint(){
		try {
			return Integer.valueOf(read());
		} catch (Exception e) {
		}
		return 0;
	}
	
	public void clear() {
		console.setText("");
	}
	
	public static class DosBoxCaret extends DefaultCaret {
		
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public DosBoxCaret() {
	        this.setBlinkRate(400);
	        this.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    }
	    
	    public void paint(Graphics g) {
	        if(isVisible()) {
	            
	            JTextComponent component = this.getComponent();
	            TextUI mapper = component.getUI();
	            Rectangle rec = null;
	            
	            try {
	                rec = mapper.modelToView(component, this.getDot());
	            } catch(Exception ex) {
	                ex.printStackTrace();
	            }
	            
	            g.fillRect(rec.x, rec.y + 2, 8, 11);
	        }
	    }
	    
	    public void setVisible(boolean arg0){
	    	if(!arg0)this.setVisible(true);
	    	super.setVisible(arg0);
	    }
	    
	    public void damage(Rectangle r) {
	        if (r != null) {
	            x = r.x;
	            y = r.y;
	            width = 8;
	            height = r.height;
	            repaint();
	            
	        }
	    }
	}
}



