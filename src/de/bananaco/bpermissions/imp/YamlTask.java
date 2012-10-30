package de.bananaco.bpermissions.imp;

import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class YamlTask implements Runnable {
	
	private final WorldManager wm = WorldManager.getInstance();
	
	YamlTask(JavaPlugin plugin) {
		// register event
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 20*10, 20*10);
	}

	@Override
	public void run() {
		for(World world : wm.getAllWorlds()) {
			if(world instanceof Runnable) {
				Runnable r = (Runnable) world;
				r.run();
			}
		}
	}

}
