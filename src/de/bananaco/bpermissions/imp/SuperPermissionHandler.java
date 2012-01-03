package de.bananaco.bpermissions.imp;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.World;

import de.bananaco.bpermissions.api.World;
/**
 * Handles all the superperms registering/unregistering
 * for PermissionAttachments (it's basically just somewhere
 * to stick all the nasty SuperPerms stuff that wouldn't exist
 * if SuperPerms was a more flexible system.
 * 
 * What's wrong with a PermissionProvider interface where we can
 * register a single PermissionProvider?!
 */
public class SuperPermissionHandler extends PlayerListener {
	
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e)
	{
		setupPlayer(e.getPlayer(), e.getPlayer().getLocation().getWorld());
	
	/**
	 * Set up the Player via the specified World object
	 * (note this is a bPermissions world, not a Bukkit world)
	 * @param player
	 * @param world
	 */
	public void setupPlayer(Player player, World world) {
		// TODO wait for the bukkit team to get their finger out
	}

}
