package eu.bibl.core.transformation.transformers;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import eu.bibl.api.event.events.system.game.GameLoadEvent;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

/**
 * Injects a {@link GameLoadEvent} dispatch in the main method of the launching version.
 * @author Bibl
 */
public class GameLoadEventTransformer extends AbstractTransformer {
	
	/** Main class of the launching version. **/
	private String main;
	
	/**
	 * Creates a new GameLoadEventTransformer.
	 * @param tweaker Parent {@link MinecraftTweaker}
	 * @param map {@link HookMap} containing hook data.
	 * @param main Main class of the launching version.
	 */
	public GameLoadEventTransformer(MinecraftTweaker tweaker, HookMap map, String main) {
		super(tweaker, map);
		this.main = main;
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		// Return true if this class is the launcher class for the game.
		return cn.name.equals(main);
	}
	
	@Override
	public void transform() {
		// Loop through all of the methods in the class.
		for(MethodNode m : cn.getMethods()) {
			// If this method is the main method.
			if (m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V")) {
				InsnList list = new InsnList();
				// Instantiate a new instance of the GameLoadEvent class.
				list.add(new TypeInsnNode(NEW, "eu/bibl/api/event/events/system/game/GameLoadEvent"));
				list.add(new InsnNode(DUP));
				list.add(new MethodInsnNode(INVOKESPECIAL, "eu/bibl/api/event/events/system/game/GameLoadEvent", "<init>", "()V"));
				// Call the dispatch method in the EventManager class.
				list.add(new MethodInsnNode(INVOKESTATIC, "eu/bibl/api/event/EventManager", "dispatch", "(Leu/bibl/api/event/events/Event;)V"));
				// Add these instructions as the first in the main method.
				m.instructions.insertBefore(m.instructions.getFirst(), list);
			}
		}
	}
}