package de.bananaco.permissions;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class ImportManager {
	private final JavaPlugin plugin;
	public ImportManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	public void importPermissionsBukkit() {
		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
		for(World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File perms = new File("plugins/PermissionsBukkit/config.yml");
			Configuration pConfig = new Configuration(perms);
			pConfig.load();
			List<String> usersList = pConfig.getKeys("users");
			List<String> groupsList = pConfig.getKeys("groups");
			if(usersList!=null)
			for(String player : usersList) {
				List<String> groups = pConfig.getStringList("users."+player+"groups", null);
				for(String group : groups)
					ps.addGroup(player, group);
			}
			if(groupsList!=null)
			for(String group : groupsList) {
				List<String> nodes = pConfig.getKeys("groups."+group+".permissions");
				List<String> wnodes = pConfig.getKeys("groups."+group+".worlds."+world.getName());
				if(nodes!=null)
				for(String node : nodes)
				ps.addNode(!pConfig.getBoolean("groups."+group+".permissions."+node, false)?node:"^"+node,group);
				if(wnodes != null)
				for(String node : wnodes)
				ps.addNode(!pConfig.getBoolean("groups."+group+".worlds."+world.getName()+"."+node, false)?node:"^"+node,group);
				
			}
		}
	}
	
	public void importGroupManager() {

		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
		for(World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File users = new File("plugins/GroupManager/worlds/"+world.getName()+"/users.yml");
			File groups = new File("plugins/GroupManager/worlds/"+world.getName()+"/groups.yml");
			Configuration uConfig = new Configuration(users);
			Configuration gConfig = new Configuration(groups);
			uConfig.load();
			gConfig.load();
			List<String> usersList = uConfig.getKeys("users");
			List<String> groupsList = gConfig.getKeys("groups");
			
			if(usersList!=null)
			for(String player : usersList) {
				String mainGroup = uConfig.getString("users."+player+".group");
				ps.addGroup(player, mainGroup);
				for(String group : uConfig.getStringList("users."+player+".subgroups", null)) {
				ps.addGroup(player, group);	
				}
			}
			if(groupsList!=null)
			for(String group : groupsList) {
				for(String node : gConfig.getStringList("groups."+group+".permissions", null)) {
					ps.addNode(node, group);
				}
			}
		}
		
	}
	public void importYML() {
		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
		for(World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File perms = new File("plugins/bPermissions/worlds/"+world.getName()+".yml");
			Configuration pConfig = new Configuration(perms);
			pConfig.load();
			List<String> usersList = pConfig.getKeys("players");
			List<String> groupsList = pConfig.getKeys("groups");
			if(usersList!=null)
			for(String player : usersList) {
				for(String group : pConfig.getStringList("players."+player, null)) {
				ps.addGroup(player, group);	
				}
			}
			if(groupsList!=null)
			for(String group : groupsList) {
				for(String node : pConfig.getStringList("groups."+group, null)) {
					ps.addNode(node, group);
				}
			}
			
		}
	}
	public void importPEX() {
		
	WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
	for(World world : plugin.getServer().getWorlds()) {
		PermissionSet ps = wpm.getPermissionSet(world);
		File users =  new File("plugins/PermissionsEx/permissions.yml");

		Configuration pConfig = new Configuration(users);
		pConfig.load();
		List<String> usersList = pConfig.getKeys("users");
		List<String> groupsList = pConfig.getKeys("groups");
		if(usersList!=null)
		for(String player : usersList) {
			for(String group : pConfig.getStringList("users."+player+".group", null)) {
			ps.addGroup(player, group);	
			}
		}
		if(groupsList!=null)
		for(String group : groupsList) {
			for(String node : pConfig.getStringList("groups."+group+".permissions", null)) {
				String prefix = pConfig.getString("groups."+group+".prefix", null);
				String suffix = pConfig.getString("groups."+group+".suffix", null);
				if(prefix != null)
					ps.addNode("prefix.0."+prefix, group);
				if(suffix != null)
					ps.addNode("suffix.0."+suffix, group);
				ps.addNode(node, group);
			}
		}
	}
	
	}
	public void importPermissions3() {
		
	WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
	for(World world : plugin.getServer().getWorlds()) {
		PermissionSet ps = wpm.getPermissionSet(world);
		File users =  new File("plugins/Permissions/"+world.getName()+"/users.yml");
		File groups = new File("plugins/Permissions/"+world.getName()+"/groups.yml");
		Configuration uConfig = new Configuration(users);
		Configuration gConfig = new Configuration(groups);
		uConfig.load();
		gConfig.load();
		List<String> usersList = uConfig.getKeys("users");
		List<String> groupsList = gConfig.getKeys("groups");
		if(usersList!=null)
		for(String player : usersList) {
			for(String group : uConfig.getStringList("users."+player+".groups", null)) {
			ps.addGroup(player, group);	
			}
		}
		if(groupsList!=null)
		for(String group : groupsList) {
			for(String node : gConfig.getStringList("groups."+group+".permissions", null)) {
				ps.addNode(node, group);
			}
		}
	}
	}
	
}
