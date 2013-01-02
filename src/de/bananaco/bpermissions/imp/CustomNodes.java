package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;
//import org.bukkit.permissions.Permission;
//import org.bukkit.permissions.PermissionDefault;

import de.bananaco.bpermissions.api.Permission;

public class CustomNodes {
	
	private final File file = new File("plugins/bPermissions/custom_nodes.yml");
	private YamlConfiguration config;// = new YamlConfiguration();
	private PermissionDefault pdo = PermissionDefault.OP;
	
	public void load() {
		// System.out.println("Loading Custom Nodes");
		try {
			List<Permission> permissions = doLoad();
			// load into API
			de.bananaco.bpermissions.api.CustomNodes.loadNodes(permissions);
			for(int i=0; i<permissions.size(); i++) {
				// If the permission doesn't already exist
				if(Bukkit.getServer().getPluginManager().getPermission(permissions.get(i).nameLowerCase()) == null) {
					// Add it!
					Bukkit.getServer().getPluginManager().addPermission(convert(permissions.get(i)));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public org.bukkit.permissions.Permission convert(Permission perm) {
		String name = perm.nameLowerCase();
		Map<String, Boolean> children = perm.getChildren();
		return new org.bukkit.permissions.Permission(name, pdo, children);
	}

	private List<Permission> doLoad() throws Exception {
		config = new YamlConfiguration();
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		config.load(file);
		List<Permission> permissions = new ArrayList<Permission>();
		Set<String> keys = config.getKeys(true);
		if(keys != null && keys.size() > 0) {
			for(String key : keys) {
				// System.out.println("Loading Key: " + key);
				String permission = key;
				List<String> childList = config.getStringList(permission);
				// System.out.println("Size: " + childList.size());
				if(childList != null && childList.size() > 0) {
					// for (String child : childList) {
					//	System.out.println(child);
					// }
					Map<String, Boolean> children = new HashMap<String, Boolean>();
					// Using our custom decoder here (yay for code re-use)
					Set<de.bananaco.bpermissions.api.Permission> perms = de.bananaco.bpermissions.api.Permission.loadFromString(childList);
					for(de.bananaco.bpermissions.api.Permission perm : perms) {
						// System.out.println("Perm To Lowercase: " + perm.nameLowerCase());
						children.put(perm.nameLowerCase(), perm.isTrue());
					}
					permission = permission.replace("permissions.", "");
					// System.out.println(permission.toLowerCase());
					Permission perm = Permission.loadWithChildren(permission.toLowerCase(), true, children);
					// System.out.println("Adding Permissions: " + permission.toLowerCase());
					permissions.add(perm);
				}
			}
		}
		return permissions;
	}
	
}
