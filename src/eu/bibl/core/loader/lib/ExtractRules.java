package eu.bibl.core.loader.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rules by which to extract things from whatever.
 */
public class ExtractRules {
	
	private ArrayList<String> exclude = new ArrayList<String>();
	
	public ExtractRules() {
	}
	
	public ExtractRules(String[] exclude) {
		if (exclude != null)
			Collections.addAll(this.exclude, exclude);
	}
	
	public ExtractRules(ExtractRules rules) {
		for(String exclude : rules.exclude)
			this.exclude.add(exclude);
	}
	
	public List<String> getExcludes() {
		return exclude;
	}

    /**
     * Whether the path is excluded or not
     * @param path The path to extract
     * @return Whether we should extract or not
     */
	public boolean shouldExtract(String path) {
		if (exclude != null) {
			for(String rule : exclude) {
				if (path.startsWith(rule))
					return false;
			}
		}
		return true;
	}
}