package de.bananaco.permissions.worlds;

import org.bukkit.World;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public enum WorldPermissionSet {

	YAML("yaml"), YAML2("yaml2");

	public static WorldPermissionSet getSet(String type) {
		if (type == null) {
			System.err.println("[bPermissions] Something horrible went wrong!");
			return null;
		} else if (type.equals("yaml")) {
			return WorldPermissionSet.YAML;
		}
		else if (type.equals("yaml2")) {
				return WorldPermissionSet.YAML2;
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
		} else if (type.equals("yaml2")) {
			return new Yaml2PermissionSet(world, permissions);
		}
		System.err.println("[bPermissions] What happened Jim?");
		return null;
	}

	public String toString() {
		return type;
	}
}
