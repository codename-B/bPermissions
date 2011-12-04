package de.bananaco.permissions.worlds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.World;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.sql.MySQL;

public class SQLWorldPermissions extends PermissionClass {

	private HashMap<String, List<String>> groupCache;
	private final MySQL sql;
	private HashMap<String, List<String>> userCache;

	public SQLWorldPermissions(World world, Permissions plugin) {
		super(world, plugin);
		this.sql = new MySQL(plugin.getServer().getLogger(), plugin
				.getDescription().getName(), plugin.hostname, plugin.port,
				plugin.database, plugin.username, plugin.password);

		userCache = new HashMap<String, List<String>>();
		groupCache = new HashMap<String, List<String>>();
	}

	
	public List<String> getAllCachedGroups() {
		List<String> groups = new ArrayList<String>();
		if (this.groupCache != null)
			for (String group : this.groupCache.keySet())
				groups.add(group);
		return groups;
	}

	
	public List<String> getAllCachedPlayers() {
		List<String> players = new ArrayList<String>();
		if (this.userCache != null)
			for (String player : this.userCache.keySet())
				players.add(player);
		return players;
	}

	
	public String getDefaultGroup() {
		return "default";
	}

	
	public List<String> getGroupNodes(String group) {
		if (groupCache.containsKey(group))
			return groupCache.get(group);

		List<String> nodes = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = sql.query("SELECT `v` FROM `" + world.getName()
					+ "_groups` WHERE `k` = '" + group + "'");
			String rString = "";
			if (rs.first()) {
				rString = rs.getString(1);
				nodes = parse(rString);
				groupCache.put(group, nodes);
			} else {
				sql.query("INSERT INTO `" + world.getName()
						+ "_groups` (`k`, `v`) VALUES ('" + group + "', '[]');");
				log("No entry for group:" + group + " entry created.");
				nodes.add("default");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodes;
	}

	
	public List<String> getGroups(String player) {
		if (userCache.containsKey(player))
			return userCache.get(player);

		List<String> groups = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = sql.query("SELECT `v` FROM `" + world.getName()
					+ "_users` WHERE `k` = '" + player + "'");
			String rString = "";
			if (rs.first()) {
				rString = rs.getString(1);
				groups = parse(rString);
				userCache.put(player, groups);
			} else {

				sql.query("INSERT INTO `" + world.getName()
						+ "_users` (`k`, `v`) VALUES ('" + player
						+ "', '[default]');");
				log("No entry for player:" + player + " entry created.");
				return getDefaultArrayList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}

	private List<String> parse(String rString) {
		ArrayList<String> nodes = new ArrayList<String>();
		if (rString.length() > 1)
			rString = rString.substring(1, rString.length() - 1);

		for (String split : rString.split(", ")) {
			nodes.add(split);
		}
		return nodes;
	}

	
	public void reload() {
		PermissionsThread.run(new Runnable() {
			public void run() {
				try {
					if (!sql.checkTable(world.getName() + "_users")) {
						sql.createTable("CREATE TABLE "
								+ world.getName()
								+ "_users (k VARCHAR(50), PRIMARY KEY(k), v TEXT);");
						log("Created table " + world.getName() + "_users");
					} else {
						log("Using table " + world.getName() + "_users");
					}
					if (!sql.checkTable(world.getName() + "_groups")) {
						sql.createTable("CREATE TABLE "
								+ world.getName()
								+ "_groups (k VARCHAR(50), PRIMARY KEY(k), v TEXT);");
						log("Created table " + world.getName() + "_groups");
					} else {
						log("Using table " + world.getName() + "_groups");
					}
				} catch (Exception e) {
					System.err.println("[bPermissions] Cannot connect to db");
					e.printStackTrace();
				}
			}
		});
		userCache.clear();
		groupCache.clear();
		log("userCache and " + "groupCache cleared. Reloaded.");
	}

	
	public void setGroups(final String player, final List<String> groups) {
		userCache.put(player, groups);
		PermissionsThread.run(new Runnable() {
			public void run() {
				try {
					sql.query("UPDATE `" + world.getName()
							+ "_users` SET `v` = '" + parse(groups)
							+ "' WHERE `k` = '" + player + "';");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		super.setGroups(player, groups);
	}

	
	public void setNodes(final String group, final List<String> nodes) {
		groupCache.put(group, nodes);
		PermissionsThread.run(new Runnable() {
			public void run() {
				try {
					sql.query("UPDATE `" + world.getName()
							+ "_groups` SET `v` = '" + parse(nodes)
							+ "' WHERE `k` = '" + group + "';");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		super.setNodes(group, nodes);
	}

}
