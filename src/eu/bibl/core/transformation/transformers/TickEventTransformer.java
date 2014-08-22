package eu.bibl.core.transformation.transformers;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import eu.bibl.bytetools.analysis.storage.hooks.ClassHook;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.analysis.storage.hooks.MethodHook;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

public class TickEventTransformer extends AbstractTransformer {
	
	private ClassHook mcHook;
	private MethodHook runtickHook;
	
	public TickEventTransformer(MinecraftTweaker tweaker, HookMap map) {
		super(tweaker, map);
		mcHook = map.getClassByRefactoredName("Minecraft");
		for(MethodHook hook : map.getMethodHooks()) {
			System.out.println(hook.getRefactoredName());
			if (hook.getRefactoredName().equals("runtick")) {
				runtickHook = hook;
				// break;
			}
		}
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		return mcHook.getObfuscatedName().equals(cn.name);
	}
	
	@Override
	public void transform() {
		for(MethodNode m : cn.getMethods()) {
			if (m.desc.equals("()V") && m.name.equals(runtickHook.getObfuscatedName())) {
				InsnList list = new InsnList();
				list.add(new TypeInsnNode(NEW, "eu/bibl/api/event/events/system/game/mod/TickEvent"));
				list.add(new InsnNode(DUP));
				list.add(new MethodInsnNode(INVOKESPECIAL, "eu/bibl/api/event/events/system/game/mod/TickEvent", "<init>", "()V"));
				list.add(new MethodInsnNode(INVOKESTATIC, "eu/bibl/api/event/EventManager", "dispatch", "(Leu/bibl/api/event/events/Event;)V"));
				m.instructions.insertBefore(m.instructions.getFirst(), list);
				break;
			}
		}
	}
}