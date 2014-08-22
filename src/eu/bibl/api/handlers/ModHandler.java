package eu.bibl.api.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.bibl.api.API;
import eu.bibl.api.event.EventManager;
import eu.bibl.api.event.events.system.client.APILoadEvent;
import eu.bibl.api.event.events.system.game.mod.TickEvent;
import eu.bibl.api.event.info.EventTarget;
import eu.bibl.api.mod.Mod;
import eu.bibl.api.mod.ModType;
import eu.bibl.api.plugin.PluginClassLoader;
import eu.bibl.bytetools.io.jar.JarDownloader;
import eu.bibl.bytetools.io.jar.JarInfo;
import eu.bibl.core.util.FileConstants;
import eu.bibl.core.util.FileUtil;

public final class ModHandler {
	
	private API api;
	private PluginClassLoader modClassLoader;
	private HashMap<String, Mod> mods;
	
	public ModHandler(API api) {
		this.api = api;
		modClassLoader = new PluginClassLoader();
		mods = new HashMap<String, Mod>();
		EventManager.register(this, TickEvent.class);
	}
	
	private void loadMods() {
		JarDownloader.VERBOSE_WARNINGS = false;
		ArrayList<File> pluginJars = FileUtil.getFiles(FileConstants.PLUGINS_DIR, ".jar");
		for(File jarPlugin : pluginJars) {
			try {
				JarDownloader contextDownloader = new JarDownloader(new JarInfo(jarPlugin));
				if (!contextDownloader.parse())
					throw new Exception("Couldn't load plugin: " + jarPlugin.getAbsolutePath());
				modClassLoader.addPlugin(contextDownloader.getContents());
				// Plugin plugin = null;
				for(Map<String, byte[]> map : contextDownloader.getContents().resources.values()) {
					for(String name : map.keySet()) {
						if (name.equals("plugin.json")) {
							
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Couldn't load plugin from " + jarPlugin.getAbsolutePath());
			}
		}
	}
	
	@EventTarget
	private void onAPILoad(APILoadEvent event) {
		loadMods();
		System.out.println("Loaded " + mods.size() + " mods.");
	}
	
	public void registerMod(Mod mod) {
		mods.put(mod.getModKey(), mod);
		if (mod.getModType() == ModType.TICKABLE) {
			EventManager.register(mod, TickEvent.class);
		}
	}
}