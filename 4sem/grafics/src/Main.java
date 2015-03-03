import java.awt.*;
import java.awt.event.*;

class MyFrame extends Frame {
	private static final long serialVersionUID = 1L;
	
	private Button btn1, btn2;
	private Panel p;
	
	private class WinLis extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	
	private class MLis extends MouseAdapter {
		private Button btn;
		public MLis(Button b) {
			btn = b;
		}
		public void mouseClicked(MouseEvent e) {
			if (btn.getLabel() != "Yes") {
				btn.setLabel("Yes");
			}
			else {
				btn.setLabel("No");
			}
		}
	}
	
	public MyFrame() {
		super("test");
		
		p = new Panel();
		btn1 = new Button("btn1");
		btn2 = new Button("btn2");
		
		btn1.setPreferredSize(new Dimension(100, 25));
		btn2.setPreferredSize(new Dimension(100, 25));	
		
		btn1.addMouseListener(new MLis(btn1));
		btn2.addMouseListener(new MLis(btn2));
		
		
		p.add(btn1);
		p.add(btn2);
		
		this.add(p);
		this.setVisible(true);
		this.pack();
		this.addWindowListener(new WinLis());
	}
}

public class Main {
	
	public static void main(String[] args) {
		MyFrame window = new MyFrame();
	}
	
}
