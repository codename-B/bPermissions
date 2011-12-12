package de.bananaco.permissions.info;

import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.User;
import de.bananaco.permissions.worlds.WorldPermissions;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class InfoReader {

	private WorldPermissionsManager wpm;

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
		// Blame CraftIRC
		if (world == null || world.equals("")) {
			System.err.println("[bPermissions] Some silly developer is checking for a blank world!");
			return "BLANKWORLD";
		}
		
		WorldPermissions perms = wpm.getPermissionSet(world).getWorldPermissions();
		Group gr = perms.getGroup(group);
		if(gr.contains(valueToGet))
			return gr.getValue(valueToGet);
		
		String value = "";
		int priority = -1;
		for (Permission perm : gr.getEffectivePermissions()) {
			if (perm.isTrue()) {
				String name = perm.name();
				String[] index = name.split("\\.", 3);
				if (index.length == 3 && index[0].equals(valueToGet)) {
					int pr = Integer.parseInt(index[1]);
					if (pr > priority) {
						value = index[2];
						priority = pr;
					}
				}
			}
		}
		gr.setValue(valueToGet, value);
		return value;
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
		// Blame CraftIRC
		if (world == null || world.equals("")) {
			System.err.println("[bPermissions] Some silly developer is checking for a blank world!");
			return "BLANKWORLD";
		}
		WorldPermissions perms = wpm.getPermissionSet(world).getWorldPermissions();
		User us = perms.getUser(player);
		if(us.contains(valueToGet))
			return us.getValue(valueToGet);
		
		String value = "";
		int priority = -1;
		for (Permission perm : us.getEffectivePermissions()) {
			if (perm.isTrue()) {
				String name = perm.name();
				String[] index = name.split("\\.", 3);
				if (index.length == 3 && index[0].equals(valueToGet)) {
					int pr = Integer.parseInt(index[1]);
					if (pr > priority) {
						value = index[2];
						priority = pr;
					}
				}
			}
		}
		us.setValue(valueToGet, value);
		return value;
	}

	public void instantiate() {
		wpm = Permissions.getWorldPermissionsManager();
	}

}
