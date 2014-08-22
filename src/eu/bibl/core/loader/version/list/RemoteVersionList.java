package eu.bibl.core.loader.version.list;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import eu.bibl.core.loader.os.OperatingSystem;
import eu.bibl.core.loader.version.CompleteMinecraftVersion;
import eu.bibl.core.util.Http;

public class RemoteVersionList extends VersionList {
	
	private final String baseUrl;
	private final Proxy proxy;
	
	public RemoteVersionList(String baseUrl, Proxy proxy) {
		this.baseUrl = baseUrl;
		this.proxy = proxy;
	}
	
	@Override
	public boolean hasAllFiles(CompleteMinecraftVersion version, OperatingSystem os) {
		return true;
	}
	
	@Override
	public String getContent(String path) throws IOException {
		return Http.performGet(getUrl(path), proxy);
	}
	
	@Override
	public URL getUrl(String file) throws MalformedURLException {
		return new URL(baseUrl + file);
	}
	
	public Proxy getProxy() {
		return proxy;
	}
}