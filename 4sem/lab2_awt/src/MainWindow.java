import java.awt.*;
import java.awt.event.*;


public class MainWindow extends Frame {
	private static final long serialVersionUID = 1L;
	
	private ControlButton add, sub, mul, div;
	private TextField valleft, valright, result;
	private Panel core, buttons, input;

	private class ControlButton extends Button {
		private static final long serialVersionUID = 1L;
		
		ControlButton(String label, String name) {
			super(label);
			super.setName(name);
		}
	}
	
	MainWindow() {
		super("Калькулятор");
		super.setSize(200, 200);
		super.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
		
		core = new Panel(new BorderLayout());
		input = new Panel();
		buttons = new Panel();
		
		add = new ControlButton("+", "add");
		sub = new ControlButton("-", "sub");
		mul = new ControlButton("*", "mul");
		div = new ControlButton("=", "div");
		
		valleft = new TextField();
		valright = new TextField();
		result = new TextField();
		
		input.add(valleft);
		input.add(valright);
		
		buttons.add(add);
		buttons.add(sub);
		buttons.add(mul);
		buttons.add(div);
		
		core.add(input, BorderLayout.NORTH);
		core.add(buttons);
		core.add(result, BorderLayout.SOUTH);
		
		super.add(core);
		
		super.setVisible(true);
	}
	
}
