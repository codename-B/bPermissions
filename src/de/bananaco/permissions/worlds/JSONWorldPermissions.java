package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.json.JSONPermission;

public class JSONWorldPermissions extends PermissionClass {
	/**
	 * The default!
	 */
	private String defaultGroup = "default";

	private Map<String, List<String>> groups;
	private JSONPermission permission;
	private Map<String, List<String>> players;

	public JSONWorldPermissions(World world, Permissions plugin) {
		super(world, plugin);
		this.permission = new JSONPermission(new File(
				"plugins/bPermissions/worlds/" + world.getName() + ".json"));
	}

	public List<String> getAllCachedGroups() {
		List<String> groups = new ArrayList<String>();
		if (this.groups != null)
			for (String group : this.groups.keySet())
				groups.add(group);
		return groups;
	}

	public List<String> getAllCachedPlayers() {
		List<String> players = new ArrayList<String>();
		if (this.players != null)
			for (String player : this.players.keySet())
				players.add(player);
		return players;
	}

	public String getDefaultGroup() {
		return this.defaultGroup;
	}

	public List<String> getGroupNodes(String group) {
		List<String> nodes = new ArrayList<String>();
		if (groups.containsKey(group))
			return groups.get(group);
		return nodes;
	}

	public List<String> getGroups(String player) {
		List<String> groups = getDefaultArrayList();
		if (players.containsKey(player))
			return players.get(player);
		return groups;
	}

	public void reload() {
		permission.load();
		Map<String, Map<String, List<String>>> loc = permission.get();
		groups = loc.get("groups");
		players = loc.get("players");
		defaultGroup = permission.getDefault();
		setupPlayers();
	}

	private void save() {
		permission.put(getDefaultGroup(), players, groups);
		permission.save();
	}

	public void setGroups(String player, List<String> groups) {
		players.put(player, groups);
		save();
		super.setGroups(player, groups);
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		groups.put(group, nodes);
		save();
		super.setNodes(group, nodes);
	}

}