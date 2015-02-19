class Main {
	static float maxF_2 = 0.87f;
	static float minF_2 = 0;
	static float maxF_3 = 0;
	static float minF_3 = -0.84f;

	public static void main(String[] args) {
		float[] mass = get_mass(0.5f, 1.0f);
		float x = 0.53f;
		int i = get_index(mass, x);
		float a, b, c;
		String rez;

		a = mass[i];
		b = mass[i + 1];
		float L1 = L_1(a, b, x);
		float R1 = L1 - func(x);
		float NL1 = NL_1(a, b, x);
		// ECHO
		System.out.println("L1 = " + L1);
		if ((R1min(a, b, x) < R1) && (R1 < R1max(a, b, x)))
			rez = "true";
		else
			rez = "false";
		System.out.println("R1 = " + R1);
		System.out.println("min(R1) < R1(x) < max(R1) - " + rez);

		a = mass[i];
		b = mass[i + 1];
		c = mass[i + 2];
		float L2 = L_2(a, b, c, x);
		float R2 = L2 - func(x);
		float NL2 = NL_2(a, b, c, x);

		System.out.println("L2 = " + L2);
		if ((R2min(a, b, c, x) < R2) && (R2 < R2max(a, b, c, x)))
			rez = "true";
		else
			rez = "false";
		System.out.println("R2 = " + R2);
		System.out.println("min(R2) < R2(x) < max(R2) - " + rez);

		System.out.println("NL1 = " + NL1);
		System.out.println("NL2 = " + NL2);
	}

	private static int get_index(float[] mass, float x) {
		for (int i = 0; i < 10; ++i) {
			if (x > mass[i])
				return i;
		}
		return 1;
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

	private static float R1max(float a/* i */, float b/* i+1 */, float x) {
		return maxF_2 * (x - a) * (x - b) / 2;
	}

	private static float R1min(float a/* i-1 */, float b/* i */, float x) {
		return minF_2 * (x - a) * (x - b) / 2;
	}

	private static float R2max(float a/* i */, float b/* i+1 */, float c/* i+1 */,
			float x) {
		return maxF_3 * (x - a) * (x - b) * (x - c) / 6;
	}

	private static float R2min(float a/* i-1 */, float b/* i */, float c/* i+1 */,
			float x) {
		return minF_3 * (x - a) * (x - b) * (x - c) / 2;
	}

	private static float L_1(float a/* i */, float b/* i+1 */, float x) {
		return func(a) * (x - b) / (a - b) + func(b) * (x - a) / (b - a);
	}

	private static float L_2(float a/* i-1 */, float b/* i */, float c/* i+1 */,
			float x) {
		return func(a) * (x - b) * (x - c) / (a - b) / (a - c) + func(b)
				* (x - a) * (x - c) / (b - a) / (b - c) + func(c) * (x - a)
				* (x - b) / (c - a) / (c - b);
	}

	private static float NL_1(float a/* i */, float b/* i+1 */, float x) {
		return func(a) + (func(b) - func(a)) * (x - a);
	}

	private static float NL_2(float a/* i-1 */, float b/* i */, float c/* i+1 */,
			float x) {
		return func(a) + (func(b) - func(a)) * (x - a)
				+ (func(c) - 2 * func(b) + func(a)) * (x - a) * (x - b);
	}
}
