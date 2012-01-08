package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class SingleGroupPromotion implements PromotionTrack {
	private final File tracks = new File("plugins/bPermisisons/tracks.yml");
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
				for (String key : keys) {
					List<String> groups = config.getStringList(key);
					if (groups != null && groups.size() > 0)
						trackmap.put(key.toLowerCase(), groups);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void promote(String player, String track, String world) {
		List<String> groups = trackmap.get(track.toLowerCase());
		if (world == null) {
			for (World w : wm.getAllWorlds()) {
				User user = w.getUser(player);
				boolean promoted = false;
				// If they don't have the group, set it to their group
				for (int i = 0; i < groups.size() && !promoted; i++)
					if (!user.getGroupsAsString().contains(groups.get(i))) {
						// Clear the groups
						user.getGroupsAsString().clear();
						// Add the new group
						user.addGroup(groups.get(i));
						// We've promoted successfully
						promoted = true;
						w.save();
					}
			}
		} else {
			User user = wm.getWorld(world).getUser(player);
			boolean promoted = false;
			// If they don't have the group, set it to their group
			for (int i = 0; i < groups.size() && !promoted; i++)
				if (!user.getGroupsAsString().contains(groups.get(i))) {
					// Clear the groups
					user.getGroupsAsString().clear();
					// Add the new group
					user.addGroup(groups.get(i));
					// We've promoted successfully
					promoted = true;
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
				boolean demoted = false;
				// If they don't have the group, set it to their group
				for (int i = groups.size() - 1; i >= 0 && !demoted; i--)
					if (user.getGroupsAsString().contains(groups.get(i))) {
						// Clear the groups
						user.getGroupsAsString().clear();
						// Add the new group
						if (i > 0)
							user.addGroup(groups.get(i - 1));
						else
							user.addGroup(wm.getWorld(world).getDefaultGroup());
						// We've demoted successfully
						demoted = true;
						w.save();
					}

			}
		} else {
			User user = wm.getWorld(world).getUser(player);
			boolean demoted = false;
			// If they don't have the group, set it to their group
			for (int i = groups.size() - 1; i >= 0 && !demoted; i--)
				if (user.getGroupsAsString().contains(groups.get(i))) {
					// Clear the groups
					user.getGroupsAsString().clear();
					// Add the new group
					if (i > 0)
						user.addGroup(groups.get(i - 1));
					else
						user.addGroup(wm.getWorld(world).getDefaultGroup());
					// We've demoted successfully
					demoted = true;
					wm.getWorld(world).save();
				}
		}
	}

	@Override
	public boolean containsTrack(String track) {
		return trackmap.containsKey(track.toLowerCase());
	}

}
