package de.bananaco.bpermissions.api.util;

public class WorldCarrier extends MetaData {
	
	private final String world;
	
	WorldCarrier(String world) {
		this.world = world;
	}
	
	public String getWorld() {
		return world;
	}

}
