package eu.bibl.core.launcher;

import java.util.Arrays;
import java.util.HashMap;

import eu.bibl.core.loader.profile.MCProfile;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.util.FileConstants;

/**
 * Sorts the arguments from the versions .json file and resolves them to the real information.
 * @author Bibl
 */
public class MinecraftArgumentFixer {
	
	/** Sorted game arguments. **/
	private HashMap<String, String> args;
	/** Logged in launcher profile. **/
	private MCProfile profile;
	/** Game version to launch. **/
	private CompleteMinecraftVersion version;
	
	/**
	 * Creates a new argument fixer.
	 * @param profile Logged in launcher profile.
	 * @param version Game version to launch.
	 */
	public MinecraftArgumentFixer(MCProfile profile, CompleteMinecraftVersion version) {
		this.profile = profile;
		this.version = version;
		args = new HashMap<String, String>();
	}
	
	/**
	 * Sets up the arguments from the version.json file.
	 */
	public void setArgs() {
		// Split individual argument name and value template. Eg {"--username", "{auth_player_name}"}
		String[] parts = version.getMinecraftArguments().split(" ");
		// Unequal name template parts.
		if (parts.length % 2 == 1) {
			System.out.println("wtf nigga, " + Arrays.toString(parts));
		}
		// Joins the name and the templates together.
		String[] args = new String[parts.length / 2];
		int j = 0;
		// Loops through all of the raw argument data.
		for(int i = 0; i < parts.length; i++) {
			// Takes the argument name and template value and joins them. Eg "--username {auth_player_name}".
			args[j++] = parts[i++] + " " + parts[i];
		}
		// Sets up the arguments to resolve.
		setArgs(args);
	}
	
	/**
	 * Turns the raw template arguments into the character named argument name and template.
	 * @param args Name template sorted args.
	 */
	public void setArgs(String[] args) {
		// Loop through every entry.
		for(String arg : args) {
			// Find the name, template.
			String[] parts = arg.split(" ");
			// Take off the "--" from the name and add name, template to the instance map.
			this.args.put(parts[0].substring(2, parts[0].length()), parts[1]);
		}
	}
	
	/**
	 * Transforms each argument template value to the real value needed.
	 */
	public void resolveArgs() {
		replace("username", "${auth_player_name}", profile.getUserAuth().getAvailableProfiles()[0].getName());
		replace("version", "${version_name}", version.getId());
		replace("gameDir", "${game_directory}", FileConstants.MC_DIR.getAbsolutePath());
		replace("assetsDir", "${assets_root}", FileConstants.ASSETS_DIR.getAbsolutePath());
		replace("uuid", "${auth_uuid}", profile.getUserAuth().getAvailableProfiles()[0].getId().toString());
		replace("accessToken", "${auth_access_token}", profile.getUserAuth().getAuthenticatedToken());
		replace("userProperties", "${user_properties}", profile.getUserAuth().getUserProperties().toString());
		replace("userType", "${user_type}", profile.getUserAuth().getUserType().getName());
		replace("assetIndex", "${assets_index_name}", version.getAssets());
	}
	
	/**
	 * Sets the regex template value in the .json file to the real one and replaces it in the map.
	 * @param key Argument name.
	 * @param template Template value for the argument.
	 * @param realVal Real value for the argument.
	 */
	private void replace(String key, String template, String realVal) {
		if (args.containsKey(key)) {
			String templateVal = args.get(key);
			templateVal = templateVal.replace(template, realVal);
			args.put(key, templateVal);
		}
	}
	
	/**
	 * Adds "--" to each argument name and sorts each name and value into an array that can be passed onto the main method of the game.
	 * @return Sorted, fixed args.
	 */
	public String[] getResolvedArgs() {
		String[] args = new String[this.args.size() * 2];
		int i = 0;
		for(String key : this.args.keySet()) {
			String val = this.args.get(key);
			args[i++] = "--" + key;
			args[i++] = val;
		}
		return args;
	}
}