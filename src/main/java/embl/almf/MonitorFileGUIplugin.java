package embl.almf;

import java.io.IOException;

import ij.IJ;
import ij.plugin.PlugIn;

public class MonitorFileGUIplugin implements PlugIn {

	@Override
	public void run(String arg0) {
		if (arg0.equals("macro")){
			IJ.log("starting up...");
			MonitorFileGUI mgui = new MonitorFileGUI();
			try {
				mgui.executeGUImacro();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (arg0.equals("jython")){
			IJ.log("starting up...");
			MonitorFileGUI mgui = new MonitorFileGUI();
			try {
				mgui.executeGUIjython();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}


