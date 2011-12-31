package de.bananaco.permissions.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bananaco.permissions.api.util.Calculable;
import de.bananaco.permissions.api.util.CalculableType;
import de.bananaco.permissions.api.util.Permission;

/**
 * The Group object extends Calculable which allows recursive inheritance of unlimited depth.
 * This is the new bPermissions, and it is awesome.
 */
public class Group extends Calculable {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Group(String name, List<String> groups, Set<Permission> permissions,
			String world) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, world);
	}

	@Override
	public CalculableType getType() {
		return CalculableType.GROUP;
	}

}
