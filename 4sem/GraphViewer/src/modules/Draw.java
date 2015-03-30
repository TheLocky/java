package modules;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.Module;
import core.Pack;
import core.Polynom;

public class Draw extends JPanel implements Module {
	private static final long serialVersionUID = 1L;

	private double[] brd; // xMin xMax yMin yMax
	private static final int scale = 50;

	private BufferedImage savedImg;
	private BufferedImage savedGraph;
	private Color bgcolor;
	private boolean panelResized;
	private Font fntForY, fntForX;
	private Vector<Polynom> polynoms;
	private double[][] points;

	public Draw() {
		brd = null;
		polynoms = new Vector<Polynom>();
		bgcolor = Color.white;
		setBorder(new EmptyBorder(0, 0, 0, 0));
		// setFont(new Font("Courier", Font.PLAIN, 12));
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				panelResized = true;
				super.componentResized(e);
			}

		});
	}

	@Override
	public void paint(Graphics g) {
		if (panelResized) {
			savedImg = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_RGB);
			savedGraph = new BufferedImage(getWidth() - scale, getHeight()
					- scale, BufferedImage.TYPE_INT_RGB);
			paintAll();
			panelResized = false;
		}
		g.drawImage(savedImg, 0, 0, null);
		g.drawImage(savedGraph, scale + 1, 0, null);
	}
	
	public void paintAll() {
		if (isInitalized()) {
			drawBackGround();
			drawAxis();
			drawPoints();
			drawPolynoms();
		}
	}

	@Override
	public Pack Request(Pack p) {
		// допустимые поля пакета
		double[] borders = p.get("Borders");
		String cmd = p.get("Command");
		double[][] pointArr = p.get("Points");
		Polynom poly = p.get("Polynom");
		double[] scaleDirect = p.get("Scale:direction");
		Point scalePoint = p.get("Scale:point");
		Point moveOldPoint = p.get("Move:oldPoint");
		Point moveNewPoint = p.get("Move:newPoint");
		// ---------------------
		Pack answer = new Pack();
		if (borders != null) {
			if ((borders[0] < borders[1]) && (borders[2] < borders[3])) {
				brd = borders;
				configFont();
			} else {
				answer.add("Error:Borders", "Incorrect borders");
			}
		}
		if (cmd != null) {
			if (cmd.indexOf("clear") != -1) {
				clearGraph();
			}
			if (cmd.indexOf("add") != -1) {
				if (pointArr != null) {
					if ((pointArr.length >= 2)
							&& (pointArr[0].length == pointArr[1].length)) {
						points = pointArr;
						clearGraph();
						drawPoints();
						drawPolynoms();
					} else {
						answer.add("Error:add:Points", "Incorrect points array");
					}
				}
				if (poly != null) {
					polynoms.addElement(poly);
					clearGraph();
					drawPoints();
					drawPolynoms();
				}
				if ((poly == null) && (pointArr == null)) {
					answer.add("Error:add", "Nothing to add");
				}
			}
			if (cmd.indexOf("Points:clear") != -1) {
				points = null;
				clearGraph();
			}
			if (cmd.indexOf("Polynom:clearAll") != -1) {
				polynoms.removeAllElements();
				clearGraph();
			}
			if (cmd.indexOf("getPanel") != -1) {
				answer.add("Panel", this);
			}
			if (cmd.indexOf("changeScale") != -1) {
				if (isInitalized()) {
					if ((scaleDirect != null) && (scalePoint != null)) {
						if (scaleDirect.length >= 1) {
							if (inDiap(scalePoint.x, scale, savedImg.getWidth())
									&& inDiap(scalePoint.y, 0,
											savedImg.getHeight())) {
								changeScale(scaleDirect[0], scalePoint.x - scale,
										scalePoint.y + scale);
							}
						} else {
							answer.add("Error:changeScale",
									"Incorrect direction");
						}
					} else {
						answer.add("Error:changeScale", "Data not sent");
					}
				} else {
					answer.add("Error:changeScale",
							"Draw module not initalized");
				}
			}
			if (cmd.indexOf("move") != -1) {
				if (isInitalized()) {
					if ((moveNewPoint != null) && (moveOldPoint != null)) {
							int x_offset = moveNewPoint.x - moveOldPoint.x;
							int y_offset = moveNewPoint.y - moveOldPoint.y;
							moveGraph(x_offset, y_offset);
					} else {
						answer.add("Error:move", "Data not sent");
					}
				} else {
					answer.add("Error:move", "Draw module not initalized");
				}
			}
		}
		return answer;
	}

	private boolean isInitalized() {
		return (brd != null) && (savedImg != null) && (savedGraph != null);
	}
	
	private void configFont() {
		if (brd == null)
			return;
		Font fnt;
		FontMetrics fm;
		String minS, maxS, tmpS;
		int tmpStrWdt;

		final int maxStrWdtY = 30;
		final int maxStrWdtX = 30;

		// for Y
		fnt = getFont();
		fm = getFontMetrics(fnt);
		minS = String.format("%.2f", brd[2] - 1);
		maxS = String.format("%.2f", brd[3] + 1);
		tmpS = minS.length() > maxS.length() ? minS : maxS;
		tmpStrWdt = fm.stringWidth(tmpS);
		while (tmpStrWdt > maxStrWdtY) {
			int size = fnt.getSize();
			fnt = fnt.deriveFont(fnt.getStyle(), size - 1);
			fm = getFontMetrics(fnt);
			tmpStrWdt = fm.stringWidth(tmpS);
		}
		fntForY = fnt;

		// for X
		fnt = getFont();
		fm = getFontMetrics(fnt);
		minS = String.format("%.1f", brd[0] - 1);
		maxS = String.format("%.1f", brd[1] + 1);
		tmpS = minS.length() > maxS.length() ? minS : maxS;
		tmpStrWdt = fm.stringWidth(tmpS);
		while (tmpStrWdt > maxStrWdtX) {
			int size = fnt.getSize();
			fnt = fnt.deriveFont(fnt.getStyle(), size - 1);
			fm = getFontMetrics(fnt);
			tmpStrWdt = fm.stringWidth(tmpS);
		}
		fntForX = fnt;
	}

	private int x2w(double x, int _W) {
		double coef = (x - brd[0]) / (brd[1] - brd[0]);
		return (int) (_W * coef);
	}

	private int y2h(double y, int _H) {
		double coef = (y - brd[2]) / (brd[3] - brd[2]);
		int th = (int) (_H * coef);
		return _H - th;
	}

	private double w2x(int w, int _W) {
		return (brd[1] - brd[0]) / _W * w + brd[0];
	}

	private double h2y(int h, int _H) {
		double th = _H - h;
		return (brd[3] - brd[2]) / _H * th;
	}

	private boolean inDiap(int p, int min, int max) {
		return (p >= min) && (p <= max);
	}

	private void changeScale(double direct, int x, int y) {
		if (!isInitalized())
			return;
		if (direct == 0) 
			return;
		direct = Math.abs(direct);
		double deltaX = (brd[1] - brd[0]) * direct / 2;
		double deltaY = (brd[3] - brd[2]) * direct / 2;
		double spotX = w2x(x, savedGraph.getWidth());
		double spotY = h2y(y, savedGraph.getHeight());
		brd[0] = spotX - deltaX;
		brd[1] = spotX + deltaX;
		brd[2] = spotY - deltaY;
		brd[3] = spotY + deltaY;
		configFont();
		paintAll();
		paint(this.getGraphics());
	}

	private void moveGraph(int x, int y) {
		if (!isInitalized())
			return;
		double pixelX = (brd[1] - brd[0]) / savedGraph.getWidth();
		double pixelY = (brd[3] - brd[2]) / savedGraph.getHeight();
		brd[0] += pixelX * -x;
		brd[1] += pixelX * -x;
		brd[2] += pixelY * y;
		brd[3] += pixelY * y;
		configFont();
		paintAll();
		paint(this.getGraphics());
	}

	private void drawBackGround() {
		if (!isInitalized())
			return;
		Graphics g = savedImg.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, savedImg.getWidth(), savedImg.getHeight());
		clearGraph();
	}

	private void drawAxis() {
		if (!isInitalized())
			return;
		Graphics g = savedImg.getGraphics();
		g.setColor(Color.BLACK);

		int hFull = savedImg.getHeight();
		int wFull = savedImg.getWidth();
		int hGraph = hFull - scale;
		int wGraph = wFull - scale;

		g.drawLine(scale, 0, scale, hFull - scale); // vertical
		g.drawLine(scale, hFull - scale, wFull, hFull - scale); // horizontal

		final int maxSegSize = 30;
		final int minSegSize = 15;

		// configure steps
		double stepY = 1;
		int segSize = hGraph - y2h(brd[2] + stepY, hGraph);
		while (segSize > maxSegSize) {
			stepY /= 2;
			segSize = hGraph - y2h(brd[2] + stepY, hGraph);
		}
		while (segSize < minSegSize) {
			stepY *= 2;
			segSize = hGraph - y2h(brd[2] + stepY, hGraph);
		}
		double stepX = 1;
		segSize = x2w(brd[0] + stepX, wGraph);
		while (segSize > maxSegSize) {
			stepX /= 2;
			segSize = x2w(brd[0] + stepX, wGraph);
		}
		while (segSize < minSegSize) {
			stepX *= 2;
			segSize = x2w(brd[0] + stepX, wGraph);
		}
		// ---------------

		// Y scale
		int minIntY = (int) brd[2] - 1;
		double iY = minIntY;
		g.setFont(fntForY);
		while (iY < brd[3]) {
			if (iY >= brd[2]) {
				int th = y2h(iY, hGraph);
				g.drawLine(scale - 10, th, scale, th);
				String label = String.format("%.2f", iY);
				g.drawString(label, 5, th + 3);
			}
			iY += stepY;
		}
		// X scale
		int minIntX = (int) brd[0] - 1;
		double iX = minIntX;
		AffineTransform tf = new AffineTransform();
		tf.rotate(Math.toRadians(-90));
		g.setFont(fntForX.deriveFont(tf));
		FontMetrics fm = getFontMetrics(fntForX);
		while (iX < brd[1]) {
			if (iX >= brd[0]) {
				int tw = x2w(iX, wGraph) + scale;
				g.drawLine(tw, hFull - scale, tw, hFull - scale + 10);
				String label = String.format("%.1f", iX);
				int offset = fm.stringWidth(label);
				g.drawString(label, tw + fntForX.getSize() / 2, hFull - 35
						+ offset);
			}
			iX += stepX;
		}
	}

	private void drawPoints() {
		if (!isInitalized())
			return;
		int w = savedGraph.getWidth();
		int h = savedGraph.getHeight();
		Graphics g = savedGraph.getGraphics();
		g.setColor(Color.BLACK);
		for (int i = 0; i < points[0].length; i++) {
			int tw = x2w(points[0][i], w);
			int th = y2h(points[1][i], h);
			if (inDiap(tw, 0, w) && inDiap(th, 0, h)) {
				g.drawLine(tw - 3, th, tw + 3, th);
				g.drawLine(tw, th - 3, tw, th + 3);
			}
		}
	}

	private void drawPolynoms() {
		if (!isInitalized())
			return;
		if (polynoms.isEmpty())
			return;
		double step = (brd[1] - brd[0]) / (savedGraph.getWidth());
		Graphics g = savedGraph.getGraphics();
		int w = savedGraph.getWidth();
		int h = savedGraph.getHeight();
		for (Polynom p : polynoms) {
			g.setColor(p.getColor());
			for (double x = brd[0]; x < brd[1]; x += step) {
				int x1 = x2w(x, w);
				int x2 = x2w(x + step, w);
				int y1 = y2h(p.getF(x), h);
				int y2 = y2h(p.getF(x + step), h);
				if (inDiap(y1, 0, h) || inDiap(y2, 0, h))
					g.drawLine(x1, y1, x2, y2);
			}
		}
	}

	private void clearGraph() {
		if (savedGraph != null) {
			Graphics g = savedGraph.getGraphics();
			g.setColor(bgcolor);
			g.fillRect(0, 0, savedGraph.getWidth(), savedGraph.getHeight());
		}
	}

}
