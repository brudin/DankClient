package eu.bibl.api.event.info;

public final class EventPriority {
	
	public static final byte HIGHEST = 0;
	public static final byte HIGH = 1;
	public static final byte NORMAL = 2;
	public static final byte LOW = 3;
	public static final byte LOWEST = 4;
	public static final byte[] PRIORITIES = { HIGHEST, HIGH, NORMAL, LOW, LOWEST };
}