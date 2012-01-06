package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;

import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PermissionSet;
/**
 * This really hasn't changed much since about 1.8
 * 
 * The basic WorldPermissionsManager. It handles mirroring, and loading of PermissionSets.
 */
public class WorldPermissionsManager {

	WorldManager wm = WorldManager.getInstance();
	
	/**
	 * Contains the PermissionSets with
	 */
	private final HashMap<String, PermissionSet> ps = new HashMap<String, PermissionSet>();

	/**
	 * Really gets the PermissionSet
	 * 
	 * @param world
	 * @return PermissionSet
	 */
	public PermissionSet getPermissionSet(String world) {
		if(ps.containsKey(world))
			return ps.get(world);
		
		de.bananaco.bpermissions.api.World w = wm.getWorld(world);
		ps.put(world, new WrapperPermissionSet(w));
		
		return ps.get(world);
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
		List<PermissionSet> ps = new ArrayList<PermissionSet>();
		for (String key : this.ps.keySet()) {
			if (!ps.contains(this.ps.get(key)) && this.ps.get(key) != null)
				ps.add(this.ps.get(key));
		}
		return ps;
	}

}
