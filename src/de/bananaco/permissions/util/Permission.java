package de.bananaco.permissions.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Permission {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set<Permission> loadFromString(List<String> listPerms) {
		Set<Permission> permissions = new HashSet();

		if (listPerms != null)
			for (String perm : listPerms)
				if (perm.startsWith("^"))
					permissions.add(new Permission(perm, false));
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

	public int hashCode() {
		return name.hashCode();
	}

	public boolean isTrue() {
		return isTrue;
	}

	public String name() {
		return name;
	}

	public String toString() {
		return name + ":" + isTrue;
	}
}
