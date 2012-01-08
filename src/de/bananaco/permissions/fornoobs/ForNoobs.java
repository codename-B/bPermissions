package de.bananaco.permissions.fornoobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class ForNoobs {
	private final WorldManager wm = WorldManager.getInstance();
	private final JavaPlugin plugin;
	
	public ForNoobs(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void addAll() {
		System.out.println("Adding to example files...");
		try {
			addDefaults(wm.getWorld(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addDefaults(World world) throws Exception {
		ArrayList<String> regPerms = getPermissions();
		// Do the groups first
		String admin = "admin";
		String mod = "moderator";
		String def = world.getDefaultGroup();
		// Let's sort the permissions into shizzledizzle
		for(String perm : regPerms) {
			if(perm.contains("user") || perm.contains("build"))
				world.getGroup(def).addPermission(perm, true);
			else if(perm.contains(".ban") || perm.contains(".kick") || perm.contains(".mod") || perm.contains(".fly"))
				world.getGroup(mod).addPermission(perm, true);
			else
				world.getGroup(admin).addPermission(perm, true);
		}
		// admin
		world.getGroup(admin).addGroup(mod);
		world.getGroup(admin).addPermission("group."+mod, false);
		world.getGroup(admin).addPermission("group."+admin, true);
		world.getGroup(admin).setValue("prefix", "&5admin");
		// moderator
		world.getGroup(mod).addGroup(def);
		world.getGroup(mod).addPermission("group."+def, false);
		world.getGroup(mod).addPermission("group."+mod, true);
		world.getGroup(mod).setValue("prefix", "&7moderator");
		// default
		world.getGroup(def).addPermission("group."+def, true);
		world.getGroup(def).setValue("prefix", "&9user");
		// Now do some example users
		String user1 = "codename_B";
		String user2 = "Notch";
		String user3 = "pyraetos";
		// And set their groups
		// user1
		world.getUser(user1).getGroupsAsString().clear();
		world.getUser(user1).addGroup(admin);
		world.getUser(user1).setValue("prefix", "&8developer");
		// user2
		world.getUser(user2).getGroupsAsString().clear();
		world.getUser(user2).addGroup(mod);
		world.getUser(user2).setValue("prefix", "&8mojang");
		// user3
		world.getUser(user3).setValue("prefix", "&3helper");
		// Finally, save the changes
		world.save();
	}

	private ArrayList<String> getPermissions() {
		ArrayList<String> regPerms = new ArrayList<String>();
		for (Permission p : plugin.getServer().getPluginManager().getPermissions()) {
			if (!p.getName().equals("*") && !p.getName().equals("*.*"))
				regPerms.add(p.getName());
		}
		Collections.sort(regPerms, new Comparator<String>() {

			public int compare(String a, String b) {
				return a.compareTo(b);
			};
		});

		return regPerms;
	}

}
