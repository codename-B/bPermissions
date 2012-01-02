package de.bananaco.bpermissions.imp;

import java.util.Map;

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
	}
	
	@Override
	public void onWorldInit(WorldInitEvent event) {
		String world = event.getWorld().getName();
		/*
		 * If the mirror exists and the world to be mirrored to is loaded
		 */
		if(mirrors.containsKey(world) && wm.containsWorld(mirrors.get(world)))
		wm.createWorld(world, wm.getWorld(mirrors.get(world)));
		/*
		 * Otherwise, create a new world
		 */
		else
		wm.createWorld(world, new YamlWorld(event.getWorld().getName()));
	}

}
