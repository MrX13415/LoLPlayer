package net.icelane.amplifire.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.ui.ui.UIFrame;
import net.mrx13415.searchcircle.imageutil.color.HSB;

public class ColorDialog extends UIFrame implements ActionListener,
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5359730769550465456L;

	private JCheckBox rainbowEffect;

	private JSlider hue;
	private JSlider saturation;
	private JSlider brightness;

	private JButton hreset;
	private JButton sreset;
	private JButton breset;

	private float fhue;
	private float fsaturation;
	private float fbrightness;

	public ColorDialog(JFrame parent) {
//		super(parent);

		rainbowEffect = new JCheckBox("Enable rainbow effect");
		rainbowEffect.setForeground(Color.white);
		rainbowEffect.addActionListener(this);
		rainbowEffect.setSelected(Application.getColors().isRainbowColor());

		hreset = new JButton();
		hreset.setPreferredSize(new Dimension(25, 25));
		hreset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				hue.setValue(0);
			}
		});

		sreset = new JButton();
		sreset.setPreferredSize(new Dimension(25, 25));
		sreset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saturation.setValue(0);
			}
		});
		
		breset = new JButton();
		breset.setPreferredSize(new Dimension(25, 25));
		breset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				brightness.setValue(0);
			}
		});
		
		hue = new JSlider(0, 10000, (int) (Application.getColors().getRainbowHue() * 10000));
		hue.setValue((int) (Application.getColors().getRainbowHue() * 10000));
		hue.setEnabled(!rainbowEffect.isSelected());
		hue.addChangeListener(this);

		saturation = new JSlider(-10000, 10000, (int) (Application.getColors().getRainbowSaturation() * 10000));
		saturation.addChangeListener(this);

		brightness = new JSlider(-10000, 10000, (int) (Application.getColors().getRainbowBrightness() * 10000));
		brightness.addChangeListener(this);

		JLabel t1 = new JLabel("Hue: ");
		t1.setForeground(Color.white);
		t1.setPreferredSize(new Dimension(70, 25));

		JLabel t2 = new JLabel("Saturation: ");
		t2.setForeground(Color.white);
		t2.setPreferredSize(new Dimension(70, 25));

		JLabel t3 = new JLabel("Brightness: ");
		t3.setForeground(Color.white);
		t3.setPreferredSize(new Dimension(70, 25));

		JPanel hPanel = new JPanel(new BorderLayout(5, 5));
		hPanel.setOpaque(false);
		hPanel.add(hue);
		hPanel.add(t1, BorderLayout.WEST);
		hPanel.add(hreset, BorderLayout.EAST);

		JPanel sPanel = new JPanel(new BorderLayout(5, 5));
		sPanel.setOpaque(false);
		sPanel.add(saturation);
		sPanel.add(t2, BorderLayout.WEST);
		sPanel.add(sreset, BorderLayout.EAST);

		JPanel bPanel = new JPanel(new BorderLayout(5, 5));
		bPanel.setOpaque(false);
		bPanel.add(brightness);
		bPanel.add(t3, BorderLayout.WEST);
		bPanel.add(breset, BorderLayout.EAST);

		JPanel sp = new JPanel(new BorderLayout());
		sp.setOpaque(false);
		sp.add(hPanel, BorderLayout.NORTH);
		sp.add(sPanel, BorderLayout.CENTER);
		sp.add(bPanel, BorderLayout.SOUTH);

		JPanel mp = new JPanel(new BorderLayout());
		mp.setOpaque(false);
		mp.add(rainbowEffect, BorderLayout.NORTH);
		mp.add(sp, BorderLayout.CENTER);
		mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.getTitleFrame().getResizeButton().setVisible(false);
		this.getResizeHandler().setBlocked(true);
		this.getContentPanePanel().add(mp);

		this.setPreferredSize(new Dimension(430, this.getPreferredSize().height));
		
		int locX = parent.getLocation().x - this.getPreferredSize().width;
		int locY = parent.getLocation().y; 
		
		this.setLocation(locX < 0 ? 10 : locX, locY < 0 ? 10 : locY);

		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
//		Thread th = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while (isDisplayable()) {
//					hue.setValue((int) (Application.getColors().getRainbowHue() * 10000));
//					System.out.println((int) (Application.getColors().getRainbowHue() * 10000));
//				}
//			}
//		});
		
//		th.setName("UI-Updater-Thread#2-ColorSliderSync");
//		th.start();		
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(rainbowEffect)) {
			if (rainbowEffect.isSelected()) {
				hue.setEnabled(false);
				Application.getColors().setRainbowColor(true);
				Application.getColors().initRainbowColorThread(
						new HSB(fhue, fsaturation, fbrightness));
			} else {
				Application.getColors().setRainbowColor(false);
				hue.setEnabled(true);
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(hue)) {
			fhue = hue.getValue() / 10000f;
		}

		if (e.getSource().equals(saturation)) {
			fsaturation = saturation.getValue() / 10000f;
		}

		if (e.getSource().equals(brightness)) {
			fbrightness = brightness.getValue() / 10000f;
		}

		Application.getColors().setRainbowHue(fhue);
		Application.getColors().setRainbowSaturation(fsaturation);
		Application.getColors().setRainbowBrightness(fbrightness);
		Application.getColors().changeColor();
		Application.getColors().applayColors();
	}

}
