package de.bananaco.permissions.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;

public class MapCalculable extends Calculable {

	public MapCalculable(String name, Set<String> groups,
			Set<Permission> permissions, WorldPermissions parent) {
		super(name, groups, permissions, parent);
	}

	private final Map<String, Boolean> permissions = new HashMap<String, Boolean>();
	
	public Map<String, Boolean> getMappedPermissions() {
		return permissions;
	}
	
	@Override
	public void calculateEffectivePermissions() {
		super.calculateEffectivePermissions();
		permissions.clear();
		for(Permission perm : getEffectivePermissions()) {
			permissions.put(perm.name(), perm.isTrue());
		}
	}
	
}
