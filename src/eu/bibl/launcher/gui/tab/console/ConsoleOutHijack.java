package eu.bibl.launcher.gui.tab.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ConsoleOutHijack extends PrintStream {
	
	private ConsoleTab console;
	private PrintStream oldPrintStream;
	
	public ConsoleOutHijack(ConsoleTab console) throws FileNotFoundException {
		super(new File("log.txt"));
		this.console = console;
		oldPrintStream = System.out;
	}
	
	@Override
	public void println() {
		super.println();
		oldPrintStream.println();
		console.write("\n");
	}
	
	@Override
	public void println(boolean x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(char x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(int x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(long x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(float x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(double x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(char x[]) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(String x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
	
	@Override
	public void println(Object x) {
		super.println(x);
		oldPrintStream.println(x);
		console.write(String.valueOf(x) + "\n");
	}
}