package de.bananaco.permissions.worlds;

import java.util.Set;

import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.User;

public class HasPermission {
	
	public static boolean has(Player player, String node) {
		return has(player.getName(), player.getWorld().getName(), node);
	}
	
	public static boolean has(String player, String world, String node) {
		node = node.toLowerCase();
		WorldPermissions wp = Permissions.getWorldPermissionsManager().getPermissionSet(world).getWorldPermissions();
		User user = wp.getUser(player);
		Set<Permission> perms = user.getEffectivePermissions();
		
		if(perms.contains(node))
			return get(perms, node);
		
		String permission = node;
		int index = permission.lastIndexOf('.');
		while (index >= 0) {
			permission = permission.substring(0, index);
			String wildcard = permission + ".*";
			if(perms.contains(wildcard))
				return get(perms, wildcard);
			index = permission.lastIndexOf('.');
		}
		return false;
	}
	
	private static boolean get(Set<Permission> perms, String node) {
		for(Permission perm : perms) {
			if(perm.nameLowerCase().equals(node))
				return perm.isTrue();
		}
		return false;
	}

}
