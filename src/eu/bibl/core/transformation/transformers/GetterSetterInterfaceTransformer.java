package eu.bibl.core.transformation.transformers;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import eu.bibl.bytetools.analysis.storage.hooks.ClassHook;
import eu.bibl.bytetools.analysis.storage.hooks.FieldHook;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.analysis.storage.hooks.InterfaceHook;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.bytetools.util.helpers.OpcodeInfo;
import eu.bibl.core.transformation.AbstractTransformer;
import eu.bibl.core.transformation.MinecraftTweaker;

/**
 * Creates dynamic getters and setters for specific classes and makes all non-enum mapped classes implement their corresponding accessor.
 * @author Bibl
 */
@SuppressWarnings("all")
public class GetterSetterInterfaceTransformer extends AbstractTransformer {
	
	/**
	 * Creates new GetterSetterInterfaceTransformer that instantly injects getters and setters. <br>
	 * <b>Note: </b> Requires non-null data; no check to stop it crashing.
	 * @param tweaker Parent {@link MinecraftTweaker}
	 * @param map {@link HookMap} containing mapping data.
	 */
	public GetterSetterInterfaceTransformer(MinecraftTweaker tweaker, HookMap map) {
		super(tweaker, map);
		// Create and inject all of the getters and setters.
		createGettersAndSetters();
	}
	
	/**
	 * Injects all of the possible getter and setter methods it can from the provided hooks file.
	 */
	public void createGettersAndSetters() {
		// Loops through all of the mapped fields.
		for(FieldHook fieldHook : map.getFieldHooks()) {
			
			// The name of the class that the getter will actually get injected into.
			String getterOwner = "";
			// The name of the class containing field that the getter/setter gets/puts to.
			String fieldOwner = "";
			
			// If it is a Minecraft hook, find the second part of the name. Eg. Minecraft$ahl -> azi.
			// Otherwise just find the raw data from the map.
			if (fieldHook.getOwner().getObfuscatedName().contains("$")) {
				getterOwner = map.getClassByRefactoredName(HookMap.getNameFirstPart(fieldHook.getOwner().getObfuscatedName())).getObfuscatedName();
				fieldOwner = HookMap.getNameSecondPart(fieldHook.getOwner().getObfuscatedName());
			} else {
				getterOwner = fieldHook.getOwner().getObfuscatedName();
				fieldOwner = fieldHook.getOwner().getObfuscatedName();
			}
			
			// Gets the node to inject into.
			ClassNode node = tweaker.getNode(getterOwner);
			// Make sure it's not null, eg it exists. This check should only ever fail if the jar is modified but the right Minecraft version hook
			// data has been found and loaded.
			if (node == null)
				continue;
			// The field data.
			String fieldName = fieldHook.getObfuscatedName();
			String fieldDesc = fieldHook.getObfuscatedDesc();
			String getterName = fieldHook.getRefactoredName();
			String getterDesc = "()" + fieldHook.getRefactoredDesc();
			boolean isStatic = fieldHook.isStatic();
			// Create the getter method implementation.
			MethodNode getter = createGetter(fieldOwner, fieldName, fieldDesc, getterName, getterDesc, isStatic);
			
			// Creates the name of the setter that would be injected.
			String stripped = fieldHook.getRefactoredName().replace("get", "");
			String setterName = "set" + (stripped.charAt(0) + "").toUpperCase() + stripped.substring(1);
			String setterDesc = "(" + fieldHook.getRefactoredDesc() + ")V";
			// Create the setter method implementation.
			MethodNode setter = createSetter(fieldOwner, fieldName, fieldDesc, setterName, setterDesc, isStatic);
			
			// Adds the getter and setter to the actual class.
			node.methods.add(getter);
			node.methods.add(setter);
		}
	}
	
