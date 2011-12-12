package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public abstract class PermissionClass implements PermissionSet {
	private static Pattern p = Pattern.compile("\\[[0-9]*-[0-9]*\\]");
	public static List<String> getRangePermissions(String input) {
		List<String> perms = new ArrayList<String>();
		String o = input;

		String[] parts = o.split("\\.");

		for (String part : parts) {
			if (isRangePermission(part)) {
				int[] pair = recurse(part);
				if (pair != null) {
					String pr = String.valueOf("[" + pair[0] + "-" + pair[1]
							+ "]");
					for (int i = pair[0]; i <= pair[1]; i++) {
						perms.add(o.replace(pr, String.valueOf(i)));
					}
				}
			}
		}
		Set<String> fPerms = new HashSet<String>();
		for (String perm : perms) {
			if (isRangePermission(perm)) {
				List<String> per = new ArrayList<String>();
				per.addAll(getRangePermissions(perm));
				fPerms.addAll(per);
			}
		}
		perms.addAll(fPerms);
		fPerms.clear();
		for (String perm : perms) {
			if (!isRangePermission(perm))
				fPerms.add(perm);
		}
		perms.clear();
		perms.addAll(fPerms);
		fPerms.clear();
		Collections.sort(perms, new Comparator<String>() {
			public int compare(String a, String b) {
				return a.compareTo(b);
			};
		});
		return perms;
	}
	public static boolean isRangePermission(String input) {
		Matcher m = p.matcher(input);
		return m.find();
	}

	private static int[] recurse(String part) {
		int[] out = new int[2];
		part = part.replace("[", "").replace("]", "");
		String[] parts = part.split("-");
		out[0] = Integer.parseInt(parts[0].replaceAll("[A-z]", ""));
		out[1] = Integer.parseInt(parts[1].replaceAll("[A-z]", ""));
		return out;
	}

	/**
	 * The main class instance
	 */
	public final Permissions plugin;

	private boolean setup = false;

	/**
	 * The world
	 */
	public final World world;

	protected PermissionClass(World world, Permissions plugin) {
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
	public final void addGroupToGroup(String main, String group) {
		main = caseCheck(main);
		group = caseCheck(group);

		List<String> groupGroups = getGroupGroups(main);
		if (!groupGroups.contains(group)) {
			groupGroups.add(group);
			log("Group:" + group + " added to player:" + main + " in world:"
					+ world.getName());
		} else {
			return;
		}
		setGroupGroups(main, groupGroups);
	}

	@Override
	public final void addNode(String node, String group) {
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
			groupNodes.remove(node);
			groupNodes.add(node);
			log("overriding node:" + node + " to group:" + group
					+ " for world:" + world.getName());
		}
		setNodes(group, groupNodes);
	}
	
	@Override
	public final void addPlayerNode(String node, String player) {
		player = caseCheck(player);

		List<String> playerNodes = getPlayerNodes(player);
		if (playerNodes == null) {
			log("the group:" + player + " does not exist for world:"
					+ world.getName());
			return;
		}
		if (!playerNodes.contains(node)) {
			playerNodes.add(node);
			log("added node:" + node + " to group:" + player + " for world:"
					+ world.getName());
		} else {
			playerNodes.remove(node);
			playerNodes.add(node);
			log("overriding node:" + node + " to group:" + player
					+ " for world:" + world.getName());
		}
		setPlayerNodes(player, playerNodes);
	}

	public String caseCheck(String input) {
		String output = input;
		if (Permissions.idiotVariable)
			output = output.toLowerCase();
		return output;
	}

	@Override
	public final List<String> getAllCachedPlayersWithGroup(String group) {
		group = caseCheck(group);

		long start = System.currentTimeMillis();
		List<String> players = new ArrayList<String>();
		for (String player : getAllCachedPlayers()) {
			if (hasGroup(player, group)) {
				players.add(player);
			}
		}
		long finish = System.currentTimeMillis() - start;
		log(players.size() + " players found in group " + group
				+ ". Search took " + finish + "ms.");
		return players;
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
	public final World getWorld() {
		return world;
	}

	@Override
	public final boolean hasGroup(Player player, String group) {
		return hasGroup(player.getName(), group);
	}

	public final boolean hasGroup(String player, String group) {
		List<String> groups = getGroups(player);
		return groups.contains(group);
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public final void log(Object input) {
		// Debugger.getDebugger().log(String.valueOf(input));
	}

	public final String parse(List<String> rList) {
		String[] rArray = new String[rList.size()];
		rArray = rList.toArray(rArray);
		return Arrays.toString(rArray);
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
	public final void removeGroupFromGroup(String main, String group) {
		main = caseCheck(main);
		group = caseCheck(group);

		List<String> groupGroups = getGroups(main);
		if (groupGroups.contains(group)) {
			groupGroups.remove(group);
			log("Group:" + group + " removed from player:" + main
					+ " in world:" + world.getName());
		} else {
			return;
		}
		setGroupGroups(main, groupGroups);
	}

	@Override
	public final void removeNode(String node, String group) {
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
	public final void removePlayerNode(String node, String player) {
		player = caseCheck(player);

		List<String> playerNodes = getPlayerNodes(player);
		if (playerNodes == null) {
			log("the player:" + player + " does not exist for world:"
					+ world.getName());
			return;
		}
		if (playerNodes.contains(node)) {
			playerNodes.remove(node);
			log("removed node:" + node + " from player:" + player + " for world:"
					+ world.getName());
		} else {
			return;
		}
		setPlayerNodes(player, playerNodes);
	}

	private List<String> sanitise(List<String> input) {
		Set<String> san = new HashSet<String>();
		List<String> output = new ArrayList<String>();
		for (String in : input) {
			if (!san.contains(in)) {
				san.add(in);
				output.add(in);
			}
		}
		san.clear();
		return output;
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

	@Override
	public void setGroups(String player, List<String> groups) {
		player = caseCheck(player);

		List<String> sanity = sanitise(groups);
		if (sanity.size() < groups.size()) {
			log("Duplicates detected!");
			setGroups(player, sanity);
			return;
		}
		log(parse(groups) + " set to player:" + player);
		// MCMA.getDebugger().log(getWorld().getName());
		setupPlayer(player);
		// Permissions.getInfoReader().clear();
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		group = caseCheck(group);

		List<String> sanity = sanitise(nodes);
		if (sanity.size() < nodes.size()) {
			log("Duplicates detected!");
			setNodes(group, sanity);
			return;
		}
		log(parse(nodes) + " set to group:" + group);
		// MCMA.getDebugger().log(getWorld().getName());
		setupPlayers();
		// Permissions.getInfoReader().clear();
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
		// SuperPermissionHandler.setupPlayer(player, getPlayerNodes(player));
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

}
