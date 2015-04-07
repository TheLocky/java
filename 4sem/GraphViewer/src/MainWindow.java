import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import modules.Draw;
import core.Matrix2D;
import core.Module;
import core.Pack;
import core.Polynom;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	Module drawModule;
	Point mouseMoveLastPoint;
	JPanel drawPanel;

	MainWindow() {
		super("GraphViewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 500);
		initDrawModule();
		setVisible(true);

	}

	private void initDrawModule() {
		drawModule = new Draw();
		Pack initDraw = new Pack();
		double[] brd = { -5, 5, -1, 9 };
		initDraw.add("Borders", brd);
		initDraw.add("Command", "getPanel add");
		// parabola
		Matrix2D tmp = new Matrix2D(3, 1);
		double[] dt = { 0, 0, 1 };
		tmp.setcol(dt, 0);
		Polynom parabola = new Polynom(tmp);
		parabola.setColor(Color.RED);
		// points
		double[][] points = { { -2, -1, 0, 1, 2 }, { 4, 1, 4, 1, 4 } };
		initDraw.add("Points", points.clone());
		initDraw.add("Polynom", parabola);
		Pack ans = drawModule.Request(initDraw);
		drawPanel = ans.get("Panel");
		if (drawPanel != null) {
			add(drawPanel, BorderLayout.CENTER);
			drawPanel.addMouseWheelListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
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
						drawPanel.setCursor(Cursor
								.getPredefinedCursor(Cursor.MOVE_CURSOR));
					}

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						mouseMoveLastPoint = null;
						drawPanel.setCursor(Cursor.getDefaultCursor());
					}
				}
			});
			drawPanel.addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					if (mouseMoveLastPoint != null) {
						Pack move = new Pack();
						move.add("Command", "move");
						move.add("Move:oldPoint", mouseMoveLastPoint.clone());
						move.add("Move:newPoint", e.getPoint().clone());
						drawModule.Request(move);
						mouseMoveLastPoint = (Point) e.getPoint().clone();
					}
				}

			});
		}
	}
}
