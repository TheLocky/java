package core;

import core.Matrix2D.MatrixType;

public class Approximation extends Polynom {
	private double xpows[][]; //массив степеней Х-сов 
	private double fs[]; //массив значений
	LUMethod lu;
	Matrix2D right;
	int N, M;
	Interpolation interp;
	
	public Approximation(Matrix2D perems, Matrix2D vals, int degree) {
		start(perems, vals, degree);
	}
	
	private void start(Matrix2D perems, Matrix2D vals, int degree) {
		Matrix2D x = new Matrix2D(perems);
		Matrix2D f = new Matrix2D(vals);
		if (x.get_type() == MatrixType.vertvector) x.transpose();
		if (f.get_type() == MatrixType.vertvector) f.transpose();
		if ((x.get_type() != MatrixType.horvector) || 
				(f.get_type() != MatrixType.horvector)) {
			throw new RuntimeException("В конструктор передан(ы) не вектор(ы)");
		}
		
		N = degree + 1; // Учитываем свободный член
		M = x.sizec();
		
		if (N > M) {
			throw new RuntimeException("Степень многочлена превышает возможную");
		}
		else if (M == N) {
			interp = new Interpolation(perems, vals);
			super.line = new Matrix2D(interp.line);
		}
		else {
			int max = 2 * (N - 1) + 1;
			xpows = new double[M][max];
			
			for (int i = 0; i < M; ++i) {
				double tmp = 1;
				for (int j = 0; j < max; ++j) {
					xpows[i][j] = tmp;
					tmp *= x.cell(0, i);
				}
			}
			
			fs = new double[M];
			System.arraycopy(f.getrow(0), 0, fs, 0, M);
			
			double sum;			
			Matrix2D left = new Matrix2D(N);
			right = new Matrix2D(N, 1);
			for (int i = 0; i < N; ++i) {
				for (int j = 0; j < N; ++j) {
					sum = 0;
					for (int k = 0; k < M; ++k) {
						sum += xpows[k][i+j];
					}
					left.cell(i, j, sum);
				}
				sum = 0;
				for (int k = 0; k < M; ++k) {
					sum += xpows[k][i] * fs[k];
				}
				right.cell(i, 0, sum);
			}
			
			lu = new LUMethod(left);
			super.line = lu.solve(right);
		}
	}
	
}
