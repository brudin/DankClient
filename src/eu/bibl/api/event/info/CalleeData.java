package eu.bibl.api.event.info;

import java.lang.reflect.Method;

public final class CalleeData {
	
	public byte priority;
	public Object src;
	public Method method;
	
	public CalleeData(byte priority, Object src, Method method) {
		super();
		this.priority = priority;
		this.src = src;
		this.method = method;
		if (!method.isAccessible())
			method.setAccessible(true);
	}
}