package core;

import core.Matrix2D.MatrixType;

public class Interpolation extends Polynom {

	private LUMethod lu;

	public Interpolation(Matrix2D perems, Matrix2D vals) {
		start(perems, vals);
	}

	private void start(Matrix2D perems, Matrix2D vals) {
		Matrix2D x = new Matrix2D(perems);
		Matrix2D f = new Matrix2D(vals);
		if (x.get_type() == MatrixType.horvector)
			x.transpose();
		if (f.get_type() == MatrixType.horvector)
			f.transpose();
		if ((x.get_type() != MatrixType.vertvector)
				|| (f.get_type() != MatrixType.vertvector)) {
			throw new RuntimeException("В конструктор передан(ы) не вектор(ы)");
		}

		Matrix2D tmp = new Matrix2D(x.sizer());
		tmp.setrandom();
		tmp.powelems(0);
		for (int i = 1; i < x.sizer(); ++i) {
			x = new Matrix2D(perems);
			x.powelems(i);
			tmp.setfrommatrix(x, 0, 0, 0, i, x.sizer(), 1);
		}
		lu = new LUMethod(tmp);
		super.line = lu.solve(f);
	}
}
