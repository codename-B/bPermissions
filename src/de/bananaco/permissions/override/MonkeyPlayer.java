package de.bananaco.permissions.override;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.permissions.Permission;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public class MonkeyPlayer extends CraftPlayer {
	
	public MonkeyPlayer(CraftPlayer original) {
		super((CraftServer) original.getServer(), original.getHandle());
	}
	@Override
	public boolean hasPermission(Permission perm) {
		return hasPermission(perm.getName());
	}
	@Override
	public boolean hasPermission(String name) {

		try {
			return internalHasPermission(name);
		} catch (Exception e) {
			System.err.println("[bPermissions] Something horrible went wrong. Please turn off your computer and walk away.");
		}
		return isOp();
	}
	
    private boolean internalHasPermission(String name) {
    	PermissionSet p = Permissions.getWorldPermissionsManager().getPermissionSet(getWorld());
    	System.out.println(name+":"+p.has(this,name));
        if (p.has(this, name))
            return p.has(this, name);
        
        int index = name.lastIndexOf('.');
        while (index >= 0) {
            name = name.substring(0, index);
            String wildcard = name + ".*";
            if (p.has(this, wildcard))
                return p.has(this, wildcard);
            index = name.lastIndexOf('.');
        }
        return p.has(this, "*") || isOp();
    }
	
}
