package core;
import core.Out;
public class Matrix2D {
	double M[][]; 
	int rows;
	int cols;
	
	public Matrix2D(int r, int c) {
		M = new double[r][c];
		rows = r;
		cols = c;
	}
	
	public Matrix2D(int rc) {
		M = new double[rc][rc];
		rows = rc;
		cols = rc;
	}
	
	public int sizer() {
		return rows;
	}
	
	public int sizec() {
		return cols;
	}
	
	public double cell(int r, int c) {
		return M[r][c];
	}
	
	public void cell(int r, int c, double val) {
		M[r][c] = val;
	}
	
	public Matrix2D row(int r) {
		Matrix2D rez = new Matrix2D(1, cols);
		for (int i = 0; i < cols; ++i) {
			rez.cell(0, i, M[r][i]);
		}
		return rez;
	}
	
	public Matrix2D col(int c) {
		Matrix2D rez = new Matrix2D(rows, 1);
		for (int i = 0; i < rows; ++i) {
			rez.cell(i, 0, M[i][c]);
		}
		return rez;
	}
	
	public static Matrix2D mul(Matrix2D a, Matrix2D b) {
		if (a.sizec() != b.sizer()) {
			Out.error("Умножение невозможно");
			return null;
		}
		Matrix2D rez = new Matrix2D(a.sizer(),b.sizec());
		for (int i = 0; i < a.sizer(); ++i) {
			for (int j = 0; j < b.sizec(); ++j) {
				double sum = 0;
				for (int k = 0; k < a.sizec(); ++k) {
					sum += a.cell(i, k) * b.cell(k, j);
				}
				rez.cell(i, j, sum);
			}
		}
		return rez;
	}

	public void print() {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				System.out.print(M[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
}