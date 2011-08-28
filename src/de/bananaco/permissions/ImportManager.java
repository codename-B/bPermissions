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
	
	public void importGroupManager() {

		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
		for(World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File users = new File("plugins/GroupManager/"+world.getName()+"/users.yml");
			File groups = new File("plugins/GroupManager/"+world.getName()+"/groups.yml");
			Configuration uConfig = new Configuration(users);
			Configuration gConfig = new Configuration(groups);
			uConfig.load();
			gConfig.load();
			List<String> usersList = uConfig.getKeys("users");
			List<String> groupsList = gConfig.getKeys("groups");
			for(String player : usersList) {
				String mainGroup = uConfig.getString("users."+player+".group");
				ps.addGroup(player, mainGroup);
				for(String group : uConfig.getStringList("users."+player+".subgroups", null)) {
				ps.addGroup(player, group);	
				}
			}
			
			for(String group : groupsList) {
				for(String node : gConfig.getStringList("groups."+group+".permissions", null)) {
					ps.addNode(node, group);
				}
			}
		}
		
	}
	
	public void importPermissions3() {
		
	WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();	
	for(World world : plugin.getServer().getWorlds()) {
		PermissionSet ps = wpm.getPermissionSet(world);
		File users = new File("plugins/Permissions/"+world.getName()+"/users.yml");
		File groups = new File("plugins/Permissions/"+world.getName()+"/groups.yml");
		Configuration uConfig = new Configuration(users);
		Configuration gConfig = new Configuration(groups);
		uConfig.load();
		gConfig.load();
		List<String> usersList = uConfig.getKeys("users");
		List<String> groupsList = gConfig.getKeys("groups");
		for(String player : usersList) {
			for(String group : uConfig.getStringList("users."+player+".groups", null)) {
			ps.addGroup(player, group);	
			}
		}
		for(String group : groupsList) {
			for(String node : gConfig.getStringList("groups."+group+".permissions", null)) {
				ps.addNode(node, group);
			}
		}
	}
	}
	
}
