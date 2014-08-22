package eu.bibl.core.loader.version.list;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.bibl.core.loader.gson.DateTypeAdapter;
import eu.bibl.core.loader.gson.LowerCaseEnumTypeAdapterFactory;
import eu.bibl.core.loader.gson.MinecraftReleaseTypeFactory;
import eu.bibl.core.loader.gson.ReleaseTypeAdapterFactory;
import eu.bibl.core.loader.os.OperatingSystem;
import eu.bibl.core.loader.rel.MinecraftReleaseType;
import eu.bibl.core.loader.rel.ReleaseType;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.loader.version.CompleteVersion;
import eu.bibl.core.loader.version.PartialVersion;
import eu.bibl.core.loader.version.Version;

public abstract class VersionList {
	
	protected final Gson gson;
	private final Map<String, Version> versionsByName = new HashMap<String, Version>();
	private final List<Version> versions = new ArrayList<Version>();
	private final Map<MinecraftReleaseType, Version> latestVersions = Maps.newEnumMap(MinecraftReleaseType.class);
	
	public VersionList() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
		builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		builder.registerTypeAdapter(ReleaseType.class, new ReleaseTypeAdapterFactory<MinecraftReleaseType>(MinecraftReleaseTypeFactory.instance()));
		builder.enableComplexMapKeySerialization();
		builder.setPrettyPrinting();
		
		gson = builder.create();
	}
	
	public List<Version> getVersions() {
		return versions;
	}
	
	public Version getLatestVersion(MinecraftReleaseType type) {
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");
		return latestVersions.get(type);
	}
	
	public Version getVersion(String name) {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Name cannot be null or empty");
		return versionsByName.get(name);
	}
	
	public CompleteMinecraftVersion getCompleteVersion(String name) throws IOException {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Name cannot be null or empty");
		Version version = getVersion(name);
		if (version == null)
			throw new IllegalArgumentException("Unknown version - cannot get complete version of null");
		return getCompleteVersion(version);
	}
	
	public CompleteMinecraftVersion getCompleteVersion(Version version) throws IOException {
		if ((version instanceof CompleteVersion))
			return (CompleteMinecraftVersion) version;
		if (version == null)
			throw new IllegalArgumentException("Version cannot be null");
		
		CompleteMinecraftVersion complete = gson.fromJson(getContent("versions/" + version.getId() + "/" + version.getId() + ".json"), CompleteMinecraftVersion.class);
		MinecraftReleaseType type = (MinecraftReleaseType) version.getType();
		
		Collections.replaceAll(versions, version, complete);
		versionsByName.put(version.getId(), complete);
		
		if (latestVersions.get(type) == version) {
			latestVersions.put(type, complete);
		}
		
		return complete;
	}
	
	protected void clearCache() {
		versionsByName.clear();
		versions.clear();
		latestVersions.clear();
	}
	
	public void refreshVersions() throws IOException {
		clearCache();
		
		RawVersionList versionList = gson.fromJson(getContent("versions/versions.json"), RawVersionList.class);
		
		for(Version version : versionList.getVersions()) {
			versions.add(version);
			versionsByName.put(version.getId(), version);
		}
		
		for(MinecraftReleaseType type : MinecraftReleaseType.values())
			latestVersions.put(type, versionsByName.get(versionList.getLatestVersions().get(type)));
	}
	
	public CompleteVersion addVersion(CompleteVersion version) {
		if (version.getId() == null)
			throw new IllegalArgumentException("Cannot add blank version");
		if (getVersion(version.getId()) != null)
			throw new IllegalArgumentException("Version '" + version.getId() + "' is already tracked");
		
		versions.add(version);
		versionsByName.put(version.getId(), version);
		
		return version;
	}
	
	public void removeVersion(String name) {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Name cannot be null or empty");
		Version version = getVersion(name);
		if (version == null)
			throw new IllegalArgumentException("Unknown version - cannot remove null");
		removeVersion(version);
	}
	
	public void removeVersion(Version version) {
		if (version == null)
			throw new IllegalArgumentException("Cannot remove null version");
		versions.remove(version);
		versionsByName.remove(version.getId());
		
		for(MinecraftReleaseType type : MinecraftReleaseType.values())
			if (getLatestVersion(type) == version)
				latestVersions.remove(type);
	}
	
	public void setLatestVersion(Version version) {
		if (version == null)
			throw new IllegalArgumentException("Cannot set latest version to null");
		latestVersions.put((MinecraftReleaseType) version.getType(), version);
	}
	
	public void setLatestVersion(String name) {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Name cannot be null or empty");
		Version version = getVersion(name);
		if (version == null)
			throw new IllegalArgumentException("Unknown version - cannot set latest version to null");
		setLatestVersion(version);
	}
	
	public String serializeVersionList() {
		RawVersionList list = new RawVersionList(null);
		
		for(MinecraftReleaseType type : MinecraftReleaseType.values()) {
			Version latest = getLatestVersion(type);
			if (latest != null) {
				list.getLatestVersions().put(type, latest.getId());
			}
		}
		
		for(Version version : getVersions()) {
			PartialVersion partial = null;
			
			if ((version instanceof PartialVersion))
				partial = (PartialVersion) version;
			else {
				partial = new PartialVersion(version);
			}
			
			list.getVersions().add(partial);
		}
		
		return gson.toJson(list);
	}
	
	public String serializeVersion(CompleteVersion version) {
		if (version == null)
			throw new IllegalArgumentException("Cannot serialize null!");
		return gson.toJson(version);
	}
	
	public abstract boolean hasAllFiles(CompleteMinecraftVersion paramCompleteMinecraftVersion, OperatingSystem paramOperatingSystem);
	
	public abstract String getContent(String paramString) throws IOException;
	
	public abstract URL getUrl(String paramString) throws MalformedURLException;
	
	public void uninstallVersion(Version version) {
		removeVersion(version);
	}
	
	private static class RawVersionList {
		
		private List<PartialVersion> versions = new ArrayList<PartialVersion>();
		private Map<MinecraftReleaseType, String> latest = Maps.newEnumMap(MinecraftReleaseType.class);
		
		public RawVersionList(Object o) {
		}
		
		public List<PartialVersion> getVersions() {
			return versions;
		}
		
		public Map<MinecraftReleaseType, String> getLatestVersions() {
			return latest;
		}
	}
}