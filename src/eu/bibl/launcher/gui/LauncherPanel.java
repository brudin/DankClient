package eu.bibl.launcher.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.bottombar.BottomBarPanel;
import eu.bibl.launcher.gui.tab.LauncherTabbedPanel;

public class LauncherPanel extends JPanel {
	
	private static final long serialVersionUID = 6709414083372389826L;
	
	private Launcher launcher;
	
	private LauncherTabbedPanel tabPanel;
	private BottomBarPanel bottomPanel;
	
	private JProgressBar progressBar;
	
	public LauncherPanel(Launcher launcher) {
		this.launcher = launcher;
		
		progressBar = new JProgressBar();
		tabPanel = new LauncherTabbedPanel(launcher);
		bottomPanel = new BottomBarPanel(launcher);
		
		setup();
	}
	
	private void setup() {
		add(createInterface());
		
		bottomPanel.register(tabPanel.getLaunchTab());
	}
	
	private JPanel createInterface() {
		JPanel result = new JPanel(new BorderLayout());
		// tabPanel.getBlog().setPage("http://mcupdate.tumblr.com");
		JPanel topWrapper = new JPanel(new BorderLayout());
		topWrapper.setSize(794, 390);
		topWrapper.setPreferredSize(topWrapper.getSize());
		topWrapper.add(tabPanel, BorderLayout.CENTER);
		topWrapper.add(progressBar, BorderLayout.SOUTH);
		
		progressBar.setVisible(false);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		
		bottomPanel.setSize(794, 77);
		bottomPanel.setPreferredSize(bottomPanel.getSize());
		
		result.add(topWrapper, BorderLayout.CENTER);
		result.add(bottomPanel, BorderLayout.SOUTH);
		
		return result;
	}
	
	public LauncherTabbedPanel getLauncherTabbedPanel() {
		return tabPanel;
	}
	
	public BottomBarPanel getBottomBarPanel() {
		return bottomPanel;
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
}