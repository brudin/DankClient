package eu.bibl.api;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.client.APILoadEvent;
import eu.bibl.api.handlers.ModHandler;
import eu.bibl.mc.accessors.IMinecraft;

public final class API {
	
	public static final String[] AUTHORS = new String[] { "Bibl" };
	
	public static final int REVISION = 179;
	public static final String RELEASE_REVISION = "dankclint rel-" + REVISION;
	
	private static API instance;
	
	public final IMinecraft mc;
	public final ModHandler modHandler;
	
	public API(IMinecraft mc) {
		instance = this;
		this.mc = mc;
		modHandler = new ModHandler(this);
		
		EventManager.dispatch(new APILoadEvent());
		// EventManager.register(this);
	}
	
	public static API getAPI() {
		return instance;
	}
}