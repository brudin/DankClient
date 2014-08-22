package eu.bibl.api.plugin;

import java.net.URL;
import java.util.HashMap;

import org.objectweb.asm.ClassWriter;

import sun.misc.URLClassPath;
import eu.bibl.bytetools.io.jar.JarContents;

public final class PluginClassLoader extends ClassLoader {
	
	private final HashMap<String, byte[]> cache;
	private final URLClassPath ucp;
	
	public PluginClassLoader() {
		cache = new HashMap<String, byte[]>();
		ucp = new URLClassPath(new URL[] {});
	}
	
	public void addPlugin(JarContents contents) {
		for(org.objectweb.asm.tree.ClassNode classNode : contents.classNodes.values()) {
			ClassWriter cw = new ClassWriter(0);
			classNode.accept(cw);
			cache.put(classNode.name, cw.toByteArray());
		}
		for(URL url : contents.resources.keySet()) {
			ucp.addURL(url);
		}
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		name = name.replace(".", "/");
		if (cache.containsKey(name))
			return defineClass(cache.get(name));
		Class<?> c = getSystemClassLoader().loadClass(name);
		return c;
	}
	
	@SuppressWarnings("deprecation")
	private Class<?> defineClass(byte[] bytes) {
		return defineClass(bytes, 0, bytes.length);
	}
}