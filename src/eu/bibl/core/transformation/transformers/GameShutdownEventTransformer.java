package eu.bibl.core.transformation.transformers;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import eu.bibl.api.event.events.system.game.GameShutdownEvent;
import eu.bibl.bytetools.analysis.storage.hooks.ClassHook;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

/**
 * Injects a {@link GameShutdownEvent} dispatch after Minecraft destroys the window.
 * @author Bibl
 */
public class GameShutdownEventTransformer extends AbstractTransformer {
	
	/** The Minecraft {@link ClassHook} instance. **/
	private ClassHook hook;
	
	/**
	 * Creates a new GameShutdownEventTransformer.
	 * @param tweaker Parent {@link MinecraftTweaker}.
	 * @param map {@link HookMap} containing class data.
	 */
	public GameShutdownEventTransformer(MinecraftTweaker tweaker, HookMap map) {
		super(tweaker, map);
		// Find the Minecraft ClassHook.
		hook = map.getClassByRefactoredName("Minecraft");
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		// Return whether the current node is the Minecraft class.
		return hook.getObfuscatedName().equals(cn.name);
	}
	
	@Override
	public void transform() {
		// Loop through all of the methods in the class.
		for(MethodNode m : cn.getMethods()) {
			// Loop through each instruction of the current MethodNode.
			ListIterator<?> it = m.instructions.iterator();
			while (it.hasNext()) {
				AbstractInsnNode ain = (AbstractInsnNode) it.next();
				// Check if the instruction is an invocation call.
				if (ain instanceof MethodInsnNode) {
					MethodInsnNode min = (MethodInsnNode) ain;
					// Check if the call is a call to Display.destroy()
					if (min.name.equals("destroy") && min.owner.equals("org/lwjgl/opengl/Display")) {
						InsnList list = new InsnList();
						// Instantiate the new GameShutdownEvent class.
						list.add(new TypeInsnNode(NEW, "eu/bibl/api/event/events/system/game/GameShutdownEvent"));
						list.add(new InsnNode(DUP));
						list.add(new MethodInsnNode(INVOKESPECIAL, "eu/bibl/api/event/events/system/game/GameShutdownEvent", "<init>", "()V"));
						// Call the dispatch method in EventManager.
						list.add(new MethodInsnNode(INVOKESTATIC, "eu/bibl/api/event/EventManager", "dispatch", "(Leu/bibl/api/event/events/Event;)V"));
						// Add this instructions after the destroy call.
						m.instructions.insert(min, list);
						// Only 1 destroy call per game.
						return;
					}
				}
			}
		}
	}
}