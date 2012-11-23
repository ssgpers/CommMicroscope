package embl.almf;
//Kota Miura (cmci.embl.de)
//20121122
import ij.plugin.PlugIn;

public class RunMacro_On_NewFile implements PlugIn {

	@Override
	public void run(String arg0) {
		// TODO Auto-generated method stub
		RunMacroOnMonitoredFiles rmf = new RunMacroOnMonitoredFiles();
		rmf.execute();
	}

}
