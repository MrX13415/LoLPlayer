package audioplayer;

import audioplayer.gui.AboutDialog;
import java.io.File;

import javax.activity.InvalidActivityException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SearchCircle;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import audioplayer.gui.UserInterface;
import audioplayer.gui.components.PlayerControler.Display;
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

/**
 * 
 * @author dausol
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
		
		analyzer = new Analyzer(getPlayerControlInterface().getPlayerInterfaceGraph());
      //analyzer.setDefaultChannelGraphColor(0, Color.red);
      //analyzer.setDefaultChannelGraphColor(1, new Color(226, 0, 116));

		analyzer.setMergedChannels(false);
                   
        getPlayerControlInterface().getPlayerInterfaceGraph().setUi(this);

		initUIupdaterThread();
	}

    public void openFiles(File[] file) {
		ppl.stop();
		apl.clear();           
		addFiles(file);
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
        boolean aplwasEmpty = apl.isEmpty();
        
        AudioFile af = new AudioFile(file);
		try {
			af.initAudioFile();
			apl.add(af);
			System.out.println("Added to playlist: " + af.getFile().getAbsolutePath());
		} catch (UnsupportedFileFormatException e) {
			raiseNotSupportedFileFormatError(af);
		}
                
        if (aplwasEmpty){
            apl.resetToFirstIndex();
            initAudioFile();
        }
    }
         
	public synchronized void initAudioFile() {
		if (!apl.isEmpty()){
	        AudioFile af = apl.get();
	        
	        initAudioProcessingLayer(af);
	        
	        System.out.println("Playing type: " + af.getType().getName() + " file: " + af.getFile().getAbsolutePath());
	             
	        this.getPlayerControlInterface().getSearchBar().setMaximum(ppl.getStreamLength());
	
	        analyzer.init(ppl.getAudioDevice());
	       
	        if (!af.isSupported()){
	        	raiseNotSupportedFileFormatError(af);
	        	apl.remove(af);
	        	if (!apl.isEmpty()) initAudioFile();
	        }
	        
	        try {
				ppl.play();
			} catch (InvalidActivityException e) {}
		}
	}

	private void raiseNotSupportedFileFormatError(AudioFile af) {
		System.err.println("Error: File format not supported!");
		System.err.printf("Type: %s File: %s\n", af.getType().getName(), af.getFile().getAbsolutePath());
		
		String msg = String.format("File format not supported!\nType: %s\nFile: %s", af.getType().getName(), af.getFile().getAbsolutePath());
		
		JOptionPane.showMessageDialog(this, msg, Applikation.App_Name_Version, JOptionPane.ERROR_MESSAGE);
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

					 if (analyzer != null) analyzer.setDebug(Applikation.isDebug());
				     getPlayerControlInterface().getSearchBar().setDebug(Applikation.isDebug());
				     getPlayerControlInterface().getVolume().setDebug(Applikation.isDebug());
					
					try {
						Thread.sleep(10);
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
							setDisplay(ppl);
							setPlayPause(ppl);

//							System.out.println(ppl.getTimePosition());
							
							// synchronize button and bar
							if (!ppl.isSkipFrames())
								getPlayerControlInterface().getSearchBar().setButtonValueButEvent(ppl.getTimePosition());
						}
					});
				}
			}
		};
	}

	public void setDisplay(AudioProcessingLayer ppl) {
		
		long time = ppl.getTimePosition();
		long lenght = ppl.getStreamLength();
		double posperc = Math.round(100d / (double) lenght * (double) time * 10d) / 10d;
		double volume = Math.round(ppl.getVolume() * 100d) / 100d;
		
		String state = String.format("%s", ppl.getState());

		String vol = String.format("%6s", String.format("%5.1f%%", volume));
		String pperc = String.format("%6s", String.format("%5.1f%%", posperc));

		Display d = this.getPlayerControlInterface().getDisplay();

		d.setTimeText(ppl.getTimePosition());
		d.setInfo1Text(state);
		d.setInfo2Text(vol);
		d.setStatusBar1Text(pperc);
		if (ppl.getAudioFile() != null)d.setStatusBar2Text(ppl.getAudioFile().getFile().getName());

	}

	public void setPlayPause(AudioProcessingLayer ppl) {
		if (ppl.isPlaying())
			this.getPlayerControlInterface().getPlay().setText("\u2759\u2759");
		else
			this.getPlayerControlInterface().getPlay().setText("\u25BA");
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
		initAudioFile();
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
			if (ppl.isStopped())
				ppl.resetPlayer();
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
	public void onMenu_file_exit(){
		Applikation.exit();
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
	public void onMenu_playlist_remove(){
		int[] rows = getPlaylistInterface().getPlaylistTable().getSelectedRows();
		for (int i : rows) {
			AudioFile af = apl.get(i);
			if (apl.get().equals(af)){
				ppl.stop();
				apl.remove(i);
				initAudioFile();
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
	public void onMenu_help_about(){
		System.out.println(Applikation.App_Name_Version);
		System.out.println(Applikation.App_About);
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
		initAudioFile();
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
