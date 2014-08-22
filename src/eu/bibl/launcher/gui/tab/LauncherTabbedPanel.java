package eu.bibl.launcher.gui.tab;

import javax.swing.JTabbedPane;

import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.tab.console.ConsoleTab;

public class LauncherTabbedPanel extends JTabbedPane {
	
	private static final long serialVersionUID = -6039642330668990250L;
	
	private Launcher launcher;
	private ConsoleTab console;
	private LaunchTab launch;
	
	public LauncherTabbedPanel(Launcher launcher) {
		super(SCROLL_TAB_LAYOUT);
		this.launcher = launcher;
		
		launch = new LaunchTab(launcher);
		addTab("Launch Options", launch);
		
		console = new ConsoleTab(launcher);
		addTab("Development Console", console);
		
		setSelectedComponent(launch);
	}
	
	public LaunchTab getLaunchTab() {
		return launch;
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
}