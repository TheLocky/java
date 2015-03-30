package core;

import java.awt.Color;
import java.util.Formatter;

import core.Matrix2D.MatrixType;

public class Polynom {
	protected Matrix2D line;
	private static final double EPSIL = 0.000000000001;
	private Color drawColor;

	public Polynom() {
		line = null;
		drawColor = Color.BLACK;
	}
	
	public Polynom(Matrix2D _line) {
		line = _line.clone();
	}

	public double getF(double x) {
		if (line == null)
			return 0;
		if (line.get_type() == MatrixType.vertvector) {
			line.transpose();
		}
		if (line.get_type() == MatrixType.horvector) {
			double sum = 0;
			for (int i = 0; i < line.sizec(); ++i) {
				sum += line.cell(0, i) * Math.pow(x, i);
			}
			return sum;
		}
		return 0;
	}

	public Matrix2D getLine() {
		return line;
	}
	
	public void setColor(Color c) {
		if (c != null) {
			drawColor = c;
		}
	}
	
	public Color getColor() {
		return drawColor;
	}

	public void print() {
		if (line == null)
			return;
		if (line.get_type() == MatrixType.vertvector) {
			line.transpose();
		}
		if (line.get_type() == MatrixType.horvector) {
			for (int i = line.sizec() - 1; i >= 0; i--) {
				String sign = "", perem = "";
				if (Math.abs(line.cell(0, i)) <= EPSIL)
					continue;
				if ((i != line.sizec() - 1) && (line.cell(0, i) > EPSIL))
					sign = "+";
				if (i != 0)
					perem = String.format("*x^%d", i);
				@SuppressWarnings("resource")
				Formatter fmt = new Formatter();
				String outs = fmt.format("%g", line.cell(0, i)).toString();
				int index = outs.indexOf("e");
				if (index > -1) {
					int deg = Integer.parseInt(outs.substring(index+1));
					outs = String.format("%s*10^(%d)", outs.substring(0, index-1), deg);
				}
				System.out.print(String.format("%s%s%s", sign, outs, perem));
			}
			System.out.println();
			return;
		}
		return;
	}
}
