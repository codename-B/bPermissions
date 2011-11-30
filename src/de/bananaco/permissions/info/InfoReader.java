package de.bananaco.permissions.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import com.ubempire.binfo.PlayerInfo;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class InfoReader implements PlayerInfo {

	private WorldPermissionsManager wpm;

	private final Map<String, String> groups = new HashMap<String, String>();
	private final Map<String, String> players = new HashMap<String, String>();

	private final boolean cache;

	public InfoReader(boolean cache) {
		this.cache = cache;
	}

	public InfoReader() {
		this(true);
	}

	public void clear() {
		groups.clear();
		players.clear();
	}

	public void instantiate() {
		wpm = Permissions.getWorldPermissionsManager();
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
		if (groups.containsKey(group+"."+world+"."+valueToGet))
			return groups.get(group+"."+world+"."+valueToGet);

		// Blame CraftIRC
		if (world == null || world.equals("")) {
			System.err
					.println("[bPermissions] Some silly developer is checking for a blank world!");
			return "BLANKWORLD";
		}

		String value = "";
		int priority = -1;
		for (String set : wpm.getPermissionSet(world).getGroupNodes(group)) {
			if (!set.startsWith("^")) {
				String name = set;
				String[] index = name.split("\\.", 4);
				if (index.length == 3 && index[0].equals(valueToGet)) {
					int pr = Integer.parseInt(index[1]);
					if (pr > priority)
						value = index[2];
				}
			}
		}

		if (!value.equals("") && cache)
			groups.put(group+"."+world+"."+valueToGet, value);

		return value;
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
		if (players.containsKey(player+"."+world+"."+valueToGet))
			return players.get(player+"."+world+"."+valueToGet);

		if (world == null || world.equals("")) {
			System.err
					.println("[bPermissions] Some silly developer is checking for a blank world!");
			return "BLANKWORLD";
		}

		String value = "";
		int priority = -1;
		for (String set : wpm.getPermissionSet(world).getPlayerNodes(player) != null ? wpm
				.getPermissionSet(world).getPlayerNodes(player)
				: new ArrayList<String>()) {
			if (!set.startsWith("^")) {
				String name = set;
				String[] index = name.split("\\.", 4);
				if (index.length == 3 && index[0].equals(valueToGet)) {
					int pr = Integer.parseInt(index[1]);
					if (pr > priority) {
						value = index[2];
						priority = pr;
					}
				}
			}
		}

		if (!value.equals("") && cache)
			players.put(player+"."+world+"."+valueToGet, value);

		return value;
	}

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
	 * Bridging method - calls getValue(String player, String world, String
	 * valueToGet);
	 * 
	 * @param player
	 * @param valueToGet
	 * @return String
	 */
	public String getValue(Player player, String valueToGet) {
		return getValue(player.getName(), player.getWorld().getName(),
				valueToGet);
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
	 * Workaround for lazy people - player suffix
	 * 
	 * @param player
	 * @return String
	 */
	public String getSuffix(Player player) {
		return getValue(player, "suffix");
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
	 * @param world
	 * @return String
	 */
	public String getSuffix(String player, String world) {
		return getValue(player, world, "suffix");
	}

}
