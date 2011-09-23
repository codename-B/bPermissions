package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.interfaces.TransitionSet;
import de.bananaco.permissions.override.MonkeyPlayer;

public class OldschoolWorldPermissions extends TransitionPermissions implements PermissionSet {
	
	/**
	 * The main class instance
	 */
	private final Permissions plugin;
	/**
	 * The world
	 */
	private final World world;
	/**
	 * The user configuration object
	 */
	private final Configuration users;
	/**
	 * The user configuration object
	 */
	private final Configuration groups;
	/**
	 * The default!
	 */
	private String defaultGroup = "default";
	
	public OldschoolWorldPermissions(World world, Permissions plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.plugin = plugin;
		this.world = world;
		this.users = new Configuration(new File("plugins/bPermissions/worlds/"
				+ world.getName() + "/users.yml"));
		this.groups = new Configuration(new File("plugins/bPermissions/worlds/"
				+ world.getName() + "/groups.yml"));
		users.load();
		groups.load();
		setup();
	}
	
	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions] " + String.valueOf(input));
	}

	@Override
	public World getWorld() {
		return world;
	}
	
	private ArrayList<String> getDefaultArrayList() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(getDefaultGroup());
		return ar;
	}

	@Override
	public void setup() {
		parseNoobery();
		reload();
	}
	
	private void parseNoobery() {
		log("Checking users.yml and groups.yml for errors...");
		List<String> userList = users.getKeys("users");
		
		List<String> groupList = groups.getKeys("groups");
		
		if(userList != null)
			for(String user : userList) {
				List<String> userPermissions = users.getStringList("users."+user+".permissions", new ArrayList<String>());
				String prefix = users.getString("users."+user+".info.prefix", null);
				if(prefix != null) {
					userPermissions.add("prefix.0."+prefix);
				}
				users.removeProperty("users."+user+".info.prefix");
				String suffix = users.getString("users."+user+".info.suffix", null);
				if(suffix != null) {
					userPermissions.add("suffix.0."+suffix);
				}
				users.removeProperty("users."+user+".info.suffix");
				boolean build = users.getBoolean("users."+user+".info.build", false);
				if(build) {
					userPermissions.add("bPermissions.build");
				}
				users.removeProperty("users."+user+".info.build");
				users.removeProperty("users."+user+".info");
				users.setProperty("users."+user+".permissions", userPermissions);
			}
		if(groupList != null)
			for(String group : groupList) {
				List<String> groupPermissions = groups.getStringList("groups."+group+".permissions", new ArrayList<String>());
				String prefix = groups.getString("groups."+group+".info.prefix", null);
				if(prefix != null) {
					groupPermissions.add("prefix.0."+prefix);
				}
				groups.removeProperty("groups."+group+".info.prefix");
				String suffix = groups.getString("groups."+group+".info.suffix", null);
				if(suffix != null) {
					groupPermissions.add("suffix.0."+suffix);
					
				}
				groups.removeProperty("groups."+group+".info.suffix");
				boolean build = groups.getBoolean("groups."+group+".info.build", false);
				if(build) {
					groupPermissions.add("bPermissions.build");
					
				}
				groups.removeProperty("groups."+group+".info.build");
				boolean def = groups.getBoolean("groups."+group+".default", false);
				if(def) {
					groups.setProperty("default", group);
				}
				groups.removeProperty("groups."+group+".default");
				groups.removeProperty("groups."+group+".info");
				groups.setProperty("groups."+group+".permissions", groupPermissions);
			}
		users.save();
		groups.save();
	}

	@Override
	public void reload() {
		users.load();
		users.save();
		groups.load();
		groups.save();
	}

	@Override
	public void addNode(String node, String group) {
		List<String> groupNodes = getGroupNodesNoInherit(group);
		
		if (!groupNodes.contains(node)) {
			groupNodes.add(node);
			log("added node:" + node + " to group:" + group + " for world:"
					+ world.getName());
		} else {
			log("node:" + node + " already exists in group:" + group
					+ " for world:" + world.getName());
		return;
		}
		groups.setProperty("groups."+group+".permissions", groupNodes);
		groups.save();	
		setupPlayers();
	}

	@Override
	public void removeNode(String node, String group) {
		List<String> groupNodes = getGroupNodesNoInherit(group);
		
		if (groupNodes.contains(node)) {
			groupNodes.remove(node);
			log("removed node:" + node + " from group:" + group + " for world:"
					+ world.getName());
		} else {
			log("node:" + node + " does not exist in group:" + group
					+ " for world:" + world.getName());
			return;
		}
		groups.setProperty("groups."+group+".permissions", groupNodes);
		groups.save();
		setupPlayers();
	}

	private List<String> getGroupNodesNoInherit(String group) {
		List<String> permissions = groups.getStringList("groups."+group+".permissions", new ArrayList<String>());
		
		return permissions;
	}
	
	@Override
	public List<String> getGroupNodes(String group) {
		List<String> permissions = groups.getStringList("groups."+group+".permissions", new ArrayList<String>());
		List<String> inheritance = groups.getStringList("groups."+group+".inheritance", new ArrayList<String>());
		
		for(String inheritGroup : inheritance)
			permissions.addAll(getGroupNodes(inheritGroup));

		return permissions;
	}

	@Override
	public List<String> getPlayerNodes(Player player) {
		return getPlayerNodes(player.getName());
	}

	@Override
	public List<String> getPlayerNodes(String player) {
		List<String> playerGroups = getGroups(player);
		List<String> playerNodes = new ArrayList<String>();
		for (String group : playerGroups) {
			for(String node : getGroupNodes(group)) {
				if(!playerNodes.contains(node))
					playerNodes.add(node);
			}
		}
		List<String> transitionNodes = ((TransitionSet) this).listTransNodes(player);
		for(String node : transitionNodes) {
			if(!playerNodes.contains(node))
				playerNodes.add(node);
		}
		List<String> perPlayerNodes = users.getStringList("users."+player+".permissions", new ArrayList<String>());
		for(String node : perPlayerNodes) {
			if(!playerNodes.contains(node))
				playerNodes.add(node);
		}
		
		return playerNodes;
	}

	@Override
	public List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public List<String> getGroups(String player) {
		
		List<String> playerGroups = users.getStringList("users."+player+".groups", new ArrayList<String>());
		
		if(playerGroups.size() == 0)
			playerGroups = getDefaultArrayList();
		
		return playerGroups;
	}

	@Override
	public void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	public void addGroup(String player, String group) {
		List<String> playerGroups = getGroups(player);
		if (!playerGroups.contains(group)) {
			playerGroups.add(group);
			log("Group:" + group + " added to player:" + player + " in world:" + world.getName());
		} else {
			log("Group:" + group + " could not be added to player:" + player
					+ " in world:"+world.getName()+" as the player already has this group");
			return;
		}
		users.setProperty("users." + player + ".groups", playerGroups);
		users.save();
		
		setupPlayers();
	}

	@Override
	public void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public void removeGroup(String player, String group) {
		List<String> playerGroups = getGroups(player);
		if (playerGroups.contains(group)) {
			playerGroups.remove(group);
			log("Group:" + group + " removed from player:" + player + " in world:" + world.getName());
		} else {
			log("Group:" + group + " could not be removed from player:" + player
					+ " in world:"+world.getName()+" as the player does not have this group");
			return;
		}
		users.setProperty("users." + player + ".groups", playerGroups);
		users.save();
		
		setupPlayers();
	}

	@Override
	public void setupPlayers() {
		for (Player player : world.getPlayers()) {
			SuperPermissionHandler sp = new SuperPermissionHandler(player);
			sp.unsetupPlayer();
			sp.setupPlayer(this.getPlayerNodes(player), plugin);
		}
	}

	public boolean has(Player player, String node) {
		return HasPermission.has(player, node);
	}

	@Override
	public void overrideCraftPlayers() {
		for(Player player : getWorld().getPlayers()) {
		if (Permissions.useMonkeyPlayer && plugin.overridePlayer) {

        if (!(player instanceof CraftPlayer)) {
            System.err.println("Player is not an instance of CraftPlayer! "+player.getName());
            return;
        }
        MonkeyPlayer newPlayer = new MonkeyPlayer((CraftPlayer) player);
        try {
            Permissions.entity_bukkitEntity.set(newPlayer.getHandle(), newPlayer);
        } catch (IllegalArgumentException e) {
            System.err.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
            e.printStackTrace();
        }
    }
}
}

	@Override
	public String getDefaultGroup() {
		return groups.getString("default", defaultGroup);
	}
	
	@Override
	public void setGroup(Player player, String group) {
		setGroup(player.getName(), group);
	}

	@Override
	public void setGroup(String player, String group) {
		addGroup(player, group);
		
		for(String removeGroup : getGroups(player))
			if(!removeGroup.equals(group))
			removeGroup(player, removeGroup);
		
	}

	@Override
	public List<String> getAllCachedGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllCachedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

}
