import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import modules.Calculate;
import modules.Draw;
import core.Matrix2D;
import core.Module;
import core.Pack;
import core.Polynom;
import modules.Input;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private boolean dragMode;
	private boolean isDrawed;
	private double[][] points;
	private double xMin, xMax, yMin;

	private JPanel pGlobal, pControl;
	private JButton bLoad, bDraw, bSwitchMode;
	private JSpinner sDegree;
	private JTextField tFile;

	private Module drawModule, inputModule, calcModule;
	private Point mouseMoveLastPoint;
	private Integer lockPointIndex;
	private JPanel drawPanel;

	MainWindow() {
		super("GraphViewer");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowStateListener(new WindowAdapter() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				drawPanel.setVisible(false);
				drawPanel.setVisible(true);
				super.windowStateChanged(e);
			}
		});
		inputModule = new Input(this);
		calcModule = new Calculate();
		initInterface();
		initDrawModule();
		setVisible(true);
		setMinimumSize(new Dimension(600, 600));
		setLocation(50, 50);
	}

	private void initInterface() {
		dragMode = false;
		MouseAdapter mAdp = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object obj = e.getSource();
				if (obj.equals(bLoad)) {
					Pack inputAns = inputModule.Request(null);
					points = inputAns.get("Points");
					if (points != null) {
						xMax = inputAns.get("xMax");
						xMin = inputAns.get("xMin");
						yMin = inputAns.get("yMin");
						tFile.setText(inputAns.get("fileName"));
						Pack load = new Pack();
						load.add("Borders", new double[]{xMin - 1, xMax + 1, yMin - 1, yMin + xMax - xMin + 1});
						load.add("Command", "clearAll add draw");
						load.add("Points", points.clone());
						drawModule.Request(load);
						bSwitchMode.setEnabled(true);
						bDraw.setEnabled(true);
						SpinnerNumberModel numModel = new SpinnerNumberModel(1, 1, points[0].length - 1, 1);
						sDegree.setModel(numModel);
						sDegree.setEnabled(true);
						isDrawed = false;
					}
				}
				if (obj.equals(bDraw)) {
					Pack calc = new Pack();
					calc.add("Points", points);
					calc.add("Degree", sDegree.getValue());
					Pack data = calcModule.Request(calc);
					data.add("Command", "Polynom:clear add draw");
					drawModule.Request(data);
					isDrawed = true;
				}
				if (obj.equals(bSwitchMode)) {
					if (dragMode) {
						dragMode = false;
						bSwitchMode.setText("Move & Scope");
					}
					else {
						dragMode = true;
						bSwitchMode.setText("Drag points");
					}
				}
				super.mouseClicked(e);
			}
		};
		pGlobal = new JPanel(new BorderLayout());
		pGlobal.setBorder(new EmptyBorder(10, 10, 5, 10));
		pControl = new JPanel();
		GridBagLayout gblControl = new GridBagLayout();
		gblControl.columnWidths = new int[]{0, 0, 0, 0, 0, 50, 0};
		gblControl.rowHeights = new int[]{0};
		gblControl.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
		gblControl.rowWeights = new double[]{0.0};
		pControl.setLayout(gblControl);
		pControl.setBorder(new EmptyBorder(10, 5, 10, 0));
		bLoad = new JButton("Load");
		bLoad.addMouseListener(mAdp);
		bDraw = new JButton("OK");
		bDraw.setEnabled(false);
		bDraw.addMouseListener(mAdp);
		bSwitchMode = new JButton("Move & Scope");
		bSwitchMode.addMouseListener(mAdp);
		bSwitchMode.setEnabled(false);
		sDegree = new JSpinner();
		sDegree.setPreferredSize(new Dimension(50, 27));
		sDegree.setEnabled(false);
		tFile = new JTextField();
		tFile.setPreferredSize(new Dimension(150, 27));
		tFile.setEditable(false);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0,0,0,5);
		c.gridy = 0;
		c.gridx = 1;
		pControl.add(bLoad, c);
		c.gridx = 3;
		pControl.add(sDegree, c);
		c.gridx = 4;
		pControl.add(bDraw, c);
		c.gridx = 6;
		pControl.add(bSwitchMode, c);
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		pControl.add(tFile, c);


		pGlobal.add(pControl,BorderLayout.SOUTH);
		add(pGlobal);
	}

	private void initDrawModule() {
		drawModule = new Draw();
		Pack initDraw = new Pack();
		initDraw.add("Command", "getPanel");
		Pack ans = drawModule.Request(initDraw);
		drawPanel = ans.get("Panel");
		if (drawPanel != null) {
			pGlobal.add(drawPanel, BorderLayout.CENTER);
			drawPanel.addMouseWheelListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (dragMode) return;
					Pack wheel = new Pack();
					wheel.add("Command", "changeScale");
					double[] dir = new double[1];
					int rot = e.getWheelRotation();
					if (rot < 0) { // UP
						dir[0] = 0.9;
					} else { // DOWN
						dir[0] = 1.1;
					}
					wheel.add("Scale:direction", dir.clone());
					wheel.add("Scale:point", e.getPoint().clone());
					drawModule.Request(wheel);
				}
			});
			drawPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						mouseMoveLastPoint = e.getPoint();
						if (dragMode) {
							Pack drag = new Pack();
							drag.add("Command", "getIndex");
							drag.add("getIndex:point", mouseMoveLastPoint);
							Pack ans = drawModule.Request(drag);
							lockPointIndex = ans.get("Index");
							if (lockPointIndex != null) {
								drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
							}
						} else
							drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					}

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						mouseMoveLastPoint = null;
						lockPointIndex = null;
						drawPanel.setCursor(Cursor.getDefaultCursor());
					}
				}
			});
			drawPanel.addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					if (dragMode) {
						if (lockPointIndex != null) {
							Pack getLoc = new Pack();
							getLoc.add("Command", "getXY");
							getLoc.add("getXY:point", e.getPoint().clone());
							getLoc = drawModule.Request(getLoc);
							double[] xy = getLoc.get("XY");
							points[1][lockPointIndex] = xy[1];
							Pack reDraw = new Pack();
							reDraw.add("Command", "clearAll add draw");
							reDraw.add("Points", points.clone());
							if (isDrawed) {
								Pack recalc = new Pack();
								recalc.add("idxToChange", lockPointIndex);
								recalc.add("newValue", xy[1]);
								reDraw.add("Polynom", calcModule.Request(recalc).get("Polynom"));
							}
							drawModule.Request(reDraw);
						}
					} else {
						if (mouseMoveLastPoint != null) {
							Pack move = new Pack();
							move.add("Command", "move");
							move.add("Move:oldPoint", mouseMoveLastPoint.clone());
							move.add("Move:newPoint", e.getPoint().clone());
							drawModule.Request(move);
							mouseMoveLastPoint = (Point) e.getPoint().clone();
						}
					}

				}

			});
		}
	}
}
