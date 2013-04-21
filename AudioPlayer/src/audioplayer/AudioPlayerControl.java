package audioplayer;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SearchCircle;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import audioplayer.gui.UserInterface;
import audioplayer.gui.components.PlayerControler.Display;
import audioplayer.player.AudioFile;
import audioplayer.player.AudioPlaylist;
import audioplayer.player.analyzer.Analyzer;
import audioplayer.player.codec.AudioProcessingLayer;
import audioplayer.player.codec.WAVEAudioProcessingLayer;
import audioplayer.player.listener.PlayerEvent;
import audioplayer.player.listener.PlayerListener;
import audioplayer.player.listener.PlaylistEvent;
import audioplayer.player.listener.PlaylistIndexChangeEvent;

/**
 * 
 * @author dausol
 */
public class AudioPlayerControl extends UserInterface implements PlayerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AudioProcessingLayer ppl = new WAVEAudioProcessingLayer();
    private AudioPlaylist apl = new AudioPlaylist();
        
	private Thread uiUpdaterThread;

	private boolean wasPausedOnSearchBarMousePressed;

	private Analyzer analyzer;
	
	/**
	 * Start a new Instance of the AudioPlayer ...
	 */
	public AudioPlayerControl() {
		apl.addPlayerListener(this);
		
		analyzer = new Analyzer(getPlayerControlInterface().getPlayerInterfaceGraph());
                //analyzer.setDefaultChannelGraphColor(0, Color.red);
                //analyzer.setDefaultChannelGraphColor(1, new Color(226, 0, 116));

                analyzer.setMergedChannels(false);
                   
                getPlayerControlInterface().getPlayerInterfaceGraph().setUi(this);

		initUIupdaterThread();

		initAudioProcessingLayer();
		// initAudioFile(new File("Scratching Harmony (Re-Orchestrated).mp3"));
	}

        
        public void openFile(File[] file) {
			ppl.stop();
			apl.clear();           
			for (File f : file) {
				addFile(f);
			}
        }
         
        public void addFile(File file) {
            boolean aplwasEmpty = apl.isEmpty();
            
            AudioFile naf = new AudioFile(file);
            apl.add(naf);
            
            if (aplwasEmpty){
                apl.resetIndex();
                initAudioFile();
            }
        }
         
	public void initAudioFile() {
        AudioFile af = apl.get();
        
        ppl.initialzePlayer(af);
        ppl.setPostion(0);

        //this.getPlayerControlInterface().getSearchBar().setMaximum(ppl.getStreamLength());

        analyzer.init(ppl.getAudioDevice());
		
        System.out.println("FILE: " + ppl.getAudioFile().getFile().getAbsolutePath());
	}

	public void initAudioProcessingLayer() {
		AudioProcessingLayer newppl = new AudioProcessingLayer();

		if (ppl != null) {
			if (!ppl.isNew())
				ppl.stop();
			newppl.setVolume(ppl.getVolume());
		}

		ppl = newppl;
		ppl.addPlayerListener(this);
	}

	private Runnable getUIupdater() {
		
		return new Runnable() {

			@Override
			public void run() {
								
				while (true) {

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}

					if (ppl == null)
						continue;
			
					//Synchronize the audio device and the analyzer ...
					ppl.getAudioDevice().setAnalyzer(analyzer);
					
					analyzer.setDetailLevel(ms.getValue());
					getPlayerControlInterface().getPlayerInterfaceGraph().setHeightLevel((ms.getValue() * 10f / (2f + 1) / 1000f + 1));//bf.getValue() / 1000f);
					
					if ((ms.getValue() % 2) == 0)((WAVEAudioProcessingLayer) ppl).bps = ms.getValue();
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
                                                                                    
							if (ppl.isNew())
								getPlayerControlInterface().getSearchBar()
										.setEnabled(false);
							else
								getPlayerControlInterface().getSearchBar()
										.setEnabled(true);

							getPlayerControlInterface().getSearchBar().setBarValue(ppl.getTimePosition());
							setDisplay(ppl);
							setPlayPause(ppl);
                                                                        
                            //getPlaylistInterface().getPlaylistTableModel().setContent(apl);

							// synchronize button and bar
							if (!ppl.isSkipFrames())
								getPlayerControlInterface().getSearchBar()
										.setButtonValueButEvent(
												ppl.getTimePosition());
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
		initAudioFile();
		ppl.togglePlayPause();
		getPlaylistInterface().getPlaylistTable().changeSelection(apl.getIndex(), 0, false, false);
		System.out.println("no. " + apl.getIndex());
	}

	@Override
	public void onButtonRev() {
		apl.decrementIndex();
		initAudioFile();
		ppl.togglePlayPause();
        getPlaylistInterface().getPlaylistTable().changeSelection(apl.getIndex(), 0, false, false);
		System.out.println("no. " + apl.getIndex());		
	}

	@Override
	public void onPlaylistDoubleClick(int index) {
		apl.setIndex(index);
		initAudioFile();
		ppl.togglePlayPause();
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
				try {
					ppl.resetPlayer();
				} catch (Exception e) {
				}
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
		JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
		fc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".mp3") || f.isDirectory();
			}
		});
		
		fc.setMultiSelectionEnabled(true);
		fc.setAcceptAllFileFilterUsed(true);
		
		int retopt = fc.showOpenDialog(this);

		if (retopt == JFileChooser.APPROVE_OPTION) {
			//initAudioProcessingLayer();
            File[] file = fc.getSelectedFiles();
            openFile(file);
		}
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
		if (apl.isLastElement()){
			ppl.stop();
			apl.resetIndex();
			initAudioFile();
		}else{
			apl.incrementIndex();
			initAudioFile();
			ppl.togglePlayPause();
		}
		
		getPlaylistInterface().getPlaylistTable().changeSelection(apl.getIndex(), 0, false, false);
		System.out.println("auto inc no. " + apl.getIndex());
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
    public void onPlaylistIncrement(PlaylistIndexChangeEvent event) {
        
    }

    @Override
    public void onPlaylistDecrement(PlaylistIndexChangeEvent event) {
        
    }

    @Override
    public void onPlaylistClear(PlaylistEvent event) {
    	getPlaylistInterface().getPlaylistTableModel().setContent(apl);
    }

    @Override
    public void onPlaylistIndexSet(PlaylistIndexChangeEvent event) {
        
    }

}
