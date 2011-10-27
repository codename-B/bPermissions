package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.List;
import org.bukkit.World;
import de.bananaco.permissions.oldschool.Configuration;

import de.bananaco.permissions.Permissions;

class WorldPermissions extends PermissionClass {
	/**
	 * The configuration object
	 */
	private final Configuration c;

	public WorldPermissions(World world, Permissions plugin) {
		super(world, plugin);
		this.c = new Configuration(new File("plugins/bPermissions/worlds/"
				+ world.getName() + ".yml"));
	}

	@Override
	public List<String> getAllCachedGroups() {
		return c.getKeys("groups");
	}

	@Override
	public List<String> getAllCachedPlayers() {
		return c.getKeys("players");
	}

	@Override
	public String getDefaultGroup() {
		return c.getString("default", "default");
	}

	@Override
	public List<String> getGroupNodes(String group) {
		List<String> groupNodes = c.getStringList("groups." + group, null);
		return groupNodes;
	}

	@Override
	public List<String> getGroups(String player) {
		List<String> playerGroups = c.getStringList("players." + player, null);
		if (playerGroups == null || playerGroups.size() == 0) {
			return getDefaultArrayList();
		}
		return playerGroups;
	}

	@Override
	public void reload() {
		c.load();
		setupPlayers();
	}

	@Override
	public void setGroups(String player, List<String> groups) {
		c.setProperty("players." + player, groups);
		c.save();
		if(groups.size() == 0)
			c.removeProperty("players." + player);
		super.setGroups(player, groups);
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		c.setProperty("groups." + group, nodes);
		c.save();
		if(nodes.size() == 0)
			c.removeProperty("groups." + group);
		super.setNodes(group, nodes);
	}

}
