package eu.bibl.launcher.gui.tab.console;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import eu.bibl.launcher.Launcher;

public class ConsoleTab extends JScrollPane {
	
	private static final long serialVersionUID = 2943969274338923729L;
	private static final Font FONT = new Font("Monospaced", 0, 12);
	
	private Launcher launcher;
	private JTextArea textArea;
	private ConsoleOutHijack outHijack;
	private ConsoleErrHijack errHijack;
	
	public ConsoleTab(Launcher launcher) {
		this.launcher = launcher;
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setFont(FONT);
		textArea.setEditable(false);
		textArea.setMargin(null);
		
		setViewportView(textArea);
		
		try {
			outHijack = new ConsoleOutHijack(this);
			errHijack = new ConsoleErrHijack(this);
			System.setOut(outHijack);
			System.setErr(errHijack);
			System.out.println("Hijaked printstreams successfully.");
		} catch (FileNotFoundException e) {
			write("Failed to hijack printstreams, going in dark.");
		}
	}
	
	private String getTime() {
		Date date = new Date();
		String s = new Timestamp(date.getTime()).toString();
		s = s.split(" ")[1];
		if (s.contains("."))
			return s.substring(0, s.lastIndexOf('.'));
		
		return s;
	}
	
	public void write(String text) {
		text = "[" + getTime() + "]: > " + text;
		Document document = textArea.getDocument();
		JScrollBar scrollBar = getVerticalScrollBar();
		boolean shouldScroll = false;
		
		if (getViewport().getView() == textArea) {
			shouldScroll = scrollBar.getValue() + scrollBar.getSize().getHeight() + FONT.getSize() * 4 > scrollBar.getMaximum();
		}
		try {
			document.insertString(document.getLength(), text, null);
		} catch (BadLocationException localBadLocationException) {}
		if (shouldScroll)
			scrollBar.setValue(Integer.MAX_VALUE);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
}