package eu.bibl.core.launcher;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

/**
 * Attemps to find the Minecraft release version of the loaded game.
 * @author Bibl
 */
public class VersionFinderTransformer extends AbstractTransformer {
	
	/** The found version. **/
	private String version;
	
	/**
	 * Creates a new VersionFinderTransformer.
	 * @param tweaker Parent {@link MinecraftTweaker}.
	 * @param map Hook data, not needed.
	 */
	public VersionFinderTransformer(MinecraftTweaker tweaker, HookMap map) {
		super(tweaker, map);
		version = null;
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		// Analyse ClassNodes until the version has been found.
		return version != null;
	}
	
	@Override
	public void transform() {
		// Loop through all of the methods in the current class.
		all: for(MethodNode m : cn.getMethods()) {
			// Iterate through every instruction in the current MethodNode.
			ListIterator<?> it = m.instructions.iterator();
			while (it.hasNext()) {
				AbstractInsnNode ain = (AbstractInsnNode) it.next();
				// Check if the current instruction is a load constant instructions.
				if (ain instanceof LdcInsnNode) {
					LdcInsnNode lin = (LdcInsnNode) ain;
					// Check if the constant is a String that begins with Minecraft 1
					if (lin.cst != null && lin.cst.toString().startsWith("Minecraft 1") && !lin.cst.toString().contains("(")) {
						// Set it.
						version = lin.cst.toString();
						break all;
					}
				}
			}
		}
	}
	
	/**
	 * @return The correct release version of the current game.
	 */
	public String getVersion() {
		// Replace the "Minecraft " from when the transformer searched for "Minecraft 1" in the class.
		return version.replace("Minecraft ", "");
	}
}