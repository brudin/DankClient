package eu.bibl.api.event.events.system.game;

import eu.bibl.api.event.events.Event;

public class GameLoadEvent implements Event {
	
	private boolean state;
	
	public GameLoadEvent() {
		state = true;
	}
	
	public GameLoadEvent(boolean state) {
		this.state = state;
	}
	
	public boolean getState() {
		return state;
	}
}