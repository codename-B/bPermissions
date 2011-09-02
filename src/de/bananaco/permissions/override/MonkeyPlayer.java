package de.bananaco.permissions.override;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.permissions.Permission;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MonkeyPlayer extends CraftPlayer {
	public static PermissionHandler ph = Permissions.Security;
	public MonkeyPlayer(CraftPlayer original) {
		super((CraftServer) original.getServer(), original.getHandle());
	}
	@Override
	public boolean hasPermission(Permission perm) {
		
		return hasPermission(perm.getName());
	}
	@Override
	public boolean hasPermission(String name) {
		return ph.has(this, name);
	}
	
}
