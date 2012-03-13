package de.bananaco.bpermissions.unit;

import de.bananaco.bpermissions.api.World;

public class WorldTest extends World {

	public WorldTest(String world) {
		super(world);
	}

	@Override
	public boolean load() {
		CalculableTest test = new CalculableTest(this);
		test.gv1222PrefixTest();
		return true;
	}

	@Override
	public boolean save() {
		
		return true;
	}

	@Override
	public String getDefaultGroup() {
		return "default";
	}

	@Override
	public boolean setupPlayer(String player) {
		return false;
	}

}
