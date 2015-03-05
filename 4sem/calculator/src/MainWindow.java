import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.*;

class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel pan, pBoard, pControl;
	private MargBox pButtons, pCoreButs, pBinarButs, pTrigButs, pFuncButs;
	private ControlButton bAdd, bSub, bMul, bDiv, bSin, bCos, bTg, bCtg;
	private ControlButton bClear, bSign, bSqrt, bPow2, bPow3, bResult;
	private JTextField cmdTF, coreTF;
	
	private int maxLength;

	public MainWindow() {
		super("Калькулятор");
		super.setFocusable(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Preferences settings = Preferences.userRoot().node(
				"/com/thelocky/calculator");
		super.setLocation(new Point(settings.getInt("startX", 100), settings
				.getInt("startY", 100)));
		super.setSize(300, 300);
		super.setResizable(false);

		Font coreFont = new Font("Arial", Font.PLAIN, 22);
		Font cmdFont = new Font("Arial", Font.PLAIN, 11);

		pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		pan.setBorder(new EmptyBorder(10, 10, 10, 10));

		pBoard = new JPanel();
		pBoard.setLayout(new BoxLayout(pBoard, BoxLayout.Y_AXIS));
		pBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		pBoard.setAlignmentX(CENTER_ALIGNMENT);

		pControl = new JPanel();
		pControl.setLayout(new BoxLayout(pControl, BoxLayout.Y_AXIS));
		pControl.setBorder(new EmptyBorder(10, 0, 0, 0));

		pButtons = new MargBox(BoxLayout.LINE_AXIS);
		pBinarButs = new MargBox(BoxLayout.PAGE_AXIS);
		pTrigButs = new MargBox(BoxLayout.PAGE_AXIS);
		pFuncButs = new MargBox(BoxLayout.PAGE_AXIS);
		pCoreButs = new MargBox(BoxLayout.PAGE_AXIS);

		cmdTF = new JTextField();
		cmdTF.setEditable(false);
		cmdTF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		cmdTF.setFont(cmdFont);
		cmdTF.setText("3 + 5 = 8");
		cmdTF.setHorizontalAlignment(JTextField.RIGHT);
		cmdTF.setBackground(Color.WHITE);
		cmdTF.setBorder(new EmptyBorder(0, 0, 0, 0));
		cmdTF.setFocusable(false);

		coreTF = new JTextField();
		coreTF.setFont(coreFont);
		coreTF.setText("0");
		coreTF.setHorizontalAlignment(JTextField.RIGHT);
		coreTF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		coreTF.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),
				new EmptyBorder(0, 0, 0, 3)));
		coreTF.setFocusable(false);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char a = e.getKeyChar();
				if (Character.isDigit(a) || (a == '.')) {
					// только одна точка может быть в числе
					if ((a == '.') && (coreTF.getText().contains("."))) {
						return;
					}
					if ((a != '.') && (coreTF.getText().compareTo("0") == 0)) {
						coreTF.setText("");
					}
					coreTF.setText(coreTF.getText() + a);
				}
				if (a == '\b') {
					// пустого поля не может быть
					if ((a == '\b') && (coreTF.getText().length() == 1)) {
						coreTF.setText("0");
						return;
					}
					coreTF.setText(coreTF.getText().substring(0,
							coreTF.getText().length() - 1));
				}
			}
		});

		bAdd = new ControlButton("+", "add");
		bSub = new ControlButton("-", "sub");
		bMul = new ControlButton("*", "mul");
		bDiv = new ControlButton("\u00f7", "div");

		bSin = new ControlButton("sin", "sin");
		bCos = new ControlButton("cos", "cos");
		bTg = new ControlButton("tg", "tg");
		bCtg = new ControlButton("ctg", "ctg");

		bSign = new ControlButton("\u00b1", "sign");
		bSqrt = new ControlButton("\u221ax", "sqrt");
		bPow2 = new ControlButton("x\u00b2", "pow2");
		bPow3 = new ControlButton("x\u00b3", "pow3");

		bClear = new ControlButton("C", "clear");
		bResult = new ControlButton("=", "result");

		pBoard.add(cmdTF);
		pBoard.add(coreTF);

		pBinarButs.add(bAdd);
		pBinarButs.add(bSub);
		pBinarButs.add(bMul);
		pBinarButs.add(bDiv);

		pTrigButs.add(bSin);
		pTrigButs.add(bCos);
		pTrigButs.add(bTg);
		pTrigButs.add(bCtg);

		pFuncButs.add(bSign);
		pFuncButs.add(bSqrt);
		pFuncButs.add(bPow2);
		pFuncButs.add(bPow3);

		pCoreButs.add(bClear);
		pCoreButs.add(Box.createVerticalGlue());
		pCoreButs.add(bResult);

		pButtons.add(pBinarButs);
		pButtons.add(pTrigButs);
		pButtons.add(pFuncButs);
		pButtons.setMarg(15);
		pButtons.add(pCoreButs);

		pControl.add(pButtons);

		pan.add(pBoard);
		pan.add(pControl);

		super.add(pan);
		super.setVisible(true);

		super.addWindowListener(new WinL(this));
		super.pack();
	}
	
	/*private void setVal(int val) {
		String sval = String.valueOf(val);
		if (sval.length() > maxLength) {
			
		}
	}
	
	private void setVal(double val) {
		
	}*/

	private class WinL extends WindowAdapter {
		private JFrame frame;

		public WinL(JFrame obj) {
			frame = obj;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			Preferences settings = Preferences.userRoot().node(
					"/com/thelocky/calculator");
			settings.putInt("startX", frame.getLocation().x);
			settings.putInt("startY", frame.getLocation().y);
			super.windowClosing(e);
		}
	}

	private class MouseL extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			JComponent obj = (JComponent) e.getSource();
			switch (obj.getClass().getSimpleName()) {
			case "ControlButton": {
				if (obj.getName() == null)
					break;
				switch (obj.getName()) {
				case "add":
					coreTF.setText("5");
				}
			}
			}
		}

	}

	private class ControlButton extends JButton {
		private static final long serialVersionUID = 1L;
		private static final int defaultWidth = 45;
		private static final int defaultHeight = 45;

		public ControlButton(String text, String name) {
			super(text);
			super.setName(name);
			defaultSettings();
		}

		private void defaultSettings() {
			super.setFocusPainted(false);
			super.setMargin(new Insets(0, 0, 0, 0));
			setFixedSize(defaultWidth, defaultHeight);
			super.setFont(new Font("Arial", Font.PLAIN, 20));
			super.addMouseListener(new MouseL());
			super.setFocusable(false);
		}

		public void setFixedSize(int width, int height) {
			Dimension dim = new Dimension(width, height);
			super.setMinimumSize(dim);
			super.setMaximumSize(dim);
			super.setPreferredSize(dim);
		}
	}

	private class MargBox extends Box {
		private static final long serialVersionUID = 1L;
		private int axis;
		private int marg;

		public void setMarg(int marg) {
			this.marg = marg;
		}

		public MargBox(int axis) {
			super(axis);
			this.axis = axis;
			marg = 5;
		}

		@Override
		public Component add(Component comp) {
			if (super.getComponentCount() > 0) {
				if (axis == BoxLayout.LINE_AXIS) {
					super.add(Box.createHorizontalStrut(marg));
				}
				if (axis == BoxLayout.PAGE_AXIS) {
					super.add(Box.createVerticalStrut(marg));
				}
			}
			return super.add(comp);
		}
	}
}
