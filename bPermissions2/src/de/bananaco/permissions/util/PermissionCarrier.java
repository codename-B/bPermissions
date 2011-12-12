package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.Set;


public abstract class PermissionCarrier {
	
	private final Set<Permission> permissions;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PermissionCarrier(Set<Permission> permissions) {
		if(permissions == null)
			permissions = new HashSet();
		this.permissions = permissions;
	}

	
	/**
	 * Return the local permissions for the object
	 * @return Set<Permission>
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}
	
	/**
	 * Used to return the (re-converted) permissions back to String
	 * form for serialization and anything else involving String use in raw form.
	 * @return Set<String>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<String> getPermissionsAsString() {
		Set<String> permissions = new HashSet();
		for(Permission permission : getPermissions()) {
			if(permission.isTrue())
				permissions.add(permission.name());
			else
				permissions.add("^"+permission.name());
		}
		return permissions;
	}

}
