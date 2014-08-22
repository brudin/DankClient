package eu.bibl.core.transformation.transformers;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import eu.bibl.bytetools.analysis.storage.hooks.ClassHook;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

public class APILoaderTransformer extends AbstractTransformer {
	
	private ClassHook mcHook;
	
	public APILoaderTransformer(MinecraftTweaker tweaker, HookMap map) {
		super(tweaker, map);
		mcHook = map.getClassByRefactoredName("Minecraft");
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		return mcHook.getObfuscatedName().equals(cn.name);
	}
	
	@Override
	public void transform() {
		all: for(MethodNode m : cn.getMethods()) {
			if (m.name.equals("<init>") && !m.desc.equals("()")) {
				ListIterator<?> it = m.instructions.iterator();
				while (it.hasNext()) {
					AbstractInsnNode ain = (AbstractInsnNode) it.next();
					if (ain.getOpcode() == INVOKESTATIC) {
						MethodInsnNode min = (MethodInsnNode) ain;
						if (min.name.equals("setUseCache")) {
							InsnList list = new InsnList();
							list.add(new TypeInsnNode(NEW, "eu/bibl/api/API"));
							list.add(new VarInsnNode(ALOAD, 0));
							// eu.bibl.mc.accessors.IMinecraft
							list.add(new MethodInsnNode(INVOKESPECIAL, "eu/bibl/api/API", "<init>", "(Leu/bibl/mc/accessors/IMinecraft;)V"));
							m.instructions.insert(min, list);
							break all;
						}
					}
				}
			}
		}
	}
}