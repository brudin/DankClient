package eu.bibl.core.loader.version.list;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public abstract class FileBasedVersionList extends VersionList {
	@Override
	public String getContent(String path) throws IOException {
		return IOUtils.toString(getFileInputStream(path)).replaceAll("\\r\\n", "\r").replaceAll("\\r", "\n");
	}
	
	protected abstract InputStream getFileInputStream(String paramString) throws FileNotFoundException;
	
	@Override
	public URL getUrl(String file) throws MalformedURLException {
		return new File(file).toURI().toURL();
	}
}