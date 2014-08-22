package eu.bibl.core.util;

import java.io.File;
import java.util.ArrayList;

/**
 * Utility functions for local IO.
 * @author Bibl
 */
public final class FileUtil {
	
	/**
	 * Recursively finds files.
	 * @param dir Directory to start in.
	 * @param exts List of file types to find.
	 * @return Files of the extensions asked for.
	 */
	public static ArrayList<File> getFiles(File dir, ArrayList<String> exts) {
		ArrayList<File> files = new ArrayList<File>();
		for(File file : dir.listFiles()) {
			if (file.isDirectory()) {
				files.addAll(getFiles(file, exts));
			} else {
				String ext = getExtentsion(file);
				if (exts.contains(ext))
					files.add(file);
			}
		}
		return files;
	}
	
	/**
	 * Recursively finds files.
	 * @param dir Directory to start in.
	 * @param exts Array of file types to find.
	 * @return Files of the extensions asked for.
	 */
	public static ArrayList<File> getFiles(File dir, String[] exts) {
		ArrayList<String> extss = new ArrayList<String>();
		for(String ext : exts) {
			extss.add(ext);
		}
		return getFiles(dir, extss);
	}
	
	/**
	 * Recursively finds files.
	 * @param dir Directory to start in.
	 * @param ext Type of files to find.
	 * @return Files of the extension asked for.
	 */
	public static ArrayList<File> getFiles(File dir, String ext) {
		return getFiles(dir, new String[] { ext });
	}
	
	/**
	 * Recursively finds jar files.
	 * @param dir Directory to start in.
	 * @return The jar files in the given directory.
	 */
	public static ArrayList<File> getJars(File dir) {
		return getFiles(dir, ".jar");
	}
	
	/**
	 * @param file File to get extension for.
	 * @return The extension of the inputted file.
	 */
	public static String getExtentsion(File file) {
		return getExtentsion(file.getAbsolutePath());
	}
	
	/**
	 * @param file Name of the file to get extension for.
	 * @return The extension of the inputted file.
	 */
	public static String getExtentsion(String file) {
		if (file.lastIndexOf(".") == -1)
			return "";
		return file.substring(file.lastIndexOf("."));
	}
}