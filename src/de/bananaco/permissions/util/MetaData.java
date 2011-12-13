package de.bananaco.permissions.util;

import java.util.HashMap;
import java.util.Map;
/**
 * This class acts as the MetaData store for direct interaction
 * with prefix/suffix get/set etcetera.
 * 
 * The InfoReader api uses this class to make subsequent prefix/suffix calls
 * considerably faster after initial calculation.
 */
public class MetaData {
	
	private final Map<String, String> values = new HashMap<String, String>();
	
	/**
	 * Return a value stored in the metadata map
	 * @param key
	 * @return String
	 */
	public String getValue(String key) {
		if(values.containsKey(key))
			return values.get(key);
		return "";
	}
	/**
	 * Show if a value is stored in the metadata map
	 * @param key
	 * @return boolean
	 */
	public boolean contains(String key) {
		return values.containsKey(key);
	}
	/**
	 * Set a value in the metadata map
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		values.put(key, value);
	}
	/**
	 * Return the map of metadata, this is a direct reference and not a copy.
	 * @return Map<String,String>
	 */
	public Map<String, String> getMeta() {
		return values;
	}
	/**
	 * Clear the map of metadata.
	 */
	public void clearValues() {
		values.clear();
	}

}
