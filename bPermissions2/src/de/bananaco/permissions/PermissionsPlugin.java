package de.bananaco.permissions;

import java.util.ArrayList;
import java.util.List;

import de.bananaco.permissions.set.YamlPermissionSet;
import de.bananaco.permissions.util.User;

import org.bukkit.plugin.java.JavaPlugin;

public class PermissionsPlugin extends JavaPlugin {
	
	public boolean idiotVariable;

	public static void main(String[] args) {
		YamlPermissionSet permissions = new YamlPermissionSet("world");
		permissions.load();
		List<String> groups = new ArrayList<String>();
		groups.add("test");
		permissions.add(new User("codename_B", groups, null, permissions));
		permissions.save();
	}
	
	public void onEnable() {}
	
	public void onDisable() {}
	
}
