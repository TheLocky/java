package core;

import java.util.HashMap;
import java.util.Map;

public class Pack {
	
	private Map<String, Object> parameters;

	public Pack() {
		parameters = new HashMap<String, Object>();
	}

	public void add(String _key, Object _value) {
		parameters.put(_key, _value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String _key) {
		boolean valid = parameters.containsKey(_key);
		return valid ? (T) parameters.get(_key) : null;
	}
	
	public void printAll() {
		System.out.println(this.toString() + " {");
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			System.out.println("\t" + entry.getKey() + " / " + entry.getValue());
		}
		System.out.println("}");
	}
}
