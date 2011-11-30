package de.bananaco.permissions;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.debug.Debugger;

public class SuperPermissionHandler {

	private static JavaPlugin plugin;
	public static void setPlugin(JavaPlugin pl) {
		plugin = pl;
	}
	
	public static void setupPlayer(Player p) {
		List<String> nodes = Permissions.getWorldPermissionsManager().getPermissionSet(p.getWorld()).getPlayerNodes(p);
		setupPlayer(p, nodes);
	}
	
	public static void setupPlayer(Player p, List<String> nodes) {
		long start = System.currentTimeMillis();
		unsetupPlayer(p, plugin);
		PermissionAttachment att = p.addAttachment(plugin);

		for (String node : nodes) {
			String tNode = (node.startsWith("^") ? node.replace("^", "") : node);
			
			att.unsetPermission(tNode);
			
			if (node.startsWith("^")) {
				att.setPermission(tNode, false);
			} else {
				att.setPermission(tNode, true);
			}
		}
		
		Permissions.getWorldPermissionsManager().correct(p);
		
		long finish = System.currentTimeMillis() - start;
		Debugger.getDebugger().log(
				"Setup player:" + p.getName() + " took " + finish + "ms");
	}

	public static void unsetupPlayer(Player p, JavaPlugin plugin) {
		Set<PermissionAttachmentInfo> pAtt = p.getEffectivePermissions();
		if (pAtt != null) {
			for (PermissionAttachmentInfo pInfo : pAtt) {
				if (pInfo.getAttachment() != null
						&& pInfo.getAttachment().getPlugin() != null
						&& pInfo.getAttachment().getPlugin().getDescription() != null
						&& pInfo.getAttachment().getPlugin().getDescription()
								.getName().equalsIgnoreCase("bPermissions")) {
					pInfo.getAttachment()
							.unsetPermission(pInfo.getPermission());
					pInfo.getAttachment().remove();
				}
			}
		}
		p.recalculatePermissions();
	}

}
