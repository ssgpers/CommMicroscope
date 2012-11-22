package embl.almf;

import java.io.File;
import java.io.IOException;

import ij.IJ;
import ij.macro.MacroRunner;
import ij.plugin.PlugIn;

public class RunMacroOnMonitoredFiles extends AbsMonitorFolderFiles implements PlugIn {

	static String macropath = "/Users/miura/Desktop/tmp/testlog.ijm";
	static String macrotext = "";
	static int maxrun = 3;
	int runcount =0;
	AbsMonitorFolderFiles mff;
	
	@Override
	public void run(String path) {
		if (path != null){
			setFOLDER(path);
		}
		mff = new RunMacroOnMonitoredFiles(macropath);
		try {
			mff.startMonitoring();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RunMacroOnMonitoredFiles(String macropath){
		super();
		this.macropath = macropath;
		macrotext = IJ.openAsString(this.macropath);
	}
	
	@Override
	void runOnNewFile(File file) {
		//File macrofile = new File(macropath);
		//MacroRunner mr = new MacroRunner(macrofile);
		String addedfilepath = "";
		try {
			addedfilepath = file.getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MacroRunner mr = new MacroRunner(macrotext, addedfilepath);
		//mr.run();
        this.runcount += 1;
        if (this.runcount >= maxrun ){
        	IJ.log("monitoring stopped: " + Integer.toString(runcount) + " iterations.");
        	try {
        		this.stopMonitor();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}     		
        }
		
	}

	@Override
	void runOnFileRemove(File file) {
        try {
            // "file" is the reference to the removed file
            IJ.log("File removed: "
                    + file.getCanonicalPath());
            // "file" does not exists anymore in the location
            IJ.log("File still exists in location: "
                    + file.exists());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
		
	}
	
	public void setRunMax(int maxrun){
		this.maxrun = maxrun;
	}
	
  /** for debugging
  * 
  * @param args
  */
 public static void main(String[] args) {
 	AbsMonitorFolderFiles mff = new RunMacroOnMonitoredFiles(macropath);
 	try {
			mff.startMonitoring();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	
 }	

}
