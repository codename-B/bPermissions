package de.bananaco.permissions.interfaces;

import java.util.List;

public interface PromotionTrack {
	
	public void load();
	/**
	 * Promote the player one level up
	 * @param player
	 * @param track
	 */
	public void promote(String player, String track, String world);
	/**
	 * Demote the player one level down
	 * @param player
	 * @param track
	 */
	public void demote(String player, String track, String world);
	/**
	 * Shows if the named track exists
	 * @param track
	 * @return boolean
	 */
	public boolean containsTrack(String track);
	
	public List<String> getGroups(String track);
}
