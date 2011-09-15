package de.bananaco.permissions.fornoobs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
		for(PermissionSet ps : this.getPermissionSets()) {
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
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("ops.txt"))));
		String line = "";
		while((line = br.readLine()) != null)
			admins.add(line);
		// Basic default setup
		ps.addNode("prefix.0.default", defaultGroup);
		ps.addNode("suffix.0.imnew", defaultGroup);
		// Basic moderator setup
		ps.addNode("prefix.10.moderator", moderatorGroup);
		ps.addNode("suffix.10.ihelp", moderatorGroup);
		// Basic admin setup
		ps.addNode("prefix.100.admin", adminGroup);
		ps.addNode("suffix.100.over9000", adminGroup);
		// Add the online admins
		for(String player : admins) {
			if(s.getPlayer(player) != null) {
				player = s.getPlayer(player).getName();
			ps.addGroup(player, defaultGroup);
			ps.addGroup(player, moderatorGroup);
			ps.addGroup(player, adminGroup);
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
		for(String permission : getPermissions()) {
			if(permission.contains("user") || permission.contains("default") || permission.contains("build"))
				ps.addNode(permission, defaultGroup);
			else if(permission.contains("ban") || permission.contains("kick") || permission.contains("mod"))
				ps.addNode(permission, moderatorGroup);
			else
			ps.addNode(permission, adminGroup);
		}
		
	}
	
	private ArrayList<String> getPermissions() {
		ArrayList<String> regPerms = new ArrayList<String>();
		for(Permission p : s.getPluginManager().getPermissions()) {
			if(!p.getName().equals("*") && !p.getName().equals("*.*"))
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
	
	for(int i=0; i<s.getWorlds().size(); i++) {
		ps[i] = wpm.getPermissionSet(worlds.get(i));
	}
	return ps;
	}

	
	
}

