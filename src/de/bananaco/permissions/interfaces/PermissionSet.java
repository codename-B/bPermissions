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
	 * Gets the default gruop
	 * @return String
	 */
	public String getDefaultGroup();
	/**
	 * New, use to set the groups to a specific list
	 * @param player
	 * @param groups
	 */
	public void setGroups(Player player, List<String> groups);
	/**
	 * New, use to set the groups to a specific list
	 * @param player
	 * @param groups
	 */
	public void setGroups(String player, List<String> groups);
	/**
	 * New, use to set the nodes to a specific list
	 * @param group
	 * @param nodes
	 */
	public void setNodes(String group, List<String> nodes);
	/**
	 * New, use to setup only one player
	 * @param player
	 */
	public void setupPlayer(Player player);
	/**
	 * New, use to search for (and if online) setup one player.
	 * @param player
	 */
	public void setupPlayer(String player);
	/**
	 * Gets all cached groups
	 * @return List<String>
	 */
	public List<String> getAllCachedGroups();
	/**
	 * Gets all cached players
	 * @return List<String>
	 */
	public List<String> getAllCachedPlayers();
	/**
	 * Recursively searches through all cached players/groups and returns the players with a specific group
	 * @param group
	 * @return
	 */
	public List<String> getAllCachedPlayersWithGroup(String group);
	/**
	 * New method added for getAllCachedPlayersWithGroup(String group);
	 * @param player
	 * @param group
	 * @return boolean
	 */
	public boolean hasGroup(Player player, String group);
	/**
	 * New method added for getAllCachedPlayersWithGroup(String group);
	 * @param player
	 * @param group
	 * @return boolean
	 */
	public boolean hasGroup(String player, String group);

	void setGroupGroups(String group, List<String> groups);

	void setPlayerNodes(String player, List<String> nodes);

	List<String> getGroupGroups(String group);
}
