package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;
import de.bananaco.permissions.interfaces.PermissionSet;

public class WrapperPermissionSet implements PermissionSet {
	
	de.bananaco.bpermissions.api.World world;
	
	public WrapperPermissionSet(de.bananaco.bpermissions.api.World world) {
		this.world = world;
	}

	@Override
	public void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	@Override
	public void addGroup(String player, String group) {
		world.getUser(player).addGroup(group);
	}

	@Override
	public void addGroupToGroup(String main, String group) {
		world.getGroup(main).addGroup(group);
	}

	@Override
	public void addNode(String node, String group) {
		world.getGroup(group).addPermission(node, true);
	}

	@Override
	public List<String> getAllCachedGroups() {
		List<String> groups = new ArrayList<String>();
		for(Calculable group : world.getAll(CalculableType.GROUP)) {
			groups.add(group.getName());
		}
		return groups;
	}

	@Override
	public List<String> getAllCachedPlayers() {
		List<String> players = new ArrayList<String>();
		for(Calculable player : world.getAll(CalculableType.USER)) {
			players.add(player.getName());
		}
		return players;
	}

	@Override
	public List<String> getAllCachedPlayersWithGroup(String group) {
		List<String> players = new ArrayList<String>();
		for(Calculable player : world.getAll(CalculableType.USER)) {
			if(player.getGroupsAsString().contains(group))
				players.add(player.getName());
		}
		return players;
	}

	@Override
	public String getDefaultGroup() {
		return world.getDefaultGroup();
	}

	@Override
	public List<String> getGroupGroups(String group) {
		return world.getGroup(group).serialiseGroups();
	}

	@Override
	public List<String> getGroupNodes(String group) {
		return world.getGroup(group).serialisePermissions();
	}

	@Override
	public List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public List<String> getGroups(String player) {
		return world.getUser(player).serialiseGroups();
	}

	@Override
	public List<String> getPlayerNodes(Player player) {
		return getPlayerNodes(player.getName());
	}

	@Override
	public List<String> getPlayerNodes(String player) {
		return world.getUser(player).serialisePermissions();
	}

	@Override
	public Set<Permission> getPlayerPermissions(String player) {
		return world.getUser(player).getPermissions();
	}

	@Override
	public World getWorld() {
		return Bukkit.getWorld(world.getName());
	}

	@Override
	public boolean hasGroup(Player player, String group) {
		return hasGroup(player.getName(), group);
	}

	@Override
	public boolean hasGroup(String player, String group) {
		return world.getUser(player).getGroupsAsString().contains(group);
	}

	@Override
	public void reload() {
		world.load();
	}

	@Override
	public void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public void removeGroup(String player, String group) {
		world.getUser(player).removeGroup(group);
	}

	@Override
	public void removeGroupFromGroup(String groupA, String groupB) {
		world.getGroup(groupA).removeGroup(groupB);
	}

	@Override
	public void removeNode(String node, String group) {
		world.getGroup(group).removePermission(node);
	}

	@Override
	public void setGroup(Player player, String group) {
		setGroup(player.getName(), group);
	}

	@Override
	public void setGroup(String player, String group) {
		world.getUser(player).getGroupsAsString().clear();
		world.getUser(player).addGroup(group);
	}

	@Override
	public void setGroupGroups(String group, List<String> groups) {
		world.getGroup(group).getGroupsAsString().clear();
		for(String g : groups)
			world.getGroup(group).addGroup(g);		
	}

	@Override
	public void setGroups(Player player, List<String> groups) {
		setGroups(player.getName(), groups);
	}

	@Override
	public void setGroups(String player, List<String> groups) {
		world.getUser(player).getGroupsAsString().clear();
		for(String g : groups)
			world.getUser(player).addGroup(g);	
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		world.getGroup(group).getPermissions().clear();
		for(String p : nodes)
			world.getGroup(group).addPermission(p, true);
	}

	@Override
	public void setPlayerNodes(String player, List<String> nodes) {
		world.getUser(player).getPermissions().clear();
		for(String p : nodes)
			world.getUser(player).addPermission(p, true);		
	}

	@Override
	public void setup() {
		world.load();
	}

	@Override
	public void setupPlayer(Player player) {
		// TODO Implement
		setupPlayer(player.getName());
	}

	@Override
	public void setupPlayer(String player) {
		// TODO Implement
	}

	@Override
	public void setupPlayers() {
		
	}

	@Override
	public void addPlayerNode(String node, String player) {
		world.getUser(player).addPermission(node, true);
	}

	@Override
	public void removePlayerNode(String node, String player) {
		world.getUser(player).removePermission(node);
	}

	@Override
	public boolean has(Player player, String node) {
		return world.getUser(player.getName()).hasPermission(node);
	}
	
	

}
