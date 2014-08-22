package eu.bibl.core.loader.hooks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.io.IOUtils;

import eu.bibl.bytetools.ByteTools;
import eu.bibl.bytetools.analysis.storage.hooks.HookMap;
import eu.bibl.core.util.FileConstants;

/**
 * Parses JSON files into HookMaps.
 */
public class HookParser {
	
	/**
	 * The directory to load hooks from.
	 */
	private File dir;
	/**
	 * The successfully loaded hooks.
	 */
	private HashMap<String, HookMap> loadedMaps;
	
	public HookParser(File dir) {
		this.dir = dir;
		if (!dir.exists())
			dir.mkdirs();
		loadedMaps = new HashMap<String, HookMap>();
	}
	
	/**
	 * Load ALL the hooks.
	 */
	public void loadAll() {
		for(File file : dir.listFiles()) {
			if (file.isFile()) {
				try {
					byte[] contents = read(file);
					HookMap map = createMap(contents);
					loadedMaps.put(file.getName().replace(".json", ""), map);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private HookMap createMap(byte[] data) {
		byte[] realContentsArray = decompress(data);
		String realContents = new String(realContentsArray);
		HookMap map = ByteTools.GSON.fromJson(realContents, HookMap.class);
		return map;
	}
	
	/**
	 * Returns a map by the given version
	 * @param version The version
	 * @return The hookmap corresponding to the given version
	 */
	public HookMap getMap(String version) {
		HookMap map = loadedMaps.get(version);
		if (map != null)
			return map;
		byte[] data = HookUtil.findOnServer(version);
		if (data == null)
			return null;
		map = createMap(data);
		try {
			IOUtils.write(data, new FileOutputStream(new File(FileConstants.HOOKS_DIR, version + ".json")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private byte[] read(File file) throws IOException {
		Path path = Paths.get(file.getAbsolutePath());
		byte[] data = Files.readAllBytes(path);
		return data;
	}
	
	private static byte[] decompress(final byte[] pCompressed) {
		final Inflater decompressor = new Inflater();
		decompressor.setInput(pCompressed);
		byte[] decompressed = new byte[] {};
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(pCompressed.length);
		try {
			final byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				try {
					final int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (final DataFormatException e) {
					throw new RuntimeException(e);
				}
			}
			decompressed = bos.toByteArray();
			bos.close();
		} catch (final IOException e) {}
		return decompressed;
	}
}