package de.bananaco.permissions;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import de.bananaco.permissions.oldschool.Configuration;

import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.User;
import de.bananaco.permissions.worlds.WorldPermissions;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class ImportManager {
	private final JavaPlugin plugin;

	public ImportManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void importGroupManager() {

		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
		for (World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File users = new File("plugins/GroupManager/worlds/"
					+ world.getName() + "/users.yml");
			File groups = new File("plugins/GroupManager/worlds/"
					+ world.getName() + "/groups.yml");
			Configuration uConfig = new Configuration(users);
			Configuration gConfig = new Configuration(groups);
			uConfig.load();
			gConfig.load();
			List<String> usersList = uConfig.getKeys("users");
			List<String> groupsList = gConfig.getKeys("groups");

			if (usersList != null)
				for (String player : usersList) {
					String mainGroup = uConfig.getString("users." + player
							+ ".group");
					ps.addGroup(player, mainGroup);
					for (String group : uConfig.getStringList("users." + player
							+ ".subgroups", null)) {
						ps.addGroup(player, group);
					}
				}
			if (groupsList != null)
				for (String group : groupsList) {
					for (String node : gConfig.getStringList("groups." + group
							+ ".permissions", null)) {
						ps.addNode(node, group);
					}
				}
		}

	}

	@SuppressWarnings("unchecked")
	public void importPermissions3() {

		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
		for (World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			WorldPermissions wp = ps.getWorldPermissions();
			
			File users = new File("plugins/Permissions/" + world.getName()
					+ "/users.yml");
			File groups = new File("plugins/Permissions/" + world.getName()
					+ "/groups.yml");
			
			YamlConfiguration uConfig = new YamlConfiguration();
			YamlConfiguration gConfig = new YamlConfiguration();
			try {
			uConfig.load(users);
			gConfig.load(groups);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ConfigurationSection usConfig = uConfig.getConfigurationSection("users");
			ConfigurationSection grConfig = gConfig.getConfigurationSection("groups");
			
			Set<String> usersList = null;
			if(usConfig != null)
				usersList = usConfig.getKeys(false);
			Set<String> groupsList = null;
			if(grConfig != null)
				groupsList = grConfig.getKeys(false);
			
			if (usersList != null)
				for (String player : usersList) {
					User user = wp.getUser(player);
					try {
					List<String> p = uConfig.getList("users."+player+".permissions");
					List<String> i = uConfig.getList("users."+player+".groups");
					
					if(p != null)
						user.getPermissions().addAll(Permission.loadFromString(p));
					if(i != null)
						user.getGroupsAsString().addAll(i);
					} catch (Exception e) {
						System.err.println("Error importing user: "+player);
					}
				}
			
			if (groupsList != null)
				for (String group : groupsList) {
					Group gr = wp.getGroup(group);
					try {
					List<String> p = gConfig.getStringList("groups."+group+".permissions");
					List<String> i = gConfig.getStringList("groups."+group+".inheritance");
					
					if(p != null)
						gr.getPermissions().addAll(Permission.loadFromString(p));
					if(i != null)
						gr.getGroupsAsString().addAll(i);
					} catch (Exception e) {
						System.err.println("Error importing group: "+group);
					}
				}
			wp.save();
		}
	}

	public void importPermissionsBukkit() {
		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
		for (World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File perms = new File("plugins/PermissionsBukkit/config.yml");
			Configuration pConfig = new Configuration(perms);
			pConfig.load();
			List<String> usersList = pConfig.getKeys("users");
			List<String> groupsList = pConfig.getKeys("groups");
			if (usersList != null)
				for (String player : usersList) {
					List<String> groups = pConfig.getStringList("users."
							+ player + "groups", null);
					for (String group : groups)
						ps.addGroup(player, group);
				}
			if (groupsList != null)
				for (String group : groupsList) {
					List<String> nodes = pConfig.getKeys("groups." + group
							+ ".permissions");
					List<String> wnodes = pConfig.getKeys("groups." + group
							+ ".worlds." + world.getName());
					if (nodes != null)
						for (String node : nodes)
							ps.addNode(!pConfig.getBoolean("groups." + group
									+ ".permissions." + node, false) ? node
									: "^" + node, group);
					if (wnodes != null)
						for (String node : wnodes)
							ps.addNode(
									!pConfig.getBoolean("groups." + group
											+ ".worlds." + world.getName()
											+ "." + node, false) ? node : "^"
											+ node, group);

				}
		}
	}

	public void importPEX() {

		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
		for (World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File users = new File("plugins/PermissionsEx/permissions.yml");

			Configuration pConfig = new Configuration(users);
			pConfig.load();
			List<String> usersList = pConfig.getKeys("users");
			List<String> groupsList = pConfig.getKeys("groups");
			if (usersList != null)
				for (String player : usersList) {
					for (String group : pConfig.getStringList("users." + player
							+ ".group", null)) {
						ps.addGroup(player, group);
					}
				}
			if (groupsList != null)
				for (String group : groupsList) {
					for (String node : pConfig.getStringList("groups." + group
							+ ".permissions", null)) {
						String prefix = pConfig.getString("groups." + group
								+ ".prefix", null);
						String suffix = pConfig.getString("groups." + group
								+ ".suffix", null);
						if (prefix != null)
							ps.addNode("prefix.0." + prefix, group);
						if (suffix != null)
							ps.addNode("suffix.0." + suffix, group);
						ps.addNode(node, group);
					}
				}
		}

	}

	public void importYML() {
		WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
		for (World world : plugin.getServer().getWorlds()) {
			PermissionSet ps = wpm.getPermissionSet(world);
			File perms = new File("plugins/bPermissions/worlds/"
					+ world.getName() + ".yml");
			Configuration pConfig = new Configuration(perms);
			pConfig.load();
			List<String> usersList = pConfig.getKeys("players");
			List<String> groupsList = pConfig.getKeys("groups");
			if (usersList != null)
				for (String player : usersList) {
					for (String group : pConfig.getStringList("players."
							+ player, null)) {
						ps.addGroup(player, group);
					}
				}
			if (groupsList != null)
				for (String group : groupsList) {
					for (String node : pConfig.getStringList("groups." + group,
							null)) {
						ps.addNode(node, group);
					}
				}

		}
	}

}