	/**
	 * Creates a ready-to-inject getter method implementation of MethodNode.
	 * @param fieldOwner The class the field is contained in.
	 * @param fieldName The name of the field.
	 * @param fieldDesc The full bytecode description of the field.
	 * @param methodName The name of the getter method to inject.
	 * @param methodDesc The full bytecode description of the getter method to inject.
	 * @param isStatic Whether the field is static or not.
	 * @return A {@link MethodNode} with all the appropriate data, ready to inject.
	 */
	private MethodNode createGetter(String fieldOwner, String fieldName, String fieldDesc, String methodName, String methodDesc, boolean isStatic) {
		// Create a new public, non-static getter method.
		MethodNode getter = new MethodNode(ACC_PUBLIC, methodName, methodDesc, null, null);
		// Set the minimum size of the stack of any getter method.
		getter.maxStack = 1;
		// If the field is not static add an ALOAD 0 call (this.) and expand the stack.
		if (!isStatic) {
			getter.instructions.add(new VarInsnNode(ALOAD, 0));
			getter.maxStack++;
		}
		// Figure out the correct get instruction.
		getter.instructions.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, fieldOwner, fieldName, fieldDesc));
		// Figure out the correct return instruction.
		getter.instructions.add(new InsnNode(OpcodeInfo.getReturnOpcode(fieldDesc)));
		
		// Set the max amount of local variables, eg this and what not. Verifier will cry without this.
		getter.maxLocals = 2;
		
		return getter;
	}
	
	/**
	 * Creates a ready-to-inject setter method implementation of MethodNode.
	 * @param fieldOwner The class the field is contained in.
	 * @param fieldName The name of the field.
	 * @param fieldDesc The full bytecode description of the field.
	 * @param methodName The name of the setter method to inject.
	 * @param methodDesc The full bytecode description of the setter method to inject.
	 * @param isStatic Whether the field is static or not.
	 * @return A {@link MethodNode} with all the appropriate data, ready to inject.
	 */
	private MethodNode createSetter(String fieldOwner, String fieldName, String fieldDesc, String methodName, String methodDesc, boolean isStatic) {
		// Create a new public, non-static getter method.
		MethodNode setter = new MethodNode(ACC_PUBLIC, methodName, methodDesc, null, null);
		// Set the minimum size of the stack of any setter method.
		setter.maxStack = 1;
		// If the field is not static add an ALOAD 0 call (this.) and expand the stack.
		if (!isStatic) {
			setter.instructions.add(new VarInsnNode(ALOAD, 0));
			setter.maxStack++;
		}
		// Figure out the load instruction for the local argument.
		setter.instructions.add(new VarInsnNode(OpcodeInfo.getLoadOpcode(fieldDesc), 1));
		// Figure out the put instruction for the field.
		setter.instructions.add(new FieldInsnNode(isStatic ? PUTSTATIC : PUTFIELD, fieldOwner, fieldName, fieldDesc));
		// Simple void return.
		setter.instructions.add(new InsnNode(RETURN));
		
		// Set the max amount of local variables, eg this and what not. Verifier will cry without this.
		setter.maxLocals = 3;
		
		return setter;
	}
	
	@Override
	public boolean accept(ClassNode cn) {
		// Return true if the class is mapped.
		ClassHook hook = map.getClassByObfuscatedName(cn.name);
		return hook != null;
	}
	
	@Override
	public void transform() {
		// TODO: Swag
		if ("".equals(""))
			return;
		
		// If the class is an enum then don't make it implement anything as enums can't.
		if (((cn.access & ACC_ENUM) != 0)) {
			System.out.println("clas " + cn.name + " " + map.getClassByObfuscatedName(cn.name).getRefactoredName());
			return;
		}
		// Find the interface mapping for the current class.
		InterfaceHook hook = map.getClassByObfuscatedName(cn.name).getInterfaceHook();
		// Null checks
		if (hook != null && hook.getInterfaceName() != null) {
			// Add the interface name to the current list of interfaces implemented by the class at the moment.
			cn.interfaces.add(hook.getInterfaceName());
		}
	}
}