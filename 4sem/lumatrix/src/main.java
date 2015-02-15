import core.*;

class Main {
	public static void main(String[] args) {
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
		R.print();
	}
}