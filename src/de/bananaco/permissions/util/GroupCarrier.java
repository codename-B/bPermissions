package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;

public abstract class GroupCarrier extends PermissionCarrier {

	private final Set<String> groups;
	private final WorldPermissions parent;

	protected GroupCarrier(Set<String> groups, Set<Permission> permissions,
			WorldPermissions parent) {
		super(permissions);
		this.parent = parent;
		this.groups = groups;
	}

	/**
	 * Returns the groups that the object inherits Calculated via the parent
	 * object (this is a fresh object every call)
	 * 
	 * @return Set<Group>
	 */
	@SuppressWarnings("unchecked")
	public Set<Group> getGroups() {
		@SuppressWarnings("rawtypes")
		Set<Group> groups = new HashSet();
		for (String name : this.groups) {
			Group group = parent.getGroup(name);
			if (group != null)
				groups.add(group);
		}
		return groups;
	}

	/**
	 * Returns the groups that the object inherits This is a direct reference to
	 * the object
	 * 
	 * @return
	 */
	public Set<String> getGroupsAsString() {
		return groups;
	}

}
