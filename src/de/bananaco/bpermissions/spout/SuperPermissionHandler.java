package de.bananaco.bpermissions.spout;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.player.Player;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
/**
 * Handles all the superperms registering/unregistering
 * for PermissionAttachments (it's basically just somewhere
 * to stick all the nasty SuperPerms stuff that wouldn't exist
 * if SuperPerms was a more flexible system.
 * 
 * What's wrong with a PermissionProvider interface where we can
 * register a single PermissionProvider?!
 */
public class SuperPermissionHandler implements Listener {

	private WorldManager wm = WorldManager.getInstance();
	
	protected SuperPermissionHandler() {
	}

	@EventHandler(order = Order.EARLIEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		setupPlayer(event.getPlayer(), wm.getWorld(event.getPlayer().getEntity().getWorld().getName()));		
	}


	private void setupPlayer(Player player, World world) {
		if(world == null)
			return;
		Calculable c = world.getUser(player.getName());
		try {
			c.calculateEffectiveMeta();
			c.calculateEffectivePermissions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}