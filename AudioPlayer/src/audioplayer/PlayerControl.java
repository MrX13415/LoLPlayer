package audioplayer;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.activity.InvalidActivityException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;

import javazoom.jl.decoder.JavaLayerException;
import net.mrx13415.searchcircle.swing.JSearchCircle;
import audioplayer.gui.AboutDialog;
import audioplayer.gui.AudioFilePropertiesDialog;
import audioplayer.gui.ColorDialog;
import audioplayer.gui.UserInterface;
import audioplayer.medialibary.DesignMedienPlayer;
import audioplayer.medialibary.DesignMedienPlayerDB;
import audioplayer.medialibary.DesignerMedienPlayerSortby;
import audioplayer.player.AudioFile;
import audioplayer.player.AudioFile.UnsupportedFileFormatException;
import audioplayer.player.AudioPlaylist;
import audioplayer.player.analyzer.Analyzer;
import audioplayer.player.analyzer.components.JGraph.DrawMode;
import audioplayer.player.codec.AudioProcessingLayer;
import audioplayer.player.codec.AudioType;
import audioplayer.player.device.AudioDeviceLayer;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;
import audioplayer.player.listener.PlaylistEvent;
import audioplayer.player.listener.PlaylistIndexChangeEvent;
import audioplayer.process.GetherAudioFileInfoProcess;
import audioplayer.process.LoadDirProcess;
import audioplayer.process.LoadFilesProcess;
import audioplayer.process.LoadPlaylistDBProcess;
import audioplayer.process.LoadPlaylistProcess;
import audioplayer.process.Process;
import audioplayer.process.SavePlaylistDBProcess;
import audioplayer.process.SavePlaylistProcess;
import audioplayer.process.SearchPlaylistProcess;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlayerControl extends UserInterface implements PlayerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AudioProcessingLayer audioProcessingLayer = AudioProcessingLayer.getEmptyInstance();

	private AudioPlaylist searchPlaylist = new AudioPlaylist();	
	private AudioPlaylist audioPlaylist = new AudioPlaylist();
	
	private AudioPlaylist currentPlaylist = audioPlaylist;
	
	private Thread uiUpdaterThread;

	private boolean wasPausedOnSearchBarMousePressed;

	private Analyzer analyzer;
	private boolean autoPlay = false;
	
	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public PlayerControl() {
		
		currentPlaylist.addPlayerListener(this);
		
		loadPlaylistFromDB();
		new LoadPlaylistProcess(this);
		new GetherAudioFileInfoProcess(this);
		
		analyzer = new Analyzer(getPlayerControlInterface().getPlayerInterfaceGraph());
//		analyzer.setDefaultChannelGraphColor(1, Colors.color_graph_defaultChannelGraphColor5);
		analyzer.setMergedChannels(false);

		initUIupdaterThread();

		try {
			System.out.print("Test audio device ...\t\t\t");
			new AudioDeviceLayer().test();
			System.out.println("OK");
		} catch (JavaLayerException ex) {
			System.out.println("ERROR");
		}

		if (!audioPlaylist.isEmpty()) {
			initAudioFile(); // autoplay on startup if playlist had content ...
		}
		
		//playlist search ..
		getPlaylistInterface().getSearchField().addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				onSearchPlaylist();
			}
		});
		
		getPlaylistInterface().getSearchField().addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					int index = getPlaylistInterface().getPlaylistTable().getSelectedRow();
					if(index >= 0) onPlaylistDoubleClick(index);
				}
			}
		});
	}

	public void onSearchPlaylist(){
		new SearchPlaylistProcess(this);
	}
	
	public AudioProcessingLayer getAudioProcessingLayer() {
		return audioProcessingLayer;
	}

	public AudioPlaylist getAudioPlaylist() {
		return audioPlaylist;
	}

	public AudioPlaylist getSearchPlaylist() {
		return searchPlaylist;
	}

	public AudioPlaylist getCurrentPlaylist() {
		return currentPlaylist;
	}

	public Thread getUiUpdaterThread() {
		return uiUpdaterThread;
	}

	public boolean isWasPausedOnSearchBarMousePressed() {
		return wasPausedOnSearchBarMousePressed;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void openFiles(File[] file) {
		audioProcessingLayer.stop();
		audioPlaylist.clear();
		addFiles(file, true);
		autoPlay = true;
	}

	public void openDirs(File[] dir) {
		audioProcessingLayer.stop();
		audioPlaylist.clear();
		addDirs(dir, true);
	}
	
	public void addDirs(File[] dir) {
		addDirs(dir, false);
	}

	public void addFiles(File[] file) {
		addFiles(file, false);
	}

	public void addDirs(File[] dir, boolean autoPlay) {
		this.autoPlay = autoPlay;
		new LoadDirProcess(this, dir);
		new GetherAudioFileInfoProcess(this);
	}

	public void addFiles(File[] file, boolean autoPlay) {
		this.autoPlay = autoPlay;
		new LoadFilesProcess(this, file);
		new GetherAudioFileInfoProcess(this);
	}

	public void loadPlaylistFromDB() {
		new LoadPlaylistDBProcess(this);
		new GetherAudioFileInfoProcess(this);
	}

	public SavePlaylistDBProcess savePlaylistToDB() {
		Process p = getStatusbar().getProcess();
		if (p == null || !(p instanceof SavePlaylistDBProcess)) {
			getStatusbar().stopAllProcess();
			return new SavePlaylistDBProcess(this);
		} else if (p instanceof SavePlaylistDBProcess)
			return (SavePlaylistDBProcess) p;
		return null;
	}
	
	public SavePlaylistProcess savePlaylistToDataFile() {
		Process p = getStatusbar().getProcess();
		if (p == null || !(p instanceof SavePlaylistProcess)) {
			getStatusbar().stopAllProcess();
			return new SavePlaylistProcess(this);
		} else if (p instanceof SavePlaylistProcess)
			return (SavePlaylistProcess) p;
		return null;
	}

	public synchronized void initAudioFileAutoPlay() {
		if (initAudioFile()) {
			try {
				audioProcessingLayer.play();
			} catch (InvalidActivityException e) {
			}
		}
	}

	public synchronized boolean initAudioFile() {
		AudioPlaylist currentPlaylist = this.currentPlaylist;
		
		if (!currentPlaylist.isEmpty()) {
			AudioFile af = currentPlaylist.get();

			try {
				if (!af.isInitialized()){
					af.initialize();
					getPlaylistInterface().getPlaylistTableModel().updateData(currentPlaylist.indexOf(af), af);
				}
								
				initAudioProcessingLayer(af);

				System.out.println("Playing type: " + af.getType().getName()
						+ " file: " + af.getFile().getAbsolutePath());

				this.getPlayerControlInterface().getSearchBar()
						.setMaximum(audioProcessingLayer.getStreamLength());
				
				analyzer.init(audioProcessingLayer.getAudioDevice());
				
				if (!af.isSupported())
					throw new UnsupportedFileFormatException(af);
				
			} catch (UnsupportedFileFormatException e) {
				raiseNotSupportedFileFormatError(af, e, false);

				if (!currentPlaylist.isEmpty())
					initAudioFile();
			}

			return true;
		}
		return false;
	}

	public void raiseNotSupportedFileFormatError(AudioFile af, Exception e, boolean provideErrorMsg) {		
		audioPlaylist.remove(af);
		searchPlaylist.remove(af);
		
		System.err.println("Error: File format not supported!:" + e);
		System.err.printf("Type: %s File: %s\n", af.getType().getName(), af
				.getFile().getAbsolutePath());

		String msg = String.format(
				"File format not supported!\nType: %s\nFile: %s", af.getType()
						.getName(), af.getFile().getAbsolutePath());

		if (provideErrorMsg)
			JOptionPane.showMessageDialog(this, msg,
					Application.App_Name_Version, JOptionPane.ERROR_MESSAGE);
	}

	public void raiseVolumeControlError() {
		System.err.println("Error: No volume control type not supported");
//		System.err.println(ex);

		String msg = String.format("Error: No volume control type not supported!");

		JOptionPane.showMessageDialog(this, msg, Application.App_Name_Version,
				JOptionPane.ERROR_MESSAGE);
	}

	public void initAudioProcessingLayer(AudioFile af) {
		AudioProcessingLayer newppl = af.getAudioProcessingLayer();
		AudioProcessingLayer oldppl = audioProcessingLayer;

		if (audioProcessingLayer != null) {
			audioProcessingLayer.stop();
			newppl.setVolume(audioProcessingLayer.getVolume());
		}

		audioProcessingLayer = newppl;
		audioProcessingLayer.addPlayerListener(this);
		audioProcessingLayer.initialzePlayer(af);
		audioProcessingLayer.setPostion(0);

		oldppl.cleanInstance();
		oldppl = null;
	}

	private Runnable getUIupdater() {

		return new Runnable() {

			@Override
			public void run() {

				while (true) {
					if (analyzer != null)
						analyzer.setDebug(Application.isDebug());
					getPlayerControlInterface().getSearchBar().setDebug(
							Application.isDebug());
					getPlayerControlInterface().getVolume().setDebug(
							Application.isDebug());

					try {
						Thread.sleep(16); //60 FPS
					} catch (InterruptedException e) {
					}
                                        
					if (audioProcessingLayer == null)
						continue;

					// Synchronize the audio device and the analyzer ...
					if (audioProcessingLayer.getAudioDevice().getAnalyzer() != analyzer)
						audioProcessingLayer.getAudioDevice().setAnalyzer(analyzer);

					// Update the song frequency in the statistic ...
					long time = audioProcessingLayer.getTimePosition();
					long lenght = audioProcessingLayer.getStreamLength();
					double posperc = Math.round(100d / (double) lenght * (double) time * 10d) / 10d;
					if (posperc > 20f){
						try {
							Application.getApplication().getDatabase().updateFrequency(audioProcessingLayer.getAudioFile().getId());
						} catch (Exception e) {}					
					}
					
//					SwingUtilities.invokeLater(new Runnable() {
//
//						@Override
//						public void run() {

							if (audioProcessingLayer.isNew())
								getPlayerControlInterface().getSearchBar()
										.setEnabled(false);
							else
								getPlayerControlInterface().getSearchBar()
										.setEnabled(true);

							getPlayerControlInterface().getSearchBar()
									.setBarValue(
											audioProcessingLayer
													.getTimePosition());
							getPlayerControlInterface().setDisplay(
									audioProcessingLayer);
							getPlayerControlInterface().setPlayPause(
									audioProcessingLayer.isPlaying());

							// synchronize button and bar
							if (!audioProcessingLayer.isSkipFrames())
								getPlayerControlInterface().getSearchBar()
										.setButtonValueButEvent(
												audioProcessingLayer
														.getTimePosition());
//						}
//					});
				}
			}
		};
	}

	private void initUIupdaterThread() {
		uiUpdaterThread = new Thread(getUIupdater());
		uiUpdaterThread.setName("UI-Updater-Thread");
		uiUpdaterThread.start();
	}
	
	public void switchPlaylist(boolean toSearchPlaylist){
		if (isSearchPlaylistActive() == toSearchPlaylist) return;
		
		currentPlaylist.removePlayerListener(this);
		
		if (toSearchPlaylist){
			getPlaylistInterface().getPlaylistViewModeButton().setSelected(true);
			System.out.println("Switched to search playlist ...");
			currentPlaylist = searchPlaylist;
		}else{
			getPlaylistInterface().getPlaylistViewModeButton().setSelected(false);
			System.out.println("Switched to audio playlist ...");
			currentPlaylist = audioPlaylist;
		}

		currentPlaylist.addPlayerListener(this);
		
		//make sure it's playing from the right playlist
		if (audioProcessingLayer.isInitialized()
				|| audioProcessingLayer.isStopped())
			initAudioFile();

		try {
			//update gui
			getPlaylistInterface().getPlaylistTableModel().setContent(currentPlaylist);
			//(re)set selection
			getPlaylistInterface().getPlaylistTable().changeSelection(currentPlaylist.getIndex(), 0, false, false);
		} catch (Exception e) {}
		
	}
	
	
	public boolean isSearchPlaylistActive(){
		return currentPlaylist.equals(searchPlaylist);
	}
	
	@Override
	public void onButtonPlaylistviewMode(boolean buttonPressed){
		switchPlaylist(buttonPressed);
	}
    
	@Override
	public void onButtonPlay() {
		getPlaylistInterface().getPlaylistTable().changeSelection(
				currentPlaylist.getIndex(), 0, false, false);
		audioProcessingLayer.togglePlayPause();
	}

	@Override
	public void onButtonStop() {
		audioProcessingLayer.stop();
		analyzer.clearData();
	}

	@Override
	public void onButtonFrw() {
		analyzer.clearData();
		currentPlaylist.nextIndex();
	}

	@Override
	public void onButtonRev() {
		analyzer.clearData();
		currentPlaylist.priorIndex();
	}

	@Override
	public void onPlaylistDoubleClick(int index) {
		currentPlaylist.setNextIndex(index);
		initAudioFileAutoPlay();
		System.out.println("no. " + currentPlaylist.getIndex());
	}
	
	@Override
	public void onPlaylistRightClick(int index) {
		new AudioFilePropertiesDialog(currentPlaylist.get(index));
	}

	@Override
	public void onSearchBarButtonMove(JSearchCircle s) {
		if (audioProcessingLayer != null)
			audioProcessingLayer.setPostion((long) s.getButtonValue());
	}

	@Override
	public void onVolumeButtonMove(final JSearchCircle v) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (audioProcessingLayer != null)
					audioProcessingLayer.setVolume((float) v.getButtonValue());
				v.setBarValue(v.getButtonValue());
			}
		});
		onPlayerVolumeChange(new PlayerEvent(audioProcessingLayer));
	}

	@Override
	public void onSearchBarMousePressed(JSearchCircle s) {
		wasPausedOnSearchBarMousePressed = audioProcessingLayer.isPaused();
		if (audioProcessingLayer.isInitialized()
				|| audioProcessingLayer.isStopped()) {
			if (audioProcessingLayer.isStopped())
				audioProcessingLayer.resetPlayer();
			audioProcessingLayer.setPause(true);
			audioProcessingLayer.createDecoderThread();
			wasPausedOnSearchBarMousePressed = true;
		} else if (!audioProcessingLayer.isPaused())
			audioProcessingLayer.togglePause();
	}

	@Override
	public void onSearchBarMouseReleased(JSearchCircle s) {
		if (!wasPausedOnSearchBarMousePressed) {
			audioProcessingLayer.setPause(false);
		} else {
			audioProcessingLayer.setPause(true);
		}
	}

	@Override
	public void onMenu_file_open() {
		JFileChooser fc = initOpenDialog();
		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			switchPlaylist(false);
			File[] file = fc.getSelectedFiles();
			openFiles(file);
		}
	}

	@Override
	public void onMenu_file_opendir() {
		JFileChooser fc = initDirOpenDialog();

		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			switchPlaylist(false);
			File[] dir = fc.getSelectedFiles();
			openDirs(dir);
		}
	}

	@Override
	public void onMenu_file_exit() {
		Application.exit();
	}

	@Override
	public void onMenu_playlist_add() {
		JFileChooser fc = initOpenDialog();
		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			File[] file = fc.getSelectedFiles();
			addFiles(file);
		}
	}

	@Override
	public void onMenu_playlist_adddir() {
		JFileChooser fc = initDirOpenDialog();

		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			File[] dir = fc.getSelectedFiles();
			addDirs(dir);
		}
	}

	@Override
	public void onMenu_playlist_remove() {
		int[] rows = getPlaylistInterface().getPlaylistTable().getSelectedRows();
		
		for (int i : rows) {
			AudioFile af = currentPlaylist.get(rows[0]);
			if (currentPlaylist.get().equals(af)) {
				audioProcessingLayer.stop();
				currentPlaylist.remove(rows[0]);
				initAudioFileAutoPlay();
				getPlaylistInterface().getPlaylistTable().changeSelection(rows[0], 0,
						false, false);
			} else {
				currentPlaylist.remove(rows[0]);
				getPlaylistInterface().getPlaylistTable().changeSelection(rows[0], 0,
						false, false);
			}
			if (currentPlaylist.isEmpty()) audioProcessingLayer = AudioProcessingLayer.getEmptyInstance();
			System.out.println("Removed from playlist: " + af.getFile().getAbsolutePath());
		}
	}

	@Override
	public void onMenu_playlist_clear() {
		audioProcessingLayer.stop();
		audioProcessingLayer = AudioProcessingLayer.getEmptyInstance();
		currentPlaylist.clear();
		System.out.println("Playlist cleared ...");
		
		if (isSearchPlaylistActive()) switchPlaylist(false);
	}

	@Override
	public void onMenu_playlist_up() {
		int[] rows = getPlaylistInterface().getPlaylistTable()
				.getSelectedRows();

		for (int i = 0; i < rows.length; i++) {
			AudioFile af = currentPlaylist.get(rows[i]);
			if (currentPlaylist.isFistElement(af))
				return;
			currentPlaylist.moveUp(af);

			System.out.println("Moved up in playlist: "
					+ af.getFile().getAbsolutePath());
		}
		getPlaylistInterface().getPlaylistTable().changeSelection(rows[0] - 1,
				0, false, false);
		getPlaylistInterface().getPlaylistTable().setRowSelectionInterval(
				rows[0] - 1, rows[rows.length - 1] - 1);
	}

	@Override
	public void onMenu_playlist_down() {
		int[] rows = getPlaylistInterface().getPlaylistTable()
				.getSelectedRows();
		for (int i = rows.length - 1; i >= 0; i--) {
			AudioFile af = currentPlaylist.get(rows[i]);
			if (currentPlaylist.isLastElement(af))
				return;
			currentPlaylist.moveDown(af);

			System.out.println("Moved down in playlist: "
					+ af.getFile().getAbsolutePath());
		}
		getPlaylistInterface().getPlaylistTable().changeSelection(rows[0] + 1,
				0, false, false);
		getPlaylistInterface().getPlaylistTable().setRowSelectionInterval(
				rows[0] + 1, rows[rows.length - 1] + 1);
	}
	
	public void onMenu_playlist_shuffle() {
		getMenu().getMenu_playlist_shuffle().setText("Shuffle: Enabled");
		getMenu().getMenu_playlist_shuffle().setText("Shuffle: Disabled");
		
		currentPlaylist.setShuffle(!currentPlaylist.isShuffle());
		
		if (currentPlaylist.isShuffle())
			getMenu().getMenu_playlist_shuffle().setText("Disable shufflemode");
		else
			getMenu().getMenu_playlist_shuffle().setText("Enable shufflemode");		
	}
	
	@Override
	public void onMenu_media_library() {
		DesignMedienPlayer mlib = new DesignMedienPlayer();
		DesignerMedienPlayerSortby mlibs = new DesignerMedienPlayerSortby();
		DesignMedienPlayerDB mlibdb = new DesignMedienPlayerDB();
		
		JFrame mlibf = new JFrame("Media Library");
		mlibf.setLayout(new BorderLayout());
		mlibf.getContentPane().add(mlib, BorderLayout.NORTH);
		mlibf.getContentPane().add(mlibdb, BorderLayout.CENTER);
		mlibf.getContentPane().add(mlibs, BorderLayout.SOUTH);
		
		mlibf.pack();
		mlibf.setVisible(true);
		
	}
	
	@Override
	public void onMenu_desing_color() {
		ColorDialog cd = new ColorDialog(this);		
	}
	
	@Override
	public void onMenu_graph_merge() {
		analyzer.setMergedChannels(!analyzer.isMergedChannels());
	}

	@Override
	public void onMenu_graph_bfilter() {
		getPlayerControlInterface().getPlayerInterfaceGraph()
				.setBlurFilter(
						!getPlayerControlInterface().getPlayerInterfaceGraph()
								.isBlurFilter());
	}
	
	@Override
	public void onMenu_graph_geffect() {
		getPlayerControlInterface().getPlayerInterfaceGraph()
				.setGlowEffect(
						!getPlayerControlInterface().getPlayerInterfaceGraph()
								.isGlowEffect());
	}
	
	@Override
	public void onMenu_graph_dmode_change(DrawMode mode) {
		getPlayerControlInterface().getPlayerInterfaceGraph().setDrawMode(mode);
		System.out.println("Graph drawing mode set to: "+ mode.toString());
	}

	@Override
	public void onMenu_help_about() {
		System.out.println(Application.App_Name_Version);
		System.out.println(Application.App_Author);
		System.out.println(Application.App_License);
		new AboutDialog(this);
	}

	private JFileChooser initOpenDialog() {
		JFileChooser fc = new JFileChooser(new File(
				System.getProperty("user.home")));

		// define file filters ...
		FileFilter ff = AudioType.getAllSupportedFilesFilter();
		fc.setFileFilter(ff);
		for (AudioType at : AudioType.getTypes()) {
			fc.setFileFilter(at);
		}

		fc.setFileFilter(ff);
		fc.setMultiSelectionEnabled(true);
		fc.setAcceptAllFileFilterUsed(true);

		return fc;
	}

	private JFileChooser initDirOpenDialog() {
		JFileChooser fc = new JFileChooser(new File(
				System.getProperty("user.home")));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(true);
		return fc;
	}

	@Override
	public void onPlayerStart(PlayerEvent event) {
		System.out.println("started @ frame "
				+ event.getSource().getTimePosition());

	}

	@Override
	public void onPlayerStop(PlayerEvent event) {
		System.out.println("stoped @ frame "
				+ event.getSource().getTimePosition());

	}

	@Override
	public void onPlayerNextSong(PlayerEvent event) {
		currentPlaylist.nextIndex();
	}

	@Override
	public void onPlayerVolumeChange(PlayerEvent event) {
		System.out.println("volume changed to: "
				+ event.getSource().getVolume());

	}

	@Override
	public void onPlayerPositionChange(PlayerEvent event) {

	}

	@Override
	public void onPlaylistFileAdd(PlaylistEvent event) {
		if (isSearchPlaylistActive()) return;
		
		boolean aplwasEmpty = audioPlaylist.size() == 1 && audioPlaylist.get(0).equals(event.getAudioFile());

		getPlaylistInterface().getPlaylistTableModel().insertData(event.getAudioFile());
		
		System.out.println("Added to playlist: " + event.getAudioFile().getFile().getAbsolutePath());

		//TODO: öalskdö
		if (aplwasEmpty) {
			audioPlaylist.resetToFirstIndex();
			if (autoPlay) initAudioFileAutoPlay();
			else initAudioFile();
		}		
	}

	@Override
	public void onPlaylistFileRemove(PlaylistEvent event) {
		getPlaylistInterface().getPlaylistTableModel().removeData(event.getIndex());
	}

	@Override
	public void onPlaylistMoveUp(PlaylistIndexChangeEvent event) {
		getPlaylistInterface().getPlaylistTableModel().updateData(event.getPriorIndex(), getAudioPlaylist().get(event.getPriorIndex()));
		getPlaylistInterface().getPlaylistTableModel().updateData(event.getNewIndex(), getAudioPlaylist().get(event.getNewIndex()));
	}

	@Override
	public void onPlaylistMoveDown(PlaylistIndexChangeEvent event) {
		getPlaylistInterface().getPlaylistTableModel().updateData(event.getPriorIndex(), getAudioPlaylist().get(event.getPriorIndex()));
		getPlaylistInterface().getPlaylistTableModel().updateData(event.getNewIndex(), getAudioPlaylist().get(event.getNewIndex()));
	}

	@Override
	public void onPlaylistIncrement(PlaylistIndexChangeEvent event) {
		if (isSearchPlaylistActive()) audioPlaylist.overrideIndex(audioPlaylist.indexOf(currentPlaylist.get()));
	
		initAudioFileAutoPlay();
		getPlaylistInterface().getPlaylistTable().changeSelection(
				event.getNewIndex(), 0, false, false);

		System.out.println("Changed playlist index from no. "
				+ event.getPriorIndex() + " to no. " + event.getNewIndex());
	}

	@Override
	public void onPlaylistDecrement(PlaylistIndexChangeEvent event) {
		onPlaylistIncrement(event);
	}

	@Override
	public void onPlaylistClear(PlaylistEvent event) {
		getPlaylistInterface().getPlaylistTableModel().setContent(currentPlaylist);
	}

	@Override
	public void onPlaylistIndexSet(PlaylistIndexChangeEvent event) {
		if (isSearchPlaylistActive()) audioPlaylist.overrideIndex(audioPlaylist.indexOf(currentPlaylist.get()));
		
		initAudioFileAutoPlay();
		getPlaylistInterface().getPlaylistTable().changeSelection(
				event.getNewIndex(), 0, false, false);

		System.out.println("Changed playlist index from no. "
				+ event.getPriorIndex() + " to no. " + event.getNewIndex());
	}

	@Override
	public void onGraphDetailBarChange(JSlider detailBar) {
		int value = detailBar.getValue();
		float hval = (value * 13f / (2f + 1) / 1000f) + 0.3f;
		System.out
				.println("DetailLevel: " + value + " HeightModifier: " + hval);

		analyzer.setDetailLevel(value);
		getPlayerControlInterface().getPlayerInterfaceGraph().setHeightLevel(
				hval);
		this.getPlayerControlInterface().getHeightlevel()
				.setValue((int) (hval * 1000));
	}

	@Override
	public void onHeightLevelBarChange(JSlider heightLevelBar) {
		int value = heightLevelBar.getValue();
		float hval = (value / 1000f);
		System.out.println("HeightModifier: " + hval);
		getPlayerControlInterface().getPlayerInterfaceGraph().setHeightLevel(
				hval);
	}
	
	@Override
	public void onZoomLevelBarChange(JSlider zoomLevelBar) {
		int value = zoomLevelBar.getValue();
		System.out.println("ZoomLevel: " + value);
		getPlayerControlInterface().getPlayerInterfaceGraph().setZoomlLevel(value);
	}

}
