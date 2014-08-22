package eu.bibl.core.loader.profile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import eu.bibl.bytetools.ByteTools;

public class Profile {
	
	byte[] username;
	byte[] password;
	UUID lastUUID;
	
	public Profile(String username, String password) {
		this.username = encrypt(username.getBytes());
		this.password = encrypt(password.getBytes());
		lastUUID = UUID.randomUUID();
	}
	
	public String getUsername() {
		return new String(decrypt(username));
	}
	
	public void setUsername(String username) {
		this.username = encrypt(username.getBytes());
	}
	
	public void save(String dir, String file) {
		File profileFile = new File(dir, file + ".json");
		try {
			FileOutputStream fos = new FileOutputStream(profileFile);
			fos.write(ByteTools.GSON.toJson(this).getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Profile read(File profileFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(profileFile));
		String contents = "";
		String line = "";
		while ((line = reader.readLine()) != null) {
			contents += line;
		}
		reader.close();
		return ByteTools.GSON.fromJson(contents, Profile.class);
	}
	
	private static byte[] encrypt(final byte[] pToCompress) {
		byte[] compressed = new byte[] {};
		final Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setInput(pToCompress);
		compressor.finish();
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(pToCompress.length)) {
			final byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				final int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
			compressed = bos.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return compressed;
	}
	
	private static byte[] decrypt(final byte[] pCompressed) {
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