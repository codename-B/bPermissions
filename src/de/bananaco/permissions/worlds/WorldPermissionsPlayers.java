package de.bananaco.permissions.worlds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

/**
 * Created to verify the stored permissions of a player against a world
 * @author codename_B
 */
public abstract class WorldPermissionsPlayers {
	/**
	 * Stores the world keyed against the playername
	 * It's ok to do this as a player can only have one world!
	 */
	private final Map<String, String> players;
	
	public WorldPermissionsPlayers() {
		players = new HashMap<String, String>();
	}
	/**
	 * Checks to see if the last known world for the player is their current world
	 * (or if we even have a last known world for them)
	 * @param player
	 * @return boolean
	 */
	public boolean isCorrect(Player player) {
		// If there is no entry, it obviously isn't correct
		if(!players.containsKey(player.getName())) {
			return false;
		}
		// Otherwise we check the two
		String against = players.get(player.getName());
		String world = player.getWorld().getName();
		// Then a simple check to see if they are equal
		return against.equals(world);
	}
	/**
	 * Updates the stored entry with the correct world of the player
	 * @param player
	 */
	public void correct(Player player) {
		players.put(player.getName(), player.getWorld().getName());
	}
}
