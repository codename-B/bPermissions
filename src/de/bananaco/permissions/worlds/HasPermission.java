package de.bananaco.permissions.worlds;

import java.util.Map;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.User;
/**
 * Used for checking offline player permissions
 * And also for anyone that doesn't want to use the SuperPerms api for checking permissions
 */
public class HasPermission {
	
	public static boolean has(Player player, String node) {
		return has(player.getName(), player.getWorld().getName(), node);
	}
	
	public static boolean has(String player, String world, String node) {
		node = node.toLowerCase();
		WorldPermissions wp = Permissions.getWorldPermissionsManager().getPermissionSet(world).getWorldPermissions();
		User user = wp.getUser(player);
		Map<String, Boolean> perms = user.getMappedPermissions();
		
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
