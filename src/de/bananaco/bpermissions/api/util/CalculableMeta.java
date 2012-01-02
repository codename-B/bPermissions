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
	public void calculateEffectiveMeta() throws RecursiveGroupException {
		try {
		effectiveMeta.clear();
		for (Group group : getGroups()) {
			group.calculateEffectiveMeta();
			Map<String, String> meta = group.getEffectiveMeta();
			for(String key : meta.keySet()) {
				effectiveMeta.put(key, meta.get(key));
			}
		}
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
	
	public String getEffectiveValue(String key) {
		return effectiveMeta.get(key);
	}
	
	public boolean containsEffectiveValue(String key) {
		return effectiveMeta.containsKey(key);
	}

}
