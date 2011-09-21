package de.bananaco.permissions.worlds;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class HasPermission {
	
	public static boolean has(Player player, String node) {
		Set<PermissionAttachmentInfo> pf = player.getEffectivePermissions();
		for(PermissionAttachmentInfo pa : pf) {
			String permission = pa.getPermission();
			boolean result = pa.getValue();
			if(permission.equalsIgnoreCase(node))
				return result;
		}
		
		return player.isOp();
	}

}
