package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import de.bananaco.bpermissions.api.WorldManager;

public class Mirrors {
	
	private final Map<String, String> mirrors;
	private final File file = new File("plugins/bPermissions/mirrors.yml");
	private final YamlConfiguration config = new YamlConfiguration();
	
	protected Mirrors(Map<String, String> mirrors) {
		this.mirrors = mirrors;
	}
	
	/**
	 * Loads the mirrors from the YamlConfiguration
	 * plugins/bPermissions/mirrors.yml
	 * No nested values, simply keyed like so - all
	 * these would be mirrored to the object "world"
	 * world_nether: world
	 * world_end: world
	 * world_economy: world
	 */
	public void load() {
		try {
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			config.load(file);
			Set<String> keys = config.getKeys(false);
			if(keys != null && keys.size() > 0) {
				for(String key : keys) 
					mirrors.put(key.toLowerCase(), config.getString(key).toLowerCase());
			}
			else {
				config.set("example_world_nether","example_world");
				config.set("example_world_end","example_world");
				config.save(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		WorldManager.getInstance().setMirrors(mirrors);
	}

}
