package de.bananaco.bpermissions.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class wraps around the Calculable
 * and effectively does the same job, just with the added
 * benefit of calculating a Map<String, Boolean> for the Set<Permission>
 * for direct access and faster permission node checking.
 * 
 * Currently only User extends MapCalculable and Group extends Calculable
 * There is no need for direct per-group permission checking
 */
public abstract class MapCalculable extends Calculable {

	public MapCalculable(String name, Set<String> groups,
			Set<Permission> permissions, String world) {
		super(name, groups, permissions, world);
	}
	
	boolean dirty = true;

	private final Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	
	/**
	 * Return the calculated map
	 * The map will be blank unless calculateEffectivePermissions has been called
	 * which admittedly is very likely to have happened.
	 * 
	 * @return Map<String, Boolean>
	 */
	public Map<String, Boolean> getMappedPermissions() {
		if(isDirty())
			try {
				calculateEffectivePermissions();
			} catch (RecursiveGroupException e) {
				e.printStackTrace();
			}
		return permissions;
	}
	
	//@Override
	protected void calculateMappedPermissions() throws RecursiveGroupException {
		if(!isDirty())
			return;
		super.calculateEffectivePermissions();
		permissions.clear();
		for(Permission perm : getEffectivePermissions()) {
			permissions.put(perm.nameLowerCase(), perm.isTrue());
		}
		this.calculateEffectiveMeta();
		dirty = false;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
}
