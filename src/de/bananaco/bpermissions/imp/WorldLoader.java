package de.bananaco.bpermissions.imp;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;

import de.bananaco.bpermissions.api.WorldManager;
/**
 * This class should handle the world mirroring
 * as well as the world loading.
 * 
 *  Currently YamlWorld is hardcoded, but support
 *  for other types will be added at some point.
 *  
 *  (again, in this classfile, it can be passed through a constructor argument)
 */
public class WorldLoader extends WorldListener {

	WorldManager wm = WorldManager.getInstance();
	Map<String, String> mirrors;
	
	protected WorldLoader(Map<String, String> mirrors) {
		this.mirrors = mirrors;
		for(World world : Bukkit.getServer().getWorlds()) {
			createWorld(world);
		}
	}
	
	@Override
	public void onWorldInit(WorldInitEvent event) {
		createWorld(event.getWorld());
	}
	
	public void createWorld(World w) {
		System.out.println(Permissions.blankFormat("Loading world: "+w.getName()));
		String world = w.getName();
		/*
		 * If the mirror exists and the world to be mirrored to is loaded
		 */
		if(mirrors.containsKey(world) && wm.containsWorld(mirrors.get(world)))
		wm.createWorld(world, wm.getWorld(mirrors.get(world)));
		/*
		 * Otherwise, create a new world
		 */
		else
		wm.createWorld(world, new YamlWorld(world));
	}

}
