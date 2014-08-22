package eu.bibl.launcher.gui.tab.cat;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import eu.bibl.bytetools.ByteTools;

public class GoogleCatHakr {
	
	private static final String MACHINE_EXTERNAL_IP = getIP();
	private static final int MAX_RESULTS = 8;
	private static final String BASE_URL = getBaseURL();
	
	private int width;
	private int height;
	private URL connectionURL;
	private URLConnection urlConnection;
	private GoogleImageQuery imageQuery;
	private long queryPage = 0;
	private int currentImage = 0;
	private int reloads = 0;
	private BufferedImage image;
	
	public GoogleCatHakr(Dimension size) {
		width = size.width;
		height = size.height;
		try {
			reloadPage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reloadPage() throws IOException {
		connectionURL = new URL(BASE_URL + "&start=" + queryPage);
		urlConnection = connectionURL.openConnection();
		
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		
		imageQuery = ByteTools.GSON.fromJson(builder.toString(), GoogleImageQuery.class);
	}
	
	private void reloadImage() throws IOException {
		if (currentImage > MAX_RESULTS - 1) {
			currentImage = 0;
			queryPage += MAX_RESULTS;
			reloadPage();
		}
		
		image = hakImage();
		if (image == null) {
			reloads++;
			if (reloads >= 10)
				return;
			else
				reloadImage();
			
		} else {
			reloads = 0;
			image = Thumbnails.of(image).forceSize(width, height).asBufferedImage();
		}
	}
	
	private BufferedImage hakImage() {
		try {
			return ImageIO.read(new URL(imageQuery.responseData.results.get(currentImage++).url));
		} catch (Exception e) {
			return null;
		}
	}
	
	public BufferedImage nextImage() {
		try {
			reloadImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private static String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	private static String getBaseURL() {
		String base = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&imgsz=large&rsz=" + MAX_RESULTS + "&q=kittens";
		if (MACHINE_EXTERNAL_IP != null)
			base += "&userip=" + MACHINE_EXTERNAL_IP;
		return base;
	}
}