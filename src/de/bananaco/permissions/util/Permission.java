package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.List;
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
				if (perm.startsWith("^"))
					permissions.add(new Permission(perm.replace("^", ""), false));
				else
					permissions.add(new Permission(perm, true));

		return permissions;
	}
	
	private final boolean isTrue;
	private final String name;
	
	Permission(String name, boolean isTrue) {
		this.name = name;
		this.isTrue = isTrue;
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
