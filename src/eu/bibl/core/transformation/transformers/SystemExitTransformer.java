package eu.bibl.core.transformation.transformers;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.bytetools.analysis.storage.hooks.ClassHook;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

/**
 * Removes System.exit() calls from the Minecraft client, allowing us to better hake.
 * @author Bibl
 */
public class SystemExitTransformer extends AbstractTransformer {
	
	/** The Minecraft {@link ClassHook} instance. **/
	private ClassHook hook;
	
	/**
	 * Creates a new SystemExitTransformer.
	 * @param tweaker Parent {@link MinecraftTweaker}.
	 * @param map {@link HookMap} containing class data.
	 */
	public SystemExitTransformer(MinecraftTweaker tweaker, HookMap map) {
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
				if (ain.getOpcode() == INVOKESTATIC) {
					MethodInsnNode min = (MethodInsnNode) ain;
					// Check if the call is a call to System.exit(int).
					if (min.owner.equals("java/lang/System") && min.name.equals("exit")) {
						// Remove the int load before the exit call.
						m.instructions.remove(min.getPrevious());
						// Removes the exit call itself.
						m.instructions.remove(min);
					}
				}
			}
		}
	}
}