package de.bananaco.bpermissions.unit;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.Permission;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.WorldManager;

public class PermissionsTest {
	
	public static void test(Player player) {
		World world = player.getWorld();
		WorldManager wm = WorldManager.getInstance();
		User user = wm.getWorld(world.getName()).getUser(player.getName());
		Set<Permission> permissions = user.getEffectivePermissions();
		System.out.println("** PERMISSION TEST FOR "+player.getName().toUpperCase());
		for(Permission perm : permissions) {
			System.out.println("** "+perm.name().toUpperCase());
			System.out.println("Expected: "+perm.isTrue()+" Got: "+player.hasPermission(perm.nameLowerCase()));
		}
	}

}
