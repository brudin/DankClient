package eu.bibl.core.loader.lib;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.StrSubstitutor;

import eu.bibl.core.loader.os.Action;
import eu.bibl.core.loader.os.CompatibilityRule;
import eu.bibl.core.loader.os.OperatingSystem;

public class MinecraftLibrary {
	
	@SuppressWarnings({ "all" })
	public static final StrSubstitutor SUBSTITUTOR = new StrSubstitutor(new HashMap() {
		private static final long serialVersionUID = 8647254657979896922L;
	});
	private String name;
	private List<CompatibilityRule> rules;
	private Map<OperatingSystem, String> natives;
	private ExtractRules extract;
	private String url;
	
	public MinecraftLibrary() {
	}
	
	public MinecraftLibrary(String name) {
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Library name cannot be null or empty");
		this.name = name;
	}
	
	public MinecraftLibrary(MinecraftLibrary library) {
		name = library.name;
		url = library.url;
		
		if (library.extract != null) {
			extract = new ExtractRules(library.extract);
		}
		
		if (library.rules != null) {
			rules = new ArrayList<CompatibilityRule>();
			for(CompatibilityRule compatibilityRule : library.rules) {
				rules.add(new CompatibilityRule(compatibilityRule));
			}
		}
		
		if (library.natives != null) {
			natives = new LinkedHashMap<OperatingSystem, String>();
			for(Entry<OperatingSystem, String> entry : library.getNatives().entrySet())
				natives.put(entry.getKey(), entry.getValue());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public MinecraftLibrary addNative(OperatingSystem operatingSystem, String name) {
		if ((operatingSystem == null) || (!operatingSystem.isSupported()))
			throw new IllegalArgumentException("Cannot add native for unsupported OS");
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Cannot add native for null or empty name");
		if (natives == null)
			natives = new EnumMap<OperatingSystem, String>(OperatingSystem.class);
		natives.put(operatingSystem, name);
		return this;
	}
	
	public List<CompatibilityRule> getCompatibilityRules() {
		return rules;
	}
	
	public boolean appliesToCurrentEnvironment() {
		if (rules == null)
			return true;
		Action lastAction = Action.DISALLOW;
		
		for(CompatibilityRule compatibilityRule : rules) {
			Action action = compatibilityRule.getAppliedAction();
			if (action != null)
				lastAction = action;
		}
		
		return lastAction == Action.ALLOW;
	}
	
	public Map<OperatingSystem, String> getNatives() {
		return natives;
	}
	
	public ExtractRules getExtractRules() {
		return extract;
	}
	
	public MinecraftLibrary setExtractRules(ExtractRules rules) {
		extract = rules;
		return this;
	}
	
	public String getArtifactBaseDir() {
		if (name == null)
			throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
		String[] parts = name.split(":", 3);
		return String.format("%s/%s/%s", new Object[] {
				parts[0].replaceAll("\\.", "/"),
				parts[1],
				parts[2] });
	}
	
	public String getArtifactPath() {
		return getArtifactPath(null);
	}
	
	public String getArtifactPath(String classifier) {
		if (name == null)
			throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
		return String.format("%s/%s", new Object[] {
				getArtifactBaseDir(),
				getArtifactFilename(classifier) });
	}
	
	public String getArtifactFilename(String classifier) {
		if (name == null)
			throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
		
		String[] parts = name.split(":", 3);
		String result = String.format("%s-%s.jar", new Object[] {
				parts[1],
				parts[2], });
		
		return SUBSTITUTOR.replace(result);
	}
	
	public boolean hasCustomUrl() {
		return url != null;
	}
	
	public String getDownloadUrl() {
		if (url != null)
			return url;
		return "https://libraries.minecraft.net/";
	}
	
	@Override
	public String toString() {
		return "Library{name='" + name + '\'' + ", rules=" + rules + ", natives=" + natives + ", extract=" + extract + '}';
	}
}