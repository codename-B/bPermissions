package de.bananaco.permissions.override;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.permissions.Permission;

import de.bananaco.permissions.worlds.HasPermission;

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
		boolean has = isOp();
		try {
			has = HasPermission.has(this, name);
		} catch (Exception e) {
			System.err
					.println("[bPermissions] Something horrible went wrong. Please turn off your computer and walk away.");
		}
		return has;
	}

}
