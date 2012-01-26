package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class SingleGroupPromotion implements PromotionTrack {
	private final File tracks = new File("plugins/bPermissions/tracks.yml");
	private final WorldManager wm = WorldManager.getInstance();
	private YamlConfiguration config = new YamlConfiguration();

	Map<String, List<String>> trackmap = new HashMap<String, List<String>>();

	public void load() {
		try {
			// Tidy up
			config = new YamlConfiguration();
			trackmap.clear();
			// Then do your basic if exists checks
			if (!tracks.exists()) {
				tracks.getParentFile().mkdirs();
				tracks.createNewFile();
			}
			config.load(tracks);
			if (config.getKeys(false) == null
					|| config.getKeys(false).size() == 0) {
				List<String> defTrack = new ArrayList<String>();
				defTrack.add("default");
				defTrack.add("moderator");
				defTrack.add("admin");
				config.set("default", defTrack);
				config.save(tracks);
			} else {
				Set<String> keys = config.getKeys(false);
				Map<String, Boolean> children = new HashMap<String, Boolean>();
				if (keys != null && keys.size() > 0)
					for (String key : keys) {
						children.put("tracks."+key.toLowerCase(), true);
						List<String> groups = config.getStringList(key);
						if (groups != null && groups.size() > 0) {
							trackmap.put(key.toLowerCase(), groups);
						}
					}
				Permission perm = new Permission("tracks.*", PermissionDefault.OP, children);
				Bukkit.getPluginManager().addPermission(perm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getIndex(String data, List<String> list) {
		for(int i=0; i<list.size(); i++) {
			if(data.equalsIgnoreCase(list.get(i)))
				return i;
		}
		return -1;
	}

	@Override
	public void promote(String player, String track, String world) {
		List<String> groups = trackmap.get(track.toLowerCase());
		if (world == null) {
			for (World w : wm.getAllWorlds()) {
				User user = w.getUser(player);
				// If they don't have the group, set it to their group
				int index = 0;
				for (int i = 0; i <groups.size(); i++) {
					System.out.println("hasGroup?"+groups.get(i)+" "+user.hasGroup(groups.get(i)));
					if(user.hasGroup(groups.get(i))) {
						int current = getIndex(groups.get(i), groups);
						if(current >= index)
							index = current+1;
					}
				}
				if(index < groups.size()) {
				System.out.println("index: "+index+" group: "+groups.get(index));
				user.getGroupsAsString().clear();
				user.addGroup(groups.get(index));
				w.save();
				}
			}
		} else {
			User user = wm.getWorld(world).getUser(player);
			// If they don't have the group, set it to their group
			int index = 0;
			for (int i = 0; i <groups.size(); i++) {
				System.out.println("hasGroup?"+groups.get(i)+" "+user.hasGroup(groups.get(i)));
				if(user.hasGroup(groups.get(i))) {
					int current = getIndex(groups.get(i), groups);
					if(current >= index)
						index = current+1;
				}
			}
			
			if(index<groups.size()) {
			System.out.println("index: "+index+" group: "+groups.get(index));
			user.getGroupsAsString().clear();
			user.addGroup(groups.get(index));
			wm.getWorld(world).save();
			}
		}
	}

	@Override
	public void demote(String player, String track, String world) {
		List<String> groups = trackmap.get(track.toLowerCase());
		if (world == null) {
			for (World w : wm.getAllWorlds()) {
				User user = w.getUser(player);
				// If they don't have the group, set it to their group
				int index = 1;
				for (int i = 0; i <groups.size(); i++) {
					if(user.hasGroup(groups.get(i))) {
						int current = getIndex(groups.get(i), groups);
						if(current > index)
							index = current;
					}
				}
				user.getGroupsAsString().clear();
				user.addGroup(groups.get(index-1));
				wm.getWorld(world).save();
			}
		} else {
			User user = wm.getWorld(world).getUser(player);
			// If they don't have the group, set it to their group
			int index = 1;
			for (int i = 0; i <groups.size(); i++) {
				if(user.hasGroup(groups.get(i))) {
					int current = getIndex(groups.get(i), groups);
					if(current > index)
						index = current;
				}
			}
			user.getGroupsAsString().clear();
			user.addGroup(groups.get(index-1));
			wm.getWorld(world).save();
		}
	}

	@Override
	public boolean containsTrack(String track) {
		return trackmap.containsKey(track.toLowerCase());
	}

}
