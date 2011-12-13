package de.bananaco.permissions.util;

import java.util.HashMap;
import java.util.Map;

public class MetaData {
	
	private final Map<String, String> values = new HashMap<String, String>();

	public String getValue(String key) {
		if(values.containsKey(key))
			return values.get(key);
		return "";
	}
	
	public boolean contains(String key) {
		return values.containsKey(key);
	}

	public void setValue(String key, String value) {
		values.put(key, value);
	}

	public Map<String, String> getMeta() {
		return values;
	}
	
	public void clearValues() {
		values.clear();
	}

}
