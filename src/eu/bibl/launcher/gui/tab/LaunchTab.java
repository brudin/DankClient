package eu.bibl.launcher.gui.tab;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.tab.cat.GoogleCatHakr;

public class LaunchTab extends JPanel implements Runnable, PopupMenuListener {
	
	private static final long serialVersionUID = -374026604672283181L;
	
	private Launcher launcher;
	private GoogleCatHakr cat;
	private boolean running;
	private Thread currentCatThread;
	private Image currentImage;
	
	public LaunchTab(Launcher launcher) {
		super(null);
		this.launcher = launcher;
		setIgnoreRepaint(false);
		running = true;
		cat = new GoogleCatHakr(new Dimension(790, 360));
		currentCatThread = new Thread(this);
		currentCatThread.start();
	}
	
	public void stopCat() {
		running = false;
	}
	
	public void resumeCat() {
		running = true;
		currentCatThread = new Thread(this);
		currentCatThread.start();
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(currentImage, 0, 0, null);
	}
	
	@Override
	public void run() {
		while (running) {
			BufferedImage image = cat.nextImage();
			if (image == null)
				break;
			currentImage = image;
			try {
				Thread.sleep(3000);
				paintImmediately(0, 0, 790, 360);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}
	
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		paintImmediately(0, 0, 790, 360);
	}
	
	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}