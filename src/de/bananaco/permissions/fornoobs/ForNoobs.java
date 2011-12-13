package de.bananaco.permissions.fornoobs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class ForNoobs {
	private final Server s;
	private final WorldPermissionsManager wpm;

	public ForNoobs(JavaPlugin p) {
		Server s = p.getServer();
		this.s = s;
		this.wpm = Permissions.getWorldPermissionsManager();
	}

	public void addAll() {
		for (PermissionSet ps : this.getPermissionSets()) {
			try {
				addDefaults(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addDefaults(PermissionSet ps) throws Exception {
		String defaultGroup = ps.getDefaultGroup();
		String moderatorGroup = "moderator";
		String adminGroup = "admin";

		ArrayList<String> admins = new ArrayList<String>();
		
		List<String> def = new ArrayList<String>();
		List<String> mod = new ArrayList<String>();
		List<String> adm = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new DataInputStream(new FileInputStream("ops.txt"))));
		String line = "";
		while ((line = br.readLine()) != null)
			admins.add(line);
		// Basic default setup
		def.add("prefix.0.&" + ChatColor.YELLOW.getCode() + "default");
		def.add("suffix.0.&" + ChatColor.GREEN.getCode() + "imnew");
		// Basic moderator setup
		mod.add("prefix.10.&" + ChatColor.DARK_RED.getCode() + "moderator");
		mod.add("suffix.10.&" + ChatColor.LIGHT_PURPLE.getCode() + "ihelp");
		// Basic admin setup
		adm.add("prefix.100.&" + ChatColor.RED.getCode() + "admin");
		adm.add("suffix.100.&" + ChatColor.DARK_PURPLE.getCode() + "over9000");
		// Add the online admins
		for (String player : admins) {
			String name = PlayerCase.correctCase(player);
			if (name != null) {
				ps.addGroup(name, defaultGroup);
				ps.addGroup(name, moderatorGroup);
				ps.addGroup(name, adminGroup);
			}
		}

		// Add the example admin
		ps.addGroup("HerpaDerpa", defaultGroup);
		ps.addGroup("HerpaDerpa", moderatorGroup);
		ps.addGroup("HerpaDerpa", adminGroup);
		// Add the example mod
		ps.addGroup("Derpy", defaultGroup);
		ps.addGroup("Derpy", moderatorGroup);

		// Add the permissions to the admins
		for (String permission : getPermissions()) {
			if (permission.contains("user") || permission.contains("default")
					|| permission.contains("build"))
				def.add(permission);
			else if (permission.contains("ban") || permission.contains("kick")
					|| permission.contains("mod"))
				mod.add(permission);
			else
				adm.add(permission);
		}

		ps.setNodes(defaultGroup, def);
		ps.setNodes(moderatorGroup, mod);
		ps.setNodes(adminGroup, adm);

	}

	private ArrayList<String> getPermissions() {
		ArrayList<String> regPerms = new ArrayList<String>();
		for (Permission p : s.getPluginManager().getPermissions()) {
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

	private PermissionSet[] getPermissionSets() {
		List<World> worlds = s.getWorlds();
		PermissionSet[] ps = new PermissionSet[worlds.size()];

		for (int i = 0; i < s.getWorlds().size(); i++) {
			ps[i] = wpm.getPermissionSet(worlds.get(i));
		}
		return ps;
	}

}
