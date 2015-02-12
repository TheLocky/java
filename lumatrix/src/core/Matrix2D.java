package core;

import java.util.Random;

public class Matrix2D {
	private double M[][];
	private int rows;
	private int cols;
	private MatrixType type;

	public enum MatrixType {
		nullmatrix, val, horvector, vertvector, matrix
	}

	public Matrix2D(int r, int c) {
		if ((r > 0) && (c > 0)) {
			M = new double[r][c];
			rows = r;
			cols = c;
		}
		det_type();
	}

	public Matrix2D(int rc) {
		if (rc > 0) {
			M = new double[rc][rc];
			rows = rc;
			cols = rc;
		}
		det_type();
	}

	public Matrix2D(Matrix2D src) {
		this.rows = src.rows;
		this.cols = src.cols;
		if (src.type != MatrixType.nullmatrix) {
			this.M = new double[this.rows][this.cols];
			for (int i = 0; i < this.rows; ++i) {
				System.arraycopy(src.M[i], 0, this.M[i], 0, src.cols);
			}
		}
	}

	public int sizer() {
		return rows;
	}

	public int sizec() {
		return cols;
	}

	public boolean is_cube() {
		return (rows == cols);
	}

	public MatrixType get_type() {
		return type;
	}

	public void det_type() {
		if ((rows == 0) || (cols == 0))
			type = MatrixType.nullmatrix;
		else if ((rows == 1) && (cols == 1))
			type = MatrixType.val;
		else if ((rows == 1) && (cols > 1))
			type = MatrixType.horvector;
		else if ((rows > 1) && (cols == 1))
			type = MatrixType.vertvector;
		else
			type = MatrixType.matrix;
	}

	public double cell(int r, int c) {
		if (type == MatrixType.nullmatrix)
			return 0;
		return M[r][c];
	}

	public void cell(int r, int c, double val) {
		if (type != MatrixType.nullmatrix)
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
		if (type == MatrixType.nullmatrix)
			return null;
		Matrix2D rez = new Matrix2D(rows, 1);
		for (int i = 0; i < rows; ++i) {
			rez.cell(i, 0, M[i][c]);
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

	public void setnull() {
		if (type == MatrixType.nullmatrix)
			return;
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				M[i][j] = 0;
			}
		}
	}

	public void seteye() {
		if (type == MatrixType.nullmatrix)
			return;
		this.setnull();
		for (int i = 0; i < Math.min(rows, cols); ++i) {
			M[i][i] = 1;
		}
	}

	public void setrandom(int mul, int sub) {
		if (type == MatrixType.nullmatrix)
			return;
		Random rand = new Random();
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				M[i][j] = rand.nextDouble() * mul + sub;
			}
		}
	}

	public void setrandom() {
		this.setrandom(1, 0);
	}

	public void transpose() {
		if ((type == MatrixType.nullmatrix) || (type == MatrixType.val))
			return;

		double N[][] = new double[cols][rows];
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				N[j][i] = M[i][j];
			}
		}
		M = N;

		int tmp = rows;
		rows = cols;
		cols = tmp;
	}

	public static Matrix2D mul(Matrix2D a, Matrix2D b) {
		if ((a.type == MatrixType.nullmatrix)
				|| (b.type == MatrixType.nullmatrix)) {
			Out.error("[MUL] Одна или две матрицы пустые");
			return null;
		}
		if (a.cols != b.rows) {
			Out.error("[MUL] Умножение невозможно");
			return null;
		}
		Matrix2D rez = new Matrix2D(a.rows, b.cols);
		for (int i = 0; i < a.rows; ++i) {
			for (int j = 0; j < b.cols; ++j) {
				double sum = 0;
				for (int k = 0; k < a.cols; ++k) {
					sum += a.M[i][k] * b.M[k][j];
				}
				rez.M[i][j] = sum;
			}
		}
		return rez;
	}
	
	public static Matrix2D sub(Matrix2D a, Matrix2D b) {
		return sub(a, b, false);
	}
	
	public static Matrix2D sub(Matrix2D a, Matrix2D b, boolean abs) {
		if ((a.rows != b.rows) && (a.cols != b.cols)) {
			Out.error("[SUB] Матрицы должны быть одинаковых размеров!");
			return null;
		}
		
		Matrix2D rez = new Matrix2D(a.rows, a.cols);
		for (int i = 0; i < a.rows; ++i) {
			for (int j = 0; j < a.cols; ++j) {
				rez.M[i][j] = a.M[i][j] - b.M[i][j];
				if (abs) rez.M[i][j] = Math.abs(rez.M[i][j]);
			}
		}
		
		return rez;
	}

	public static boolean cmp(Matrix2D a, Matrix2D b, double epsil) {
		if (a.rows != b.rows)
			return false;
		if (a.cols != b.cols)
			return false;
		for (int i = 0; i < a.rows; ++i) {
			for (int j = 0; j < a.cols; ++j) {
				if (Math.abs(a.M[i][j] - b.M[i][j]) > epsil)
					return false;
			}
		}
		return true;
	}

	public static boolean cmp(Matrix2D a, Matrix2D b) {
		return cmp(a, b, 0.0000000001);
	}
}