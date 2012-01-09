package de.bananaco.bpermissions.api.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class acts as the MetaData store for direct interaction
 * with prefix/suffix get/set etcetera.
 * 
 * The InfoReader api uses this class to make subsequent prefix/suffix calls
 * considerably faster after initial calculation.
 * 
 * The sorting API has been moved to MetaData as that's where it fits best
 */
public class MetaData {
	
	private final Map<String, String> values = new HashMap<String, String>();
	
	private final static Comparator<String> compar = new Comparator<String>()
	{
		public int compare(String f1, String f2) {
			int i = (f1.length() > f2.length()) ? f2.length() : f1.length();
			for (int n = 0; n < i - 1; n++) {
				String a = f1.substring(n, n + 1);
				String b = f2.substring(n, n + 1);
				if (a.compareTo(b) != 0)
					return a.compareTo(b);
			}
			return 0;
		}
	};
	
	private final static Comparator<Calculable> comparCalc = new Comparator<Calculable>()
    {
        public int compare(Calculable f1, Calculable f2)
        {
            return f1.getName().compareTo(f2.getName());
        }        
    };
	
	private static boolean sort = true;
	
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
	
	/**
	 * Performance will be improved by using statics for this
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public static void sort(List<?> data) {
		if(data == null)
			return;
		if(data.size() == 0)
			return;
		if(!sort)
			return;
		if(data.get(0) instanceof String) {
			List<String> d = (List<String>) data;
			Collections.sort(d, compar);
		} else if(data.get(0) instanceof Calculable) {
			List<Calculable> d = (List<Calculable>) data;
			Collections.sort(d, comparCalc);
		} else {
			return;
		}
	}
	
	/**
	 * Used to enable/disable sorting globally
	 * @param sorting
	 */
	public static void setSorting(boolean sorting) {
		sort = sorting;
	}
	
	/**
	 * Shows wether sorting is enabled or not
	 * @return boolean
	 */
	public boolean getSorting() {
		return sort;
	}
	
	/**
	 * Returns the priority of the metadata
	 * @return priority
	 */
	public int getPriority() {
		int priority = 0;
		// Do some parsing if possible
		try {
			if(this.getValue("priority") != null) {
				int p = Integer.parseInt(this.getValue("priority"));
				priority = p;
			}
		} catch (Exception e) {}
		// And return the priority (will usually be 0)
		return priority;
	}

}
