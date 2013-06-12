package audioplayer;

import audioplayer.gui.AboutDialog;

import java.awt.Color;
import java.io.File;

import javax.activity.InvalidActivityException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SearchCircle;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import audioplayer.gui.UserInterface;
import audioplayer.player.AudioDeviceLayer;
import audioplayer.player.AudioPlaylist;
import audioplayer.player.analyzer.Analyzer;
import audioplayer.player.codec.AudioFile;
import audioplayer.player.codec.AudioFile.UnsupportedFileFormatException;
import audioplayer.player.codec.AudioProcessingLayer;
import audioplayer.player.codec.AudioType;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;
import audioplayer.player.listener.PlaylistEvent;
import audioplayer.player.listener.PlaylistIndexChangeEvent;
import audioplayer.process.LoadDirProcess;
import audioplayer.process.LoadFilesProcess;
import audioplayer.process.LoadPlaylistDBProcess;
import audioplayer.process.Process;
import audioplayer.process.SavePlaylistDBProcess;
import javazoom.jl.decoder.JavaLayerException;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class Control extends UserInterface implements PlayerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AudioProcessingLayer audioProcessingLayer = AudioProcessingLayer
			.getEmptyInstance();
	private AudioPlaylist audioPlaylist = new AudioPlaylist();

	private Thread uiUpdaterThread;

	private boolean wasPausedOnSearchBarMousePressed;

	private Analyzer analyzer;

	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public Control() {

		audioPlaylist.addPlayerListener(this);
		loadPlaylistFromDB();

		analyzer = new Analyzer(getPlayerControlInterface()
				.getPlayerInterfaceGraph());
		analyzer.setDefaultChannelGraphColor(1, new Color(255, 80, 0));
		analyzer.setMergedChannels(false);

		getPlayerControlInterface().getPlayerInterfaceGraph().setUi(this);

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
	}

	public AudioProcessingLayer getAudioProcessingLayer() {
		return audioProcessingLayer;
	}

	public AudioPlaylist getAudioPlaylist() {
		return audioPlaylist;
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
		addFiles(file);
	}

	public void openDirs(File[] dir) {
		audioProcessingLayer.stop();
		audioPlaylist.clear();
		addDirs(dir);
	}

	public void addDirs(File[] dir) {
		new LoadDirProcess(this, dir);
	}

	public void addFiles(File[] file) {
		new LoadFilesProcess(this, file);
	}

	public void loadPlaylistFromDB() {
		new LoadPlaylistDBProcess(this);
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

	public void addFile(File file) {
		addFile(file, true);
	}

	public void addFile(File file, boolean provideErrorMsg) {
		boolean aplwasEmpty = audioPlaylist.isEmpty();

		AudioFile af = new AudioFile(file);
		try {
			af.initAudioFile();
			audioPlaylist.add(af);
			System.out.println("Added to playlist: "
					+ af.getFile().getAbsolutePath());
		} catch (UnsupportedFileFormatException e) {
			raiseNotSupportedFileFormatError(af, provideErrorMsg);
		}

		if (aplwasEmpty) {
			audioPlaylist.resetToFirstIndex();
			initAudioFileAutoPlay();
		}
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
		if (!audioPlaylist.isEmpty()) {
			AudioFile af = audioPlaylist.get();

			initAudioProcessingLayer(af);

			System.out.println("Playing type: " + af.getType().getName()
					+ " file: " + af.getFile().getAbsolutePath());

			this.getPlayerControlInterface().getSearchBar()
					.setMaximum(audioProcessingLayer.getStreamLength());

			analyzer.init(audioProcessingLayer.getAudioDevice());

			if (!af.isSupported()) {
				raiseNotSupportedFileFormatError(af, true);
				audioPlaylist.remove(af);
				if (!audioPlaylist.isEmpty())
					initAudioFile();
			}

			return true;
		}
		return false;
	}

	public void raiseNotSupportedFileFormatError(AudioFile af,
			boolean provideErrorMsg) {
		System.err.println("Error: File format not supported!");
		System.err.printf("Type: %s File: %s\n", af.getType().getName(), af
				.getFile().getAbsolutePath());

		String msg = String.format(
				"File format not supported!\nType: %s\nFile: %s", af.getType()
						.getName(), af.getFile().getAbsolutePath());

		if (provideErrorMsg)
			JOptionPane.showMessageDialog(this, msg,
					Application.App_Name_Version, JOptionPane.ERROR_MESSAGE);
	}

	public void raiseVolumeControlError(Exception ex) {
		System.err.println("Error: Volume control not supported");
		System.err.println(ex);

		String msg = String.format("Volume control not supported!");

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
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}

					if (audioProcessingLayer == null)
						continue;

					// Synchronize the audio device and the analyzer ...
					audioProcessingLayer.getAudioDevice().setAnalyzer(analyzer);

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

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
						}
					});
				}
			}
		};
	}

	private void initUIupdaterThread() {
		uiUpdaterThread = new Thread(getUIupdater());
		uiUpdaterThread.setName("UI-Updater-Thread");
		uiUpdaterThread.start();
	}

	@Override
	public void onButtonPlay() {
		getPlaylistInterface().getPlaylistTable().changeSelection(
				audioPlaylist.getIndex(), 0, false, false);
		audioProcessingLayer.togglePlayPause();
	}

	@Override
	public void onButtonStop() {
		audioProcessingLayer.stop();
	}

	@Override
	public void onButtonFrw() {
		audioPlaylist.incrementIndex();
	}

	@Override
	public void onButtonRev() {
		audioPlaylist.decrementIndex();
	}

	@Override
	public void onPlaylistDoubleClick(int index) {
		audioPlaylist.setIndex(index);
		initAudioFileAutoPlay();
		System.out.println("no. " + audioPlaylist.getIndex());
	}

	@Override
	public void onSearchBarButtonMove(SearchCircle s) {
		if (audioProcessingLayer != null)
			audioProcessingLayer.setPostion((long) s.getButtonValue());
	}

	@Override
	public void onVolumeButtonMove(final SearchCircle v) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (audioProcessingLayer != null)
					audioProcessingLayer.setVolume((float) v.getButtonValue());
				v.setBarValue(v.getButtonValue());
			}
		});
	}

	@Override
	public void onSearchBarMousePressed(SearchCircle s) {
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
	public void onSearchBarMouseReleased(SearchCircle s) {
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
			// initAudioProcessingLayer();
			File[] file = fc.getSelectedFiles();
			openFiles(file);
		}
	}

	@Override
	public void onMenu_file_opendir() {
		JFileChooser fc = initDirOpenDialog();

		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
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
		int[] rows = getPlaylistInterface().getPlaylistTable()
				.getSelectedRows();
		for (int i : rows) {
			AudioFile af = audioPlaylist.get(i);
			if (audioPlaylist.get().equals(af)) {
				audioProcessingLayer.stop();
				audioPlaylist.remove(i);
				initAudioFileAutoPlay();
				getPlaylistInterface().getPlaylistTable().changeSelection(i, 0,
						false, false);
			} else {
				audioPlaylist.remove(i);
				getPlaylistInterface().getPlaylistTable().changeSelection(i, 0,
						false, false);
			}
			if (audioPlaylist.isEmpty()) audioProcessingLayer = AudioProcessingLayer.getEmptyInstance();
			System.out.println("Removed from playlist: "
					+ af.getFile().getAbsolutePath());
		}
	}

	@Override
	public void onMenu_playlist_clear() {
		audioProcessingLayer.stop();
		audioProcessingLayer = AudioProcessingLayer.getEmptyInstance();
		audioPlaylist.clear();
		System.out.println("Playlist cleared ...");
	}

	@Override
	public void onMenu_playlist_up() {
		int[] rows = getPlaylistInterface().getPlaylistTable()
				.getSelectedRows();

		for (int i = 0; i < rows.length; i++) {
			AudioFile af = audioPlaylist.get(rows[i]);
			if (audioPlaylist.isFistElement(af))
				return;
			audioPlaylist.moveUp(af);

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
			AudioFile af = audioPlaylist.get(rows[i]);
			if (audioPlaylist.isLastElement(af))
				return;
			audioPlaylist.moveDown(af);

			System.out.println("Moved down in playlist: "
					+ af.getFile().getAbsolutePath());
		}
		getPlaylistInterface().getPlaylistTable().changeSelection(rows[0] + 1,
				0, false, false);
		getPlaylistInterface().getPlaylistTable().setRowSelectionInterval(
				rows[0] + 1, rows[rows.length - 1] + 1);
	}

	@Override
	public void onMenu_graph_merge() {
		analyzer.setMergedChannels(!analyzer.isMergedChannels());
	}

	@Override
	public void onMenu_graph_gfilter() {
		getPlayerControlInterface().getPlayerInterfaceGraph()
				.setGaussianFilter(
						!getPlayerControlInterface().getPlayerInterfaceGraph()
								.isGaussianFilter());
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
		audioPlaylist.incrementIndex();
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
		getPlaylistInterface().getPlaylistTableModel()
				.setContent(audioPlaylist);
	}

	@Override
	public void onPlaylistFileRemove(PlaylistEvent event) {
		getPlaylistInterface().getPlaylistTableModel()
				.setContent(audioPlaylist);
	}

	@Override
	public void onPlaylistMoveUp(PlaylistIndexChangeEvent event) {
		getPlaylistInterface().getPlaylistTableModel()
				.setContent(audioPlaylist);

	}

	@Override
	public void onPlaylistMoveDown(PlaylistIndexChangeEvent event) {
		getPlaylistInterface().getPlaylistTableModel()
				.setContent(audioPlaylist);
	}

	@Override
	public void onPlaylistIncrement(PlaylistIndexChangeEvent event) {
		initAudioFileAutoPlay();
		getPlaylistInterface().getPlaylistTable().changeSelection(
				event.getNewIndex(), 0, false, false);

		System.out.println("Changed playlist index from no. "
				+ event.getPreviousIndex() + " to no. " + event.getNewIndex());
	}

	@Override
	public void onPlaylistDecrement(PlaylistIndexChangeEvent event) {
		onPlaylistIncrement(event);
	}

	@Override
	public void onPlaylistClear(PlaylistEvent event) {
		getPlaylistInterface().getPlaylistTableModel()
				.setContent(audioPlaylist);
	}

	@Override
	public void onPlaylistIndexSet(PlaylistIndexChangeEvent event) {

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

}
