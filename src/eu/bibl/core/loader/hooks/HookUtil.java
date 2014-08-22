package eu.bibl.core.loader.hooks;

import java.io.IOException;
import java.net.URL;

import eu.bibl.bytetools.io.jar.JarDownloader;

public class HookUtil {
	
	private static final String HOOKS_URL = "http://topdank.org/hooks/";
	
	public static byte[] findOnServer(String version) {
		try {
			return JarDownloader.read(new URL(HOOKS_URL + version + ".json").openStream());
		} catch (IOException e) {
			return null;
		}
	}
}