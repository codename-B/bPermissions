package de.bananaco.permissions.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldManager {
	
	public static WorldManager instance = null;
	
	public static WorldManager getInstance() {
		if(instance == null)
			instance = new WorldManager();
		return instance;
	}
	
	Map<String, World> worlds = new HashMap<String, World>();
	
	protected WorldManager() {
		// TODO globals?
	}
	
	public World getWorld(String name) {
		name = name.toLowerCase();
		return worlds.get(name);
	}
	
	public boolean containsWorld(String name) {
		name = name.toLowerCase();
		return worlds.containsKey(name);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<World> getAllWorlds() {
		Set<World> worlds = new HashSet();
		for(String key : this.worlds.keySet()) {
			worlds.add(this.worlds.get(key));
		}
		return worlds;
	}
	
	/**
	 * Used to store a reference to a World object by name
	 * @param name
	 * @param world
	 */
	public void createWorld(String name, World world) {
		name = name.toLowerCase();
		worlds.put(name, world);
		world.load();
	}

}
