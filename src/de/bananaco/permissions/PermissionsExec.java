package de.bananaco.permissions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bananaco.permissions.interfaces.PermissionSet;

public class PermissionsExec {
	private final Permissions plugin;

	public PermissionsExec(Permissions plugin) {
		this.plugin = plugin;
	}

	private String checkPlayer(String player) {
		Player players = plugin.getServer().getPlayer(player);
		if (players != null)
			return players.getName();
		return player;
	}

	public boolean exec(CommandSender sender, String[] args, String world) {
		if(args.length<2)
			return false;
		String perm = "bPermissions.admin." + args[1];
		if (sender instanceof Player) {
			if (!(sender.hasPermission(perm) || sender
					.hasPermission("bPermissions.admin"))) {
				sender.sendMessage("You don't have permission.");
				return false;
			}
		}
		World w = plugin.getServer().getWorld(world);
		if (w == null) {
			sender.sendMessage("That world does not exist.");
			return false;
		}
		if (args.length >= 4 && args[1].equalsIgnoreCase(plugin.addGroup)) {
			/*
			 * ADDGROUP
			 */
			String player = args[3];
			String group = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addGroup(player, group);
			sender.sendMessage("Added group:" + group + " to player:" + player);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.addGroupToGroup)) {
			/*
			 * ADDGROUPTOGROUP
			 */
			String main = args[3];
			String group = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addGroupToGroup(main, group);
			sender.sendMessage("Added group:" + group + " to group:" + main);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.setGroup)) {
			/*
			 * SETGROUP
			 */
			String player = args[3];
			String group = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.setGroup(player, group);
			sender.sendMessage("Set player:" + player + " to group:" + group);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removeGroup)) {
			/*
			 * REMOVEGROUP
			 */
			String player = args[3];
			String group = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removeGroup(player, group);
			sender.sendMessage("Removed group:" + group + " from player:"
					+ player);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removeGroupFromGroup)) {
			/*
			 * REMOVEGROUPGROUP
			 */
			String main = args[3];
			String group = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removeGroupFromGroup(main, group);
			sender.sendMessage("Removed group:" + group + " from group:" + main);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listGroup)) {
			/*
			 * LISTGROUP
			 */
			String player = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> playerGroups = p.getGroups(player);
			String list = Arrays.toString(playerGroups.toArray())
					.replace("[", "").replace("]", "");
			sender.sendMessage(player + " in world:" + world
					+ " has these groups:");
			sender.sendMessage(list);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listGroupGroup)) {
			/*
			 * LISTGROUPGROUP
			 */
			String main = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> groupGroups = p.getGroupGroups(main);
			String list = Arrays.toString(groupGroups.toArray())
					.replace("[", "").replace("]", "");
			sender.sendMessage(main + " in world:" + world
					+ " has these groups:");
			sender.sendMessage(list);
			return true;
		} else if (args.length >= 3 && args[1].equalsIgnoreCase(plugin.inGroup)) {
			/*
			 * INGROUP
			 */
			String group = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> players = p.getAllCachedPlayersWithGroup(group);
			String list = Arrays.toString(players.toArray()).replace("[", "")
					.replace("]", "");
			sender.sendMessage(players.size() + " players found in group:"
					+ group + " for world:" + world);
			sender.sendMessage(list);
			return true;
		} else if (args.length >= 4 && args[1].equalsIgnoreCase(plugin.addNode)) {
			/*
			 * ADDNODE
			 */
			String node = args[2];
			String group = args[3];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addNode(node, group);
			sender.sendMessage("Added node:" + node + " to group:" + group
					+ " in world:" + world);
			return true;
		} else if (args.length >= 4 && args[1].equalsIgnoreCase(plugin.addPlayerNode)) {
			/*
			 * ADDPLAYERNODE
			 */
			String node = args[2];
			String player = args[3];
			player = checkPlayer(player);
			
			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addPlayerNode(node, player);
			sender.sendMessage("Added node:" + node + " to player:" + player
					+ " in world:" + world);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removeNode)) {
			/*
			 * REMOVENODE
			 */
			String node = args[2];
			String group = args[3];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removeNode(node, group);
			sender.sendMessage("Removed node:" + node + " from group:" + group
					+ " in world:" + world);
			return true;
		}  else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removePlayerNode)) {
			/*
			 * REMOVEPLAYERNODE
			 */
			String node = args[2];
			String player = args[3];
			player = checkPlayer(player);
			
			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removePlayerNode(node, player);
			sender.sendMessage("Removed node:" + node + " from player:" + player
					+ " in world:" + world);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listNode)) {
			/*
			 * LISTNODE
			 */
			String group = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> groupNodes = p.getGroupNodes(group);
			String list = Arrays.toString(groupNodes.toArray())
					.replace("[", "").replace("]", "");
			sender.sendMessage(group + " in world:" + world
					+ " has these nodes:");
			sender.sendMessage(list);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listPlayerNode)) {
			/*
			 * LISTPLAYERNODE
			 */
			String player = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> playerNodes = p.getPlayerNodes(player);
			String list = Arrays.toString(playerNodes.toArray())
					.replace("[", "").replace("]", "");
			sender.sendMessage(player + " in world:" + world
					+ " has these nodes:");
			sender.sendMessage(list);
			return true;
		}
		return false;
	}

}
