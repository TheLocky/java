import core.*;

class Main {

	public static void lab13test() {
		Out.msgln("Векторы:");
		Matrix2D A = new Matrix2D(1, 5);
		double vec[] = {0, 1, 2, 4, 0};
		A.setrow(vec, 0);
		A.print();
		Matrix2D B = new Matrix2D(1, 5);
		double vec2[] = {4, 2, 4, 2, 4};
		B.setrow(vec2, 0);
		B.print();
		Out.msgln("Аппроксимация");
		Approximation approx = new Approximation(A, B, 3);
		approx.print();
	}

	public static void lab12test() {
		Out.msgln("\nВекторы:");
		Matrix2D A = new Matrix2D(1, 4);
		double vec[] = {0, 1, 2, 4};
		A.setrow(vec, 0);
		A.print();
		A.transpose();
		Matrix2D B = new Matrix2D(1, 4);
		double vec2[] = {4, 2, 3, 0};
		B.setrow(vec2, 0);
		B.print();
		B.transpose();
		Out.msgln("Интерполяция");
		Interpolation inter = new Interpolation(A, B);
		inter.print();
	}

	public static void lab11test() {
		Matrix2D A = new Matrix2D(10);
		A.setrandom(100, -50);
		Out.msgln("Матрица:\n");
		A.print();
		LUMethod lum = new LUMethod(A);
		Matrix2D F = new Matrix2D(10, 1);
		F.setrandom(10, 25);
		Out.msgln("\nВектор значений:\n");
		F.print();
		Matrix2D X = lum.solve(F);
		Out.msgln("\nВектор корней:\n");
		X.print();
		Matrix2D R = Matrix2D.sub(Matrix2D.mul(A, X), F, true);
		Out.msgln("\nВектор невязки:\n");
		R.print(20);
	}

	public static void main(String[] args) {
		lab11test();
		lab12test();
		Out.ln();
		lab13test();
	}
}