package eu.bibl.api.plugin;

import eu.bibl.api.API;

public abstract class Plugin {
	
	protected final API api;
	
	public Plugin(API api) {
		this.api = api;
	}
}