package de.bananaco.bpermissions.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.Group;

public class CalculableMeta extends GroupCarrier {

	Map<String, String> effectiveMeta;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CalculableMeta(Set<String> groups, Set<Permission> permissions,
			String world) {
		super(groups, permissions, world);
		effectiveMeta = new HashMap();
	}
	
	/**
	 * Used to calculate the total permissions gained by the object
	 * @throws RecursiveGroupException 
	 */
	protected void calculateEffectiveMeta() throws RecursiveGroupException {
		try {
		// Implement meta priorities
		effectiveMeta.clear();
		int lastGroup = -1;
		for (Group group : getGroups()) {
			if(group.getPriority() > lastGroup) {
				lastGroup = group.getPriority();
				group.calculateEffectiveMeta();
				Map<String, String> meta = group.getEffectiveMeta();
				for(String key : meta.keySet()) {
					effectiveMeta.put(key, meta.get(key));
				}
			}
		}
		// Obviously local priority wins every time
		Map<String, String> meta = this.getMeta();
		for(String key : meta.keySet()) {
			effectiveMeta.put(key, meta.get(key));
		}
		} catch (StackOverflowError e) {
			throw new RecursiveGroupException(this);
		}
	}
	
	public Map<String, String> getEffectiveMeta() {
		return effectiveMeta;
	}
	
	/**
	 * Here you go MiracleM4n!
	 * Returns the stored "effective" meta value (calculated with inheritance/priority)
	 * @param key
	 * @return String (or "" if no value)
	 */
	public String getEffectiveValue(String key) {
		if(containsEffectiveValue(key))
			return effectiveMeta.get(key);
		return "";
	}
	
	public boolean containsEffectiveValue(String key) {
		return effectiveMeta.containsKey(key);
	}

}
