package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;

public class JSONWorldPermissions extends TransitionPermissions implements PermissionSet {
	
	/**
	 * The main class instance
	 */
	@SuppressWarnings("unused")
	private final Permissions plugin;
	/**
	 * The world
	 */
	@SuppressWarnings("unused")
	private final World world;
	/**
	 * The default!
	 */
	@SuppressWarnings("unused")
	private String defaultGroup = "default";
	
	public JSONWorldPermissions(World world, Permissions plugin) {
		super(new HashMap<String, ArrayList<String>>());
		this.plugin = plugin;
		this.world = world;
		setup();
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addNode(String node, String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(String node, String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getGroupNodes(String group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPlayerNodes(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPlayerNodes(String player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getGroups(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getGroups(String player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGroup(Player player, String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addGroup(String player, String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroup(Player player, String group) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGroup(String player, String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupPlayers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void overrideCraftPlayers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean has(Player player, String node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDefaultGroup() {
		// TODO Auto-generated method stub
		return null;
	}
	
}