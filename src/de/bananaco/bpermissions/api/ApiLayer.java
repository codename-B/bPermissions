package de.bananaco.bpermissions.api;

import java.util.Set;

import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class ApiLayer {
	private static WorldManager wm = WorldManager.getInstance();
	
	/*
	 * Used for getting values
	 */
	
	/**
	 * Used to get the groups of a user or a group as a String[] array
	 * @param world
	 * @param type
	 * @param name
	 * @return String[]
	 */
	public static String[] getGroups(String world, CalculableType type, String name) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		Set<String> g = c.getGroupsAsString();
		String[] groups = g.toArray(new String[g.size()]);
		return groups;
	}
	/**
	 * Used to get the permissions of a user or a group as a Permission[] array
	 * Remember, Permission can be true or false
	 * @param world
	 * @param type
	 * @param name
	 * @return Permission[]
	 */
	public static Permission[] getPermissions(String world, CalculableType type, String name) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		Set<Permission> p = c.getPermissions();
		Permission[] permissions = p.toArray(new Permission[p.size()]);
		return permissions;
	}
	/**
	 * Used to return the metadata value for a user or a group. Will never return null but may return ""
	 * @param world
	 * @param type
	 * @param name
	 * @param key
	 * @return String
	 */
	public static String getValue(String world, CalculableType type, String name, String key) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		return c.getValue(key);
	}
	
	/*
	 * Used for setting values
	 */
	
	/**
	 * Used to add a single group to a user or a group
	 * @param world
	 * @param type
	 * @param name
	 * @param groupToAdd
	 */
	public static void addGroup(String world, CalculableType type, String name, String groupToAdd) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.addGroup(groupToAdd);
	}
	/**
	 * Used to set the group of a user or a group
	 * @param world
	 * @param type
	 * @param name
	 * @param groupToAdd
	 */
	public static void setGroup(String world, CalculableType type, String name, String groupToAdd) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.getGroupsAsString().clear();
		c.addGroup(groupToAdd);
	}
	/**
	 * Used to remove a single group from a user or a group
	 * @param world
	 * @param type
	 * @param name
	 * @param groupToRemove
	 */
	public static void removeGroup(String world, CalculableType type, String name, String groupToRemove) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.removeGroup(groupToRemove);
	}
	/**
	 * Returns true if the user or group directly carries the named group as a child group
	 * @param world
	 * @param type
	 * @param name
	 * @param group
	 * @return boolean
	 */
	public static boolean hasGroup(String world, CalculableType type, String name, String group) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		return c.hasGroup(group);
	}
	/**
	 * Returns true if the user or group or any inherited groups carry the named group as a child group
	 * @param world
	 * @param type
	 * @param name
	 * @param group
	 * @return boolean
	 */
	public static boolean hasGroupRecursive(String world, CalculableType type, String name, String group) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		return c.hasGroupRecursive(group);
	}
	/**
	 * Adds a single permission (String, Boolean) to a user or a group
	 * @param world
	 * @param type
	 * @param name
	 * @param permissionToAdd
	 */
	public static void addPermission(String world, CalculableType type, String name, Permission permissionToAdd) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.addPermission(permissionToAdd.name(), permissionToAdd.isTrue());
	}
	/**
	 * Removes a single permission (String, Boolean) from a user or a group
	 * The permission object is just for consistency, the boolean does not matter here.
	 * @param world
	 * @param type
	 * @param name
	 * @param permissionToRemove
	 */
	public static void removePermission(String world, CalculableType type, String name, Permission permissionToRemove) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.removePermission(permissionToRemove.name());
	}
	/**
	 * Used to set the metadata value for a user or a group
	 * @param world
	 * @param type
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setValue(String world, CalculableType type, String name, String key, String value) {
		World w = wm.getWorld(world);
		Calculable c = w.get(name, type);
		c.setValue(key, value);
	}
	
}
