package de.bananaco.permissions.worlds;

import org.bukkit.World;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public enum WorldPermissionSet {
	
	SQL("sql"),
	YAML("yaml"),
	YAML_OLDSCHOOL("yaml_oldschool"),
	BML("bml");
	
	private final String type;
	WorldPermissionSet(String type) {
		this.type = type;
	}
	public PermissionSet get(World world, Permissions permissions) {
		if(type == null) {
			System.err.println("[bPermissions] Something horrible went wrong!");
			return null;
		}
		else if(type.equals("yaml")) {
			return new WorldPermissions(world, permissions);
		}
		else if(type.equals("yaml_oldschool")) {
			return new OldschoolWorldPermissions(world, permissions);
		}
		else if(type.equals("bml")) {
			return new NewWorldPermissions(world, permissions);
		}
		else if(type.equals("sql")) {
			return new SQLWorldPermissions(world, permissions);
		}
		System.err.println("[bPermissions] What happened Jim?");
		return null;
	}
	public String toString() {
		return type;
	}
	
	public static WorldPermissionSet getSet(String type) {
		if(type == null) {
			System.err.println("[bPermissions] Something horrible went wrong!");
			return null;
		}
		else if(type.equals("yaml")) {
			return WorldPermissionSet.YAML;
		}
		else if(type.equals("yaml_oldschool")) {
			return WorldPermissionSet.YAML_OLDSCHOOL;
		}
		else if(type.equals("bml")) {
			return WorldPermissionSet.BML;
		}
		else if(type.equals("sql")) {
			return WorldPermissionSet.SQL;
		}
		System.err.println("[bPermissions] What happened Jim?");
		return null;
	}
}
