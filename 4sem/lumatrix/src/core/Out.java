package core;
public class Out {
	public static void error(String s) {
		System.out.println("Error: " + s);
	}
	
	public static void msg(String s) {
		System.out.println(s);
	}
	
	public static void ln() {
		System.out.println();
	}
	
	public static void ln(int count) {
		for (;count > 0; count--)
			ln();
	}
}