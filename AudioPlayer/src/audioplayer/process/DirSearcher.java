package audioplayer.process;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public abstract class DirSearcher implements Runnable {

	private Thread searcherTh = new Thread();
	private ArrayList<String> dirsToSearch = new ArrayList<String>();
	private FilenameFilter filenameFilter;
	private boolean running = false;

	private int dirsDone;
	private int dirsCount;
	private int filesCount;

	public void addDir(File dir) {
		if (dir.isDirectory())
			dirsToSearch.add(dir.getAbsolutePath());
	}

	private void search(File dir) {
		File[] files = filenameFilter != null ? dir.listFiles(filenameFilter)
				: dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!running)
					break;
				if (file.isDirectory()) {
					dirsCount++;
					dirsToSearch.add(file.getAbsolutePath());
				} else {
					filesCount++;
					processFile(file);
				}
			}
		}
		dirsToSearch.remove(dir);
		dirsDone++;
	}

	public void startSearcher() {
		running = true;
		searcherTh = new Thread(this);
		searcherTh.setName("dirSearcher");
		searcherTh.start();
	}

	public void stopSearcher() {
		running = false;
	}

	public FilenameFilter getFilenameFilter() {
		return filenameFilter;
	}

	public void setFilenameFilter(FilenameFilter filenameFilter) {
		this.filenameFilter = filenameFilter;
	}

	public int getDirsToSearch() {
		return dirsToSearch.size();
	}

	public int getDirsDoneSearched() {
		return dirsDone;
	}

	public boolean isRunning() {
		return running;
	}

	public int getDirsCount() {
		return dirsCount;
	}

	public int getFilesCount() {
		return filesCount;
	}

	@Override
	public void run() {
		running = true;
		for (int i = 0; i < dirsToSearch.size(); i++) {
			if (!running)
				break;

			File dir = new File(dirsToSearch.get(i));
			if (dir.exists()) {
				search(dir);
			} else
				dirsToSearch.remove(dir);
		}
		running = false;
	}

	public abstract void processFile(File f);

}
