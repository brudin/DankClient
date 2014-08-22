package eu.bibl.core.loader.version;

import eu.bibl.core.loader.lib.MinecraftLibrary;
import eu.bibl.core.loader.os.Action;
import eu.bibl.core.loader.os.CompatibilityRule;
import eu.bibl.core.loader.os.OperatingSystem;
import eu.bibl.core.loader.rel.ReleaseType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.util.*;

/**
 * Represents a complete Minecraft release.
 */
public class CompleteMinecraftVersion implements CompleteVersion {

    /**
     * The ID of the release
     */
	private String id;
    /**
     * The current time
     */
	private Date time;
    /**
     * The time of the release
     */
	private Date releaseTime;
    /**
     * The release type of this version
     */
	private ReleaseType type;
    /**
     * The Minecraft arguments to start the client with
     */
	private String minecraftArguments;
    /**
     * The list of libraries we have to chimble
     */
	private List<MinecraftLibrary> libraries;
    /**
     * The main class of the minecraft jar
     */
	private String mainClass;
    /**
     * The MINIMUM launcher version supported.
     */
	private int minimumLauncherVersion;
    /**
     * Why we are incompatible (it's not you, it's me)
     */
	private String incompatibilityReason;
    /**
     * The assets.
     */
    private String assets;

    /**
     * Compatibility requirements that ABSOLUTELY MUST BE MET to continue.
     */
	private List<CompatibilityRule> compatibilityRules;
    /**
     * Whether we are synchronized
     */
	private transient boolean synced = false;
	
	public CompleteMinecraftVersion() {
	}
	
	public CompleteMinecraftVersion(String id, Date releaseTime, Date updateTime, ReleaseType type, String mainClass, String minecraftArguments) {
		if ((id == null) || (id.length() == 0))
			throw new IllegalArgumentException("ID cannot be null or empty");
		if (releaseTime == null)
			throw new IllegalArgumentException("Release time cannot be null");
		if (updateTime == null)
			throw new IllegalArgumentException("Update time cannot be null");
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		if ((mainClass == null) || (mainClass.length() == 0))
			throw new IllegalArgumentException("Main class cannot be null or empty");
		if (minecraftArguments == null)
			throw new IllegalArgumentException("Process arguments cannot be null or empty");
		
		this.id = id;
		this.releaseTime = releaseTime;
		time = updateTime;
		this.type = type;
		this.mainClass = mainClass;
        libraries = new ArrayList<>();
		this.minecraftArguments = minecraftArguments;
	}
	
	public CompleteMinecraftVersion(CompleteMinecraftVersion version) {
		this(version.getId(), version.getReleaseTime(), version.getUpdatedTime(), version.getType(), version.getMainClass(), version.getMinecraftArguments());
		minimumLauncherVersion = version.minimumLauncherVersion;
		incompatibilityReason = version.incompatibilityReason;
		for(MinecraftLibrary library : version.getLibraries())
			libraries.add(new MinecraftLibrary(library));
	}
	
