package de.bananaco.bpermissions.api.util;

import java.util.List;
import java.util.Set;

//import de.bananaco.bpermissions.api.Permission;
@Deprecated
public abstract class Permission {
	@Deprecated
	public static Set<de.bananaco.bpermissions.api.Permission> loadFromString(List<String> listPerms) {
		return de.bananaco.bpermissions.api.Permission.loadFromString(listPerms);
	}
	@Deprecated
	public static de.bananaco.bpermissions.api.Permission loadFromString(String perm) {
		return de.bananaco.bpermissions.api.Permission.loadFromString(perm);
	}
	
	public abstract boolean isTrue();
	
	public abstract String name();
	
	public abstract String nameLowerCase();

}
