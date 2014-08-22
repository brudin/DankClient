package eu.bibl.launcher;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.game.GameLoadEvent;
import eu.bibl.core.launcher.MinecraftArgumentFixer;
import eu.bibl.core.launcher.MinecraftLauncher;
import eu.bibl.core.loader.profile.MCProfile;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.launcher.gui.LauncherFrame;

/**
 * @author Bibl <br>
 * Launches client.
 */
public class Launcher {
	
	/** Internal launcher instance **/
	private static final Launcher instance = new Launcher();
	
	/** Actual launcher frame **/
	private LauncherFrame launcherFrame;
	/** Current Minecraft launcher **/
	private MinecraftLauncher minecraftLauncher;
	
	/** Cannot be reinstantiated. */
	private Launcher() {
		if (instance != null)
			throw new UnsupportedOperationException("Cannot reinstantiate Launcher.");
	}
	
	/**
	 * Starts the launcher in the EDT.
	 * @throws Exception Generic uncaught errors.
	 */
	private void start() throws Exception {
		// Run in another thread!
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcherFrame = new LauncherFrame(Launcher.this);
			}
		});
	}
	
	public void launch(CompleteMinecraftVersion verison, MCProfile profile) {
		launch(verison, profile, false);
	}
	
	/**
	 * Launches Minecraft using the specified settings.
	 * @param version Version of the game to use
	 * @param profile User profile for first login.
	 */
	public void launch(CompleteMinecraftVersion version, MCProfile profile, boolean b) {
		minecraftLauncher = new MinecraftLauncher(this, version);
		if (!minecraftLauncher.download()) {
			JOptionPane.showMessageDialog(null, "Error loading game, check console.", "Error", JOptionPane.ERROR_MESSAGE);
			EventManager.dispatch(new GameLoadEvent(false));
			return;
		}
		
		MinecraftArgumentFixer fixer = new MinecraftArgumentFixer(profile, version);
		fixer.setArgs();
		fixer.resolveArgs();
		String[] args = fixer.getResolvedArgs();
		
		System.out.println("Launching Minecraft...");
		if (minecraftLauncher.isGamePrimed()) {
			String res = minecraftLauncher.launch(version.getMainClass(), args);
			if (!res.equals("Success")) {
				JOptionPane.showMessageDialog(null, res, "Error loading game", JOptionPane.ERROR_MESSAGE);
				EventManager.dispatch(new GameLoadEvent(false));
			}
		} else {
			EventManager.dispatch(new GameLoadEvent(false));
			JOptionPane.showMessageDialog(null, "Cannot load version " + version.getId() + ".", "Invalid version", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * @return Launcher frame
	 */
	public JFrame getWindow() {
		return launcherFrame;
	}
	
	public static void main(String[] args) throws Exception {
		// This is hackery get on the floor
		// ClassLoader cl = ClassLoader.getSystemClassLoader();
		// URLClassLoader ucl = (URLClassLoader) cl;
		// Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		// addURL.setAccessible(true);
		// addURL.invoke(ucl, new File("C:/Users/Bibl/git/dankclient/lib/natives/lwjgl.dll").toURI().toURL());
		// addURL.invoke(ucl, new File("C:/Users/Bibl/git/dankclient/lib/natives/lwjgl64.dll").toURI().toURL());
		// addURL.invoke(ucl, new File("C:/Users/Bibl/git/dankclient/lib/natives/OpenAL32.dll").toURI().toURL());
		// addURL.invoke(ucl, new File("C:/Users/Bibl/git/dankclient/lib/natives/OpenAL64.dll").toURI().toURL());
		
		// Start it all.
		instance.start();
	}
}