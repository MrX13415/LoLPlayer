package net.icelane.amplifire.player.device;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.icelane.amplifire.Application;
import net.icelane.amplifire.analyzer.Analyzer;
import net.icelane.amplifire.ui.ui.UIFrame;

public class FrequencyGenerator extends AudioDeviceLayer {

	private static FrequencyGenerator currentFrequencyDeviceLayer = null;
	private static SettingsUI settingsUI = null;
	private int frequency = 432;
	private float volume = 0.2f; 
	
	private Thread generatorThread;
	
	public FrequencyGenerator() {
		super();
		
		try {
			initGenerator();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getDisplayName() {
		return "Frequency Tone Generator";
	}

	@Override
	public String getDescription() {
		return "A device to generate tones via frequencies";
	}

	public static FrequencyGenerator getInstance(){
		if (currentFrequencyDeviceLayer == null)
			currentFrequencyDeviceLayer = new FrequencyGenerator();

		return currentFrequencyDeviceLayer;
	}
	
	public void start(){

	}
	
	@Override
	public void setVolume(float vol) {
		float vmax = 1f;
		float vmin = 0f;
		
		//linear:
		float vnew = (vmin + (vmax - vmin) / 1f * vol);
			
		//log:
//		float vdelta = vmin - vmax;
//		float vnew = (float) (Math.log(vol/100f) * (vdelta / Math.log(0.01f/100f)) + vmax);
//		if (vnew > vmax) vnew = vmax;
//		if (vnew < vmin) vnew = vmin;
		System.out.println(vnew);
		volume = vnew;
	}
	
	
	/**
	 * 
	 * 
	 * @throws LineUnavailableException
	 */
	public void initGenerator() throws LineUnavailableException {

		generatorThread = new Thread(
			(Runnable) () -> {				
				claim(this);
				super.open(new AudioFormat(48000, 16, 2, true, false));
				
				while(settingsUI != null)
				{
					byte[] b = monoToStereo(generateSineWave(frequency, 1000, volume));
					try {
						write(this, b, 0, b.length);
					} catch (LineUnavailableException e1) {						
						e1.printStackTrace();
					}
				}
				
				flush();
				close();
				release(this);
			}
		);
	}
	
	public void setFrequency(int freq) {
		if (freq >= 0) frequency = freq;
	}

	@Override
	public boolean hasSettingsUI() {
		return true;
	}

	@Override
	public void OpenSettingsUI(JFrame parent) {
		if (settingsUI == null){
			settingsUI = new SettingsUI(parent, this);
			generatorThread.start();
		}
	}
	
	@Override
	public void CloseSettingsUI() {
		if (settingsUI != null){
			settingsUI.setVisible(false);
			settingsUI.dispose();
			settingsUI = null;
		}
	}
	
	public class SettingsUI extends UIFrame implements ChangeListener, ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3673906249509467472L;
		
		private JSlider freqSilder;
		private JSlider volSilder;
		private JTextField freqTextField;
		
		private FrequencyGenerator frequencyGen;
		private int lastFreqency = 432; //Hz
		private int lastVolume = 20; 
		
		public SettingsUI(JFrame parent, FrequencyGenerator frequencyGen) {
//			super(parent);
		
			this.frequencyGen = frequencyGen;
			
			JLabel freqLabel = new JLabel("Tone frequency: ");
			freqLabel.setForeground(Application.getColors().color_forground1);
			
			JLabel volLabel = new JLabel("Tone Volume: ");
			volLabel.setForeground(Application.getColors().color_forground1);
			
			freqSilder = new JSlider(1, 1000, lastFreqency);
			freqSilder.addChangeListener(this);
			
			freqTextField = new JTextField();
			freqTextField.setSize(5, 5);
			freqTextField.addActionListener(this);
			freqTextField.setText(String.valueOf(lastFreqency) + " Hz");
			
			volSilder = new JSlider(0, 100, lastVolume);
			volSilder.addChangeListener(this);
			
			JPanel freqPanel = new JPanel(new BorderLayout());
			freqPanel.setOpaque(false);
			freqPanel.add(freqLabel, BorderLayout.NORTH);
			freqPanel.add(freqSilder, BorderLayout.CENTER);
			freqPanel.add(freqTextField, BorderLayout.SOUTH);
			
			JPanel volPanel = new JPanel(new BorderLayout());
			volPanel.setOpaque(false);
			volPanel.add(volLabel, BorderLayout.NORTH);
			volPanel.add(volSilder, BorderLayout.CENTER);
			
			JPanel mp = new JPanel(new BorderLayout());
			mp.setOpaque(false);
			mp.add(freqPanel, BorderLayout.CENTER);
			mp.add(volPanel, BorderLayout.SOUTH);
			mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			this.getTitleFrame().getResizeButton().setVisible(false);
			this.getResizeHandler().setBlocked(true);
			this.getContentPanePanel().add(mp);

			this.setPreferredSize(new Dimension(430, this.getPreferredSize().height));
			
			int locX = parent.getLocation().x;
			int locY = parent.getLocation().y + this.getPreferredSize().height; 
			
			this.setLocation(locX < 0 ? 10 : locX, locY < 0 ? 10 : locY);

			this.pack();
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			this.setVisible(true);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource().equals(freqSilder)){
				lastFreqency = freqSilder.getValue();
				setFrequency(lastFreqency);
				freqTextField.setText(String.valueOf(lastFreqency) + " Hz");
			}
			if (e.getSource().equals(volSilder)){
				lastVolume = volSilder.getValue();
				setVolume((float)lastVolume / 100f);
				volSilder.setValue(lastVolume);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(freqTextField)){
				try {
					lastFreqency = Integer.valueOf(freqTextField.getText().replace("Hz", "").trim());
				} catch (Exception e2) {
					freqTextField.setText(String.valueOf(lastFreqency));
				}
				
				if (lastFreqency < 0) {
					lastFreqency = 0;
					freqTextField.setText(String.valueOf(lastFreqency));
				}
				
				if (lastFreqency < freqSilder.getMaximum())
					freqSilder.setValue(lastFreqency);
				
				if (!freqTextField.getText().contains("Hz"))
					freqTextField.setText(freqTextField.getText() + " Hz");
				
				setFrequency(lastFreqency);
			}
		}
		
		public void setFrequency(int freq){
			if (frequencyGen != null) frequencyGen.setFrequency(freq);
		}
	}
	
}
