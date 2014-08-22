package eu.bibl.api.event.events;

public abstract class EventStoppable implements Event {
	
	private boolean stopped;
	
	public EventStoppable() {
		super();
	}
	
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	
	public boolean isStopped() {
		return stopped;
	}
}