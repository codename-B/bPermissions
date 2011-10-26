package de.bananaco.permissions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperPermissionHandler {

	public static void setupPlayer(Player p, List<String> nodes, JavaPlugin plugin) {
		unsetupPlayer(p, plugin);
		/*
		 * Test fix for mChat - dunno wtf is up
		 */
		for (String node : nodes) {
			String tNode = (node.startsWith("^")?node.replace("^",""):node);
		PermissionAttachment att = p.addAttachment(plugin);
			if (node.startsWith("^")) {
				att.setPermission(tNode, false);
			} else {
				att.setPermission(tNode, true);
			}
			//System.out.println(tNode+":"+p.hasPermission(tNode));
		}
	}

	public static void unsetupPlayer(Player p, JavaPlugin plugin) {
		Set<PermissionAttachmentInfo> pAtt = p.getEffectivePermissions();
		if (pAtt != null) {
			for (PermissionAttachmentInfo pInfo : pAtt) {
				PermissionAttachment pa = pInfo.getAttachment();
				if(pa != null) {
				Map<String, Boolean> paNodes = pa.getPermissions();
				if (paNodes != null) {
					Set<String> keys = paNodes.keySet();
					for (String key : keys) {
						pa.unsetPermission(key);
					}
				}
			}
			}
			p.recalculatePermissions();
		}
	}

}
