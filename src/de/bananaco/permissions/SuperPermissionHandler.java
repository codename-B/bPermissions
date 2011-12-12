package de.bananaco.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.util.Permission;

public class SuperPermissionHandler {

	private static Map<String, String> players = new HashMap<String, String>();

	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin pl) {
		plugin = pl;
	}

	public static synchronized void setupPlayer(Player p) {
		Set<Permission> nodes = Permissions.getWorldPermissionsManager()
				.getPermissionSet(p.getWorld())
				.getPlayerPermissions(p.getName());
		setupPlayer(p, nodes);
	}

	public static synchronized void setupPlayer(Player p, Set<Permission> nodes) {
		long start = System.currentTimeMillis();
		unsetupPlayer(p);
		PermissionAttachment att = p.addAttachment(plugin);

		for (Permission node : nodes)
			att.setPermission(node.name(), node.isTrue());

		att.setPermission("world."+p.getWorld().getName(), true);
				
		p.recalculatePermissions();
		players.put(p.getName(), p.getWorld().getName());
		long finish = System.currentTimeMillis() - start;
		Debugger.getDebugger().log(
				"Setup player:" + p.getName() + " took " + finish + "ms");
	}

	public static synchronized void setupPlayerIfChangedWorlds(Player p) {
		if (players.containsKey(p.getName())) {
			String world = players.get(p.getName());
			if (world.equals(p.getWorld().getName()) && p.hasPermission("world."+p.getWorld().getName()))
				return;
		}
		setupPlayer(p);
	}

	public static synchronized void unsetupPlayer(Player p) {
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
			p.recalculatePermissions();
		}
	}

}
