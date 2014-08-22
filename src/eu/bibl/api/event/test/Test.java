package eu.bibl.api.event.test;

import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.info.EventTarget;

public class Test {
	
	public static void main(String[] args) {
		Test tester = new Test();
		EventManager.register(tester, TestEvent.class);
		for(int i = 0; i < 10; i++) {
			EventManager.dispatch(new TestEvent());
		}
		
		EventManager.unregister(tester, TestEvent.class);
		for(int i = 0; i < 10; i++) {
			EventManager.dispatch(new TestEvent());
		}
	}
	
	int i = 0;
	
	@EventTarget
	public void listen(TestEvent event) {
		System.out.println("called! " + i++);
	}
}