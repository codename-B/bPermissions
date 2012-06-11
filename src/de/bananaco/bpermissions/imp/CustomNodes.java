package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CustomNodes {
	
	private final File file = new File("plugins/bPermissions/custom_nodes.yml");
	private YamlConfiguration config;// = new YamlConfiguration();
	
	public void load() {
		try {
			List<Permission> permissions = doLoad();
			for(int i=0; i<permissions.size(); i++) {
				// If the permission doesn't already exist
				if(Bukkit.getServer().getPluginManager().getPermission(permissions.get(i).getName()) == null) {
					// Add it!
					Bukkit.getServer().getPluginManager().addPermission(permissions.get(i));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Permission> doLoad() throws Exception {
		config = new YamlConfiguration();
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		config.load(file);
		List<Permission> permissions = new ArrayList<Permission>();
		Set<String> keys = config.getKeys(false);
		if(keys != null && keys.size() > 0) {
			for(String key : keys) {
				String permission = key;
				List<String> childList = config.getStringList(permission+".children");
				if(childList != null && childList.size() > 0) {
					Map<String, Boolean> children = new HashMap<String, Boolean>();
					// Using our custom decoder here (yay for code re-use)
					Set<de.bananaco.bpermissions.api.util.Permission> perms = de.bananaco.bpermissions.api.util.Permission.loadFromString(childList);
					for(de.bananaco.bpermissions.api.util.Permission perm : perms) {
						children.put(perm.nameLowerCase(), perm.isTrue());
					}
					// Now load the rest of that buggerypokery!
					String pd = config.getString(permission+".default", "op");
					PermissionDefault pdo = PermissionDefault.OP;
					if(pd.equalsIgnoreCase("not-op")) {
						pdo = PermissionDefault.NOT_OP;
					} else if(pd.equalsIgnoreCase("true")) {
						pdo = PermissionDefault.TRUE;
					} else if(pd.equalsIgnoreCase("false")) {
						pdo = PermissionDefault.FALSE;
					}
					Permission perm = new Permission(permission.toLowerCase(), pdo, children);
					permissions.add(perm);
				}
			}
		}
		return permissions;
	}
	
}
