package com.ubempire.permissions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionFunctions {
	private Permissions Permissions;
	public HashMap<String, List<String>> playerPermissions = new HashMap<String, List<String>>();
	public HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	public HashMap<String, String> playerGroups = new HashMap<String, String>();

	PermissionFunctions(Permissions Permissions) {
		this.Permissions = Permissions;
	}

	public void getPermissions(Player Player) {
		// Setup the player reader
		PermissionReader pr = new PermissionReader(Player, Permissions);
		// Setup the superperms handler
		SuperPermissionHandler sp = new SuperPermissionHandler(Player,
				Permissions);
		// Read the permission nodes
		pr.readNodes();
		// Grab the permission nodes
		List<String> nodes = pr.getNodes();
		// Log
		Log(null, "Reading permission nodes from " + pr.getGroupFileName());
		sp.setupPlayer(nodes);
		String PlayerName = Player.getName();
		playerPermissions.put(PlayerName, nodes);
		playerGroups.put(PlayerName, pr.getGroup());
	}

	public void unsetPermissions(Player Player) {
		// Setup the superperms handler
		SuperPermissionHandler sp = new SuperPermissionHandler(Player,
				Permissions);
		sp.unsetupPlayer();
	}

	public void getAllPermissions() {
		for (Player Player : Permissions.getServer().getOnlinePlayers())
			getPermissions(Player);
	}

	public void Log(Player Player, String log) {
		if (Player != null)
			Player.sendMessage(log);
		Permissions.getServer().getLogger().info(log);
	}

	public void unsetAllPermissions() {
		Set<String> keys = playerPermissions.keySet();
		Iterator<String> keysIt = keys.iterator();
		while (keysIt.hasNext()) {
			String player = keysIt.next();
			Player Player = Permissions.getServer().getPlayer(player);
			if (player != null) {
				unsetPermissions(Player);
			}
		}
	}

	public void refreshPermissions() {
		unsetAllPermissions();
		getAllPermissions();
	}

	public void setGroup(String PlayerName, String WorldName, String Group,
			Boolean forWorlds) {
		if (!forWorlds) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(
					PlayerName, WorldName, Permissions);
			pr.readNodes();
			pr.setGroup(Group);
			return;
		}
		// Sets this player to the group for every world (yeah)
		for (World w : Bukkit.getServer().getWorlds()) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(
					PlayerName, w.getName(), Permissions);
			pr.readNodes();
			pr.setGroup(Group);
		}

	}

	public void addNode(String GroupName, String WorldName, String Node,
			Boolean forWorlds) {
		if (!forWorlds) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(false,
					GroupName, WorldName, Permissions);
			pr.readNodes();
			pr.addNode(Node);
			return;
		}
		// Adds this node in ever world (yesa)
		for (World w : Bukkit.getServer().getWorlds()) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(false,
					GroupName, w.getName(), Permissions);
			pr.readNodes();
			pr.addNode(Node);
		}
	}

	public void removeNode(String GroupName, String WorldName, String Node,
			Boolean forWorlds) {
		if (!forWorlds) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(false,
					GroupName, WorldName, Permissions);
			pr.readNodes();
			pr.removeNode(Node);
			return;
		}
		// Removes this node from every world. (yeah)
		for (World w : Bukkit.getServer().getWorlds()) {
			OfflinePermissionWriter pr = new OfflinePermissionWriter(false,
					GroupName, w.getName(), Permissions);
			pr.readNodes();
			pr.removeNode(Node);
		}
	}

	public List<String> getNodes(String GroupName, String WorldName) {
		OfflinePermissionWriter pr = new OfflinePermissionWriter(false,
				GroupName, WorldName, Permissions);
		pr.readNodes();
		return pr.getNodes();
	}
}
