package eu.bibl.api.event.events.system;

import org.objectweb.asm.tree.ClassNode;

import eu.bibl.api.event.events.Event;

public class InjectionCycleEvent implements Event {
	
	private ClassNode node;
	
	public InjectionCycleEvent(ClassNode node) {
		this.node = node;
	}
	
	public ClassNode getNode() {
		return node;
	}
}