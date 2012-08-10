package de.bananaco.bpermissions.unit;

import java.util.Arrays;
import java.util.Map;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;
import de.bananaco.bpermissions.api.util.RecursiveGroupException;

public class CalculableTest {

	private final World world;

	public CalculableTest(World world) {
		this.world = world;
	}
	
	public void printLine() {
		System.out.println("#################################################");
	}
	
	public void slipcorTest() {
		printLine();
		System.out.println("slipcor test begin!");
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "donator", "priority", "52");
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "donator", "default");
		ApiLayer.setGroup(world.getName(), CalculableType.USER, "slipcor", "donator");
		
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "mage", "priority", "55");
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "mage", "default");
		ApiLayer.addGroup(world.getName(), CalculableType.USER, "slipcor", "mage");
		
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "mayor", "priority", "66");
		ApiLayer.addGroup(world.getName(), CalculableType.USER, "slipcor", "mayor");
		
		ApiLayer.addPermission(world.getName(), CalculableType.GROUP, "default", Permission.loadFromString("^test.test"));
		ApiLayer.addPermission(world.getName(), CalculableType.GROUP, "mage", Permission.loadFromString("test.test"));
		
		ApiLayer.update();
		
		//System.out.print("slipcor has groups: ");
		//for(String group : ApiLayer.getGroups(world.getName(), CalculableType.USER, "slipcor")) {
		//	System.out.print(group+" ");
		//}
		//System.out.println(".");
		
		boolean perm = ApiLayer.hasPermission(world.getName(), CalculableType.USER, "slipcor", "test.test");
		//System.out.println("slipcor has perm - test.test: "+String.valueOf(perm));
		if(perm) {
			System.out.println("Test passed!");
		} else {
			System.out.println("Test failed!");
		}
	}
	
	public void slipcorTest2() {
		printLine();
		System.out.println("slipcor test2 begin!");
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "smage", "priority", "51");
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "smage", "mage");
		
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "mage", "priority", "50");
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "mage", "default");
		
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "default", "priority", "1");
		
		ApiLayer.setGroup(world.getName(), CalculableType.USER, "slipcor", "default");
		ApiLayer.addGroup(world.getName(), CalculableType.USER, "slipcor", "smage");
		
		ApiLayer.addPermission(world.getName(), CalculableType.GROUP, "default", Permission.loadFromString("^test.test"));
		ApiLayer.addPermission(world.getName(), CalculableType.GROUP, "smage", Permission.loadFromString("test.test"));
		
		ApiLayer.update();
		
		//System.out.print("slipcor has groups: ");
		for(String group : ApiLayer.getGroups(world.getName(), CalculableType.USER, "slipcor")) {
			System.out.print(group+" ");
		}
		//System.out.println(".");
		
		boolean perm = ApiLayer.hasPermission(world.getName(), CalculableType.USER, "slipcor", "test.test");
		//System.out.println("slipcor has perm - test.test: "+String.valueOf(perm));
		
		if(perm) {
			System.out.println("Test passed!");
		} else {
			System.out.println("Test failed!");
		}
	}
	
	public void slipcorTest3() {
		printLine();
		System.out.println("slipcor test3 begin!");
		ApiLayer.addPermission(world.getName(), CalculableType.GROUP, "groupA", Permission.loadFromString("node"));
		
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "groupB", "groupA");
		ApiLayer.addGroup(world.getName(), CalculableType.GROUP, "groupC", "groupB");
		ApiLayer.setGroup(world.getName(), CalculableType.USER, "test", "groupC");
		ApiLayer.addGroup(world.getName(), CalculableType.USER, "test", "groupD");
		
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "groupC", "priority", "5");
		ApiLayer.setValue(world.getName(), CalculableType.GROUP, "groupD", "priority", "25");
		
		ApiLayer.update();
		
		boolean perm = ApiLayer.hasPermission(world.getName(), CalculableType.USER, "test", "node");
		if(perm) {
			System.out.println("Test passed!");
		} else {
			System.out.println("Test failed!");
		}
	}
	
	public void negativeInheritanceCheck() {
		printLine();
		WorldManager.getInstance().setDefaultWorld(world);
		try {
			ApiLayer.addPermission(null, CalculableType.GROUP, "groupA", Permission.loadFromString("command.node"));
			ApiLayer.addPermission(null, CalculableType.GROUP, "groupB", Permission.loadFromString("^command.node"));
			ApiLayer.addGroup(null, CalculableType.GROUP, "groupB", "groupA");
			ApiLayer.addGroup(null, CalculableType.USER, "test", "groupB");
			
			Calculable test = WorldManager.getInstance().getDefaultWorld().get("test", CalculableType.USER);
			System.out.println("Testing (true if passed) groupA: command.node groupB: ^command.node");
			System.out.println("user hasgroup groupB - expected negative node: result");
			test.calculateEffectivePermissions();
			System.out.println(test.hasPermission("command.node") == false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGlobalPermissionsCalculation() {
		printLine();		
		
		WorldManager.getInstance().setUseGlobalFiles(true);
		
		ApiLayer.setGroup(null, CalculableType.USER, "test", "default");
		ApiLayer.setGroup(world.getName(), CalculableType.USER, "test", "default");
		
		ApiLayer.addPermission(null, CalculableType.USER, "test", Permission.loadFromString("^global.negative"));
		ApiLayer.addPermission(null, CalculableType.USER, "test", Permission.loadFromString("global.positive"));
		ApiLayer.addPermission(null, CalculableType.USER, "test", Permission.loadFromString("^world.positive"));
		ApiLayer.addPermission(world.getName(), CalculableType.USER, "test", Permission.loadFromString("world.positive"));
		
		WorldManager.getInstance().update();
		
		Map<String, Boolean> permissions = ApiLayer.getEffectivePermissions(world.getName(), CalculableType.USER, "test");
		
		for(String key : permissions.keySet()) {
			System.out.println(key+":"+String.valueOf(permissions.get(key)));
		}
		
		if(permissions.get("global.negative") == false && permissions.get("global.positive") == true && permissions.get("world.positive") == true) {
			System.out.println("test passed!");
		} else {
			System.out.println("test failed");
		}
	}
	
	public void nullPassCheck() {
		printLine();
		WorldManager.getInstance().setDefaultWorld(world);
		try {
		ApiLayer.getValue(null, CalculableType.USER, "test", "test");
		System.out.println("test passed!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("test failed!");
		}
	}
	
	public void ApiLayerTest() {
		printLine();
		User user = new User("codename_B", world);
		user.setValue("prefix", "test");
		try {
		user.calculateEffectiveMeta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		world.add(user);
		System.out.println("Expected: test");
		System.out.println("Got: "+ApiLayer.getValue(world.getName(), CalculableType.USER, user.getName(), "prefix"));
	}
	
	public void gv1222PrefixTest() {
		printLine();
		
		User user = new User("gv1222", world);
		user.addGroup("a");

		Group a = new Group("A", world);
		a.setValue("priority", "100");
		a.setValue("prefix", "a");
		
		Group b = new Group("B", world);
		b.setValue("priority", "50");
		b.setValue("prefix", "b");
		
		
		world.add(a);
		world.add(b);
		world.add(user);
		
		try {
			user.calculateEffectiveMeta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("gv1222 has group a with prefix: "+user.getEffectiveValue("prefix"));
		
		user.addGroup("b");
		
		try {
			user.calculateEffectiveMeta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("gv1222 has group a and b with prefix: "+user.getEffectiveValue("prefix"));
		
		System.out.println("Test "+(user.getEffectiveValue("prefix").equals("a")?"passed":"failed"));
	}
	
	public void testNegativeToPositive() {
		printLine();
		
		User user = new User("user", world);
		user.addGroup("moderator");
		world.add(user);
		Group group0 = new Group("default", world);
		group0.addPermission("permission.*", true);
		group0.addPermission("permission.moderator", false);
		world.add(group0);
		Group group1 = new Group("moderator", world);
		group1.addGroup("default");
		group1.addPermission("permission.moderator", true);
		world.add(group1);
		
		try {
			user.calculateEffectivePermissions();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("user has group moderator");
		System.out.println("moderator inherits default");
		System.out.println("default has permission.* and ^permission.moderator");
		System.out.println("moderator has permission.moderator");
		
		System.out.print("expected false got "); System.out.println(group0.hasPermission("permission.moderator"));
		System.out.print("expected true got "); System.out.println(group0.hasPermission("permission.default"));
		
		System.out.print("expected true got "); System.out.println(group1.hasPermission("permission.moderator"));
		System.out.print("expected true got "); System.out.println(group1.hasPermission("permission.default"));
		
		System.out.print("expected true got "); System.out.println(user.hasPermission("permission.moderator"));
	}

	public void test100LevelInheritance() {
		Calculable base = new Group("base", world);
		base.setValue("priority", "0");
		base.setValue("prefix", "base");
		
		world.add(base);
		
		Calculable last = null;
		
		for(int i=1; i<100; i++) {
		Calculable next = new Group("next"+String.valueOf(i), world);
		next.setValue("priority", String.valueOf(i));
		next.setValue("prefix", "next"+String.valueOf(i));
		// Set up the inheritance structure
		if(i == 1)
			next.addGroup("base");
		else
			next.addGroup("next"+String.valueOf(i-1));
		world.add(next);
		last = next;
		}
		
		Calculable user = new User("test", world);
		user.addGroup(last.getName());
		world.add(user);
		
		try {
			user.calculateEffectiveMeta();
		} catch (RecursiveGroupException e) {
			e.printStackTrace();
		}
		
		printLine();
		System.out.println("Expected "+last.getEffectiveValue("prefix"));
		System.out.println("Got "+user.getEffectiveValue("prefix"));
	}
	
	public void testPriority() {
		printLine();
		// Create the groups
		Calculable group0 = new Group("default", world);
		Calculable group1 = new Group("moderator", world);
		Calculable group2 = new Group("admin", world);
		// Define the priority
		group0.setValue("priority", "0");
		group1.setValue("priority", "5");
		group2.setValue("priority", "20");
		// Define the test0
		group0.setValue("test0", group0.getName());
		group1.setValue("test0", group1.getName());
		group2.setValue("test0", group2.getName());
		// Define the test1
		group0.setValue("test1", group0.getName());
		group1.setValue("test1", group1.getName());
		// Define the test2
		group0.setValue("test2", group0.getName());
		// Add to the system
		world.add(group0);
		world.add(group1);
		world.add(group2);
		// Create the user
		Calculable user = new User("test", world);
		// Add the groups
		user.addGroup(group0.getName());
		user.addGroup(group1.getName());
		user.addGroup(group2.getName());
		// Add to the system
		world.add(user);
		// Calculate the meta
		try {
			user.calculateEffectiveMeta();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Test the test0
		String test0 = user.getEffectiveValue("test0");
		String test1 = user.getEffectiveValue("test1");
		String test2 = user.getEffectiveValue("test2");

		// Print the results
		System.out.println("default, moderator, admin assigned to User 'user'");
		System.out.println("'user' has groups:");
		System.out.println(Arrays.toString(ApiLayer.getGroups(world.getName(), CalculableType.USER, user.getName())));
		System.out.println("Primary group " + ApiLayer.getGroups(world.getName(), CalculableType.USER, user.getName())[0]);
		System.out.println("default priority: "+group0.getPriority());
		System.out.println("moderator priority: "+group1.getPriority());
		System.out.println("admin priority: "+group2.getPriority());

		System.out.println("test0 expected admin got "+test0);
		System.out.println("test1 expected moderator got "+test1);
		System.out.println("test2 expected default got "+test2);
	}

	public void testPermissions() {
		printLine();
		// Create the groups
		Calculable group0 = new Group("default", world);
		Calculable group1 = new Group("moderator", world);
		Calculable group2 = new Group("admin", world);
		// A non-building group to add
		Calculable group3 = new Group("non-builder", world);
		group3.setValue("priority", "100");
		// add the permissions
		group0.addPermission("permission.build", false);
		group1.addPermission("permission.build", true);
		
		group3.addPermission("permission.build", false);
		// Add to the system
		world.add(group0);
		world.add(group1);
		world.add(group2);
		world.add(group3);
		// Create the user
		Calculable user = new User("test", world);
		// Add the groups
		group1.addGroup(group0.getName());
		group2.addGroup(group1.getName());
		user.addGroup(group2.getName());
		// Add to the system
		world.add(user);
		// Calculate the permissions
		try {
		user.calculateEffectivePermissions();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("default: permission.build:false");
		System.out.println("moderator: permission.build: true");
		System.out.println("admin: unset");
		System.out.println("non-builder: permission.build: false priority: 100");
		System.out.println("admin -> moderator -> default");
		System.out.println("user has group: admin");
		System.out.println("expected: true - user has permission.build:"+user.hasPermission("permission.build"));
		System.out.println("addgroup: non-builder");
		user.addGroup(group3.getName());
		System.out.println("expected: false - user has permission.build:"+user.hasPermission("permission.build"));
		System.out.println("remove permission.build from group: non-builder");
		group3.removePermission("permission.build");
		System.out.println("expected: true - user has permission.build:"+user.hasPermission("permission.build"));
	}

}
