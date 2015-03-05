package core;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Out {
	private static final PrintStream oldStream = System.out;
	
	public static void setFile(String file) {
		try {
			System.setOut(new PrintStream(new FileOutputStream(file)));
		} catch (Exception e) {
			System.err.println("Не могу установить файл вывода");
		}
	}
	
	public static void setConsole() {
		System.out.close();
		System.setOut(oldStream);
	}
	
	public static void error(String s) {
		System.out.println("Error: " + s);
	}
	
	public static void msg(String s) {
		System.out.print(s);
	}
	
	public static void msgln(String s) {
		msg(s);
		ln();
	}
	
	public static void ln() {
		System.out.println();
	}
	
	public static void ln(int count) {
		for (;count > 0; count--)
			ln();
	}
}