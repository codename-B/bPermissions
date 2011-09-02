package de.bananaco.permissions.interfaces;

import java.util.List;

import org.bukkit.entity.Player;

public interface TransitionSet {
	/**
	 * Adds a node that is never written to a config file to a specific player
	 * @param player
	 * @param node
	 */
	public void addTransNode(Player player, String node);
	/**
	 * Adds a node that is never written to a config file to a specific player
	 * @param player
	 * @param node
	 */
	public void addTransNode(String player, String node);
	/**
	 * Removes a node that is never written to a config file from a specific player
	 * @param player
	 * @param node
	 */
	public void removeTransNode(Player player, String node);
	/**
	 * Removes a node that is never written to a config file from a specific player
	 * @param player
	 * @param node
	 */
	public void removeTransNode(String player, String node);
	/**
	 * Clears all the transitional nodes for a player
	 * @param player
	 */
	public void clearTransNodes(Player player);
	/**
	 * Clears all the transitional nodes for a player
	 * @param player
	 */
	public void clearTransNodes(String player);
	/**
	 * Lists all transitional nodes for a player
	 * @param player
	 * @return List<String>
	 */
	public List<String> listTransNodes(Player player);
	/**
	 * Lists all transitional nodes for a player
	 * @param player
	 * @return List<String>
	 */
	public List<String> listTransNodes(String player);

}
