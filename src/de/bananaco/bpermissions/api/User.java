package de.bananaco.bpermissions.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MapCalculable;
import de.bananaco.bpermissions.api.util.Permission;

/**
 * User extends MapCalculable to make permission checks in the HasPermission class considerably faster.
 * A slight increase in memory usage for a dramatic increase in speed is definately a worthwhile trade-off.
 */
public class User extends MapCalculable {
	
	Map<String, Boolean> cache;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public User(String name, List<String> groups, Set<Permission> permissions,
			String world) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, world);
		cache = new HashMap();
	}

	@Override
	public CalculableType getType() {
		return CalculableType.USER;
	}
	
	public boolean hasPermission(String node) {
		node = node.toLowerCase();
		
		if(cache.containsKey(node))
			return cache.get(node);
		
		boolean allowed = internalHasPermission(node);
		
		cache.put(node, allowed);
		
		return allowed;
	}
	
	private boolean internalHasPermission(String node) {
		Map<String, Boolean> perms = getMappedPermissions();
		
		if(perms.containsKey(node))
			return perms.get(node);
		
		String permission = node;
		int index = permission.lastIndexOf('.');
		while (index >= 0) {
			permission = permission.substring(0, index);
			String wildcard = permission + ".*";
			if(perms.containsKey(wildcard))
				return perms.get(wildcard);
			index = permission.lastIndexOf('.');
		}
		if(perms.containsKey("*"))
			return perms.get("*");
		return false;
	}

}
