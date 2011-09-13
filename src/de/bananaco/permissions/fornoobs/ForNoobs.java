package de.bananaco.permissions.fornoobs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
		String adminGroup = "admin";
		ArrayList<String> admins = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("ops.txt"))));
		String line = "";
		while((line = br.readLine()) != null)
			admins.add(line);
		// Basic default setup
		ps.addNode("bPermissions.build", defaultGroup);
		ps.addNode("prefix.0.default", defaultGroup);
		ps.addNode("suffix.0.imnew", defaultGroup);
		// Basic admin setup
		ps.addNode("bPermissions.admin", adminGroup);
		ps.addNode("prefix.100.admin", adminGroup);
		ps.addNode("suffix.100.over9000", adminGroup);
		// Add the admins
		for(String player : admins) {
			ps.addGroup(player, defaultGroup);
			ps.addGroup(player, adminGroup);
		}
		// Add the permissions to the admins
		for(String permission : getPermissions())
			ps.addNode(permission, adminGroup);
		
	}
	
	private ArrayList<String> getPermissions() {
		ArrayList<String> regPerms = new ArrayList<String>();
		for(Permission p : s.getPluginManager().getPermissions()) {
			if(!p.getName().contains("bpermissions") && !p.getName().contains("bPermissions") && !p.getName().equals("*")
					&& !p.getName().equals("*.*") && !p.getName().equals("*.*.*")) { 
			regPerms.add(p.getName());
			}
		}
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
