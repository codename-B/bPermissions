package de.bananaco.bpermissions.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class Tests {
	
	public static void main(String[] args) {
		WorldManager wm = WorldManager.getInstance();
		wm.createWorld("blank", new BlankWorld("blank"));
		// The world object
		World world = wm.getWorld("blank");
		
		// The user to test
		String name = "testUser";
		List<String> groups = new ArrayList<String>();
		groups.add("default");
		List<String> permissions = new ArrayList<String>();
		permissions.add("test.permission1");
		// The user object
		User user = new User(name, groups, Permission.loadFromString(permissions), world.getName(), world);
		user.setValue("prefix", "userPrefix");
		
		world.add(user);
		// The group to test
		String gName = "default";
		List<String> gPermissions = new ArrayList<String>();
		gPermissions.add("test.permission2");
		// The group object
		Group group = new Group(gName, null, Permission.loadFromString(gPermissions), world.getName(), world);
		group.setValue("prefix", "groupPrefix");
		group.setValue("suffix", "groupSuffix");
		
		world.add(group);
		
		try {
		user.calculateEffectivePermissions();
		user.calculateEffectiveMeta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Testing the ApiLayer.java
		System.out.println("Expected output: [default]");
		System.out.println(Arrays.toString(ApiLayer.getGroups(world.getName(), CalculableType.USER, name)));
		
		System.out.println("Expected output: [test.permission1:true]");
		System.out.println(Arrays.toString(ApiLayer.getPermissions(world.getName(), CalculableType.USER, name)));
		
		System.out.println("Expected output: userPrefix");
		System.out.println(ApiLayer.getValue(world.getName(), CalculableType.USER, name, "prefix"));
		
		System.out.println("Expected output: groupSuffix");
		System.out.println(ApiLayer.getValue(world.getName(), CalculableType.USER, name, "suffix"));
		
		System.out.println("Expected ouput: false true true");
		System.out.println(ApiLayer.hasPermission(world.getName(), CalculableType.USER, name, "a.random.node")+
		" " + ApiLayer.hasPermission(world.getName(), CalculableType.USER, name, "test.permission1") +
		" " + ApiLayer.hasPermission(world.getName(), CalculableType.USER, name, "test.permission2"));
		
		System.out.println("Expected output: true false");
		System.out.println(ApiLayer.hasGroup(world.getName(), CalculableType.USER, name, gName)+
				" "+ApiLayer.hasGroup(world.getName(), CalculableType.USER, name, "random"));
		
	}
	
	static class BlankWorld extends World {

		public BlankWorld(String world) {
			super(world);
		}

		@Override
		public boolean load() {
			return false;
		}

		@Override
		public boolean save() {
			return false;
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

}
