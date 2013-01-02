package de.bananaco.bpermissions.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * This class stores a String and a Boolean.
 * The hashCode returns the same for a true and a false String, allowing for only storing
 * one in a HashSet which makes for very useful Permission calculation.
 */
public class Permission {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * Used to load a Set<Permission> from a List<String>
	 * Particularly useful in de-serialisation.
	 * @param listPerms
	 * @return Set<Permission>
	 */
	public static Set<Permission> loadFromString(List<String> listPerms) {
		Set<Permission> permissions = new HashSet();
		if (listPerms != null)
			for (String perm : listPerms)
				permissions.add(loadFromString(perm));
					return permissions;
	}

	/**
	 * Only use in single-set scenarios, use loadFromString(List<String>) in most cases.
	 * @param perm
	 * @return Permission
	 */
	public static Permission loadFromString(String perm) {
		if(perm.startsWith("^"))
			return new Permission(perm.replace("^", ""), false);
		if(perm.startsWith("-"))
			return new Permission(perm.replace("-", ""), false);
		else
			return new Permission(perm, true);
	}

	public static Permission loadWithChildren(String perm, boolean value, Map<String, Boolean> children) {
		// error checking
		if(perm == null || children == null) {
			return null;
		}
		return new Permission(perm, value, children);
	}

	public static Map<String, Boolean> reverse(Map<String, Boolean> perms) {
		// reverse everything for negatives
		Map<String, Boolean> newChildren = new HashMap<String, Boolean>();
		for(String key : perms.keySet()) {
			newChildren.put(key, !perms.get(key));
		}
		// cleanup and reference change
		perms.clear();
		perms = newChildren;

		return perms;
	}

	private final boolean isTrue;
	private final String name;
	private final Map<String, Boolean> children;

	Permission(String name, boolean isTrue) {
		this.name = name;
		this.isTrue = isTrue;
		this.children = null;
	}

	Permission(String name, boolean isTrue, Map<String, Boolean> children) {
		this.name = name;
		this.isTrue = isTrue;
		// create a new Map<String, Boolean> for safety
		this.children = new HashMap<String, Boolean>(children);
	}

	/**
	 * Returns the children of this permission, if any exist. Or simply an empty Map
	 * @return children Map<String, Boolean>
	 */
	public Map<String, Boolean> getChildren() {
		if(children == null) {
			return new HashMap<String, Boolean>();
		} else {
			return new HashMap<String, Boolean>(children);
		}
	}

	/**
	 * Returns if the permission is true of false
	 * @return boolean
	 */
	public boolean isTrue() {
		return isTrue;
	}

	/**
	 * Returns the name of the Permission, cased as it was input
	 * @return String
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the name of the Permission (lowercased)
	 * @return String
	 */
	public String nameLowerCase() {
		return name.toLowerCase();
	}

	@Override
	public String toString() {
		return name + ":" + isTrue;
	}

	@Override
	public int hashCode() {
		return nameLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object.hashCode() == this.hashCode();
	}
}
