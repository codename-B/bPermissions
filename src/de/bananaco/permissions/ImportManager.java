package de.bananaco.permissions;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Permission;

public class ImportManager {

	private WorldManager wm = WorldManager.getInstance();
	private final JavaPlugin plugin;
	
	protected ImportManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void importYML() throws Exception {
		for (World world : plugin.getServer().getWorlds()) {
			de.bananaco.bpermissions.api.World wd = wm.getWorld(world.getName());
			File perms = new File("plugins/bPermissions/worlds/"
					+ world.getName() + ".yml");
			YamlConfiguration pConfig = new YamlConfiguration();//new Configuration(perms);
			pConfig.load(perms);
			// Here we grab the different bits and bobs
			ConfigurationSection users = pConfig.getConfigurationSection("players");
			ConfigurationSection groups = pConfig.getConfigurationSection("groups");
			// Load users
			if(users.getKeys(false) != null && users.getKeys(false).size() > 0) {
				Set<String> u = users.getKeys(false);
				for(String usr : u) {
					List<String> g = users.getStringList(usr);
					// Clear the groups in their list firstly
					wd.getUser(usr).getGroupsAsString().clear();
					for(String group : g)
						wd.getUser(usr).addGroup(group);
				}
			}
			// Load groups
			if(groups.getKeys(false) != null && groups.getKeys(false).size() > 0) {
				Set<String> g = groups.getKeys(false);
				for(String grp : g) {
					List<String> p = groups.getStringList(grp);
					for(String perm : p)
						wd.getGroup(grp).getPermissions().add(Permission.loadFromString(perm));
				}
			}

		}
	}
	
}
