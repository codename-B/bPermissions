package de.bananaco.permissions.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;
/**
 * This class wraps around the Calculable
 * and effectively does the same job, just with the added
 * benefit of calculating a Map<String, Boolean> for the Set<Permission>
 * for direct access and faster permission node checking.
 * 
 * Currently only User extends MapCalculable and Group extends Calculable
 * There is no need for direct per-group permission checking
 */
public class MapCalculable extends Calculable {

	public MapCalculable(String name, Set<String> groups,
			Set<Permission> permissions, WorldPermissions parent) {
		super(name, groups, permissions, parent);
	}

	private final Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	
	/**
	 * Return the calculated map
	 * The map will be blank unless calculateEffectivePermissions has been called
	 * which admittedly is very likely to have happened.
	 * 
	 * @return Map<String, Boolean>
	 */
	public Map<String, Boolean> getMappedPermissions() {
		return permissions;
	}
	
	@Override
	public void calculateEffectivePermissions() throws RecursiveGroupException {
		super.calculateEffectivePermissions();
		permissions.clear();
		for(Permission perm : getEffectivePermissions()) {
			permissions.put(perm.nameLowerCase(), perm.isTrue());
		}
	}
	
}
