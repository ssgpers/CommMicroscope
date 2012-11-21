package embl.almf;

import java.io.File;
import java.io.IOException;

import ij.IJ;
import ij.macro.MacroRunner;
import ij.plugin.PlugIn;

public class RunMacroOnMonitoredFiles extends AbsMonitorFolderFiles implements PlugIn {

	String macropath = "/Users/miura/Desktop/tmp/testlog.txt";
	@Override
	public void run(String path) {
		if (path != null){
			setFOLDER(path);
		}
		AbsMonitorFolderFiles mff = new RunMacroOnMonitoredFiles(macropath);
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
	}
	
	@Override
	void runOnNewFile(File file) {
		File macrofile = new File(macropath);
		MacroRunner mr = new MacroRunner(macrofile);
		mr.run();
        
		
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
