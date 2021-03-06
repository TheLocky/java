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
    private static final int axisSize = 50;
    private static final int radius = 3;
    private double xMin, xMax, yMin, yMax, stepX, stepY;
    private double xLim, yLim;
    private double drawedAX, drawedAY;
    private boolean brdIsSet;
    private BufferedImage savedImg;
    private BufferedImage savedGraph;
    private Color bgcolor;
    private boolean panelResized;
    private Font fntForY, fntForX;
    private Vector<Polynom> polynoms;
    private double[][] points;

    public Draw() {
        brdIsSet = false;
        polynoms = new Vector<>();
        bgcolor = Color.white;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        panelResized = true;
        xLim = 500;
        yLim = 500;
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
        if (g != null) {
            int W = getWidth();
            int H = getHeight();
            if ((W > 0) && (H > 0)) {
                if (panelResized) {
                    savedImg = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
                    savedGraph = new BufferedImage(W - axisSize, H - axisSize, BufferedImage.TYPE_INT_ARGB);
                    configSteps();
                    paintAll();
                    panelResized = false;
                }
                BufferedImage resultImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
                Graphics res = resultImage.getGraphics();
                res.drawImage(savedImg, 0, 0, null);
                res.drawImage(savedGraph, axisSize + 1, 0, null);
                g.drawImage(resultImage, 0, 0, null);
            }
        }
    }

    private void paintAll() {
        drawBackGround();
        drawAxis();
        drawPoints();
        drawPolynoms();
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
        Point getIndexPoint = p.get("getIndex:point");
        Point getXYPoint = p.get("getXY:point");
        // ---------------------
        Pack answer = new Pack();
        if (borders != null) {
            if ((borders[0] < borders[1]) && (borders[2] < borders[3])) {
                setBorders(borders[0], borders[1], borders[2], borders[3]);
                drawedAX = (int) (xMin - 1);
                drawedAY = (int) (yMin - 1);
                drawBackGround();
                drawAxis();
                paint(this.getGraphics());
            } else {
                answer.add("Error:Borders", "Incorrect borders");
            }
        }
        if (cmd != null) {
            if (cmd.contains("clearAll")) {
                points = null;
                polynoms.removeAllElements();
            }
            if (cmd.contains("Points:clear")) {
                points = null;
            }
            if (cmd.contains("Polynom:clear")) {
                polynoms.removeAllElements();
            }
            if (cmd.contains("add")) {
                if (pointArr != null) {
                    if ((pointArr.length >= 2)
                            && (pointArr[0].length == pointArr[1].length)) {
                        points = pointArr;
                    } else {
                        answer.add("Error:add:Points", "Incorrect points array");
                    }
                }
                if (poly != null) {
                    polynoms.addElement(poly);
                }
                if ((poly == null) && (pointArr == null)) {
                    answer.add("Error:add", "Nothing to add");
                }
            }
            if (cmd.contains("draw")) {
                clearGraph();
                drawPoints();
                drawPolynoms();
                paint(this.getGraphics());
            }
            if (cmd.contains("getPanel")) {
                answer.add("Panel", this);
            }
            if (cmd.contains("changeScale")) {
                if (isInitalized()) {
                    if ((scaleDirect != null) && (scalePoint != null)) {
                        if (scaleDirect.length >= 1) {
                            if (inDiap(scalePoint.x, axisSize, savedImg.getWidth()) && inDiap(scalePoint.y, 0,
                                    savedImg.getHeight())) {
                                changeScale(scaleDirect[0], scalePoint.x - axisSize, scalePoint.y);
                            }
                        } else {
                            answer.add("Error:changeScale", "Incorrect direction");
                        }
                    } else {
                        answer.add("Error:changeScale", "Data not sent");
                    }
                } else {
                    answer.add("Error:changeScale",
                            "Draw module not initalized");
                }
            }
            if (cmd.contains("move")) {
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
            if (cmd.contains("getIndex")) {
                if (getIndexPoint != null) {
                    if ((points != null) && (savedGraph != null)) {
                    	int index = -1;
                        double x = w2x(getIndexPoint.x - axisSize, savedGraph.getWidth());
                        int len = points[0].length;
                        if (x < points[0][0])
                        	index = 0;
                        else if (x > points[0][len-1])
                        	index = len-1;
                        else {
	                        for (int i = 0; i < len; i++) {
	                        	if ((x > points[0][i]) && (x <= points[0][i+1])) {
	                        		if (x - points[0][i] < points[0][i+1] - x) {
	                        			index = i;
	                        		} else {
	                        			index = i+1;
	                        		}
	                        		break;
	                        	}
	                        }
                        }
                        if (index >= 0) {
                        	int spotX = x2w(points[0][index], savedGraph.getWidth());
                        	int spotY = y2h(points[1][index], savedGraph.getHeight());
                        	if (inDiap(getIndexPoint.x - axisSize, spotX - radius, spotX + radius) &&
                        			inDiap(getIndexPoint.y, spotY - radius, spotY + radius)) {
                        		answer.add("Index", index);
                        	}
                        }
                    }
                }
            }
            if (cmd.contains("getXY")) {
                if ((getXYPoint != null) && (savedGraph != null))  {
                    double[] retPoint = new double[2];
                    retPoint[0] = w2x(getXYPoint.x - axisSize, savedGraph.getWidth());
                    retPoint[1] = h2y(getXYPoint.y, savedGraph.getHeight());
                    answer.add("XY", retPoint);
                }
            }
        }
        return answer;
    }

    private void setBorders(double x1, double x2, double y1, double y2) {
    	xMin = x1;
    	xMax = x2;
    	yMin = y1;
    	yMax = y2;
        brdIsSet = true;
        configFont();
        configSteps();
    }

    private boolean isInitalized() {
        return (brdIsSet) && (savedImg != null) && (savedGraph != null);
    }

    private void configFont() {
        if (!brdIsSet)
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
        minS = String.format("%.2f", yMin - 1);
        maxS = String.format("%.2f", yMax + 1);
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
        minS = String.format("%.1f", xMin - 1);
        maxS = String.format("%.1f", xMax + 1);
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

    private void configSteps() {
        if (!isInitalized())
            return;

        final int maxSegSize = 30;
        final int minSegSize = 15;

        int hGraph = savedImg.getHeight() - axisSize;
        int wGraph = savedImg.getWidth() - axisSize;

        stepY = 1;
        int segSize = hGraph - y2h(yMin + stepY, hGraph);
        while (segSize > maxSegSize) {
            stepY /= 2;
            segSize = hGraph - y2h(yMin + stepY, hGraph);
        }
        while (segSize < minSegSize) {
            stepY *= 2;
            segSize = hGraph - y2h(yMin + stepY, hGraph);
        }
        stepX = 1;
        segSize = x2w(xMin + stepX, wGraph);
        while (segSize > maxSegSize) {
            stepX /= 2;
            segSize = x2w(xMin + stepX, wGraph);
        }
        while (segSize < minSegSize) {
            stepX *= 2;
            segSize = x2w(xMin + stepX, wGraph);
        }
    }

    private int x2w(double x, int _W) {
        double coef = (x - xMin) / (xMax - xMin);
        return (int) (_W * coef);
    }

    private int y2h(double y, int _H) {
        double coef = (y - yMin) / (yMax - yMin);
        int th = (int) (_H * coef);
        return _H - th;
    }

    private double w2x(int w, int _W) {
        double pixel = (xMax - xMin) / _W;
        return pixel * w + xMin;
    }

    private double h2y(int h, int _H) {
        h = _H - h;
        double pixel = (yMax - yMin) / _H;
        return pixel * h + yMin;
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
        double spotX = w2x(x, savedGraph.getWidth());
        double spotY = h2y(y, savedGraph.getHeight());
        double xMinNew = spotX - (spotX - xMin) * direct;
        double xMaxNew = spotX + (xMax - spotX) * direct;
        double yMinNew = spotY - (spotY - yMin) * direct;
        double yMaxNew = spotY + (yMax - spotY) * direct;
        if (xMaxNew - xMinNew >= xLim) {
        	xMinNew = xMin;
        	xMaxNew = xMax;
        }
        if (yMaxNew - yMinNew >= yLim) {
        	yMinNew = yMin;
        	yMaxNew = yMax;
        }
        setBorders(xMinNew, xMaxNew, yMinNew, yMaxNew);
        drawedAX = (int) (xMin - 1);
        drawedAY = (int) (yMin - 1);
        paintAll();
        paint(this.getGraphics());
    }

    private void moveGraph(int x, int y) {
        if (!isInitalized())
            return;
        double pixelX = (xMax - xMin) / savedGraph.getWidth();
        double pixelY = (yMax - yMin) / savedGraph.getHeight();
        setBorders(xMin + pixelX * -x, xMax + pixelX * -x, yMin + pixelY * y, yMax + pixelY * y);
        paintAll();
        paint(this.getGraphics());
    }

    private void drawBackGround() {
        if (savedImg == null)
            return;
        Graphics g = savedImg.getGraphics();
        g.setColor(bgcolor);
        g.fillRect(0, 0, savedImg.getWidth(), savedImg.getHeight());
        clearGraph();
    }

    private void clearGraph() {
        if (savedGraph != null) {
            savedGraph = new BufferedImage(savedGraph.getWidth(), savedGraph.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = savedGraph.getGraphics();
            Color c = new Color(255, 255, 255, 0);
            g.setColor(c);
            g.fillRect(0, 0, savedGraph.getWidth(), savedGraph.getHeight());
        }
    }

    private void drawAxis() {
        if (!isInitalized())
            return;
        Graphics g = savedImg.getGraphics();

        int hFull = savedImg.getHeight();
        int wFull = savedImg.getWidth();
        int hGraph = hFull - axisSize;
        int wGraph = wFull - axisSize;

        g.setColor(Color.LIGHT_GRAY);
        //null
        int nullX = x2w(0, wGraph) + axisSize;
        int nullY = y2h(0, hGraph);
        if (inDiap(nullX, axisSize + 1, wFull))
            g.drawLine(nullX, 0, nullX, hGraph);
        if (inDiap(nullY, 0, hGraph - 1))
            g.drawLine(axisSize + 1, nullY, wFull, nullY);

        g.setColor(Color.BLACK);
        g.drawLine(axisSize, 0, axisSize, hFull - axisSize); // vertical
        g.drawLine(axisSize, hFull - axisSize, wFull, hFull - axisSize); // horizontal

        // Y axis
        int numAX = 0;
        double iY = drawedAY;
        while (iY > yMin)
            iY -= stepY;
        g.setFont(fntForY);
        while (iY < yMax) {
            if (iY >= yMin) {
                numAX++;
                if (numAX == 1)
                    drawedAY = iY;
                int th = y2h(iY, hGraph);
                g.drawLine(axisSize - 10, th, axisSize, th);
                String label = String.format("%.2f", iY);
                g.drawString(label, 5, th + 3);
            }
            iY += stepY;
        }
        // X axis
        numAX = 0;
        double iX = drawedAX;
        while (iX > xMin)
            iX -= stepX;
        AffineTransform tf = new AffineTransform();
        tf.rotate(Math.toRadians(-90));
        g.setFont(fntForX.deriveFont(tf));
        FontMetrics fm = getFontMetrics(fntForX);
        while (iX < xMax) {
            if (iX >= xMin) {
                numAX++;
                if (numAX == 1)
                    drawedAX = iX;
                int tw = x2w(iX, wGraph) + axisSize;
                g.drawLine(tw, hFull - axisSize, tw, hFull - axisSize + 10);
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
        if (points == null)
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
        double step = (xMax - xMin) / (savedGraph.getWidth());
        Graphics g = savedGraph.getGraphics();
        int w = savedGraph.getWidth();
        int h = savedGraph.getHeight();
        for (Polynom p : polynoms) {
            g.setColor(Color.BLACK);
            for (double x = xMin; x < xMax; x += step) {
                int x1 = x2w(x, w);
                int x2 = x2w(x + step, w);
                int y1 = y2h(p.getF(x), h);
                int y2 = y2h(p.getF(x + step), h);
                if (inDiap(y1, 0, h) || inDiap(y2, 0, h))
                    g.drawLine(x1, y1, x2, y2);
            }
        }
    }

}
