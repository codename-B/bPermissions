package de.bananaco.permissions.info;

import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.WorldManager;

public class InfoReader {

	private WorldManager wm = WorldManager.getInstance();

	/**
	 * Workaround for lazy people - group prefix
	 * 
	 * @param group
	 * @param world
	 * @return String
	 */
	public String getGroupPrefix(String group, String world) {
		return getGroupValue(group, world, "prefix");
	}

	/**
	 * Workaround for lazy people - group suffix
	 * 
	 * @param group
	 * @param world
	 * @return String
	 */
	public String getGroupSuffix(String group, String world) {
		return getGroupValue(group, world, "suffix");
	}

	/**
	 * The workhorse of getGroupValue();
	 * 
	 * @param group
	 * @param world
	 * @param valueToGet
	 * @return String
	 */
	public String getGroupValue(String group, String world, String valueToGet) {
		if(group == null || world == null || valueToGet == null)
			return "";
		if(wm.getWorld(world) == null)
			return "";
		
		return wm.getWorld(world).getGroup(group).getEffectiveValue(valueToGet);
	}

	/**
	 * Workaround for lazy people - player prefix
	 * 
	 * @param player
	 * @return String
	 */
	public String getPrefix(Player player) {
		return getValue(player, "prefix");
	}

	/**
	 * Workaround for lazy people - player prefix
	 * 
	 * @param player
	 * @param world
	 * @return String
	 */
	public String getPrefix(String player, String world) {
		return getValue(player, world, "prefix");
	}

	/**
	 * Workaround for lazy people - player suffix
	 * 
	 * @param player
	 * @return String
	 */
	public String getSuffix(Player player) {
		return getValue(player, "suffix");
	}

	/**
	 * Workaround for lazy people - player suffix
	 * 
	 * @param player
	 * @param world
	 * @return String
	 */
	public String getSuffix(String player, String world) {
		return getValue(player, world, "suffix");
	}

	/**
	 * Bridging method - calls getValue(String player, String world, String
	 * valueToGet);
	 * 
	 * @param player
	 * @param valueToGet
	 * @return String
	 */
	public String getValue(Player player, String valueToGet) {
		if(player == null)
			return "";
		return getValue(player.getName(), player.getWorld().getName(),
				valueToGet);
	}

	/**
	 * The workhorse of getValue();
	 * 
	 * @param player
	 * @param world
	 * @param valueToGet
	 * @return String
	 */
	public String getValue(String player, String world, String valueToGet) {
		if(player == null || world == null || valueToGet == null)
			return "";
		if(wm.getWorld(world) == null)
			return "";
		return wm.getWorld(world).getUser(player).getEffectiveValue(valueToGet);
	}

	public void instantiate() {
	    //
	}

}
