package core;

import core.Matrix2D.MatrixType;

public class LUMethod {
	private Matrix2D L;
	private Matrix2D U;
	private Matrix2D src;

	public LUMethod(Matrix2D matr) throws Error {
		if (!matr.is_cube())
			throw new Error("Матрица не квадратная!");
		src = new Matrix2D(matr);
		
		for (int i = 0; i < src.sizer(); ++i) {
			if (src.cell(i, i) == 0)
				throw new Error("Присутствует 0 на диагонали!");
		}
		
		L = new Matrix2D(src.sizer(), src.sizer());
		U = new Matrix2D(src.sizer(), src.sizer());
		U.seteye();

		for (int i = 0; i < src.sizer(); ++i) {
			L.cell(i, 0, src.cell(i, 0));
			U.cell(0, i, src.cell(0, i) / L.cell(0, 0));
		}

		double sum;
		for (int i = 1; i < src.sizer(); ++i) {
			for (int j = 1; j < src.sizer(); ++j) {
				if (i >= j) {
					sum = 0;
					for (int k = 0; k < j; ++k)
						sum += L.cell(i, k) * U.cell(k, j);
					L.cell(i, j, src.cell(i, j) - sum);
				} else {
					sum = 0;
					for (int k = 0; k < i; ++k)
						sum += L.cell(i, k) * U.cell(k, j);
					U.cell(i, j, (src.cell(i, j) - sum) / L.cell(i, i));
				}
			}
		}
	}
	
	public Matrix2D getL() {
		return new Matrix2D(L);
	}
	
	public Matrix2D getU() {
		return new Matrix2D(U);
	}

	public Matrix2D solve(Matrix2D f) {
		if (f.get_type() != MatrixType.vertvector)
		{
			//Out.error("Значением СЛАУ может быть только вертикальный вектор!");
			return null;
		}
		
		if (f.sizer() != src.sizer())
		{
			//Out.error("Вектор не соответствует размеру матрицы!");
			return null;
		}
		
		Matrix2D y = new Matrix2D(f.sizer(),1);
		for (int i = 0; i < f.sizer(); ++i) {
			double tmp = f.cell(i, 0);
			for (int j = 0; j < i; ++j) {
				tmp -= L.cell(i, j) * y.cell(j, 0);
			}
			tmp /= L.cell(i, i);
			y.cell(i, 0, tmp);
		}
		
		Matrix2D x = new Matrix2D(y.sizer(), 1);
		for (int i = y.sizer() - 1; i >= 0; --i) {
			double tmp = y.cell(i, 0);
			for (int j = y.sizer() - 1; j > i; --j) {
				tmp -= U.cell(i, j) * x.cell(j, 0);
			}
			x.cell(i, 0, tmp);
		}
		
		return x;
	}
}
