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

	private WorldManager wm = WorldManager.getInstance();
	private Map<String, String> mirrors;
	private Permissions permissions;
	
	protected WorldLoader(Permissions permissions, Map<String, String> mirrors) {
		this.mirrors = mirrors;
		this.permissions = permissions;
		for(World world : Bukkit.getServer().getWorlds()) {
			createWorld(world);
		}
	}
	
	@Override
	public void onWorldInit(WorldInitEvent event) {
		createWorld(event.getWorld());
	}
	
	public void createWorld(World w) {
		// TODO this is probably going to be an issue
		String world = w.getName().toLowerCase();
		
		if(!mirrors.containsKey(world)) {
		System.out.println(Permissions.blankFormat("Loading world: "+w.getName()));
		wm.createWorld(world, new YamlWorld(world, permissions));
		}
	}

}
