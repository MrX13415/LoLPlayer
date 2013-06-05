/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.PlayerControler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author dausol
 */
public class Display extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8638164518936991223L;

	private JLabel time;
	private JLabel info1;
	private JLabel info2;
	private JLabel info3;
	private JLabel statusbar1;
	private JLabel statusbar2;

	public Display() {
		defineComponets();
		addComponents();
	}

	private void defineComponets() {
		time = new JLabel("<html><font size=5>00:00:00</font><font size=3>  500</font></html>");
		time.setHorizontalAlignment(JLabel.CENTER);

		info1 = new JLabel("PLAYING");
		info1.setForeground(new Color(255, 128 ,0));
		
		info2 = new JLabel("80.0%");
		info2.setForeground(new Color(255, 128 ,0));
		info2.setPreferredSize(new Dimension(60, info2.getPreferredSize().height));
		info2.setHorizontalAlignment(JLabel.RIGHT);
		
		info3 = new JLabel(" ");

		statusbar1 = new JLabel("50.0%");
		statusbar1.setPreferredSize(new Dimension(45, statusbar1.getPreferredSize().height));
		statusbar1.setHorizontalAlignment(JLabel.RIGHT);
		statusbar1.setForeground(Color.gray);
		
		statusbar2 = new JLabel("My Song Title");
		statusbar2.setForeground(Color.gray);
	}

	private void addComponents() {

		JPanel infoPane = new JPanel(new GridLayout(3, 1, 5, 5));
		infoPane.setOpaque(false);
		infoPane.add(info1);
		infoPane.add(info2);

		JPanel timepane = new JPanel(new BorderLayout());
		timepane.setOpaque(false);
		timepane.add(time, BorderLayout.CENTER);
		timepane.add(infoPane, BorderLayout.LINE_END);

		JPanel bottomline = new JPanel(new BorderLayout());
		bottomline.setOpaque(false);
		bottomline.add(statusbar1, BorderLayout.LINE_START);
		bottomline.add(statusbar2, BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(timepane, BorderLayout.CENTER);
		this.add(bottomline, BorderLayout.PAGE_END);
		this.setOpaque(false);
		
	}

	public void setTimeText(long time) {
		
//		FontMetrics fm = this.time.getFontMetrics(this.time.getFont());
//		int size = (this.time.getHeight() - 2 * 17);
//		
//		while (true){
//			
//		}
//		
//		
//		System.out.println(fm.getHeight() + " " + size);
//		
//		
		this.time.setText(String.format("<html><font size=6 color=\"#ffffff\">%1$tH:%1$tM:%1$tS</font><font size=4 color=\"#ffffff\">  %1$tL</font></html>", time - 3600000));
	}

	public void setInfo1Text(String info1) {
		this.info1.setText(info1);
	}

	public void setInfo2Text(String info2) {
		this.info2.setText(info2);
	}

	public void setInfo3Text(String info3) {
		this.info3.setText(info3);
	}

	public void setStatusBar1Text(String info4) {
		this.statusbar1.setText(info4 + " ");
	}

	public void setStatusBar2Text(String bottomline) {
		this.statusbar2.setText(bottomline);
	}

}