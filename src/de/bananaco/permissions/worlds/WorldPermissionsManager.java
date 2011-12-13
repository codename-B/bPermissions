package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
/**
 * This really hasn't changed much since about 1.8
 * 
 * The basic WorldPermissionsManager. It handles mirroring, and loading of PermissionSets.
 */
public class WorldPermissionsManager {
	private boolean engaged = false;
	/**
	 * The instance of the main class
	 */
	private final Permissions jp;

	/**
	 * Contains the PermissionSets with
	 */
	private final HashMap<String, PermissionSet> ps;

	/**
	 * Instantiate this bitch
	 * 
	 * @param jp
	 */
	public WorldPermissionsManager(Permissions jp) {
		this.ps = new HashMap<String, PermissionSet>();
		this.jp = jp;
	}

	/**
	 * Adds all worlds active on the server
	 */
	public void addAllWorlds() {
		if (!this.engaged) {
			System.err
					.println("[bPermissions] WorldPermissionsManager not engaged!");
			return;
		}
		for (World world : jp.getServer().getWorlds()) {
			PermissionSet p = null;
			p = getPermissionSet(world);
			if(p != null)
			p.setup();
			else
			System.err.println(world.getName()+" has a null PermissionSet!");

			/*
			 * String wName = world.getName(); if
			 * (jp.mirror.containsKey(world.getName()) &&
			 * jp.mirror.get(world.getName()) != null) wName =
			 * jp.mirror.get(world.getName()); World tWorld =
			 * jp.getServer().getWorld(wName) != null ? jp
			 * .getServer().getWorld(wName) : world; if (ps.containsKey(wName))
			 * { p = this.getPermissionSet(wName); p.reload(); } else p =
			 * jp.wps.get(tWorld, jp); p.setupPlayers();
			 * 
			 * ps.put(world.getName(), p); log("Setup world:" +
			 * world.getName());
			 */
		}
	}

	public void engage() {
		this.engaged = true;
		this.addAllWorlds();
		log("WorldPermissionsManager engaged");
	}

	/**
	 * Really gets the PermissionSet
	 * 
	 * @param world
	 * @return PermissionSet
	 */
	public PermissionSet getPermissionSet(String world) {
		if (!this.engaged) {
			System.err.println("[bPermissions] WorldPermissionsManager not engaged!");
			return null;
		}
		if (jp.mirror.containsKey(world) && jp.mirror.get(world) != null) {
			world = jp.mirror.get(world);
		}
		if (ps.containsKey(world)) {
			return ps.get(world);
		} else {
			World w = jp.getServer().getWorld(world);

			if (w == null)
				return null;

			PermissionSet p = jp.wps.get(w, jp);
			ps.put(world, p);
			log("Created PermissionSet for world:" + world);
			p.getWorldPermissions().load();
			return p;
		}
	}

	/**
	 * Gets the PermissionSet
	 * 
	 * @param world
	 * @return PermissionSet
	 */
	public PermissionSet getPermissionSet(World world) {
		return getPermissionSet(world.getName());
	}

	/**
	 * Added in preparation for GUI permissions managers (EWW)
	 * 
	 * @return List<PermissionSet> unique only
	 */
	public List<PermissionSet> getPermissionSets() {
		if (!this.engaged) {
			System.err
					.println("[bPermissions] WorldPermissionsManager not engaged!");
			return null;
		}
		List<PermissionSet> ps = new ArrayList<PermissionSet>();
		for (String key : this.ps.keySet()) {
			if (!ps.contains(this.ps.get(key)) && this.ps.get(key) != null)
				ps.add(this.ps.get(key));
		}
		return ps;
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		jp.log(input);
	}
}
