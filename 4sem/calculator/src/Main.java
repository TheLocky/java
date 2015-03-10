import java.io.*;
import java.util.prefs.Preferences;

public class Main {

	public static void main(String[] args) {
		getPrefs();
		new MainWindow();
		//System.out.println(String.format("%5i", 1111111111));
	}

	private static void getPrefs() {
		PrintStream tmp = System.err;
		PrintStream newPrint = new PrintStream(new OutputStream() {
			public void write(int b) throws IOException {}
		});
		System.setErr(newPrint);
		Preferences.userRoot();
		System.setErr(tmp);
		//System.err.println("KOSTIL RABOTAET");
	}

}