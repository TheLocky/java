package core;

import java.util.Scanner;
import java.io.*;

import core.Matrix2D.MatrixType;

public class Interpolation extends Polynom {
	
	public Interpolation(String filename) {
		Scanner scan = null;
		try {
			scan = new Scanner(new BufferedReader(new FileReader(filename)));
		} catch (FileNotFoundException e) {
			Out.error("[Interpolation] Не могу открыть файл");
		}
		if (scan != null) {
			int n = scan.nextInt();
			Matrix2D per, val;
			per = new Matrix2D(n, 1);
			val = new Matrix2D(n, 1);
			per.setfromscanner(scan);
			val.setfromscanner(scan);
			start(per,val);
			scan.close();
		}
	}
	
	public Interpolation(Matrix2D perems, Matrix2D vals) {
		start(perems, vals);
	}
	
	private void start(Matrix2D perems, Matrix2D vals) {
		Matrix2D x = new Matrix2D(perems);
		Matrix2D f = new Matrix2D(vals);
		if (x.get_type() == MatrixType.horvector) x.transpose();
		if (f.get_type() == MatrixType.horvector) f.transpose();
		if ((x.get_type() != MatrixType.vertvector) || 
				(f.get_type() != MatrixType.vertvector)) {
			throw new RuntimeException("В конструктор передан(ы) не вектор(ы)");
		}
		
		Matrix2D tmp = new Matrix2D(x.sizer());
		tmp.setrandom();
		tmp.powelems(0);
		for (int i = 1; i < x.sizer(); ++i) {
			x = new Matrix2D(perems);
			x.powelems(i);
			tmp.setfrommatrix(x, 0, 0, 0, i, x.sizer(), 1);
		}
		LUMethod lu = new LUMethod(tmp);
		super.line = lu.solve(f);
		super.line.transpose();
	}
}
