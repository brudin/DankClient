package eu.bibl.core.loader.version.list;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import eu.bibl.core.loader.os.OperatingSystem;
import eu.bibl.core.loader.rel.MinecraftReleaseType;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.loader.version.CompleteVersion;
import eu.bibl.core.loader.version.Version;

public class LocalVersionList extends FileBasedVersionList {
	
	private final File baseDirectory;
	private final File baseVersionsDir;
	
	public LocalVersionList(File baseDirectory) {
		if ((baseDirectory == null) || (!baseDirectory.isDirectory()))
			throw new IllegalArgumentException("Base directory is not a folder!");
		
		this.baseDirectory = baseDirectory;
		baseVersionsDir = new File(this.baseDirectory, "versions");
		if (!baseVersionsDir.isDirectory())
			baseVersionsDir.mkdirs();
	}
	
	@Override
	protected InputStream getFileInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(new File(baseDirectory, path));
	}
	
	@Override
	public void refreshVersions() throws IOException {
		clearCache();
		
		File[] files = baseVersionsDir.listFiles();
		if (files == null)
			return;
		
		for(File directory : files) {
			String id = directory.getName();
			File jsonFile = new File(directory, id + ".json");
			if ((directory.isDirectory()) && (jsonFile.exists())) {
				try {
					String path = "versions/" + id + "/" + id + ".json";
					CompleteVersion version = gson.fromJson(getContent(path), CompleteMinecraftVersion.class);
					if (version.getType() == null) {
						return;
					}
					
					if (version.getId().equals(id))
						addVersion(version);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		for(Version version : getVersions()) {
			MinecraftReleaseType type = (MinecraftReleaseType) version.getType();
			
			if ((getLatestVersion(type) == null) || (getLatestVersion(type).getUpdatedTime().before(version.getUpdatedTime())))
				setLatestVersion(version);
		}
	}
	
	public void saveVersionList() throws IOException {
		String text = serializeVersionList();
		PrintWriter writer = new PrintWriter(new File(baseVersionsDir, "versions.json"));
		writer.print(text);
		writer.close();
	}
	
	public void saveVersion(CompleteVersion version) throws IOException {
		String text = serializeVersion(version);
		File target = new File(baseVersionsDir, version.getId() + "/" + version.getId() + ".json");
		if (target.getParentFile() != null)
			target.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(target);
		writer.print(text);
		writer.close();
	}
	
	public File getBaseDirectory() {
		return baseDirectory;
	}
	
	@Override
	public boolean hasAllFiles(CompleteMinecraftVersion version, OperatingSystem os) {
		Set<String> files = version.getRequiredFiles(os);
		for(String file : files) {
			if (!new File(baseDirectory, file).isFile()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void uninstallVersion(Version version) {
		super.uninstallVersion(version);
		File dir = new File(baseVersionsDir, version.getId());
		if (dir.isDirectory())
			FileUtils.deleteQuietly(dir);
	}
}