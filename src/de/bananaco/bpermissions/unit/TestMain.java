package de.bananaco.bpermissions.unit;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class TestMain {
	
	public static void main(String[] args) {
		String w = "world";
		World world = new WorldTest(w);
		World global = new WorldTest("*");
		WorldManager.getInstance().createWorld(w, world);
		//WorldManager.getInstance().createWorld("global", global);
		WorldManager.getInstance().setDefaultWorld(global);
		WorldManager.getInstance().setUseGlobalFiles(true);
		
		CalculableTest ct = new CalculableTest(world);
		ct.customNodesTest();
		/*
		ct.globalPermsTest();
		ct.heroChatTest();
		ct.slipcorTest3();
		ct.slipcorTest2();
		ct.ApiLayerTest();
		ct.gv1222PrefixTest();
		ct.negativeInheritanceCheck();
		ct.nullPassCheck();
		ct.test100LevelInheritance();
		ct.testPermissions();
		*/
		
	}

}
