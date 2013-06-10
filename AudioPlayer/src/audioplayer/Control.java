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
import javazoom.jl.decoder.JavaLayerException;


/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class Control extends UserInterface implements PlayerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AudioProcessingLayer ppl = AudioProcessingLayer.getEmptyInstance();
    private AudioPlaylist apl = new AudioPlaylist();
        
	private Thread uiUpdaterThread;

	private boolean wasPausedOnSearchBarMousePressed;

	private Analyzer analyzer;
	
	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public Control() {
        
            apl.addPlayerListener(this);
            apl.loadFromDB();
            
            analyzer = new Analyzer(getPlayerControlInterface().getPlayerInterfaceGraph());
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
            
            if (!apl.isEmpty()){
            	initAudioFile(); //autoplay on startup if playlist had content ...
            }
	}

    public void openFiles(File[] file) {
		ppl.stop();
		apl.clear();           
		addFiles(file);
    }
    
    public void openDirs(File[] dir) {
    	ppl.stop();
		apl.clear();
    	addDirs(dir);
    }
    
    public void addDirs(final File[] dir) {
    	
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				getStatusbar().getBar().setMinimum(0);
				getStatusbar().getBar().setMaximum(dir.length);
				getStatusbar().setMessageText(String.format("Loading dir ... (dirs: %6s/%6s ; files: %6s)", 0, dir.length, 0));
				getStatusbar().setVisible(true);
			
		    	DirSearcher ds = new DirSearcher() {
					@Override
					public void processFile(File f) {
						System.out.println(f);
						addFile(f, false);
					}
				};
				
				for (File file : dir) {
					ds.addDir(file);
				}
				ds.setFilenameFilter(AudioType.getAllSupportedFilenamesFilter());
				ds.startSearcher();
				
				while (ds.isRunning()){
					getStatusbar().getBar().setMaximum(ds.getDirsCount());
					getStatusbar().getBar().setValue(ds.getDirsDoneSearched());
					getStatusbar().setMessageText(String.format("Loading dir ... (dirs: %6s/%6s ; files: %6s)",
							ds.getDirsDoneSearched(),
							ds.getDirsCount(),
							ds.getFilesCount()));
				}
				
				getStatusbar().setVisible(false);
			}
		}).start();
    }
     
    public void addFiles(final File[] file) {       
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				getStatusbar().getBar().setMinimum(0);
				getStatusbar().getBar().setMaximum(file.length);
				getStatusbar().setMessageText(String.format("Loading file ... (%s/%s)", 0, file.length));
				getStatusbar().setVisible(true);
				
				for (int i = 0; i < file.length; i++) {
					getStatusbar().setMessageText(
							String.format("Loading file ... (%s/%s)", i, file.length));
					
					getStatusbar().getBar().setValue(i);
					
					addFile(file[i]);
				}
				
				getStatusbar().setVisible(false);
			}
		}).start();
    }
    
    public void addFile(File file) {
    	addFile(file, true);
    }
    
    public void addFile(File file, boolean provideErrorMsg) {
        boolean aplwasEmpty = apl.isEmpty();
        
        AudioFile af = new AudioFile(file);
		try {
			af.initAudioFile();
			apl.add(af);
			System.out.println("Added to playlist: " + af.getFile().getAbsolutePath());
		} catch (UnsupportedFileFormatException e) {
			raiseNotSupportedFileFormatError(af, provideErrorMsg);
		}
                
        if (aplwasEmpty){
            apl.resetToFirstIndex();
            initAudioFileAutoPlay();
        }
    }
         
    
	public synchronized void initAudioFileAutoPlay() {
		if (initAudioFile()){
	        try {
				ppl.play();
			} catch (InvalidActivityException e) {}
		}
	}
	
	public synchronized boolean initAudioFile() {
		if (!apl.isEmpty()){
	        AudioFile af = apl.get();
	        
	        initAudioProcessingLayer(af);
	        
	        System.out.println("Playing type: " + af.getType().getName() + " file: " + af.getFile().getAbsolutePath());
	             
	        this.getPlayerControlInterface().getSearchBar().setMaximum(ppl.getStreamLength());
	
	        analyzer.init(ppl.getAudioDevice());
	       
	        if (!af.isSupported()){
	        	raiseNotSupportedFileFormatError(af, true);
	        	apl.remove(af);
	        	if (!apl.isEmpty()) initAudioFile();
	        }
	        
	        return true;
		}
		return false;
	}

	public void raiseNotSupportedFileFormatError(AudioFile af, boolean provideErrorMsg) {
		System.err.println("Error: File format not supported!");
		System.err.printf("Type: %s File: %s\n", af.getType().getName(), af.getFile().getAbsolutePath());
		
		String msg = String.format("File format not supported!\nType: %s\nFile: %s", af.getType().getName(), af.getFile().getAbsolutePath());
		
		if (provideErrorMsg) JOptionPane.showMessageDialog(this, msg, Application.App_Name_Version, JOptionPane.ERROR_MESSAGE);
	}
        
        public void raiseVolumeControlError(Exception ex) {
                System.err.println("Error: Volume control not supported");
                System.err.println(ex);
                
		String msg = String.format("Volume control not supported!");
		
		JOptionPane.showMessageDialog(this, msg, Application.App_Name_Version, JOptionPane.ERROR_MESSAGE);
	}

	public void initAudioProcessingLayer(AudioFile af) {
		AudioProcessingLayer newppl = af.getAudioProcessingLayer();
		AudioProcessingLayer oldppl = ppl;
		
		if (ppl != null) {
			ppl.stop();
			newppl.setVolume(ppl.getVolume());
		}

		ppl = newppl;
		ppl.addPlayerListener(this);
        ppl.initialzePlayer(af);
        ppl.setPostion(0);
        
		oldppl.cleanInstance();
		oldppl = null;
	}

	private Runnable getUIupdater() {
		
		return new Runnable() {

			@Override
			public void run() {
								
				while (true) {

					 if (analyzer != null) analyzer.setDebug(Application.isDebug());
				     getPlayerControlInterface().getSearchBar().setDebug(Application.isDebug());
				     getPlayerControlInterface().getVolume().setDebug(Application.isDebug());
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}

					if (ppl == null) continue;
			
					//Synchronize the audio device and the analyzer ...
					ppl.getAudioDevice().setAnalyzer(analyzer);
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
                                                                                    
							if (ppl.isNew())
								getPlayerControlInterface().getSearchBar().setEnabled(false);
							else
								getPlayerControlInterface().getSearchBar().setEnabled(true);

							getPlayerControlInterface().getSearchBar().setBarValue(ppl.getTimePosition());
							getPlayerControlInterface().setDisplay(ppl);
							getPlayerControlInterface().setPlayPause(ppl.isPlaying());

							// synchronize button and bar
							if (!ppl.isSkipFrames())
								getPlayerControlInterface().getSearchBar().setButtonValueButEvent(ppl.getTimePosition());
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
		getPlaylistInterface().getPlaylistTable().changeSelection(apl.getIndex(), 0, false, false);
		ppl.togglePlayPause();
	}

	@Override
	public void onButtonStop() {
		ppl.stop();
	}

	@Override
	public void onButtonFrw() {
		apl.incrementIndex();
	}

	@Override
	public void onButtonRev() {
		apl.decrementIndex();		
	}

	@Override
	public void onPlaylistDoubleClick(int index) {
		apl.setIndex(index);
		initAudioFileAutoPlay();
		System.out.println("no. " + apl.getIndex());
	}
	
	@Override
	public void onSearchBarButtonMove(SearchCircle s) {
		if (ppl != null)
			ppl.setPostion((long) s.getButtonValue());
	}

	@Override
	public void onVolumeButtonMove(final SearchCircle v) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (ppl != null)
					ppl.setVolume((float) v.getButtonValue());
				v.setBarValue(v.getButtonValue());
			}
		});
	}

	@Override
	public void onSearchBarMousePressed(SearchCircle s) {
		wasPausedOnSearchBarMousePressed = ppl.isPaused();
		if (ppl.isInitialized() || ppl.isStopped()) {
			if (ppl.isStopped()) ppl.resetPlayer();
			ppl.setPause(true);
			ppl.createDecoderThread();
			wasPausedOnSearchBarMousePressed = true;
		} else if (!ppl.isPaused())
			ppl.togglePause();
	}

	@Override
	public void onSearchBarMouseReleased(SearchCircle s) {
		if (!wasPausedOnSearchBarMousePressed) {
			ppl.setPause(false);
		} else {
			ppl.setPause(true);
		}
	}

	@Override
	public void onMenu_file_open() {
		JFileChooser fc = initOpenDialog();
		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			//initAudioProcessingLayer();
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
	public void onMenu_file_exit(){
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
	public void onMenu_playlist_remove(){
		int[] rows = getPlaylistInterface().getPlaylistTable().getSelectedRows();
		for (int i : rows) {
			AudioFile af = apl.get(i);
			if (apl.get().equals(af)){
				ppl.stop();
				apl.remove(i);
				initAudioFileAutoPlay();
				getPlaylistInterface().getPlaylistTable().changeSelection(apl.getIndex(), 0, false, false);
			}else{
				apl.remove(i);
			}
			System.out.println("Removed from playlist: " + af.getFile().getAbsolutePath());
		}
	}
	
	@Override
	public void onMenu_playlist_clear(){
		ppl.stop();
		apl.clear();
		System.out.println("Playlist cleared ...");
	}
	
	@Override
	public void onMenu_playlist_up(){
		int[] rows = getPlaylistInterface().getPlaylistTable().getSelectedRows();

		for (int i = 0; i < rows.length; i++) {
			AudioFile af = apl.get(rows[i]);
			if (apl.isFistElement(af)) return; 
			apl.moveUp(af);
					
			System.out.println("Moved up in playlist: " + af.getFile().getAbsolutePath());
		}
		getPlaylistInterface().getPlaylistTable().changeSelection(rows[0] - 1, 0, false, false);
		getPlaylistInterface().getPlaylistTable().setRowSelectionInterval(rows[0] - 1, rows[rows.length - 1] - 1);
	}
	
	@Override
	public void onMenu_playlist_down(){
		int[] rows = getPlaylistInterface().getPlaylistTable().getSelectedRows();
		for (int i = rows.length - 1; i >=0 ; i--) {
			AudioFile af = apl.get(rows[i]);
			if (apl.isLastElement(af)) return; 
			apl.moveDown(af);
						
			System.out.println("Moved down in playlist: " + af.getFile().getAbsolutePath());
		}
		getPlaylistInterface().getPlaylistTable().changeSelection(rows[0] + 1, 0, false, false);
		getPlaylistInterface().getPlaylistTable().setRowSelectionInterval(rows[0] + 1, rows[rows.length - 1] + 1);
	}
	
	@Override
	public void onMenu_graph_merge(){
		analyzer.setMergedChannels(!analyzer.isMergedChannels());
	}
	
	@Override
	public void onMenu_graph_gfilter(){
		getPlayerControlInterface().getPlayerInterfaceGraph().setGaussianFilter(!getPlayerControlInterface().getPlayerInterfaceGraph().isGaussianFilter());
	}
	
	@Override
	public void onMenu_help_about(){
		System.out.println(Application.App_Name_Version);
		System.out.println(Application.App_Author);
                System.out.println(Application.App_License);
                new AboutDialog(this);
	}
	
	private JFileChooser initOpenDialog() {
		JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
		
		//define file filters ...
		FileFilter ff = AudioType.getAllSupportedFilesFilter();
		fc.setFileFilter(ff);
		for (AudioType at : AudioType.getTypes()){
			fc.setFileFilter(at);
		}
		
		fc.setFileFilter(ff);
		fc.setMultiSelectionEnabled(true);
		fc.setAcceptAllFileFilterUsed(true);
		
		return fc;
	}
	
	private JFileChooser initDirOpenDialog() {
		JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(true);
		return fc;
	}

	@Override
	public void onPlayerStart(PlayerEvent event) {
		System.out.println("started @ frame " + event.getSource().getTimePosition());
		
	}

	@Override
	public void onPlayerStop(PlayerEvent event) {
		System.out.println("stoped @ frame " + event.getSource().getTimePosition());
		
	}
	
	@Override
	public void onPlayerNextSong(PlayerEvent event) { 
		apl.incrementIndex();
	}
	
	@Override
	public void onPlayerVolumeChange(PlayerEvent event) {
		System.out.println("volume changed to: " + event.getSource().getVolume());
		
	}
	
	@Override
	public void onPlayerPositionChange(PlayerEvent event) {
		
	}

    @Override
    public void onPlaylistFileAdd(PlaylistEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    }

    @Override
    public void onPlaylistFileRemove(PlaylistEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    }
    
    @Override
    public void onPlaylistMoveUp(PlaylistIndexChangeEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    	
    }
    
    @Override
    public void onPlaylistMoveDown(PlaylistIndexChangeEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    }

    
    @Override
    public void onPlaylistIncrement(PlaylistIndexChangeEvent event) {
		initAudioFileAutoPlay();
        getPlaylistInterface().getPlaylistTable().changeSelection(event.getNewIndex(), 0, false, false);

        System.out.println("Changed playlist index from no. " + event.getPreviousIndex() + " to no. " + event.getNewIndex());
    }

    @Override
    public void onPlaylistDecrement(PlaylistIndexChangeEvent event) {
    	onPlaylistIncrement(event);
    }

    @Override
    public void onPlaylistClear(PlaylistEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    }

    @Override
    public void onPlaylistIndexSet(PlaylistIndexChangeEvent event) {
        
    }

	@Override
	public void onGraphDetailBarChange(JSlider detailBar) {
		int value = detailBar.getValue();
		float hval = (value * 13f / (2f + 1) / 1000f) + 0.3f;
		System.out.println("DetailLevel: " + value + " HeightModifier: " + hval);
		
		analyzer.setDetailLevel(value);
		getPlayerControlInterface().getPlayerInterfaceGraph().setHeightLevel(hval);
		this.getPlayerControlInterface().getHeightlevel().setValue((int) (hval * 1000));
	}
	
	@Override
	public void onHeightLevelBarChange(JSlider heightLevelBar) {
		int value = heightLevelBar.getValue();
		float hval = (value / 1000f);
		System.out.println("HeightModifier: " + hval);
		getPlayerControlInterface().getPlayerInterfaceGraph().setHeightLevel(hval);
	}

}
