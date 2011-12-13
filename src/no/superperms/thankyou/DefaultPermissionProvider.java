package no.superperms.thankyou;

import org.bukkit.entity.Player;

public class DefaultPermissionProvider implements PermissionProvider {

	@Override
	public boolean hasPermission(Player player, String permission) {
		return player.isOp();
	}

}
