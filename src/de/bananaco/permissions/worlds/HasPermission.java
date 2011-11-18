package de.bananaco.permissions.worlds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public class HasPermission {
	
	private static Map<String, Boolean> perms = new HashMap<String, Boolean>();
	
	public static void clearCache() {
		perms.clear();
	}

	private static boolean contains(HashSet<String> hnodes, String node) {
		if (hnodes.contains("^" + node))
			return true;
		if (hnodes.contains(node))
			return true;
		return false;
	}

	private static boolean get(HashSet<String> hnodes, String node) {
		if (hnodes.contains("^" + node))
			return false;
		if (hnodes.contains(node))
			return true;
		return false;
	}
	
	public static boolean has(Player player, String node) {
		return has(player.getName(), player.getWorld().getName(), node);
	}

	public static boolean has(String player, String world, String node) {
		player = PermissionClass.caseCheck(player);
		
		node = node.toLowerCase();
		
		String pString = player+"."+world+"."+node;
		boolean perm = false;
		if(perms.containsKey(pString))
			perm = perms.get(pString);
		else {
			perm = hasPerm(player, world, node);
			perms.put(pString, perm);
		}
		return perm;
	}
	
	private static boolean hasPerm(String player, String world, String node) {
		PermissionSet set = Permissions.getWorldPermissionsManager().getPermissionSet(world);
		if (set == null)
			return false;
		List<String> nodes = set.getPlayerNodes(player);
		HashSet<String> hnodes = new HashSet<String>();
		for(String n : nodes) {
			n = n.toLowerCase();
			hnodes.add(n);
		}
		if (contains(hnodes, node))
			return get(hnodes, node);
		int index = node.lastIndexOf('.');
		while (index >= 0) {
			node = node.substring(0, index);
			String wildcard = node + ".*";
			if (contains(hnodes, wildcard))
				return get(hnodes, wildcard);
			index = node.lastIndexOf('.');
		}
		if (contains(hnodes, "*"))
			return get(hnodes, "*");
		hnodes.clear();
		return false;
	}

}
