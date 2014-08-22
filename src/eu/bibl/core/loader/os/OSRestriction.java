package eu.bibl.core.loader.os;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSRestriction {
	
	private OperatingSystem name;
	private String version;
	
	public OSRestriction() {
	}
	
	public OperatingSystem getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public OSRestriction(OSRestriction osRestriction) {
		name = osRestriction.name;
		version = osRestriction.version;
	}
	
	public boolean isCurrentOperatingSystem() {
		if ((name != null) && (name != OperatingSystem.getCurrentPlatform()))
			return false;
		
		if (version != null)
			try {
				Pattern pattern = Pattern.compile(version);
				Matcher matcher = pattern.matcher(System.getProperty("os.version"));
				if (!matcher.matches())
					return false;
			} catch (Throwable ignored) {}
		return true;
	}
	
	@Override
	public String toString() {
		return "OSRestriction{name=" + name + ", version='" + version + '\'' + '}';
	}
}