package de.bananaco.permissions.override;

import org.getspout.spoutapi.event.permission.PlayerPermissionEvent;
import org.getspout.spoutapi.event.permission.PermissionListener;

import de.bananaco.permissions.worlds.HasPermission;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class SpoutMonkey extends PermissionListener {
	public SpoutMonkey(WorldPermissionsManager wpm) {
		
	}
	public SpoutMonkey() {
		
	}
	@Override
	public void onPlayerPermission(PlayerPermissionEvent event) {
		String node = event.getPermissionString();
		boolean result = HasPermission.has(event.getPlayer(), node);
		event.setResult(result);
	}
	
}
