package de.bananaco.permissions.worlds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.interfaces.TransitionSet;
import de.bananaco.permissions.override.MonkeyPlayer;
import de.bananaco.permissions.sql.MySQL;

public class SQLWorldPermissions extends TransitionPermissions implements PermissionSet {
	private final World world;
	private final Permissions plugin;
	private final MySQL sql;
	private HashMap<String, List<String>> userCache;
	private HashMap<String, List<String>> groupCache;
	public SQLWorldPermissions(World world, Permissions plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.world = world;
		this.plugin = plugin;
		this.sql = new MySQL(plugin.getServer().getLogger(), plugin.getDescription().getName(), plugin.hostname, plugin.port, plugin.database, plugin.username, plugin.password);
		
		userCache = new HashMap<String, List<String>>();
		groupCache = new HashMap<String, List<String>>();
		
		setup();
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions] " + String.valueOf(input));
	}
	
	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setup() {
		PermissionsThread.run(new Runnable() { public void run() {
		try {
		if(!sql.checkTable(world.getName()+"_users")) {
			sql.createTable("CREATE TABLE "+world.getName()+"_users (k VARCHAR(50), PRIMARY KEY(k), v TEXT);");
			log("Created table "+world.getName()+"_users");
		} else {
			log("Using table "+world.getName()+"_users");
		}
		if(!sql.checkTable(world.getName()+"_groups")) {
			sql.createTable("CREATE TABLE "+world.getName()+"_groups (k VARCHAR(50), PRIMARY KEY(k), v TEXT);");
			log("Created table "+world.getName()+"_groups");
		} else {
				log("Using table "+world.getName()+"_groups");
			}
		} catch (Exception e) {
			System.err.println("[bPermissions] Cannot connect to db");
			e.printStackTrace();
		}	
		}});
		reload();
	}

	@Override
	public void reload() {
		userCache.clear();
		groupCache.clear();
		log(
				"userCache and "+
				"groupCache cleared. Reloaded.");
	}

	@Override
	public void addNode(String node, final String group) {
		final List<String> nodes = getGroupNodes(group);
		if(!nodes.contains(node))
			nodes.add(node);
		groupCache.put(group, nodes);
		PermissionsThread.run(new Runnable() { public void run() {
			try {
				sql.query("UPDATE `"+world.getName()+"_groups` SET `v` = '"+parse(nodes)+"' WHERE `k` = '"+group+"';");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	}

	private ArrayList<String> getDefaultArrayList() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(getDefaultGroup());
		return ar;
	}
	
	@Override
	public void removeNode(String node, final String group) {
		final List<String> nodes = getGroupNodes(group);
		if(nodes.contains(node))
			nodes.remove(node);
		groupCache.put(group, nodes);
		PermissionsThread.run(new Runnable() { public void run() {
			try {
				sql.query("UPDATE `"+world.getName()+"_groups` SET `v` = '"+parse(nodes)+"' WHERE `k` = '"+group+"';");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	}
	
	private List<String> parse(String rString) {
		ArrayList<String> nodes = new ArrayList<String>();
		if(rString.length()>1)
		rString = rString.substring(1, rString.length()-1);
		
		for(String split : rString.split(", ")) {
			nodes.add(split);
		}
		return nodes;
	}
	
	private String parse(List<String> rList) {
		String[] rArray = new String[rList.size()];
		rArray = rList.toArray(rArray);
		return Arrays.toString(rArray);
	}
	
	@Override
	public List<String> getGroupNodes(String group) {
		if(groupCache.containsKey(group))
			return groupCache.get(group);
		
		List<String> nodes = new ArrayList<String>();
		ResultSet rs = null;
		try {
		rs = sql.query("SELECT `v` FROM `"+world.getName()+"_groups` WHERE `k` = '"+group+"'");
		String rString = "";
		if(rs.first()) {
			rString = rs.getString(1);
			nodes = parse(rString);
			groupCache.put(group, nodes);
		} else {
			sql.query("INSERT INTO `"+world.getName()+"_groups` (`k`, `v`) VALUES ('"+group+"', '[]');");
			log("No entry for group:"+group+" entry created.");
			nodes.add("default");
		}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return nodes;
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
			for(String node : getGroupNodes(group)) {
				if(!playerNodes.contains(node))
					playerNodes.add(node);
			}
		}
		List<String> transitionNodes = ((TransitionSet) this).listTransNodes(player);
		for(String node : transitionNodes) {
			if(!playerNodes.contains(node))
				playerNodes.add(node);
		}
		return playerNodes;
	}

	@Override
	public List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public List<String> getGroups(String player) {
		if(userCache.containsKey(player))
			return userCache.get(player);
		
		List<String> groups = new ArrayList<String>();
		ResultSet rs = null;
		try {
		rs = sql.query("SELECT `v` FROM `"+world.getName()+"_users` WHERE `k` = '"+player+"'");
		String rString = "";
		if(rs.first()) {
			rString = rs.getString(1);
			groups = parse(rString);
			userCache.put(player, groups);
		} else {
			
			sql.query("INSERT INTO `"+world.getName()+"_users` (`k`, `v`) VALUES ('"+player+"', '[default]');");
			log("No entry for player:"+player+" entry created.");
			return getDefaultArrayList();
		}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return groups;
	}

	@Override
	public void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	@Override
	public void addGroup(final String player, String group) {
		final List<String> groups = getGroups(player);
		if(!groups.contains(group))
			groups.add(group);
		userCache.put(player, groups);
		PermissionsThread.run(new Runnable() { public void run() {
			try {
				sql.query("UPDATE `"+world.getName()+"_users` SET `v` = '"+parse(groups)+"' WHERE `k` = '"+player+"';");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	
	}

	@Override
	public void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public void removeGroup(final String player, String group) {
		final List<String> groups = getGroups(player);
		if(groups.contains(group))
			groups.remove(group);
		userCache.put(player, groups);
		PermissionsThread.run(new Runnable() { public void run() {
			try {
				sql.query("UPDATE `"+world.getName()+"_users` SET `v` = '"+parse(groups)+"' WHERE `k` = '"+player+"';");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	}

	@Override
	public void setupPlayers() {
		for (Player player : world.getPlayers()) {
			SuperPermissionHandler sp = new SuperPermissionHandler(player);
			sp.unsetupPlayer();
			sp.setupPlayer(this.getPlayerNodes(player), plugin);
		}
	}
	
	public boolean has(Player player, String node) {
		return HasPermission.has(player, node);
	}

	@Override
	public void overrideCraftPlayers() {
		for(Player player : getWorld().getPlayers()) {
		if (Permissions.useMonkeyPlayer && plugin.overridePlayer) {

        if (!(player instanceof CraftPlayer)) {
            System.err.println("Player is not an instance of CraftPlayer! "+player.getName());
            return;
        }
        MonkeyPlayer newPlayer = new MonkeyPlayer((CraftPlayer) player);
        try {
            Permissions.entity_bukkitEntity.set(newPlayer.getHandle(), newPlayer);
        } catch (IllegalArgumentException e) {
            System.err.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
            e.printStackTrace();
        }
    }
}
}

	@Override
	public String getDefaultGroup() {
		return "default";
	}
	
	@Override
	public void setGroup(Player player, String group) {
		setGroup(player.getName(), group);
	}

	@Override
	public void setGroup(String player, String group) {
		addGroup(player, group);
		
		for(String removeGroup : getGroups(player))
			if(!removeGroup.equals(group))
			removeGroup(player, removeGroup);
		
	}

}
