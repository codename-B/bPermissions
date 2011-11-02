package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.debug.MCMA;
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
	
	public static String caseCheck(String input) {
		String output = input;
		if(Permissions.idiotVariable)
			output = output.toLowerCase();
		return output;
	}
	
	public static void main(String[] args) {
		String test = "test.[2-10]";
		for(String perm : getRangePermissions(test))
			System.out.println(perm);
	}
	
	private static Pattern p = Pattern.compile("\\[[0-9]*-[0-9]*\\]");
				
	public static boolean isRangePermission(String input) {
		Matcher m = p.matcher(input);
		return m.find();
	}
	
	public static List<String> getRangePermissions(String input) {
		List<String> perms = new ArrayList<String>();
		
		String o = input;
		String t = input.substring(0, input.lastIndexOf("."));
		while(o.contains(".") && o.indexOf(".") < o.indexOf("[")) {
			o = o.replace(o.substring(0, o.indexOf(".")+1), "").replace("[", "").replace("]", "");
			System.out.println(o);
		}
		String[] se = o.split("-");
		int x = Integer.parseInt(se[0]);
		int y = Integer.parseInt(se[1]);
		for(int i=x; i<=y; i++)
			perms.add(t+"."+i);
		return perms;
	}

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
		player = caseCheck(player);
		group = caseCheck(group);
		
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
		node = caseCheck(node);
		group = caseCheck(group);
		
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
		ar.add(caseCheck(getDefaultGroup()));
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
			if(group.startsWith("p:")) {
			String node = group.substring(2);
			playerNodes.add(node);
			} else {
			for (String node : getGroupNodes(group)) {
				if(isRangePermission(node)) {
					List<String> rNodes = getRangePermissions(node);
					for(String nd : rNodes) {
						if (!playerNodes.contains(nd))
							playerNodes.add(nd);
					}
				}
				else if (!playerNodes.contains(node))
					playerNodes.add(node);
			}
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
		player = caseCheck(player);
		group = caseCheck(group);
		
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
		node = caseCheck(node);
		group = caseCheck(group);
		
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
		player = caseCheck(player);
		
		List<String> groups = new ArrayList<String>();
		groups.add(group);
		setGroups(player, groups);
	}

	@Override
	public final void setGroups(Player player, List<String> groups) {
		setGroups(player.getName(), groups);
	}

	private List<String> sanitise(List<String> input) {
		Set<String> san = new HashSet<String>();
		List<String> output = new ArrayList<String>();
		for(String in : input) {
			if(!san.contains(in)) {
				san.add(in);
				output.add(in);
			}
		}
		san.clear();
		return output;
	}
	
	@Override
	public void setGroups(String player, List<String> groups) {
		player = caseCheck(player);
		
		List<String> sanity = sanitise(groups);
		if(sanity.size() < groups.size()) {
			Debugger.getDebugger().log("Duplicates detected!");
			setGroups(player, sanity);
			return;
		}
		log(parse(groups) + " set to player:" + player);
		MCMA.getDebugger().log(getWorld().getName());
		setupPlayer(player);
		Permissions.getInfoReader().clear();
		HasPermission.clearCache();
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		group = caseCheck(group);
		
		List<String> sanity = sanitise(nodes);
		if(sanity.size() < nodes.size()) {
			Debugger.getDebugger().log("Duplicates detected!");
			setNodes(group, sanity);
			return;
		}
		log(parse(nodes) + " set to group:" + group);
		MCMA.getDebugger().log(getWorld().getName());
		setupPlayers();
		Permissions.getInfoReader().clear();
		HasPermission.clearCache();
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
		SuperPermissionHandler.setupPlayer(player, getPlayerNodes(player));
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

	public final String parse(List<String> rList) {
		String[] rArray = new String[rList.size()];
		rArray = rList.toArray(rArray);
		return Arrays.toString(rArray);
	}
	
	@Override
	public final List<String> getAllCachedPlayersWithGroup(String group) {
		group = caseCheck(group);
		
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
	public final boolean hasGroup(Player player, String group) {
		return hasGroup(player.getName(), group);
	}
	
	public final boolean hasGroup(String player, String group) {
		List<String> groups = getGroups(player);
		return groups.contains(group);
	}
	
	

}
