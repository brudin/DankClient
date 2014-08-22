package eu.bibl.core.transformation;

import org.objectweb.asm.Opcodes;

import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;

/**
 * Represents a transformer that can transform a class into a lady you can fuk
 * @author Bibl
 */
public abstract class AbstractTransformer implements Opcodes {
	
	/** Parent {@link MinecraftTweaker} **/
	protected MinecraftTweaker tweaker;
	/** Current accepted {@link ClassNode} **/
	protected ClassNode cn;
	/** Current {@link HookMap} data **/
	protected HookMap map;
	
	/**
	 * Called from subclass transformer.
	 * @param tweaker Parent {@link MinecraftTweaker}.
	 * @param map Hook data.
	 */
	public AbstractTransformer(MinecraftTweaker tweaker, HookMap map) {
		this.tweaker = tweaker;
		this.map = map;
	}
	
	/**
	 * Under overridable call to attemp to transform a {@link ClassNode}
	 * @param cn ClassNode to attemp to transform.
	 */
	public final void transform(ClassNode cn) {
		// Check if the impdep method accept accepts the current node.
		if (accept(cn)) {
			// Set the protected ClassNode instance.
			this.cn = cn;
			// Run the impdep transform method.
			transform();
		}
	}
	
	/**
	 * @param name Bytecode name of the class.
	 * @return {@link ClassNode} if present.
	 */
	public ClassNode getNode(String name) {
		// Request the ClassNode from the parent tweaker.
		return tweaker.getNode(name);
	}
	
	/**
	 * Whether the classnode will be accepted by the tribe.
	 * @param cn The {@link ClassNode}
	 * @return Implementation dependent.
	 */
	public abstract boolean accept(ClassNode cn);
	
	/** Implementation dependent. Called after <code>accept(ClassNode)</code> returns true. **/
	public abstract void transform();
}