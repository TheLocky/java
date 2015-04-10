package core;

import core.Matrix2D.MatrixType;

public class Interpolation extends Polynom {

	private LUMethod lu;

	public Interpolation(Matrix2D perems, Matrix2D vals) {
		start(perems, vals);
	}

	private void start(Matrix2D perems, Matrix2D vals) {
		Matrix2D ptmp = new Matrix2D(perems);
		Matrix2D vtmp = new Matrix2D(vals);
		if (ptmp.get_type() == MatrixType.horvector)
			ptmp.transpose();
		if (vtmp.get_type() == MatrixType.horvector)
			vtmp.transpose();
		if ((ptmp.get_type() != MatrixType.vertvector)
				|| (vtmp.get_type() != MatrixType.vertvector)) {
			throw new RuntimeException("В конструктор передан(ы) не вектор(ы)");
		}

		Matrix2D tmp = new Matrix2D(ptmp.sizer());
		tmp.setrandom();
		tmp.powelems(0);
		Matrix2D x = ptmp.clone();
		for (int i = 1; i < x.sizer(); ++i) {
			x = ptmp.clone();
			x.powelems(i);
			tmp.setfrommatrix(x, 0, 0, 0, i, x.sizer(), 1);
		}
		lu = new LUMethod(tmp);
		super.line = lu.solve(vtmp);
	}
	
	public void recalc(Matrix2D vals) {
		Matrix2D vtmp = new Matrix2D(vals);
		if (vtmp.get_type() == MatrixType.horvector)
			vtmp.transpose();
		if (vtmp.get_type() == MatrixType.vertvector) {
			super.line = lu.solve(vtmp);
		}
	}
}
