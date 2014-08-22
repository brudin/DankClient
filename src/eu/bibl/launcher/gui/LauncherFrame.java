package eu.bibl.launcher.gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.client.ClientShutdownEvent;
import eu.bibl.launcher.Launcher;

public class LauncherFrame extends JFrame {
	
	private static final long serialVersionUID = 3289532260379896950L;
	public static final Dimension WINDOW_DIMENSION = new Dimension(800, 500);
	
	private Launcher launcher;
	private LauncherPanel launcherPanel;
	
	public LauncherFrame(Launcher launcher) {
		super("Launcher");
		this.launcher = launcher;
		initFrame();
		setupFrame();
		
		setVisible(true);
	}
	
	private void initFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	private void setupFrame() {
		getContentPane().removeAll();
		setPreferredSize(WINDOW_DIMENSION);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				EventManager.dispatch(new ClientShutdownEvent());
				dispose();
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		launcherPanel = new LauncherPanel(launcher);
		getContentPane().add(launcherPanel);
		pack();
		setLocationRelativeTo(null);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	public LauncherPanel getLauncherPanel() {
		return launcherPanel;
	}
}