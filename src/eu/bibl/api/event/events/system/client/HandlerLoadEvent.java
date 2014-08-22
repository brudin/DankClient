package eu.bibl.api.event.events.system.client;

import eu.bibl.api.API;
import eu.bibl.api.event.events.Event;

public class HandlerLoadEvent implements Event {
	
	private API api;
	
	public HandlerLoadEvent(API api) {
		this.api = api;
	}
	
	public API getAPI() {
		return api;
	}
}