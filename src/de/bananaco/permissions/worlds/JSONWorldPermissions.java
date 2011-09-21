package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.SuperPermissionHandler;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.interfaces.TransitionSet;
import de.bananaco.permissions.json.JSONPermission;
import de.bananaco.permissions.override.MonkeyPlayer;

public class JSONWorldPermissions extends TransitionPermissions implements PermissionSet {
	
	/**
	 * The main class instance
	 */
	private final Permissions plugin;
	/**
	 * The world
	 */
	private final World world;
	/**
	 * The default!
	 */
	@SuppressWarnings("unused")
	private String defaultGroup = "default";
	
	private JSONPermission permission;
	private Map<String, List<String>> groups;
	private Map<String, List<String>> players;
	
	public JSONWorldPermissions(World world, Permissions plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.plugin = plugin;
		this.world = world;
		this.permission = new JSONPermission(new File("plugins/bPermissions/worlds/"+world.getName()+".json"));
		setup();
	}

	private ArrayList<String> getDefaultArrayList() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(getDefaultGroup());
		return ar;
	}
	
	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public void setup() {
		reload();
	}
	
	private void save() {
		permission.put(getDefaultGroup(), players, groups);
		permission.save();
	}
	
	@Override
	public void reload() {
		permission.load();
		permission.save();
		Map<String, Map<String, List<String>>> loc = permission.get();
		groups = loc.get("groups");
		players = loc.get("players");
		defaultGroup = permission.getDefault();
		setupPlayers();
	}

	private void setGroupNodes(String group, List<String> nodes) {
		groups.put(group, nodes);
		save();
	}
	
	@Override
	public void addNode(String node, String group) {
		List<String> nodes = getGroupNodes(group);
		if(!nodes.contains(node)) {
			nodes.add(node);
			setGroupNodes(group, nodes);
		}
	}

	@Override
	public void removeNode(String node, String group) {
		List<String> nodes = getGroupNodes(group);
		if(nodes.contains(node)) {
			nodes.remove(node);
			setGroupNodes(group, nodes);
		}
	}

	@Override
	public List<String> getGroupNodes(String group) {
		List<String> nodes = new ArrayList<String>();
		if(groups.containsKey(group))
			return groups.get(group);
		return nodes;
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
		return playerNodes;
	}
	
	private void setGroups(String player, List<String> groups) {
		players.put(player, groups);
		save();
	}
	
	@Override
	public List<String> getGroups(Player player) {
		return getGroups(player.getName());
	}

	@Override
	public List<String> getGroups(String player) {
		List<String> groups = getDefaultArrayList();
		if(players.containsKey(player))
			return players.get(player);
		return groups;
	}

	@Override
	public void addGroup(Player player, String group) {
		addGroup(player.getName(), group);
	}

	@Override
	public void addGroup(String player, String group) {
		List<String> groups = getGroups(player);
		if(!groups.contains(group)) {
			groups.add(group);
			setGroups(player, groups);
		}
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
	public void removeGroup(Player player, String group) {
		removeGroup(player.getName(), group);
	}

	@Override
	public void removeGroup(String player, String group) {
		List<String> groups = getGroups(player);
		if(groups.contains(group)) {
		groups.remove(group);
		setGroups(player, groups);
		}
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
	public void setupPlayers() {
		for (Player player : world.getPlayers()) {
			SuperPermissionHandler sp = new SuperPermissionHandler(player);
			sp.unsetupPlayer();
			sp.setupPlayer(this.getPlayerNodes(player), plugin);
		}
	}

	@Override
	public boolean has(Player player, String node) {
		return HasPermission.has(player, node);
	}
	
	@Override
	public String getDefaultGroup() {
		return this.defaultGroup;
	}
	
}