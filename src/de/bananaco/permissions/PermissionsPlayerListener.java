package de.bananaco.permissions;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.iplock.IpLock;

public class PermissionsPlayerListener extends PlayerListener {
	
	private final Permissions permissions;
	
	public PermissionsPlayerListener(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(!permissions.isEnabled())
			return;
		if(event.getPlayer() == null)
			return;
		if(event.getPlayer().getLocation() == null)
			return;
		PermissionSet ps = permissions.pm.getPermissionSet(event.getPlayer().getLocation().getWorld());
		new SuperPermissionHandler(event.getPlayer()).setupPlayer(ps.getPlayerNodes(event.getPlayer()), permissions);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!permissions.isEnabled())
			return;
		if(event.getPlayer() == null)
			return;
		if(!permissions.useIpLock)
		return;
		
		Player player = event.getPlayer();
		if(player.hasPermission("bPermissions.iplock.lock")) {
			IpLock iplock = permissions.iplock;
			if(iplock.hasEntry(player)) {
				if(iplock.isIpLocked(player)) {
			player.sendMessage("Please login before you are kicked!");
			iplock.startTimeout(player);
			}
			} else {
			player.sendMessage("Please set a password!");
			}
		}
	}
	
	public boolean can(Player player) {
		return (player.hasPermission("bPermissions.build") || player.hasPermission("bPermissions.admin") || player.isOp());
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(!can(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!can(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(!permissions.isEnabled())
			return;
		if(event.isCancelled())
			return;
		if(event.getPlayer() == null)
			return;
		if(event.getTo() == null)
			return;
		PermissionSet ps = permissions.pm.getPermissionSet(event.getTo().getWorld());
		new SuperPermissionHandler(event.getPlayer()).setupPlayer(ps.getPlayerNodes(event.getPlayer()), permissions);
	}
}