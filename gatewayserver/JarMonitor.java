package il.co.ilrd.gatewayserver;

//import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;

import il.co.ilrd.observer.Callback;
import il.co.ilrd.observer.Dispatcher;

// ***** TODO : pass the class inside gatewayServer and change modifier of class to private !!!!  ******

public class JarMonitor implements DirMonitor {
	private final String JAR_EXTENSION = ".jar";
	private final String MONITOR_CHANGE_MESSAGE = "JarMonitor noticed change in: \n";
	private boolean watcherRunning = true;
	private Dispatcher<String> dispatcher;
	protected WatchService watcher;
	private File folderToWatch;
	private String dirPath;

	public JarMonitor(String dirPath) throws IOException {
		this.dirPath = dirPath;
		folderToWatch = new File(dirPath);
		dispatcher = new Dispatcher<>();
		watcher = FileSystems.getDefault().newWatchService();;
		new WatcherThread(folderToWatch).start();
	}
	
	@Override
	public void register(Callback<String> callback) {
		dispatcher.register(Objects.requireNonNull(callback));		
	}
	
	@Override
	public void unregister(Callback<String> callback) {
		dispatcher.unregister(Objects.requireNonNull(callback));		
	}

	@Override
	public void stopUpdate() throws IOException {
		watcherRunning = false;
		watcher.close();
	}
	
	private void updateAll(WatchEvent<?> event) {
		System.out.println(MONITOR_CHANGE_MESSAGE + dirPath + event.context().toString());
		dispatcher.updateAll(dirPath + event.context().toString());
	}
	
	private	class WatcherThread extends Thread {
		public WatcherThread(File fileName) throws IOException {
			Objects.requireNonNull(fileName).toPath().register(watcher, ENTRY_MODIFY);
		}
		
		@Override
		public void run() {
			while (watcherRunning) {
				try {
					checkEvents(watcher.take());					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ClosedWatchServiceException e){ }
			}
		}
	}
	
	private void checkEvents(WatchKey eventsKey) {
		for (WatchEvent<?> event : Objects.requireNonNull(eventsKey).pollEvents()) {
			final Path changedFile = (Path) event.context();
			if (changedFile.toString().endsWith(JAR_EXTENSION)) {
				updateAll(event);
			}
		}
		
		watcherRunning = eventsKey.reset();
	}	
}