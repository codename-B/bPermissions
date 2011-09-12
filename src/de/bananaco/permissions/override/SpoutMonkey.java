package de.bananaco.permissions.override;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.permission.PlayerPermissionEvent;
import org.getspout.spoutapi.event.permission.PermissionListener;

import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class SpoutMonkey extends PermissionListener {
	private final WorldPermissionsManager wpm;
	public SpoutMonkey(WorldPermissionsManager wpm) {
		this.wpm = wpm;
	}
	
	public void onPlayerPermissionEvent(PlayerPermissionEvent event) {
		String node = event.getPermissionString();
		Player player = event.getPlayer();
		boolean result = wpm.getPermissionSet(player.getWorld()).has(player, node);
		event.setResult(result);
	}
	
}
