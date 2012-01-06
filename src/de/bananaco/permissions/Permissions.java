package de.bananaco.permissions;

import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Permissions {
	
	private static WorldPermissionsManager wpm = new WorldPermissionsManager();
	private static InfoReader info = new InfoReader();
	
	public static WorldPermissionsManager getWorldPermissionsManager() {
		return wpm;
	}
	
	public static InfoReader getInfoReader() {
		return info;
	}

}
