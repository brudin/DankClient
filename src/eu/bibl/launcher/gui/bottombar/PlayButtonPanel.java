package eu.bibl.launcher.gui.bottombar;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.game.GameLoadEvent;
import eu.bibl.api.event.events.system.game.GameShutdownEvent;
import eu.bibl.api.event.info.EventTarget;
import eu.bibl.core.loader.profile.MCProfile;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.LauncherFrame;

public class PlayButtonPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 4148884551574200083L;
	
	private Launcher launcher;
	private BottomBarPanel bottomBarPanel;
	private JButton playButton;
	
	public PlayButtonPanel(Launcher launcher, BottomBarPanel bottomBarPanel) {
		super(new GridBagLayout());
		this.launcher = launcher;
		this.bottomBarPanel = bottomBarPanel;
		setSize(265, 50);
		setPreferredSize(getSize());
		setup();
	}
	
	protected void setup() {
		playButton = new JButton("Play");
		playButton.addActionListener(this);
		playButton.setFocusable(false);
		playButton.setSize(265, 50);
		playButton.setPreferredSize(playButton.getSize());
		playButton.setFont(playButton.getFont().deriveFont(Font.BOLD, playButton.getFont().getSize() + 2));
		add(playButton);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final CompleteMinecraftVersion version = bottomBarPanel.getSelectedVersion();
		final MCProfile profile = bottomBarPanel.getSelectedProfile();
		if (version != null && profile != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					updateButton(false);
					LauncherFrame frame = (LauncherFrame) launcher.getWindow();
					frame.getLauncherPanel().getLauncherTabbedPanel().getLaunchTab().stopCat();
					EventManager.register(PlayButtonPanel.this, GameLoadEvent.class);
					launcher.launch(version, profile);
				}
			}).start();
		} else {
			JOptionPane.showMessageDialog(null, "Invalid launch options", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@EventTarget
	private void onGameLoad(GameLoadEvent event) {
		if (!event.getState()) {
			updateButton(true);
			LauncherFrame frame = (LauncherFrame) launcher.getWindow();
			frame.getLauncherPanel().getLauncherTabbedPanel().getLaunchTab().resumeCat();
		} else {
			EventManager.register(this, GameShutdownEvent.class);
		}
		EventManager.unregister(this, GameLoadEvent.class);
	}
	
	@EventTarget
	private void onGameShutdown(GameShutdownEvent event) {
		updateButton(true);
		LauncherFrame frame = (LauncherFrame) launcher.getWindow();
		frame.getLauncherPanel().getLauncherTabbedPanel().getLaunchTab().resumeCat();
	}
	
	public void updateButton(boolean state) {
		playButton.setEnabled(state);
	}
}