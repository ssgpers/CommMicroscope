package embl.almf;

/***
 * @author Christian Tischer
 * using a (modified) class for Windows Registry communication, written by Oleg Ryaboy
 */


import ij.*;
import ij.plugin.*;
import ij.gui.GenericDialog;
import embl.almf.WindowsRegistry;



public class ReadWriteWindowsRegistry implements PlugIn {


	public void run(String arg) {

		String winreglocation = "HKCU\\SOFTWARE\\VB and VBA Program Settings\\OnlineImageAnalysis\\macro";
		String winregseparator = "REG_SZ";
		String winregkey = "";
		String winregvalue = "";

		String action ="";

		String[] actions = {"read", "write"};
		//static String action	
		GenericDialog gd = new GenericDialog("Windows Registry");
		gd.addChoice("Action: ", actions, actions[1]);
		gd.addStringField("Location:", winreglocation);
		gd.addStringField("Key:", winregkey);
		gd.addStringField("Value (Write only):", winregkey);
		gd.addStringField("Windows Registry output separator (Read only):", winregseparator);
		gd.showDialog();
		if(gd.wasCanceled()) return;
		action = (String)gd.getNextChoice();
		winreglocation = (String)gd.getNextString();
		winregkey = (String)gd.getNextString();
		winregvalue = (String)gd.getNextString();
		winregseparator = (String)gd.getNextString();

		IJ.log("INPUT");
		IJ.log("winreg location: "+winreglocation);
		IJ.log("winreg key: "+winregkey);
		IJ.log("winreg value: "+winregvalue);
		IJ.log("winreg separator: "+winregseparator);
		IJ.log("OUTPUT");



		// action choice
		if (action.equals("write")) {
			WindowsRegistry.writeRegistry(winreglocation, winregkey, winregvalue);
			IJ.log("wrote to registry: "+winregvalue);
		}
		else if (action.equals("read")) {
			winregvalue = WindowsRegistry.getTrimmedRegistryValue(winreglocation, winregkey, winregseparator);
			IJ.log("read from registry: "+winregvalue);
		}

	}
}
