package core;

import core.Matrix2D.MatrixType;

public class Approximation extends Polynom {
	
	public Approximation(Matrix2D perems, Matrix2D vals, int degree) {
		Matrix2D x = new Matrix2D(perems);
		Matrix2D f = new Matrix2D(vals);
		if (x.get_type() == MatrixType.horvector) x.transpose();
		if (f.get_type() == MatrixType.horvector) f.transpose();
		if ((x.get_type() != MatrixType.vertvector) || 
				(f.get_type() != MatrixType.vertvector)) {
			throw new RuntimeException("В конструктор передан(ы) не вектор(ы)");
		}
		if (x.sizer() <= degree) {
			throw new RuntimeException("Степень многочлена превышает возможную");
		}
		else if (x.sizer() == degree + 1) {
			Out.msg("Вызов интерполяции (степень многочлена равна количеству точек - 1)");
			super.line = new Matrix2D(new Interpolation(perems, vals).line);
		}
		else {
			Matrix2D left = new Matrix2D(degree + 1);
			Matrix2D right = new Matrix2D(degree + 1, 1);
			for (int k = 0; k < degree + 1; ++k) {
				for (int i = 0; i < degree + 1; ++i) {
					double leftsum = 0, rightsum = 0;
					for (int j = 0; j < x.sizer(); ++j) {
						leftsum += Math.pow(x.cell(j, 0), k + i);
						rightsum += Math.pow(x.cell(j, 0), k) * f.cell(j, 0);
					}
					left.cell(k, i, leftsum);
					right.cell(k, 0, rightsum);
				}
			}
			LUMethod lu = new LUMethod(left);
			super.line = lu.solve(right);
			super.line.transpose();
		}
	}
}
