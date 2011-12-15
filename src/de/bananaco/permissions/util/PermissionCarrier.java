package de.bananaco.permissions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public abstract class PermissionCarrier extends MetaData {

	private final Set<Permission> permissions;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PermissionCarrier(Set<Permission> permissions) {
		if (permissions == null)
			permissions = new HashSet();
		this.permissions = permissions;
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
		List<String> groups = new ArrayList<String>(getPermissionsAsString());
		Collections.sort(groups,
                new Comparator<String>()
                {
                    public int compare(String f1, String f2)
                    {
                        return f1.compareTo(f2);
                    }        
                });
		return groups;
	}

}
