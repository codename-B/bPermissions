package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.Calculable;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.User;

public abstract class WorldPermissions extends PermissionClass {

	private final Map<String, Group> groups;
	private final Map<String, User> users;
	private final World world;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WorldPermissions(World world, Permissions plugin) {
		super(world, plugin);
		this.world = world;
		this.users = new HashMap();
		this.groups = new HashMap();
	}
	
	@Override
	public void reload() {
		groups.clear();
		users.clear();
		load();
		save();
	}

	/**
	 * This adds the Calculable to either groups or users depending
	 * on if the calculable is an instance of either.
	 * If the calculable is not an instance of a group or a user, it is
	 * not added.
	 * This means you cannot add base calculables (or any other class which
	 * extends calculable) to this.
	 * @param calculable
	 */
	public void add(Calculable calculable) {
		if (calculable instanceof User)
			users.put(calculable.getName(), (User) calculable);
		else if (calculable instanceof Group)
			groups.put(calculable.getName(), (Group) calculable);
		else
			System.err.println("Calculable not instance of User or Group!");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getAllCachedGroups() {
		return new ArrayList(getGroupsAsString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getAllCachedPlayers() {
		return new ArrayList(getUsersAsString());
	}

	public Group getGroup(String name) {
		return groups.get(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getGroupGroups(String group) {
		if (getGroup(group) == null) {
			Group gr = new Group(group, null, null, this);
			add(gr);
			gr.calculateEffectivePermissions();
		}
		return new ArrayList(getGroup(group).getGroupsAsString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getGroupNodes(String group) {
		if (getGroup(group) == null) {
			Group gr = new Group(group, null, null, this);
			add(gr);
			gr.calculateEffectivePermissions();
		}
		return new ArrayList(getGroup(group).getPermissionsAsString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Group> getGroups() {
		Set<String> names = groups.keySet();
		Set<Group> groups = new HashSet();
		for (String name : names)
			groups.add(getGroup(name));
		return groups;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getGroups(String player) {
		if (getUser(player) == null) {
			User us = new User(player, getDefaultArrayList(), null, this);
			add(us);
			us.calculateEffectivePermissions();
		}
		return new ArrayList(getUser(player).getGroupsAsString());
	}

	public Set<String> getGroupsAsString() {
		return groups.keySet();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getPlayerNodes(String player) {
		if (getUser(player) == null) {
			User us = new User(player, null, null, this);
			add(us);
			us.calculateEffectivePermissions();
		}
		return new ArrayList(getUser(player).getPermissionsAsString());
	}

	@Override
	public Set<Permission> getPlayerPermissions(String player) {
		if (getUser(player) == null) {
			User us = new User(player, null, null, this);
			add(us);
			us.calculateEffectivePermissions();
		}
		return getUser(player).getEffectivePermissions();
	}

	public User getUser(String name) {
		return users.get(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<User> getUsers() {
		Set<String> names = users.keySet();
		Set<User> users = new HashSet();
		for (String name : names)
			users.add(getUser(name));
		return users;
	}

	public Set<String> getUsersAsString() {
		return users.keySet();
	}

	public String getWorldName() {
		if (world == null)
			return "world";
		return world.getName();
	}

	@Override
	public WorldPermissions getWorldPermissions() {
		return this;

	}

	public int hashCode() {
		return world.hashCode();
	}

	public abstract void load();

	public abstract void save();

	@Override
	public void setGroupGroups(String group, List<String> groups) {
		Group gr = getGroup(group);
		Set<String> grgr = gr.getGroupsAsString();
		grgr.clear();
		grgr.addAll(groups);
		gr.calculateEffectivePermissions();
		save();
	}

	@Override
	public void setGroups(String player, List<String> groups) {
		User us = getUser(player);
		Set<String> gr = us.getGroupsAsString();
		gr.clear();
		gr.addAll(groups);
		us.calculateEffectivePermissions();
		save();
	}

	@Override
	public void setNodes(String group, List<String> nodes) {
		Group gr = getGroup(group);
		Set<Permission> pr = gr.getPermissions();
		pr.clear();
		pr.addAll(Permission.loadFromString(nodes));
		gr.calculateEffectivePermissions();
		save();
	}

	@Override
	public void setPlayerNodes(String player, List<String> nodes) {
		User us = getUser(player);
		Set<Permission> pr = us.getPermissions();
		pr.clear();
		pr.addAll(Permission.loadFromString(nodes));
		us.calculateEffectivePermissions();
		save();
	}
}
