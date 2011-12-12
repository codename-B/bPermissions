package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;

public class User extends Calculable {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public User(String name, List<String> groups, Set<Permission> permissions,
			WorldPermissions parent) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, parent);
	}

}
