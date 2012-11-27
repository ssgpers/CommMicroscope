package embl.almf;

/** GUI for Monitoring a folder, the main aim of this GUI is to stop watching a folder. 
 * @author Kota Miura
 * http://cmci.embl.de
 * 20121123
 *  
 */

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

// http://nadeausoftware.com/articles/2008/04/java_tip_how_list_and_find_threads_and_thread_groups#Gettingalistofallthreadgroups


//public class MonitorFileGUI extends JFrame implements ChangeListener, ActionListener {
public class MonitorFileGUI extends JFrame implements ActionListener, Observer {
	String currentMonitorPath = "/Users/you";
	String currentProgramPath = "/Users/you/macro.ijm";
	FileAlterationMonitor monitor = null;
	JLabel label1, label2, label3, label4, label5, label6;
	JLabel labelCount1, labelCount2;
	JButton b2, b3;
	private RunMacroOnMonitoredFiles mff;
	private RunJythonOnMonitoredFiles jff;
	private MonitorFileGUI mgui;
	private String scripttype = "macro";

	private static final long serialVersionUID = 1L;
	
	public void setMonitor(FileAlterationMonitor monitor) {
		this.monitor = monitor;
	}

	public MonitorFileGUI(FileAlterationMonitor monitor) {
		if (monitor == null)
				return;
		this.monitor = monitor;
		try {
			createGUI(this.monitor, currentProgramPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public MonitorFileGUI() {
		super();
		IJ.log("Constructor...");
	}
	
	public void executeGUImacro() throws IOException {
		scripttype = "macro";
		mff = new RunMacroOnMonitoredFiles();
		IJ.log("Monitor starting up...");
		mff.execute();
		if (mff.monitor == null){
			IJ.log("no monitor found");
			return;
		}
		mff.addObserver(this);
		this.monitor = mff.monitor;
		IJ.log("Filling up GUI...");
		
		createGUI(this.monitor, mff.macropath);
		
		if (!this.isVisible())
			this.setVisible(true);
	}

	public void executeGUIjython() throws IOException {
		scripttype = "jytnon";
		jff = new RunJythonOnMonitoredFiles();
		IJ.log("Monitor starting up...");
		jff.execute();
		if (jff.monitor == null){
			IJ.log("no monitor found");
			return;
		}
		jff.addObserver(this);
		this.monitor = jff.monitor;
		IJ.log("Filling up GUI...");
		
		createGUI(this.monitor, jff.jythonpath);
		
		if (!this.isVisible())
			this.setVisible(true);
	}	
	
	public void createGUI(FileAlterationMonitor monitor, String macrofile) throws IOException{
		String targetfolder = "";
		Iterable<FileAlterationObserver> obslist = monitor.getObservers();
		for (FileAlterationObserver obs : obslist){
			targetfolder = obs.getDirectory().getCanonicalPath();
			IJ.log(targetfolder);
			guicontructor(targetfolder, macrofile);
		}
		
	}
	
	public void update(Observable o, Object rmf) {
		String newfile;
		int runcount = 0;
		int maxcount = 0;
		if (scripttype == "macro"){
			runcount = mff.runcount + 1;
			maxcount = mff.maxrun;
			newfile = ((RunMacroOnMonitoredFiles) rmf).getLatestFileName();
		} else {
			runcount = jff.runcount + 1;
			maxcount = jff.maxrun;
			newfile = ((RunJythonOnMonitoredFiles) rmf).getLatestFileName();
		}	
		labelCount2.setText(Integer.toString(runcount) + " / " + Integer.toString(maxcount));
		IJ.log("new:" + newfile);
		label6.setText(newfile);
		if (runcount >= maxcount){
			b3.setEnabled(false);
			labelCount1.setText("Counts(stopped):");
		}
	}
	
	
	public void guicontructor(String targetfolder, String macrofile){
		//getContentPane().setLayout(new FlowLayout());

		Font font1 = new Font("Default", Font.PLAIN, 12);
		Font font1small = new Font("DefaultSmall", Font.PLAIN, 12);		
		Font font2 = new Font("Serif", Font.BOLD, 15);
		Font font3 = new Font("Times New Roman", Font.ITALIC, 15);
		Font font4 = new Font("Arial", Font.ITALIC|Font.BOLD, 12);
		Font fontBIG = new Font("Arial", Font.BOLD, 18);
		
		label1 = new JLabel("Monitoring:  ");
		label2 = new JLabel(targetfolder);
		label2.setFont(font1small);
		
		label3 = new JLabel("Script File:  ");
		label4 = new JLabel(macrofile);
		label4.setFont(font1small);

		b2 = new JButton("Close");
		b2.addActionListener(this);
		
		b3 = new JButton("Stop");
		b3.addActionListener(this);
		
		labelCount1 = new JLabel("Counts:");
		if (scripttype == "macro")
			labelCount2 = new JLabel(Integer.toString(mff.runcount) + " / " + Integer.toString(mff.maxrun));		
		else
			labelCount2 = new JLabel(Integer.toString(jff.runcount) + " / " + Integer.toString(jff.maxrun));
		labelCount2.setFont(fontBIG);
		label5 = new JLabel("The Latest File:");
		label6 = new JLabel("");
		label6.setFont(font1small);
		
		String authortxt = "<html>Ver. 1.0.0 - Kota@CMCI, EMBL "+
				"<a href='http://cmci.embl.de'>http://cmci.embl.de</a></html>";
		JLabel labelCMCI = new JLabel(authortxt);
		labelCMCI.setFont(font1small);

		JPanel upperp = new JPanel();
		upperp.setLayout(new GridLayout(4, 1));
		upperp.setBorder(BorderFactory.createTitledBorder("Monitor Settings"));
		upperp.add(label1);
		upperp.add(label2);		
		upperp.add(label3);
		upperp.add(label4);

		JPanel middlep = new JPanel();
		middlep.setLayout(new GridLayout(2, 1));		
		middlep.setBorder(BorderFactory.createTitledBorder("Status Controls"));
		JPanel buttonsp = new JPanel();
		buttonsp.add(b3);
		buttonsp.add(b2);		
		middlep.add(buttonsp);
		JPanel countsp = new JPanel();
		countsp.setLayout(new FlowLayout());
		countsp.add(labelCount1);
		countsp.add(labelCount2);
		middlep.add(countsp);
		
		JPanel  lowerp = new JPanel();
		lowerp.setLayout(new GridLayout(3, 1));
		lowerp.setBorder(BorderFactory.createTitledBorder("Info"));
		lowerp.add(label5);
		lowerp.add(label6);
		lowerp.add(labelCMCI);

		JPanel fullp = new JPanel();
		fullp.setLayout(new GridLayout(3, 1));
		fullp.add(upperp);
		fullp.add(middlep);
		fullp.add(lowerp);
		
//		getContentPane().add(label1);
//		getContentPane().add(label2);
//		getContentPane().add(label3);
//		getContentPane().add(label4);
//		getContentPane().add(b3);
//		getContentPane().add(b2);

//		JToggleButton b1;
//		if (monitor != null)
//			b1 = new JToggleButton("Running");
//		else
//			b1 = new JToggleButton("OFF");
//		b1.addChangeListener(this);
//		getContentPane().add(b1);

		getContentPane().add(fullp, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Monitoring Status");
		setSize(400, 350);
		setVisible(true);
	}
	public void destroyObservers(){
		
	}
//	public void stateChanged(ChangeEvent e) {
//		JToggleButton btn = (JToggleButton)e.getSource();
//		Iterable<FileAlterationObserver> obslist = monitor.getObservers();
////		for (FileAlterationObserver obs : obslist)
////			IJ.log(obs.toString());	
//		if (btn.isSelected()) {
//			if ()
//				btn.setText("Running");
//	
//			else
//				btn.setText("offline");
//		} else {
//			btn.setText("stopped");
////			if (monitor != null){
////				try {
////					monitor.stop();
////				} catch (Exception e1) {
////					// TODO Auto-generated catch block
////					e1.printStackTrace();
////				}
//			}
//		}
//	}
	
	public void actionPerformed(ActionEvent e) {
		//close clicked
		if (e.getSource() == b2) {
			if (mff != null)
				mff.removeObservers();
			if (jff != null)
				jff.removeObservers();				
			System.out.println("Desroying GUI...");
			this.dispose();
		}
		//stop clicked
		if (e.getSource() == b3){
			if (mff != null)
				mff.removeObservers();
			if (jff != null)
				jff.removeObservers();
			b3.setEnabled(false);
			labelCount1.setText("Counts(stopped):");
		}
		
	}
	
//	public String getCurrentMonitorPath() {
//		return currentMonitorPath;
//	}
//	public void setCurrentMonitorPath(String currentMonitorPath) {
//		this.currentMonitorPath = currentMonitorPath;
//		this.label2.setText(currentMonitorPath);
//	}
//	public String getCurrentMacroPath() {
//		return currentMacroPath;
//	}
//	public void setCurrentMacroPath(String currentMacroPath) {
//		this.currentMacroPath = currentMacroPath;
//		this.label2.setText(currentMacroPath);
//	}

	/** debugging
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MonitorFileGUI mgui = new MonitorFileGUI();
		try {
			//mgui.executeGUImacro();
			mgui.executeGUIjython();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
