import java.util.HashMap;
import java.util.Map;


public class Main {
	
	static private class Package {
		private Map<String, Object> parameters;
		
		public Package() {
			parameters = new HashMap<String, Object>();
		}
		
		public void set(String _key, Object _value) {
			parameters.put(_key, _value);
		}
		
		public Object get(String _key) {
			return parameters.containsKey(_key) ? parameters.get(_key) : null;
		}
	}
	
	interface Module {
		public Package Request(Package p);
	}
	
	static private class Calc implements Module {
		
		@Override
		public Package Request(Package p) {
			Package pack = new Package();
			double d[] = {1, 2, 3};
			pack.set("[double][d]", d.clone());
			d[0] = 5;
			return pack;
		}
		
	}
	
	static private class PrintCalcPack implements Module {
		
		@Override
		public Package Request(Package p) {
			double d[];
			d = (double[])p.get("[double][d]");
			if (d != null) {
				int l = d.length;
				System.out.print("Array d = \n\t");
				for (int i = 0; i < l; i++) {
					System.out.print(d[i]);
					System.out.print(" ");
				}
				System.out.println();
			}
			return null;
		}
		
	}

	public static void main(String[] args) {
		Package calculatePack = new Calc().Request(null);
		new PrintCalcPack().Request(calculatePack);
	}

}