	public CompleteMinecraftVersion(Version version, String mainClass, String minecraftArguments) {
		this(version.getId(), version.getReleaseTime(), version.getUpdatedTime(), version.getType(), mainClass, minecraftArguments);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public ReleaseType getType() {
		return type;
	}
	
	@Override
	public Date getUpdatedTime() {
		return time;
	}
	
	@Override
	public Date getReleaseTime() {
		return releaseTime;
	}
	
	public List<MinecraftLibrary> getLibraries() {
		return libraries;
	}
	
	public String getMainClass() {
		return mainClass;
	}
	
	public void setUpdatedTime(Date time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");
		this.time = time;
	}
	
	public void setReleaseTime(Date time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");
		releaseTime = time;
	}
	
	public void setType(ReleaseType type) {
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		this.type = type;
	}
	
	public void setMainClass(String mainClass) {
		if ((mainClass == null) || (mainClass.length() == 0))
			throw new IllegalArgumentException("Main class cannot be null or empty");
		this.mainClass = mainClass;
	}
	
	public List<MinecraftLibrary> getRelevantLibraries() {
		List<MinecraftLibrary> result = new ArrayList<MinecraftLibrary>();
		
		for(MinecraftLibrary library : libraries) {
			if (library.appliesToCurrentEnvironment()) {
				result.add(library);
			}
		}
		
		return result;
	}
	
	public List<File> getClassPath(OperatingSystem os, File base) {
		List<MinecraftLibrary> libraries = getRelevantLibraries();
		List<File> result = new ArrayList<File>();
		
		for(MinecraftLibrary library : libraries) {
			if (library.getNatives() == null) {
				result.add(new File(base, "libraries/" + library.getArtifactPath()));
			}
		}
		
		result.add(new File(base, "versions/" + getId() + "/" + getId() + ".jar"));
		
		return result;
	}
	
	public Collection<String> getExtractFiles(OperatingSystem os) {
		Collection<MinecraftLibrary> libraries = getRelevantLibraries();
		Collection<String> result = new ArrayList<String>();
		
		for(MinecraftLibrary library : libraries) {
			Map<OperatingSystem, String> natives = library.getNatives();
			
			if ((natives != null) && (natives.containsKey(os))) {
				result.add("libraries/" + library.getArtifactPath(natives.get(os)));
			}
		}
		
		return result;
	}
	
	public Set<String> getRequiredFiles(OperatingSystem os) {
		Set<String> neededFiles = new HashSet<String>();
		for(MinecraftLibrary library : getRelevantLibraries()) {
			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);
				if (natives != null)
					neededFiles.add("libraries/" + library.getArtifactPath(natives));
			} else {
				neededFiles.add("libraries/" + library.getArtifactPath());
			}
		}
		return neededFiles;
	}
	
	public boolean hasLibraries(OperatingSystem os, Proxy proxy, File targetDirectory, boolean ignoreLocalFiles) throws MalformedURLException {
		int count = 0;
		
		for(MinecraftLibrary library : getRelevantLibraries()) {
			String file = null;
			
			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);
				if (natives != null)
					file = library.getArtifactPath(natives);
			} else {
				file = library.getArtifactPath();
			}
			
			if (file != null) {
				File local = new File(targetDirectory, "libraries/" + file);
				if ((!local.isFile()) || (!library.hasCustomUrl())) {
					count++;
				}
			}
		}
		return count > 0;
	}
	
	@Override
	public String toString() {
		return "CompleteVersion{id='" + id + '\'' + ", updatedTime=" + time + ", releasedTime=" + time + ", type=" + type + ", libraries=" + libraries + ", mainClass='" + mainClass + '\'' + ", minimumLauncherVersion=" + minimumLauncherVersion + '}';
	}
	
	public String getMinecraftArguments() {
		return minecraftArguments;
	}
	
	public void setMinecraftArguments(String minecraftArguments) {
		if (minecraftArguments == null)
			throw new IllegalArgumentException("Process arguments cannot be null or empty");
		this.minecraftArguments = minecraftArguments;
	}
	
	@Override
	public int getMinimumLauncherVersion() {
		return minimumLauncherVersion;
	}
	
	public void setMinimumLauncherVersion(int minimumLauncherVersion) {
		this.minimumLauncherVersion = minimumLauncherVersion;
	}
	
	@Override
	public boolean appliesToCurrentEnvironment() {
		if (compatibilityRules == null)
			return true;
		Action lastAction = Action.DISALLOW;
		
		for(CompatibilityRule compatibilityRule : compatibilityRules) {
			Action action = compatibilityRule.getAppliedAction();
			if (action != null)
				lastAction = action;
		}
		
		return lastAction == Action.ALLOW;
	}
	
	public void setIncompatibilityReason(String incompatibilityReason) {
		this.incompatibilityReason = incompatibilityReason;
	}
	
	@Override
	public String getIncompatibilityReason() {
		return incompatibilityReason;
	}
	
	@Override
	public boolean isSynced() {
		return synced;
	}
	
	@Override
	public void setSynced(boolean synced) {
		this.synced = synced;
	}
	
	public String getAssets() {
		return assets;
	}
	
	public void setAssets(String assets) {
		this.assets = assets;
	}
}