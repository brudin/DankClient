package eu.bibl.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.bibl.api.event.events.Event;
import eu.bibl.api.event.events.EventStoppable;
import eu.bibl.api.event.info.CalleeData;
import eu.bibl.api.event.info.EventPriority;
import eu.bibl.api.event.info.EventTarget;

public class EventManager {
	
	private static final HashMap<Class<? extends Event>, List<CalleeData>> REGISTERED_LISTENERS = new HashMap<Class<? extends Event>, List<CalleeData>>();
	
	/**
	 * Don't fucking try it m80.
	 */
	private EventManager() {
		throw new UnsupportedOperationException("Cannot instantiate EventManager");
	}
	
	/**
	 * Registers all the methods marked with the {@link EventTarget} annotation as listeners.
	 * @param src Source object
	 */
	public static void register(Object src) {
		if (src == null)
			return;
		for(Method method : src.getClass().getDeclaredMethods()) {
			if (!isValid(method))
				continue;
			@SuppressWarnings("unchecked")
			Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
			CalleeData data = new CalleeData(method.getAnnotation(EventTarget.class).priority(), src, method);
			putMap(eventClass, data);
		}
	}
	
	/**
	 * Registers all the methods marked with the {@link EventTarget} annotation that uses the appropriate event type.
	 * @param src Source object.
	 * @param eventClass Appropriate event type.
	 */
	public static void register(Object src, Class<? extends Event> eventClass) {
		if (src == null)
			return;
		for(Method method : src.getClass().getDeclaredMethods()) {
			if (!isValid(method))
				continue;
			if (!method.getParameterTypes()[0].equals(eventClass))
				continue;
			CalleeData data = new CalleeData(method.getAnnotation(EventTarget.class).priority(), src, method);
			putMap(eventClass, data);
		}
	}
	
	/**
	 * Unregisters all of the methods that have been registered as listeners. <br>
	 * <b>NOTE:<b> it is faster to use the {@link #unregister(Object, Class)} method to remove specific listener types.
	 * @param src Source object.
	 */
	public static void unregister(Object src) {
		if (src == null)
			return;
		for(Class<? extends Event> eventClass : REGISTERED_LISTENERS.keySet()) {
			List<CalleeData> dataList = REGISTERED_LISTENERS.get(eventClass);
			if (dataList == null)
				continue;
			ArrayList<CalleeData> safeList = new ArrayList<CalleeData>(dataList);
			for(CalleeData data : safeList) {
				if (data.src.equals(src))
					dataList.remove(data);
			}
		}
	}
	
	/**
	 * Unregisters the methods that have been registered as listeners of the appropriate event type.
	 * @param src Source object
	 * @param eventClass Appropriate event type.
	 */
	public static void unregister(Object src, Class<? extends Event> eventClass) {
		if (src == null)
			return;
		List<CalleeData> dataList = REGISTERED_LISTENERS.get(eventClass);
		if (dataList == null)
			return;
		ArrayList<CalleeData> safeList = new ArrayList<CalleeData>(dataList);
		for(CalleeData data : safeList) {
			if (data.src.equals(src))
				dataList.remove(data);
		}
	}
	
	/**
	 * Sends event to all of the registered listeners of the appropriate type.
	 * @param event Event to send.
	 */
	public static void dispatch(Event event) {
		Class<? extends Event> eventClass = event.getClass();
		List<CalleeData> dataList = REGISTERED_LISTENERS.get(eventClass);
		if (dataList == null)
			return;
		if (event instanceof EventStoppable) {
			EventStoppable stoppable = (EventStoppable) event;
			for(CalleeData data : dataList) {
				try {
					data.method.invoke(data.src, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
				if (stoppable.isStopped())
					break;
			}
		} else {
			for(CalleeData data : dataList) {
				try {
					data.method.invoke(data.src, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
			}
		}
	}
	
	private static void putMap(Class<? extends Event> eventClasss, CalleeData data) {
		List<CalleeData> dataList = REGISTERED_LISTENERS.get(eventClasss);
		if (dataList == null)
			dataList = new CopyOnWriteArrayList<CalleeData>();
		dataList.add(data);
		if (!REGISTERED_LISTENERS.containsKey(eventClasss))
			REGISTERED_LISTENERS.put(eventClasss, dataList);
		prioritise(eventClasss);
	}
	
	private static void prioritise(Class<? extends Event> eventClass) {
		List<CalleeData> dataList = REGISTERED_LISTENERS.get(eventClass);
		List<CalleeData> newList = new CopyOnWriteArrayList<CalleeData>();
		if (dataList != null) {
			for(byte priority : EventPriority.PRIORITIES) {
				for(CalleeData data : dataList) {
					if (data.priority == priority)
						newList.add(data);
				}
			}
			REGISTERED_LISTENERS.put(eventClass, newList);
		}
	}
	
	/**
	 * Checks whether the method is valid to be registered as a listener method.
	 * @param method Method to check.
	 * @return Whether it is valid.
	 */
	public static boolean isValid(Method method) {
		return method.getParameterTypes().length == 1 && method.isAnnotationPresent(EventTarget.class) && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
	}
}