package de.bananaco.permissions.set;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bananaco.permissions.util.Calculable;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.User;

public abstract class WorldPermissionSet {

	private final String world;
	private final Map<String, User> users;
	private final Map<String, Group> groups;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WorldPermissionSet(String world) {
		this.world = world;
		this.users = new HashMap();
		this.groups = new HashMap();
	}
	
	public String getWorld() {
		return world;
	}
	
	public int hashCode() {
		return world.hashCode();
	}
	
	public User getUser(String name) {
		return users.get(name);
	}
	
	public Group getGroup(String name) {
		return groups.get(name);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<User> getUsers() {
		Set<String> names = users.keySet();
		Set<User> users = new HashSet();
		for(String name : names)
			users.add(getUser(name));
		return users;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Group> getGroups() {
		Set<String> names = groups.keySet();
		Set<Group> groups = new HashSet();
		for(String name : names)
			groups.add(getGroup(name));
		return groups;
	}
	
	public void add(Calculable calculable) {
		if(calculable instanceof User)
			users.put(calculable.getName(), (User) calculable);
		else if(calculable instanceof Group)
			groups.put(calculable.getName(), (Group) calculable);
		else
			System.err.println("Calculable not instance of User or Group!");
	}
	
	public abstract void load();
	
	public abstract void save();
}
