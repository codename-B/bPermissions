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
		if(players != null)
			return players.getName();
		return player;
	}
	public boolean exec(CommandSender sender, String[] args, String world) {

		World w = plugin.getServer().getWorld(world);
		if (w == null) {
			sender.sendMessage("That world does not exist.");
			return false;
		}

		if (args.length >= 4 && args[1].equalsIgnoreCase(plugin.addGroup)) {
			String player = args[3];
			String group = args[2];
			player = checkPlayer(player);
			
			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addGroup(player, group);
			sender.sendMessage("Added group:" + group + " to player:" + player);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removeGroup)) {
			String player = args[3];
			String group = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removeGroup(player, group);
			sender.sendMessage("Removed group:" + group + " from player:"
					+ player);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listGroup)) {
			String player = args[2];
			player = checkPlayer(player);

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> playerGroups = p.getGroups(player);
			String list = Arrays.toString(playerGroups.toArray()).replace("[", "").replace("]", "");
			sender.sendMessage(player + " in world:" + world
					+ " has these groups:");
			sender.sendMessage(list);
			return true;
		} else if (args.length >= 4 && args[1].equalsIgnoreCase(plugin.addNode)) {
			String node = args[2];
			String group = args[3];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.addNode(node, group);
			sender.sendMessage("Added node:" + node + " to group:" + group
					+ " in world:" + world);
			return true;
		} else if (args.length >= 4
				&& args[1].equalsIgnoreCase(plugin.removeNode)) {
			String node = args[2];
			String group = args[3];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			p.removeNode(node, group);
			sender.sendMessage("Removed node:" + node + " from group:" + group
					+ " in world:" + world);
			return true;
		} else if (args.length >= 3
				&& args[1].equalsIgnoreCase(plugin.listNode)) {
			String group = args[2];

			PermissionSet p = plugin.pm.getPermissionSet(w);
			List<String> groupNodes = p.getGroupNodes(group);
			String list = Arrays.toString(groupNodes.toArray()).replace("[", "").replace("]", "");
			sender.sendMessage(group + " in world:" + world
					+ " has these nodes:");
			sender.sendMessage(list);
			return true;
		}
		return false;
	}

}
