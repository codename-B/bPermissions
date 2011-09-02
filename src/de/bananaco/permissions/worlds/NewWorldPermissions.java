package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.config.Configuration;
import de.bananaco.permissions.interfaces.PermissionSet;

class NewWorldPermissions extends TransitionPermissions implements PermissionSet {
	/**
	 * The main class instance
	 */
	private final JavaPlugin plugin;
	/**
	 * The world
	 */
	private final World world;
	/**
	 * The configuration object
	 */
	private final Configuration c;

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions] " + String.valueOf(input));
	}

	public NewWorldPermissions(World world, JavaPlugin plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.plugin = plugin;
		this.world = world;
		this.c = new Configuration(new File("plugins/bPermissions/worlds/"
				+ world.getName() + ".bml"));
		setup();
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setup() {
		log("Setting up config for world:" + world.getName());
		reload();
	}
	
	@Override
	public void reload() {
		c.load();
		if(c.getComment("")==null) {
			c.comment("", "Welcome to the bPermissions config file!");
			c.comment("", "If you're seeing this message, you've decided to make the change to our new .bml format, well done.");
			c.comment("", "This makes you part of an elite group of people, and also improves the performance of bPermissions.");
			c.comment("", "To import your .yml permissions to .bml use /p import yml");
			c.comment("", "Don't worry, you can still import P3, GM etc using their relevant commands too!");
		}
		if(c.getComment("players")==null) {
			c.comment("players", "This is where the players and their groups are stored!");
			c.comment("players", "Some relevant commands:");
			c.comment("players", "/p global addgroup playername");
			c.comment("players", "/p global rmgroup playername");
			c.comment("players", "/p global lsgroup playername");
		}
		if(c.getComment("groups")==null) {
			c.comment("groups", "This is where the groups and their permission nodes are stored!");
			c.comment("groups", "Some relevant commands:");
			c.comment("groups", "/p global addnode node.node groupname");
			c.comment("groups", "/p global rmnode node.node groupname");
			c.comment("groups", "/p global lsnode groupname");
		}
		c.save();
		setupPlayers();
	}

	@Override
	public void addNode(String node, String group) {
		List<String> groupNodes = c.getStringList("groups." + group, null);
		if (groupNodes == null) {
			log("the group:" + group + " does not exist for world:"
					+ world.getName());
			return;
		}
		if (!groupNodes.contains(node)) {
			groupNodes.add(node);
			log("added node:" + node + " to group:" + group + " for world:"
					+ world.getName());
		} else {
			log("node:" + node + " already exists in group:" + group
					+ " for world:" + world.getName());
		return;
		}
		c.setProperty("groups." + group, groupNodes);
		c.save();
		setupPlayers();
	}

	@Override
	public void removeNode(String node, String group) {
		List<String> groupNodes = c.getStringList("groups." + group, null);
		if (groupNodes == null) {
			log("the group:" + group + " does not exist for world:"
					+ world.getName());
			return;
		}
		if (groupNodes.contains(node)) {
			groupNodes.remove(node);
			log("removed node:" + node + " from group:" + group + " for world:"
					+ world.getName());
		} else {
			log("node:" + node + " does not exist in group:" + group
					+ " for world:" + world.getName());
			return;
		}
		c.setProperty("groups." + group, groupNodes);
		c.save();
		setupPlayers();
	}

	@Override
	public List<String> getGroupNodes(String group) {
		List<String> groupNodes = c.getStringList("groups." + group, null);
		return groupNodes;
	}

	@Override
	public List<String> getPlayerNodes(Player player) {
		return getPlayerNodes(player.getName());
	}

	@Override
	public List<String> getPlayerNodes(String player) {
		List<String> playerGroups = getGroups(player);
		List<String> playerNodes = new ArrayList<String>();
		for (String group : playerGroups) {
			playerNodes.addAll(getGroupNodes(group));
		}
		return playerNodes;
	}

	@Override
	public List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public List<String> getGroups(String player) {
		List<String> playerGroups = c.getStringList("players." + player, null);
		if (playerGroups == null || playerGroups.size() == 0) {
			playerGroups = new ArrayList<String>();
			playerGroups.add((c.getStringList("default").size()==0)? "default" : c.getStringList("default").get(0));
			log(player
					+ " does not have a group in world:"
					+ world.getName()
					+ ", creating an entry for them and setting them to the default group");
			c.setProperty("players." + player, playerGroups);
			c.save();
			setupPlayers();
		}
		return playerGroups;
	}

	@Override
	public void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	@Override
	public void addGroup(String player, String group) {
		List<String> playerGroups = c.getStringList("players." + player, null);
		if (!playerGroups.contains(group)) {
			playerGroups.add(group);
			log("Group:" + group + " added to player:" + player + " in world:" + world.getName());
		} else {
			log("Group:" + group + " could not be added to player:" + player
					+ " in world:"+world.getName()+" as the player already has this group");
			return;
		}
		c.setProperty("players." + player, playerGroups);
		c.save();
		setupPlayers();
	}

	@Override
	public void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public void removeGroup(String player, String group) {
		List<String> playerGroups = c.getStringList("players." + player, null);
		if (playerGroups.contains(group)) {
			playerGroups.remove(group);
			log("Group:" + group + " removed from player:" + player + " in world:" + world.getName());
		} else {
			log("Group:" + group + " could not be removed from player:"
					+ player + " in world:"+world.getName()+" as the player does not have this group");
			return;
		}
		c.setProperty("players." + player, playerGroups);
		c.save();
		setupPlayers();
	}

	@Override
	public void setupPlayers() {
		for (Player player : world.getPlayers()) {
			SuperPermissionHandler sp = new SuperPermissionHandler(player);
			sp.unsetupPlayer();
			sp.setupPlayer(this.getPlayerNodes(player), plugin);
		}
	}

}
