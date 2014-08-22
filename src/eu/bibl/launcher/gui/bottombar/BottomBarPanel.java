package eu.bibl.launcher.gui.bottombar;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuListener;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.client.ClientShutdownEvent;
import eu.bibl.api.event.info.EventTarget;
import eu.bibl.core.io.config.GlobalConfig;
import eu.bibl.core.loader.profile.MCProfile;
import eu.bibl.core.loader.profile.Profile;
import eu.bibl.core.loader.profile.ProfileFinder;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.loader.version.Version;
import eu.bibl.core.loader.version.list.LocalVersionList;
import eu.bibl.core.util.FileConstants;
import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.login.AddProfileDialog;

public class BottomBarPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 5934775883249388355L;
	private static final String VERSION_KEY = "launcher.version";
	private static final String PROFILE_KEY = "launcher.profile";
	
	private Launcher launcher;
	
	private PlayButtonPanel playButtonPanel;
	
	private JComboBox<String> versionsBox;
	private HashMap<String, CompleteMinecraftVersion> versions;
	private JComboBox<String> profilesBox;
	private HashMap<String, MCProfile> profiles;
	
	public BottomBarPanel(Launcher launcher) {
		super(null);
		this.launcher = launcher;
		playButtonPanel = new PlayButtonPanel(launcher, this);
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setup();
		
		EventManager.register(this, ClientShutdownEvent.class);
	}
	
	private void setup() {
		setupVersions();
		
		playButtonPanel.setLocation(265, 12);
		add(playButtonPanel);
		
		setupProfiles();
	}
	
	private void setupVersions() {
		LocalVersionList lister = new LocalVersionList(FileConstants.MC_DIR);
		try {
			lister.refreshVersions();
		} catch (IOException e) {
			e.printStackTrace();
		}
		versions = new HashMap<String, CompleteMinecraftVersion>();
		List<Version> vs = lister.getVersions();
		for(Version version : vs) {
			versions.put(version.getId(), (CompleteMinecraftVersion) version);
		}
		String[] cBoxs = versions.keySet().toArray(new String[versions.keySet().size()]);
		versionsBox = new JComboBox<String>(cBoxs);
		versionsBox.setFont(versionsBox.getFont().deriveFont(Font.PLAIN, versionsBox.getFont().getSize() + 2));
		versionsBox.setFocusable(false);
		versionsBox.setSize(135, 40);
		versionsBox.setPreferredSize(versionsBox.getSize());
		((JLabel) versionsBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		versionsBox.setLocation(23, 17);
		
		String lastVersion = (String) GlobalConfig.GLOBAL_CONFIG.get(VERSION_KEY, null);
		if (lastVersion != null) {
			for(int i = 0; i < cBoxs.length; i++) {
				String profile = cBoxs[i];
				if (lastVersion.equals(profile)) {
					versionsBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		add(versionsBox);
	}
	
	private void setupProfiles() {
		ProfileFinder finder = new ProfileFinder(FileConstants.DATA_DIR);
		try {
			finder.find();
		} catch (IOException e) {
			e.printStackTrace();
		}
		profiles = new HashMap<String, MCProfile>();
		ArrayList<Profile> profileList = finder.getProfiles();
		for(int i = 0; i < profileList.size(); i++) {
			Profile key = profileList.get(i);
			profiles.put(key.getUsername(), new MCProfile(key));
		}
		profiles.put("Add profile", null);
		String[] cBoxs = profiles.keySet().toArray(new String[profiles.keySet().size()]);
		profilesBox = new JComboBox<String>(cBoxs);
		profilesBox.setFont(profilesBox.getFont().deriveFont(Font.PLAIN, profilesBox.getFont().getSize() + 2));
		profilesBox.setFocusable(false);
		profilesBox.setSize(135, 40);
		profilesBox.setPreferredSize(profilesBox.getSize());
		profilesBox.setLocation(636, 17);
		((JLabel) profilesBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		String lastProfile = (String) GlobalConfig.GLOBAL_CONFIG.get(PROFILE_KEY, null);
		if (lastProfile != null) {
			for(int i = 0; i < cBoxs.length; i++) {
				String profile = cBoxs[i];
				if (lastProfile.equals(profile)) {
					profilesBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		profilesBox.addActionListener(this);
		
		add(profilesBox);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(profilesBox)) {
			if (getSelectedProfile() == null) {
				new AddProfileDialog(launcher, this);
			}
		}
	}
	
	public void register(PopupMenuListener listener) {
		versionsBox.addPopupMenuListener(listener);
		profilesBox.addPopupMenuListener(listener);
	}
	
	public void addProfile(MCProfile profile) {
		String name = profile.getUserAuth().getSelectedProfile().getName();
		profilesBox.addItem(name);
		profiles.put(name, profile);
		profilesBox.setSelectedIndex(profiles.size() - 1);
		profile.save();
	}
	
	public CompleteMinecraftVersion getSelectedVersion() {
		return versions.get(versionsBox.getSelectedItem());
	}
	
	public MCProfile getSelectedProfile() {
		return profiles.get(profilesBox.getSelectedItem());
	}
	
	public PlayButtonPanel getPlayButtonPanel() {
		return playButtonPanel;
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	private void save() {
		GlobalConfig.GLOBAL_CONFIG.update(VERSION_KEY, (String) versionsBox.getSelectedItem());
		GlobalConfig.GLOBAL_CONFIG.update(PROFILE_KEY, (String) profilesBox.getSelectedItem());
	}
	
	@EventTarget
	private void onClientShutdown(ClientShutdownEvent event) {
		save();
	}
}