package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.List;
import org.bukkit.World;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.config.Configuration;

class NewWorldPermissions extends PermissionClass {
	/**
	 * The configuration object
	 */
	private final Configuration c;

	public NewWorldPermissions(World world, Permissions plugin) {
		super(world, plugin);
		this.c = new Configuration(new File("plugins/bPermissions/worlds/"
				+ world.getName() + ".bml"));
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
		List<String> ls = c.getStringList("default");
		if (ls.size() == 0)
			return "default";
		return ls.get(0);
	}

	@Override
	public List<String> getGroupNodes(String group) {
		group = caseCheck(group);
		
		List<String> groupNodes = c.getStringList("groups." + group, null);
		return groupNodes;
	}

	@Override
	public List<String> getGroups(String player) {
		player = caseCheck(player);
		
		List<String> playerGroups = c.getStringList("players." + player, null);
		return playerGroups;
	}

	@Override
	public void reload() {
		c.load();
		c.save();
		setupPlayers();
	}

	@Override
	public void setGroups(String player, List<String> groups) {
		player = caseCheck(player);
		
		c.setProperty("players." + player, groups);
		c.save();
		super.setGroups(player, groups);
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		group = caseCheck(group);
		
		c.setProperty("groups." + group, nodes);
		c.save();
		super.setNodes(group, nodes);
	}

}
