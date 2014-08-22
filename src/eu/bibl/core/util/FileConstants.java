package eu.bibl.core.util;

import java.io.File;

/**
 * A bunch of constant directories and files needed to the run the client.
 * @author Bibl
 */
public interface FileConstants {
	
	/** The base Minecraft directory. **/
	public static final File MC_DIR = new File(System.getProperty("user.home") + "/AppData/Roaming/.minecraft");
	/** The downloaded assets directory for Minecraft. **/
	public static final File ASSETS_DIR = new File(MC_DIR, "assets");
	/** The downloaded libraries directory for Minecraft. **/
	public static final File LIBRARIES_DIR = new File(MC_DIR, "libraries");
	/** The directory containing all the launch versions of the game. **/
	public static final File VERSIONS_DIR = new File(MC_DIR, "versions");
	// public static final File NATIVES_DIR = new File(MC_DIR, "natives-temp");
	
	/** DankClient main directory. **/
	public static final File CLIENT_DIR = new File(MC_DIR, "dankclient");
	/** Base DankClient data directory. **/
	public static final File DATA_DIR = new File(CLIENT_DIR, "data");
	/** DankClient launch profile data directory. **/
	public static final File PROFILE_DIR = new File(DATA_DIR, "profiles");
	/** DankClient hook data directory. **/
	public static final File HOOKS_DIR = new File(DATA_DIR, "hooks");
	/** DankClient extracted native directories. **/
	public static final File CLIENT_NATIVES_DIR = new File(DATA_DIR, "natives");
	/** DankClient user installed game plugins. **/
	public static final File PLUGINS_DIR = new File(DATA_DIR, "plugins");
	
	/** Location of the global config file. **/
	public static final File CONFIG_FILE = new File(DATA_DIR, "global.config");
}