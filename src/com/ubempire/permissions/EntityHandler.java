package com.ubempire.permissions;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EntityHandler extends PlayerListener {

	private final Permissions plugin;
	/*
	 * Initiate the listener
	 */
	public EntityHandler(Permissions callbackPlugin) {
		plugin = callbackPlugin;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!player.hasPermission("bPermissions.build"))
				event.setCancelled(true);
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.pf.getPermissions(event.getPlayer());
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.pf.unsetPermissions(event.getPlayer());
	}
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.getFrom().getWorld()!=event.getTo().getWorld())
		{
		plugin.pf.unsetPermissions(event.getPlayer());
		plugin.pf.getPermissions(event.getPlayer());
		}
	}
}
