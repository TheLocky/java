package core;

import core.Matrix2D.MatrixType;

public class Polynom {
	protected Matrix2D line;

	public Polynom() {
		line = null;
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

	public void print() {
		if (line == null)
			return;
		if (line.get_type() == MatrixType.vertvector) {
			line.transpose();
		}
		if (line.get_type() == MatrixType.horvector) {
			for (int i = line.sizec() - 1; i >= 0; i--) {
				String sign = "", perem = "";
				if (Math.abs(line.cell(0, i)) == 0.000001)
					continue;
				if ((i != line.sizec() - 1) && (line.cell(0, i) > 0.000001))
					sign = "+";
				if (i != 0)
					perem = String.format("*x^%d", i);
				Out.msg(String.format("%s%.2f%s", sign, line.cell(0, i), perem));
			}
			Out.ln();
			return;
		}
		return;
	}
}
