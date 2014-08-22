package eu.bibl.core.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.bytetools.asm.ClassNode;
import eu.bibl.bytetools.io.jar.JarDownloader;
import eu.bibl.core.launcher.MinecraftLauncher;
import eu.bibl.core.launcher.VersionFinderTransformer;
import eu.bibl.core.loader.hooks.HookParser;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.transformation.transformers.APILoaderTransformer;
import eu.bibl.core.transformation.transformers.GameLoadEventTransformer;
import eu.bibl.core.transformation.transformers.GameShutdownEventTransformer;
import eu.bibl.core.transformation.transformers.GetterSetterInterfaceTransformer;
import eu.bibl.core.transformation.transformers.SystemExitTransformer;
import eu.bibl.core.transformation.transformers.TickEventTransformer;
import eu.bibl.core.util.FileConstants;

/**
 * Handles all internal editing of the Minecraft classes.
 * @author Bibl
 */
@SuppressWarnings("all")
public final class MinecraftTweaker {
	
	/** Parent {@link MinecraftLauncher}. **/
	private MinecraftLauncher launcher;
	/** Version data. **/
	private CompleteMinecraftVersion version;
	/** Resource downloader that was used to get all of the libraries and game contents. **/
	private JarDownloader gameLoader;
	/** ClassNode cache. **/
	private Map<String, ClassNode> nodes;
	/** Loaded hook data. **/
	private HookMap hookMap;
	
	/**
	 * Creates a new instance of the MinecraftTweaker.
	 * @param launcher Parent launcher instance.
	 * @param version Version data.
	 * @param gameLoader Downloader for the game.
	 */
	public MinecraftTweaker(MinecraftLauncher launcher, CompleteMinecraftVersion version, JarDownloader gameLoader) {
		this.launcher = launcher;
		this.version = version;
		this.gameLoader = gameLoader;
		// Reformat from ASM ClassNodes to ByteTools 2 ClassNodes.
		nodes = new HashMap<String, ClassNode>();
		for(org.objectweb.asm.tree.ClassNode cn : gameLoader.getContents().classNodes.values()) {
			nodes.put(cn.name, (ClassNode) cn);
		}
		// Loads all hook data files that have been downloaded in the hook dir.
		HookParser parser = new HookParser(FileConstants.HOOKS_DIR);
		parser.loadAll();
		// Request a certain map for the version specified in .json file.
		hookMap = parser.getMap(launcher.getVersion());
		// If the map wasn't already downloaded, try to find it again.
		if (hookMap == null) {
			// Attempts to analyse the game file to find the game version.
			VersionFinderTransformer finder = new VersionFinderTransformer(this, null);
			runTransformer(finder);
			// Try again to find the hook data.
			hookMap = parser.getMap(finder.getVersion());
			if (hookMap == null) {
				// If everything failed, just don't bother trying to edit the game.
				return;
			}
		}
		// If it was found properly, attempt to edit the game files.
		internalTransform();
	}
	
	/**
	 * @param name Internal name of the class.
	 * @return The found {@link ClassNode}.
	 */
	public ClassNode getNode(String name) {
		return nodes.get(name);
	}
	
	/**
	 * Runs internal transformations.
	 */
	private void internalTransform() {
		// Loads all of the system transformers into a list.
		ArrayList<AbstractTransformer> transformers = new ArrayList<>();
		transformers.add(new GameLoadEventTransformer(this, hookMap, version.getMainClass().replace(".", "/")));
		transformers.add(new GameShutdownEventTransformer(this, hookMap));
		transformers.add(new SystemExitTransformer(this, hookMap));
		transformers.add(new GetterSetterInterfaceTransformer(this, hookMap));
		transformers.add(new APILoaderTransformer(this, hookMap));
		transformers.add(new TickEventTransformer(this, hookMap));
		
		// Runs all of the transformers.
		runTransformer(transformers.toArray(new AbstractTransformer[transformers.size()]));
	}
	
	/**
	 * Goes through the transformation process on every loaded {@link ClassNode}.
	 * @param transformers The transformer(s) to run.
	 */
	private void runTransformer(AbstractTransformer... transformers) {
		for(AbstractTransformer transformer : transformers) {
			for(ClassNode node : nodes.values()) {
				transformer.transform(node);
			}
		}
	}
	
	/**
	 * @return Whether the hook data was loaded and in turn whether the game files were edited.
	 */
	public boolean hasWorked() {
		return hookMap != null;
	}
}