package eu.bibl.core.io.config;

import eu.bibl.bytetools.ByteTools;

import java.io.*;
import java.util.HashMap;

/**
 * A configuration system that is key->value based
 * @author bibl
 */
public class Config {

    /**
     * The path of the file being saved to.
     */
	protected File path;

    /**
     * The key->value store.
     */
	protected HashMap<String, Object> values;

    /**
     * The default configuration constructor, taking in a file as an argument.
     * @param path The path of the file.
     */
	public Config(File path) {
		this.path = path;
		values = new HashMap<String, Object>();
		load();
	}

    /**
     * Updates a key to be a certain value
     * @param key The key to set
     * @param value The value to set the key to
     */
	public void update(String key, String value) {
        if (value == null) {
            remove(key);
            return;
        }
		values.put(key, value);
	}

    /**
     * Removes a key.
     * @param key The key to remove the value for.
     */
	public void remove(String key) {
		values.remove(key);
	}

    /**
     * Returns the object contained in the key-value store
     * @param key The key to fetch the value for
     * @param defaultValue Object to return if no object found in the key-value store.
     * @return The fetched object
     */
    @SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		Object result = values.get(key);
		if (result != null)
			return (T)result;
		return defaultValue;
	}

    /**
     * Loads from the file.
     */
	@SuppressWarnings("unchecked")
	private void load() {
		if (!path.exists()) {
			try {
				path.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String contents = "";
			String line;
			while ((line = reader.readLine()) != null) {
				contents += line;
			}
			HashMap<String, Object> jsonMap = ByteTools.GSON.fromJson(contents, HashMap.class);
			if (jsonMap != null) {
				values.putAll(jsonMap);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Reloads the configuration
     */
	public void reload() {
		load();
	}

    /**
     * Saves the configuration
     */
	public void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(ByteTools.GSON.toJson(values));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}