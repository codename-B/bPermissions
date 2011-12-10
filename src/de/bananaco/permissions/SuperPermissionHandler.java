package de.bananaco.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.debug.Debugger;

public class SuperPermissionHandler {

	private static JavaPlugin plugin;
	
	private static Map<String, String> players = new HashMap<String, String>();
	
	public static void setPlugin(JavaPlugin pl) {
		plugin = pl;
	}
	
	public static synchronized void setupPlayerIfChangedWorlds(Player p) {
		if(players.containsKey(p.getName())) {
			String world = players.get(p.getName());
			if(world.equals(p.getWorld().getName()))
				return;
		}
		setupPlayer(p);
	}
	
	public static synchronized void setupPlayer(Player p) {
		List<String> nodes = Permissions.getWorldPermissionsManager().getPermissionSet(p.getWorld()).getPlayerNodes(p);
		setupPlayer(p, nodes);
	}
	
	public static synchronized void setupPlayer(Player p, List<String> nodes) {
		long start = System.currentTimeMillis();
		unsetupPlayer(p, plugin);
		PermissionAttachment att = p.addAttachment(plugin);

		for (String node : nodes) {
			String tNode = (node.startsWith("^") ? node.replace("^", "") : node);
			
			if (node.startsWith("^")) {
				att.setPermission(tNode, false);
			} else {
				att.setPermission(tNode, true);
			}
		}
		players.put(p.getName(), p.getWorld().getName());
		long finish = System.currentTimeMillis() - start;
		Debugger.getDebugger().log(
				"Setup player:" + p.getName() + " took " + finish + "ms");
	}

	public static synchronized void unsetupPlayer(Player p, JavaPlugin plugin) {
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
