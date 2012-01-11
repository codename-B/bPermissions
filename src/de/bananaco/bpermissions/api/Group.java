package de.bananaco.bpermissions.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MapCalculable;
import de.bananaco.bpermissions.api.util.Permission;

/**
 * The Group object extends Calculable which allows recursive inheritance of unlimited depth.
 * This is the new bPermissions, and it is awesome.
 */
public class Group extends MapCalculable {

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
