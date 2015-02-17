class Main {

	public static void main(String[] args) {

	}

	private static float[] get_mass(float a, float b) {
		float[] rez = new float[11];
		float h = (b - a) / 10;
		for (int i = 0; i < 11; ++i) {
			rez[i] = a + i * h;
		}
		return rez;
	}

	private static float func(float x) {
		// set function #9
		return x - (float) Math.cos((double) x);
	}

	private static float L_1(float a, float b, float x) {
		return func(a) * (x - b) / (a - b) + func(b) * (x - a) / (b - a);
	}

	private static float L_2(float a, float b, float c, float x) {
		return func(a) * (x - b) * (x - c) / (a - b) / (a - c)
				+ func(b) * (x - a) * (x - c) / (b - a) / (b - c)
				+ func(c) * (x - a) * (x - b) / (c - a) / (c - b);
	}
}
