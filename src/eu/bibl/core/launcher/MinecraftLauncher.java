package eu.bibl.core.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.tree.ClassNode;

import eu.bibl.bytetools.io.jar.JarDownloader;
import eu.bibl.bytetools.io.jar.JarInfo;
import eu.bibl.core.loader.lib.ExtractRules;
import eu.bibl.core.loader.lib.MinecraftLibrary;
import eu.bibl.core.loader.os.OperatingSystem;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.transformation.MinecraftTweaker;
import eu.bibl.core.util.FileConstants;
import eu.bibl.launcher.Launcher;

/**
 * Downloads, edits and launches Minecraft.
 * @author Bibl
 */
public class MinecraftLauncher {
	
	/** The calling launcher instance. */
	private Launcher launcher;
	
	/** The minecraft version launched by this launcher. **/
	private CompleteMinecraftVersion minecraftVersion;
	
	/*** JarDownloader for loading all relevant libraries and associated content for this version **/
	private JarDownloader gameLoader;
	
	/** Boolean used to store whether the game can launch or not. **/
	private boolean primed;
	
	/** The Minecraft tweaker to be used by this instance of the game launcher. **/
	private MinecraftTweaker tweaker;
	
	/**
	 * Creates a new ready MinecraftLauncher.
	 * @param launcher The launcher
	 * @param minecraftVersion The minecraft version to launch with
	 */
	public MinecraftLauncher(Launcher launcher, CompleteMinecraftVersion minecraftVersion) {
		this.launcher = launcher;
		this.minecraftVersion = minecraftVersion;
	}
	
	/**
	 * Downloads all of the required Minecraft jars and libs
	 * @return Whether the download was successful.
	 */
	public boolean download() {
		List<File> files = minecraftVersion.getClassPath(OperatingSystem.getCurrentPlatform(), FileConstants.MC_DIR);
		JarInfo[] resInfo = new JarInfo[files.size()];
		for(int i = 0; i < files.size(); i++) {
			resInfo[i] = new JarInfo(files.get(i));
		}
		gameLoader = new JarDownloader(resInfo);
		// gameLoader = new JarDownloader(resInfo[files.size() - 1]);
		primed = gameLoader.parse();
		if (!primed) {
			System.out.println("ret2");
			return false;
		}
		
		tweaker = new MinecraftTweaker(this, minecraftVersion, gameLoader);
		primed = tweaker.hasWorked();
		if (!primed) {
			return false;
		}
		
		// JarDumper dumper = new JarDumper(gameLoader.getContents());
		// dumper.dump(new File("C:/Users/Bibl/Desktop/dumpsw0g.jar"));
		return true;
	}
	
	/**
	 * Launches the client
	 * @param name The name of the class to launch with.
	 * @param args The arguments to start the client with.
	 * @return A message containing the result of the launch.
	 */
	public String launch(String name, final String[] args) {
		final Class<?> launchClass = getLaunchClass(name);
		
		if (launchClass == null)
			return "Invalid launch class/loading";
		
		if (!hackClassLoader())
			return "ClassLoader hack fail";
		
		try {
			Method mainMethod = launchClass.getMethod("main", String[].class);
			mainMethod.invoke(null, (Object) args);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Game error";
		}
	}
	
	/**
	 * Attempts to find the main Minecraft class.
	 * @param name The name of the class.
	 * @return The Minecraft launch class, returns null upon failure.
	 */
	private Class<?> getLaunchClass(String name) {
		Class<?> launchClass = null;
		try {
			launchClass = gameLoader.getClassLoader().loadClass(name);
		} catch (ClassNotFoundException e1) {
			ClassNode node = gameLoader.getContents().classNodes.get(name);
			try {
				launchClass = gameLoader.getClassLoader().defineNode(node);
			} catch (Exception ignored) {}
		}
		return launchClass;
	}
	
	/**
	 * This hacks the classloader into changing the natives paths.
	 * @return Whether hacking the gibso- classloader worked.
	 */
	private boolean hackClassLoader() {
		if (!FileConstants.CLIENT_NATIVES_DIR.exists()) {
			try {
				unpackNatives(FileConstants.CLIENT_NATIVES_DIR);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		// This is hackery get on the floor.
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		
		System.setProperty("java.library.path", FileConstants.CLIENT_NATIVES_DIR.getAbsolutePath());
		return true;
	}
	
	/**
	 * Attempts to unpack natives into the provided directory.
	 * @param targetDir The directory to unpack the natives into.
	 * @throws IOException If there is an IO problem.
	 */
	private void unpackNatives(File targetDir) throws IOException {
		OperatingSystem os = OperatingSystem.getCurrentPlatform();
		Collection<MinecraftLibrary> libraries = minecraftVersion.getRelevantLibraries();
		for(MinecraftLibrary library : libraries) {
			Map<OperatingSystem, String> nativesPerOs = library.getNatives();
			if ((nativesPerOs != null) && (nativesPerOs.get(os) != null)) {
				String[] parts = library.getName().split(":", 3);
				String result = String.format("%s-%s%s.jar", new Object[] {
						parts[1],
						parts[2],
						"-" + nativesPerOs.get(os) });
				String name = MinecraftLibrary.SUBSTITUTOR.replace(result);
				boolean is64bit = false;
				if (System.getProperty("os.name").contains("Windows")) {
					is64bit = (System.getenv("ProgramFiles(x86)") != null);
				} else {
					is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
				}
				name = name.replace("${arch}", is64bit ? "64" : "32");
				File file = new File(FileConstants.LIBRARIES_DIR, library.getArtifactBaseDir() + "/" + name);
				ZipFile zip = new ZipFile(file);
				ExtractRules extractRules = library.getExtractRules();
				try {
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						if ((extractRules == null) || (extractRules.shouldExtract(entry.getName()))) {
							File targetFile = new File(targetDir, entry.getName());
							if (targetFile.getParentFile() != null)
								targetFile.getParentFile().mkdirs();
							if (!entry.isDirectory()) {
								BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));
								byte[] buffer = new byte[2048];
								FileOutputStream outputStream = new FileOutputStream(targetFile);
								BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
								try {
									int length;
									while ((length = inputStream.read(buffer, 0, buffer.length)) != -1)
										bufferedOutputStream.write(buffer, 0, length);
								} finally {
									bufferedOutputStream.close();
									outputStream.close();
									inputStream.close();
								}
							}
						}
					}
				} finally {
					zip.close();
				}
			}
		}
	}
	
	/**
	 * Whether the game is primed and ready to start or not.
	 * @return primed
	 */
	public boolean isGamePrimed() {
		return primed;
	}
	
	/**
	 * The version of the client
	 * @return The version
	 */
	public String getVersion() {
		return minecraftVersion.getId();
	}
}