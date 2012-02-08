package de.bananaco.bpermissions.unit;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class TestMain {
	
	public static void main(String[] args) {
		String w = "world";
		World world = new WorldTest(w);
		WorldManager.getInstance().createWorld(w, world);
	}

}
