package de.bananaco.permissions.worlds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.sql.MySQL;

public class SQLWorldPermissions extends TransitionPermissions implements PermissionSet {
	private final World world;
	//private final Permissions plugin;
	private final MySQL sql;
	
	public SQLWorldPermissions(World world, Permissions plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.world = world;
		//this.plugin = plugin;
		this.sql = new MySQL(plugin.getServer().getLogger(), plugin.getDescription().getName(), plugin.hostname, plugin.port, plugin.database, plugin.username, plugin.password);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setup() {
		try {
		if(!sql.checkTable("bPermissions_users")) {
			sql.createTable("CREATE TABLE bPermissions_users (key char(50), value text(5000))");
			System.out.println("[bPermissions] Created table bPermissions_users");
		}
		if(!sql.checkTable("bPermissions_groups")) {
			sql.createTable("CREATE TABLE bPermissions_groups (key char(50), value text(5000))");
			System.out.println("[bPermissions] Created table bPermissions_groups");
		}
		} catch (Exception e) {
			System.err.println("[bPermissions] Cannot connect to db");
			e.printStackTrace();
		}	
	}

	@Override
	public void reload() {
		
	}

	@Override
	public void addNode(String node, String group) {
		
	}

	@Override
	public void removeNode(String node, String group) {
		
	}
	
	private List<String> parseGroups(String rString) {
		ArrayList<String> nodes = new ArrayList<String>();
		for(String split : rString.replace("[", "").replace("]", "").split(", ")) {
			nodes.add(split);
		}
		return nodes;
	}
	private List<String> parsePermissions(String rString) {
		ArrayList<String> nodes = new ArrayList<String>();
		
		for(String split : rString.replace("[", "").replace("]", "").split(", ")) {
			String[] result = split.split("=");
			String perm = result[0];
			if(result[1].equals("false"))
				perm = "^" + perm;
			nodes.add(perm);
		}
		return nodes;
	}
	@Override
	public List<String> getGroupNodes(String group) {
		List<String> nodes = new ArrayList<String>();
		ResultSet rs = null;
		try {
		rs = sql.query("SELECT `value` FROM `bPermissions_groups` WHERE `key` = '"+group+"' LIMIT 0 , 1");
		String rString = "";
		if(rs.getBoolean(0))
			rString = rs.getString(0);
			nodes = parsePermissions(rString);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return nodes;
	}

	@Override
	public List<String> getPlayerNodes(Player player) {
		
		return null;
	}

	@Override
	public List<String> getPlayerNodes(String player) {
		List<String> nodes = new ArrayList<String>();
		ResultSet rs = null;
		try {
		rs = sql.query("SELECT `value` FROM `bPermissions_groups` WHERE `key` = '"+player+"' LIMIT 0 , 1");
		String rString = "";
		if(rs.getBoolean(0))
			rString = rs.getString(0);
			nodes = parseGroups(rString);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return nodes;
	}

	@Override
	public List<String> getGroups(Player player) {
		
		return null;
	}

	@Override
	public List<String> getGroups(String player) {
		
		return null;
	}

	@Override
	public void addGroup(Player player, String group) {
		
	}

	@Override
	public void addGroup(String player, String group) {
		
	}

	@Override
	public void removeGroup(Player player, String group) {
		
	}

	@Override
	public void removeGroup(String Player, String group) {
		
	}

	@Override
	public void setupPlayers() {
		
	}

	@Override
	public void overrideCraftPlayers() {
		
	}

	@Override
	public boolean has(Player player, String node) {
		
		return false;
	}
	

}
