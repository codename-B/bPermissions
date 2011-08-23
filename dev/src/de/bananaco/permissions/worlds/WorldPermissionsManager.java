package de.bananaco.permissions.worlds;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.interfaces.PermissionSet;

public class WorldPermissionsManager {
	/**
	 * Contains the PermissionSets with
	 */
	private final HashMap<String,PermissionSet> ps;
	/**
	 * The instance of the main class
	 */
	private final JavaPlugin jp;
	/**
	 * Instantiate this bitch
	 * @param jp
	 */
	public WorldPermissionsManager(JavaPlugin jp) {
		this.ps = new HashMap<String,PermissionSet>();
		this.jp = jp;
		this.addAllWorlds();
		log("WorldPermissionsManager engaged");
	}
	/**
	 * Adds all worlds active on the server
	 */
	public void addAllWorlds() {
		for(World world : jp.getServer().getWorlds()) {
			PermissionSet p = new WorldPermissions(world,jp);
			p.setupPlayers();
			ps.put(world.getName(), p);
			log("Setup world:"+world.getName());
		}
	}
	/**
	 * Just the logger man
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions] "+String.valueOf(input));
	}
	/**
	 * Gets the PermissionSet
	 * @param world
	 * @return PermissionSet
	 */
	public PermissionSet getPermissionSet(World world) {
	return getPermissionSet(world.getName());
	}
	/**
	 * Really gets the PermissionSet
	 * @param world
	 * @return PermissionSet
	 */
	public PermissionSet getPermissionSet(String world) {
	if(ps.containsKey(world)) {
	return ps.get(world);
	}
	else {
	World w = jp.getServer().getWorld(world);
	PermissionSet p = new WorldPermissions(w,jp);
	ps.put(world, p);
	return p;
	}
	}
}
