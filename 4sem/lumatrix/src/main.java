import core.*;

class Main {

	public static void lab13test() {
		Matrix2D A = new Matrix2D(5, 1);
		A.cell(0, 0, 0);
		A.cell(1, 0, 1);
		A.cell(2, 0, 2);
		A.cell(3, 0, 3);
		A.cell(3, 0, 4);
		Matrix2D B = new Matrix2D(5, 1);
		B.cell(0, 0, 4);
		B.cell(1, 0, 2);
		B.cell(2, 0, 4);
		B.cell(3, 0, 2);
		B.cell(4, 0, 4);
		Approximation approx = new Approximation(A, B, 3);
		approx.getLine().print();
		Out.msg(String.format("%f", approx.getF(2)));
	}

	public static void lab12test() {
		Matrix2D A = new Matrix2D(4, 1);
		A.cell(0, 0, 0);
		A.cell(1, 0, 1);
		A.cell(2, 0, 2);
		A.cell(3, 0, 4);
		Matrix2D B = new Matrix2D(4, 1);
		B.cell(0, 0, 4);
		B.cell(1, 0, 2);
		B.cell(2, 0, 3);
		B.cell(3, 0, 0);
		Interpolation inter = new Interpolation(A, B);
		Out.msg(String.format("%f", inter.getF(2)));
	}

	public static void lab11test() {
		Matrix2D A = new Matrix2D(10);
		A.setrandom(100, -50);
		Out.msg("Матрица:\n");
		A.print();
		LUMethod lum = new LUMethod(A);
		Matrix2D F = new Matrix2D(10, 1);
		F.setrandom(10, 25);
		Out.msg("\nВектор значений:\n");
		F.print();
		Matrix2D X = lum.solve(F);
		Out.msg("\nВектор корней:\n");
		X.print();
		Matrix2D R = Matrix2D.sub(Matrix2D.mul(A, X), F, true);
		Out.msg("\nВектор невязки:\n");
		R.print(20);
	}

	public static void main(String[] args) {
		// lab11test();
		// lab12test();
		lab13test();
	}
}