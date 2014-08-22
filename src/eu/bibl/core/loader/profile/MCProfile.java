package eu.bibl.core.loader.profile;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import eu.bibl.core.util.FileConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Wrapper for a Minecraft profile.
 */
public class MCProfile {

    /**
     * The profile to wrap
     */
	private Profile profile;
    /**
     * The authentication object to use
     */
	private YggdrasilUserAuthentication auth;
	
	public MCProfile(String username, String password) {
		this(new Profile(username, password));
	}
	
	public MCProfile(Profile profile) {
		this.profile = profile;
		auth = getUserAuth();
		try {
			auth.logIn();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
	
	public YggdrasilUserAuthentication getUserAuth() {
		if (auth != null)
			return auth;
		
		if (profile.lastUUID == null)
			profile.lastUUID = UUID.randomUUID();
		YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, profile.lastUUID.toString());
		YggdrasilUserAuthentication auth = new YggdrasilUserAuthentication(authService, Agent.MINECRAFT);
		auth.setUsername(new String(decrypt(profile.username)));
		auth.setPassword(new String(decrypt(profile.password)));
		return auth;
	}
	
	public void save() {
		profile.save(FileConstants.PROFILE_DIR.getAbsolutePath(), getUserAuth().getSelectedProfile().getName());
	}
	
	public void setUsername(String username) {
		profile.setUsername(username);
	}
	
	public static MCProfile read(File profileFile) throws IOException {
		return new MCProfile(Profile.read(profileFile));
	}
	
	public String getUUID() {
		return profile.lastUUID.toString();
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