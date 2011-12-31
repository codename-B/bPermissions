package de.bananaco.permissions.api.util;

public class WorldCarrier extends MetaData {
	
	private final String world;
	
	WorldCarrier(String world) {
		this.world = world;
	}
	
	public String getWorld() {
		return world;
	}

}
