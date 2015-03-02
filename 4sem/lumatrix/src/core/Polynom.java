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
}
