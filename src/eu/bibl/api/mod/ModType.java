package eu.bibl.api.mod;

import eu.bibl.api.event.events.Event;

public enum ModType {
	
	/** Mod that requires an update call per tick. **/
	TICKABLE(),
	/** A mod that does things based on certain {@link Event}s or handles data in some way. **/
	FUNCTIONAL();
}