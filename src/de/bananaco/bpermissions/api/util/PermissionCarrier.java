package de.bananaco.bpermissions.api.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * This class is any object which carries Permissions.
 * 
 * Permissions are stored in a Set<Permission> where Permission is a custom Permission object
 * containing a String and a Boolean rather than the more heavyweight Bukkit object.
 * 
 * Optimisation ftw.
 */
public abstract class PermissionCarrier extends WorldCarrier {

	private final Set<Permission> permissions;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PermissionCarrier(Set<Permission> permissions, String world) {
		super(world);
		if (permissions == null)
			permissions = new HashSet();
		this.permissions = permissions;
	}
	
	/**
	 * Used to easily add a new permission, can override an existing node
	 * @param permission
	 * @param isTrue
	 */
	public void addPermission(String permission, boolean isTrue) {
		permissions.add(new Permission(permission, isTrue));
	}
	
	/**
	 * Used to easily remove a permission, if no entry is there will do nothing
	 * @param permission
	 */
	public void removePermission(String permission) {
		for(Permission p : permissions)
			if(p.name().equalsIgnoreCase(permission))
				permissions.remove(p);
	}

	/**
	 * Return the local permissions for the object
	 * 
	 * @return Set<Permission>
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * Used to return the (re-converted) permissions back to String form for
	 * serialization and anything else involving String use in raw form.
	 * 
	 * @return Set<String>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<String> getPermissionsAsString() {
		Set<String> permissions = new HashSet();
		for (Permission permission : getPermissions()) {
			if (permission.isTrue())
				permissions.add(permission.name());
			else
				permissions.add("^" + permission.name());
		}
		return permissions;
	}
	
	/**
	 * Used to make saving prettier
	 * 
	 * @return
	 */
	public List<String> serialisePermissions() {
		List<String> permissions = new ArrayList<String>(getPermissionsAsString());
		// Using our new static awesomeness
		sort(permissions);
		return permissions;
	}

}
