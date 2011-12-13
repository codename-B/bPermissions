package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;
/**
 * User extends MapCalculable to make permission checks in the HasPermission class considerably faster.
 * A slight increase in memory usage for a dramatic increase in speed is definately a worthwhile trade-off.
 */
public class User extends MapCalculable {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public User(String name, List<String> groups, Set<Permission> permissions,
			WorldPermissions parent) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, parent);
	}

}
