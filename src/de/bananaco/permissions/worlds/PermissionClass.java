package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.interfaces.PermissionSet;

public abstract class PermissionClass implements PermissionSet {
	private boolean setup = false;
	/**
	 * The main class instance
	 */
	public final Permissions plugin;
	/**
	 * The world
	 */
	public final World world;

	PermissionClass(World world, Permissions plugin) {
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public final void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	@Override
	public final void addGroup(String player, String group) {
		List<String> playerGroups = getGroups(player);
		if (!playerGroups.contains(group)) {
			playerGroups.add(group);
			log("Group:" + group + " added to player:" + player + " in world:"
					+ world.getName());
		} else {
			return;
		}
		setGroups(player, playerGroups);
	}

	@Override
	public final void addNode(String node, String group) {
		List<String> groupNodes = getGroupNodes(group);
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
			return;
		}
		setNodes(group, groupNodes);
	}

	public final ArrayList<String> getDefaultArrayList() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(getDefaultGroup());
		return ar;
	}

	@Override
	public final List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public final List<String> getPlayerNodes(Player player) {
		return getPlayerNodes(player.getName());
	}

	@Override
	public final List<String> getPlayerNodes(String player) {
		List<String> playerGroups = getGroups(player);
		List<String> playerNodes = new ArrayList<String>();
		for (String group : playerGroups) {
			for (String node : getGroupNodes(group)) {
				if (!playerNodes.contains(node))
					playerNodes.add(node);
			}
		}
		return playerNodes;
	}

	@Override
	public final World getWorld() {
		return world;
	}

	public final boolean has(Player player, String node) {
		return HasPermission.has(player, node);
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public final void log(Object input) {
		Debugger.getDebugger().log(String.valueOf(input));
	}

	@Override
	public final void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public final void removeGroup(String player, String group) {
		List<String> playerGroups = getGroups(player);
		if (playerGroups.contains(group)) {
			playerGroups.remove(group);
			log("Group:" + group + " removed from player:" + player
					+ " in world:" + world.getName());
		} else {
			return;
		}
		setGroups(player, playerGroups);
	}

	@Override
	public final void removeNode(String node, String group) {
		List<String> groupNodes = getGroupNodes(group);
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
			return;
		}
		setNodes(group, groupNodes);
	}

	@Override
	public final void setGroup(Player player, String group) {
		setGroup(player.getName(), group);
	}

	@Override
	public final void setGroup(String player, String group) {
		List<String> groups = new ArrayList<String>();
		groups.add(group);
		setGroups(player, groups);
	}

	@Override
	public final void setGroups(Player player, List<String> groups) {
		setGroups(player.getName(), groups);
	}

	@Override
	public void setGroups(String player, List<String> groups) {
		log(parse(groups) + " set to player:" + player);
		setupPlayer(player);
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		log(parse(nodes) + " set to group:" + group);
		setupPlayers();
	}

	@Override
	public final void setup() {
		if (!setup) {
			log("Setting up config for world:" + world.getName());
			reload();
		}
		setup = true;
	}

	@Override
	public final void setupPlayer(Player player) {
		SuperPermissionHandler.setupPlayer(player, getPlayerNodes(player),
				plugin);
	}

	@Override
	public final void setupPlayer(String player) {
		Player p = plugin.getServer().getPlayer(player);
		if (p != null)
			setupPlayer(p);
	}

	@Override
	public final void setupPlayers() {
		long start = System.currentTimeMillis();
		for (Player player : world.getPlayers()) {
			setupPlayer(player);
		}
		long finish = System.currentTimeMillis() - start;
		log("Setup players for world:" + getWorld().getName() + " took "
				+ finish + "ms.");
	}

	public String parse(List<String> rList) {
		String[] rArray = new String[rList.size()];
		rArray = rList.toArray(rArray);
		return Arrays.toString(rArray);
	}
	
	@Override
	public final List<String> getAllCachedPlayersWithGroup(String group) {
		long start = System.currentTimeMillis();
		List<String> players = new ArrayList<String>();
		for(String player : getAllCachedPlayers()) {
			if(hasGroup(player, group)) {
				players.add(player);
			}
		}
		long finish = System.currentTimeMillis() - start;
		Debugger.getDebugger().log(players.size()+" players found in group "+group+". Search took "+finish+"ms.");
		return players;
	}
	
	@Override
	public boolean hasGroup(Player player, String group) {
		return hasGroup(player.getName(), group);
	}
	
	public boolean hasGroup(String player, String group) {
		List<String> groups = getGroups(player);
		return groups.contains(group);
	}

}
