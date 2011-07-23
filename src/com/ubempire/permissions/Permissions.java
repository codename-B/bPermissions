package com.ubempire.permissions;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Permissions extends JavaPlugin {
	private EntityHandler playerInteract = new EntityHandler(this);
	public String dataFolder = "plugins/bPermissions/";
	public String defaultGroup = "default";
	public PermissionFunctions pf;

	@Override
	public void onDisable() {
		pf.unsetAllPermissions();
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println("[" + (pdfFile.getName()) + "]" + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerInteract,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerInteract,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerInteract,
				Event.Priority.Normal, this);
		System.out.println("[" + (pdfFile.getName()) + "]" + " version "
				+ pdfFile.getVersion() + " is enabled!");

		pf = new PermissionFunctions(this);
		Configuration c = getConfiguration();
		c.load();
		c.setProperty("default", c.getString("default", "default"));
		c.save();
		defaultGroup = c.getString("default");

		pf.getAllPermissions();
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		Player player = null;
		boolean isOp = true;
		// check if it's the console or an admin sending this command
		if (sender instanceof Player) {
			player = (Player) sender;
			if (!(player.isOp() || player.hasPermission("bPermissions.admin")))
				isOp = false;
		}
		// if the player does not have admin permissions, tell them about it!
		if (!isOp) {
			sender.sendMessage(ChatColor.RED
					+ "[WARNING] YOU DO NOT HAVE PERMISSION!");
			return true;
		}
		/*
		 * Exclusively player commands
		 */
		if (sender instanceof Player && args.length < 3) {
			Player Player = (Player) sender;
			String world = Player.getWorld().getName();
			if (cmd.getName().equalsIgnoreCase("setgroup") && args.length == 2) {
				// setGroup(Player, World, Group);
				// ./setgroup player group world
				pf.setGroup(args[0], world, args[1]);
				Log(player, "set " + args[0] + " to " + args[1] + "!");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("addnode") && args.length == 2) {
				// addNode(Group,World,Node);
				// ./addnode node group world
				pf.addNode(args[1], world, args[0]);
				Log(player, args[0] + " added to " + args[1]);
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("rmnode") && args.length == 2) {
				// removeNode(Group,World,Node);
				// ./rmnode node group world
				pf.removeNode(args[1], world, args[0]);
				Log(player, args[0] + " removed from " + args[1]);
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("lsnode") && args.length == 1) {
				// listNodes(Group, World);
				// ./lsnode group world
				List<String> nodes = pf.getNodes(args[0], world);
				String nodelist = "";
				for (String node : nodes) {
					nodelist = nodelist + " " + node;
				}
				Log(player, args[1] + " has these nodes");
				Log(player, nodelist);
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("setgroup") && args.length == 3) {
			// setGroup(Player, World, Group);
			// ./setgroup player group world
			pf.setGroup(args[0], args[2], args[1]);
			Log(player, "set " + args[0] + " to " + args[1] + "!");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("addnode") && args.length == 3) {
			// addNode(Group,World,Node);
			// ./addnode node group world
			pf.addNode(args[1], args[2], args[0]);
			Log(player, args[0] + " added to " + args[1]);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("rmnode") && args.length == 3) {
			// removeNode(Group,World,Node);
			// ./rmnode node group world
			pf.removeNode(args[1], args[2], args[0]);
			Log(player, args[0] + " removed from " + args[1]);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("lsnode") && args.length == 2) {
			// listNodes(Group, World);
			// ./lsnode group world
			List<String> nodes = pf.getNodes(args[0], args[1]);
			String nodelist = "";
			for (String node : nodes) {
				nodelist = nodelist + " " + node;
			}
			Log(player, args[1] + " has these nodes");
			Log(player, nodelist);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("nodereload")) {
			// ./nodereload
			pf.refreshPermissions();
			Log(player, "nodes reloaded.");
			return true;
		}
		return false;
	}

	public void Log(Player player, String log) {
		pf.Log(player, log);
	}

}
