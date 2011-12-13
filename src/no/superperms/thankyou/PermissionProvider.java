package no.superperms.thankyou;

import org.bukkit.entity.Player;

public interface PermissionProvider {
    
	public boolean hasPermission(Player player, String permission);
	
}
