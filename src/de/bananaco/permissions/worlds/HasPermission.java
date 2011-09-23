package de.bananaco.permissions.worlds;

import java.util.HashSet;
import java.util.List;
import org.bukkit.entity.Player;
import de.bananaco.permissions.Permissions;

public class HasPermission {
	
	public static boolean has(Player player, String node) {
		List<String> nodes = Permissions.getWorldPermissionsManager().getPermissionSet(player.getWorld()).getPlayerNodes(player);
		HashSet<String> hnodes = new HashSet<String>();
		hnodes.addAll(nodes);
		if(contains(hnodes, node))
			return get(hnodes,node);
        int index = node.lastIndexOf('.');
        while (index >= 0) {
            node = node.substring(0, index);
            String wildcard = node + ".*";
            if(contains(hnodes, wildcard))
            	return get(hnodes, wildcard);
            index = node.lastIndexOf('.');
        }
        
        hnodes.clear();
		return player.isOp();
	}
	
	private static boolean contains(HashSet<String> hnodes, String node) {
		if(hnodes.contains("^"+node))
			return true;
		if(hnodes.contains(node))
			return true;
		return false;
	}
	
	private static boolean get(HashSet<String> hnodes, String node) {
		if(hnodes.contains("^"+node))
			return false;
		if(hnodes.contains(node))
			return true;
		return false;
	}

}
