package de.bananaco.permissions.interfaces;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface PermissionSet {
	/**
	 * The World for which these permissions apply
	 * 
	 * @return World
	 */
	public World getWorld();

	/**
	 * Setup this PermissionSet
	 */
	public void setup();

	/**
	 * Reload this PermissionSet
	 */
	public void reload();

	/**
	 * Add a node
	 * 
	 * @param node
	 * @param group
	 */
	public void addNode(String node, String group);

	/**
	 * Remove a node
	 * 
	 * @param node
	 * @param group
	 */
	public void removeNode(String node, String group);

	/**
	 * Get a groups nodes
	 * 
	 * @param group
	 * @return List<String>
	 */
	public List<String> getGroupNodes(String group);

	/**
	 * Get a players nodes
	 * 
	 * @param player
	 * @return List<String>
	 */
	public List<String> getPlayerNodes(Player player);

	/**
	 * Get a players nodes
	 * 
	 * @param player
	 * @return List<String>
	 */
	public List<String> getPlayerNodes(String player);

	/**
	 * Get a players groups
	 * 
	 * @param player
	 * @return List<String>
	 */
	public List<String> getGroups(Player player);

	/**
	 * Get a players groups
	 * 
	 * @param player
	 * @return List<String>
	 */
	public List<String> getGroups(String player);

	/**
	 * Add a group to a player
	 * 
	 * @param player
	 */
	public void addGroup(Player player, String group);

	/**
	 * Add a group to a player
	 * 
	 * @param player
	 */
	public void addGroup(String player, String group);

	/**
	 * Set a player to a single group
	 * 
	 * @param player
	 * @param group
	 */
	public void setGroup(Player player, String group);

	/**
	 * Set a player to a single group
	 * 
	 * @param player
	 * @param group
	 */
	public void setGroup(String player, String group);

	/**
	 * Remove a group from a player
	 * 
	 * @param player
	 */
	public void removeGroup(Player player, String group);

	/**
	 * Remove a group from a player
	 * 
	 * @param Player
	 */
	public void removeGroup(String player, String group);

	/**
	 * Sets up the online players in that world
	 */
	public void setupPlayers();

	/**
	 * Method added to account for infinite loop in the bridge, silly me.
	 * 
	 * @param player
	 * @return boolean
	 */
	public boolean has(Player player, String node);

	public String getDefaultGroup();

	public void setGroups(Player player, List<String> groups);

	public void setGroups(String player, List<String> groups);

	public void setNodes(String group, List<String> nodes);

	public void setupPlayer(Player player);

	public void setupPlayer(String player);

	public List<String> getAllCachedGroups();

	public List<String> getAllCachedPlayers();
}
