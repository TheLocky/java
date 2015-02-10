package main;
import core.Matrix2D;
import core.Out;
class Main {
	public static void main(String[] args) {
		Matrix2D tmp = new Matrix2D(2);
		tmp.cell(0, 0, 1.2);
		tmp.cell(1, 1, -3.5);
		Out.msg("������� �:");
		tmp.print();
		Matrix2D row = tmp.col(0);
		Out.msg("������� B:");
		row.print();
		Out.msg("������� ��������...");
		Matrix2D c = Matrix2D.mul(tmp, row);
		c.print();
	}
}