package embl.almf;

// Kota Miura
// based on
// http://andreinc.net/2012/06/30/writing-a-simple-file-monitor-in-java-using-commons-io/

import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public abstract class AbsMonitorFolderFiles {
    // A hardcoded path to a folder you are monitoring .
    private static String FOLDER =
            "/Users/miura/Desktop/tmp/watch";
    public static String getFOLDER() {
		return FOLDER;
	}

	public static void setFOLDER(String fOLDER) {
		FOLDER = fOLDER;
	}

	final long pollingInterval = 1000;
	FileAlterationMonitor monitor = null; 

    public void startMonitoring() throws Exception {
        // The monitor will perform polling on the folder every 1 seconds
        File folder = new File(FOLDER);

        if (!folder.exists()) {
            // Test to see if monitored folder exists
            throw new RuntimeException("Directory not found: " + FOLDER);
        }

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        monitor =
                new FileAlterationMonitor(pollingInterval);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            // Is triggered when a file is created in the monitored folder
            @Override
            public void onFileCreate(File file) {
            	runOnNewFile(file);
            }

            // Is triggered when a file is deleted from the monitored folder
            @Override
            public void onFileDelete(File file) {
            	runOnFileRemove(file);
            }
        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
    }
    
    public void stopMonitor(){
    	try {
			monitor.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // to be implemented
    abstract void runOnNewFile(File file);

    // to be implemented
    abstract void runOnFileRemove(File file);   


}