package eu.bibl.core.loader.profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Finds profiles
 */
public class ProfileFinder {
	
	private File profileDir;
	private ArrayList<Profile> profiles;
	
	public ProfileFinder(File dataDir) {
		profileDir = new File(dataDir, "profiles");
		if (!profileDir.exists())
			profileDir.mkdirs();
	}
	
	public void find() throws IOException {
		profiles = new ArrayList<Profile>();
		for(File file : profileDir.listFiles()) {
			if (file.isFile()) {
				Profile theProfile = Profile.read(file);
				profiles.add(theProfile);
			}
		}
	}
	
	public ArrayList<Profile> getProfiles() {
		return profiles;
	}
}