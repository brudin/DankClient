package eu.bibl.core.io.config;

import java.io.File;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.client.ClientShutdownEvent;
import eu.bibl.api.event.info.EventPriority;
import eu.bibl.api.event.info.EventTarget;
import eu.bibl.core.util.FileConstants;

/**
 * Wrapper class containing a reference to the global DankClient configuration file.
 */
public class GlobalConfig extends Config {
	
	public static final Config GLOBAL_CONFIG = new GlobalConfig(FileConstants.CONFIG_FILE);
	
	private GlobalConfig(File path) {
		super(path);
		EventManager.register(this, ClientShutdownEvent.class);
		
	}
	
	@EventTarget(priority = EventPriority.LOWEST)
	private void onClientShutdown(ClientShutdownEvent event) {
		save();
	}
}