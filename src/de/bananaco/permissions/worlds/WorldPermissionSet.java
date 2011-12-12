package de.bananaco.permissions.worlds;

import org.bukkit.World;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public enum WorldPermissionSet {

	BML("bml"), JSON("json"), SQL("sql"), YAML("yaml");

	public static WorldPermissionSet getSet(String type) {
		if (type == null) {
			System.err.println("[bPermissions] Something horrible went wrong!");
			return null;
		} else if (type.equals("yaml")) {
			return WorldPermissionSet.YAML;
		}
		System.err.println("[bPermissions] What happened Jim?");
		return null;
	}

	private final String type;

	WorldPermissionSet(String type) {
		this.type = type;
	}

	public PermissionSet get(World world, Permissions permissions) {
		if (type == null) {
			System.err.println("[bPermissions] Something horrible went wrong!");
			return null;
		} else if (type.equals("yaml")) {
			return new YamlPermissionSet(world, permissions);
		}
		System.err.println("[bPermissions] What happened Jim?");
		return null;
	}

	public String toString() {
		return type;
	}
}
