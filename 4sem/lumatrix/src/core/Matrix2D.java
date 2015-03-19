package core;

import java.util.Random;
import java.util.Scanner;

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
		}
		rows = r;
		cols = c;
		det_type();
	}

	public Matrix2D(int rc) {
		if (rc > 0) {
			M = new double[rc][rc];
		}
		rows = rc;
		cols = rc;
		det_type();
	}

	public Matrix2D(Matrix2D src) {
		this.rows = src.rows;
		this.cols = src.cols;
		this.type = src.type;
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

	public void resize(int r, int c) {
		int minr = Math.min(rows, r);
		int minc = Math.min(cols, c);
		double newM[][] = new double[r][c];
		for (int i = 0; i < minr; ++i) {
			System.arraycopy(M[i], 0, newM[i], 0, minc);
		}
		M = newM;
		rows = r;
		cols = c;
		det_type();
	}

	public void resize(int rc) {
		this.resize(rc, rc);
	}

	public boolean is_cube() {
		return (rows == cols);
	}

	public MatrixType get_type() {
		return type;
	}

	private void det_type() {
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

	private String getspace(int len) {
		String rez = "";
		for (int i = 0; i < len; ++i) {
			rez += " ";
		}
		return rez;
	}

	public void print(int num) {
		String rez = "";
		String form = String.format("%s%d%s", "%.", num, "f ");
		int maxlen = 0;
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				String cl = String.format(form, M[i][j]);
				int len = cl.length() - 4;
				if (len > maxlen)
					maxlen = len;
				String prev = String.format("{%d}", len);
				rez += prev + cl;
			}
			rez += "\n";
		}
		for (int i = 1; i <= maxlen; ++i) {
			String find = String.format("\\{%d\\}", i);
			rez = rez.replaceAll(find, getspace(maxlen - i));
		}
		Out.msg(rez);
	}

	public void print() {
		print(2);
	}
	
	public void printtofile(String filename, int num) {
		Out.setFile(filename);
		print(num);
	}
	
	public void printtofile(String filename) {
		printtofile(filename, 2);
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

	public void setfrommatrix(Matrix2D src, int srbeg, int scbeg,
			int drbeg, int dcbeg, int rcount, int ccount) {
		int srend = srbeg + rcount;
		for (int si = srbeg, di = drbeg; si < srend; ++si, ++di) {
			System.arraycopy(src.M[si], scbeg, this.M[di], dcbeg, ccount);
		}
	}
	
	public void setfromscanner(Scanner scan) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				if (!scan.hasNextDouble()) return;
				M[i][j] = scan.nextDouble();
			}
		}				
	}
	
	public void setrow(double [] line, int num) {
		int str = Math.min(Math.max(num, 0), rows);
		int count = Math.min(cols, line.length);
		System.arraycopy(line, 0, M[str], 0, count);
	}
	
	public void setcol(double [] line, int num) {
		this.transpose();
		this.setrow(line, num);
		this.transpose();
	}
	
	public double[] getrow(int num) {
		int str = Math.min(Math.max(num, 0), rows);
		return M[str];
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
		
		det_type();
	}
	
	public void powelems(double deg) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				M[i][j] = Math.pow(M[i][j], deg);
			}
		}
	}
	
	public void mulelems(double mn) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				M[i][j] *= mn;
			}
		}
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
				if (abs)
					rez.M[i][j] = Math.abs(rez.M[i][j]);
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