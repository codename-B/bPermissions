package de.bananaco.bpermissions.imp;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This code is distributed for your use and modification.
 * Do what you like with it, but credit me for the original!
 * 
 * Also I'd be interested to see what you do with it.
 * @author codename_B
 */
public class BukkitCompat {

	// lots of lovely Reflection in order to access parts of Bukkit we're not really supposed to be able to but do anyway
	private static Field permissions;
	private static Field base;
	private static Field basePermissions;
	private static Field attachments;

	static {
		try {
			// these are all CraftBukkit only
			permissions = PermissionAttachment.class.getDeclaredField("permissions");
			permissions.setAccessible(true);
			// especially this one
			base = CraftHumanEntity.class.getDeclaredField("perm");
			base.setAccessible(true);
			basePermissions = PermissibleBase.class.getDeclaredField("permissions");
			basePermissions.setAccessible(true);
			attachments = PermissibleBase.class.getDeclaredField("attachments");
			attachments.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use to efficiently set a Map<String, Boolean> onto a Player
	 * Assumes one large PermissionAttachment
	 * @param p
	 * @param plugin
	 * @param perm
	 * @return 
	 */
	public static PermissionAttachment setPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) {
		try {
			return doBukkitPermissions(p, plugin, perm);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Thanks @Wolvereness it looks like my reflection is no longer needed!
	 * @param p
	 * @param plugin
	 * @param permissions
	 * @return
	 */
	public static PermissionAttachment doBukkitPermissions(Permissible p, Plugin plugin, Map<String, Boolean> permissions) {
		Player player = (Player) p;
		
		Permission positive = plugin.getServer().getPluginManager().getPermission(player.getName());
		Permission negative = plugin.getServer().getPluginManager().getPermission("^"+player.getName());
		
		if(positive != null) {
			plugin.getServer().getPluginManager().removePermission(positive);
		}
		if(negative != null) {
			plugin.getServer().getPluginManager().removePermission(negative);
		}
		
		Map<String, Boolean> po = new HashMap<String, Boolean>();
		Map<String, Boolean> ne = new HashMap<String, Boolean>();
		
		for(String key : permissions.keySet()) {
			if(permissions.get(key)) {
				po.put(key, true);
			} else {
				ne.put(key, false);
			}
		}
		
		positive = new Permission(player.getName(), PermissionDefault.FALSE, po);
		negative = new Permission("^"+player.getName(), PermissionDefault.FALSE, ne);
		
		plugin.getServer().getPluginManager().addPermission(positive);
		plugin.getServer().getPluginManager().addPermission(negative);
		PermissionAttachment att = null;
		for(PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
			if(pai.getAttachment() != null && pai.getAttachment().getPlugin() != null) {
				if(pai.getAttachment().getPlugin() instanceof Permissions) {
					att = pai.getAttachment();
					break;
				}
			}
		}
		// only if null
		if(att == null) {
			att = player.addAttachment(plugin);
			att.setPermission(player.getName(), true);
			att.setPermission("^"+player.getName(), true);
		}
		// recalculate permissions
		player.recalculatePermissions();
		return att;
	}
	
	public static PermissionAttachment doSlowBukkitPermissions(Permissible p, Plugin plugin, Map<String, Boolean> permissions) {
		PermissionAttachment att = p.addAttachment(plugin);
		Map<String, Boolean> po = new HashMap<String, Boolean>();
		Map<String, Boolean> ne = new HashMap<String, Boolean>();
		
		for(String key : permissions.keySet()) {
			if(permissions.get(key)) {
				po.put(key, true);
			} else {
				ne.put(key, false);
			}
		}
		
		for(String key : po.keySet()) {
			att.setPermission(key, true);
		}
		for(String key : ne.keySet()) {
			att.setPermission(key, false);
		}
		
		return att;
	}

	/**
	 * Internal method, called from setPermissions()
	 * @param p
	 * @param plugin
	 * @param perm
	 * @return 
	 * @throws Exception
	 */
	public static PermissionAttachment doSetPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) throws Exception {
		// Grab a reference to the original object
		PermissibleBase pb = getBase(p);
		// I know, it's a lot more reflection
		Map<String, PermissionAttachmentInfo> info = getInfo(pb);
		info.clear();
		// What can I do? Even more reflection! This also slows things down by about 2ms (but in the scale of things it works, yay!)
		List<PermissionAttachment> delete = new ArrayList<PermissionAttachment>();
		List<PermissionAttachment> attach = getAttachments(pb);
		for(PermissionAttachment att : attach) {
			if(att.getPlugin().getName().equalsIgnoreCase("bpermissions")) {
				Debugger.log("Removing "+att.toString());
				delete.add(att);
			}
		}
		for(PermissionAttachment att : delete) {
			attach.remove(att);
		}
		delete.clear();
		// and push our changes!
		PermissionAttachment att = pb.addAttachment(plugin);
		permissions.set(att, perm);
		pb.recalculatePermissions();
		return att;
	}

	/**
	 * Return the PermissibleBase from the Permissible instanceof CraftHumanEntity
	 * @param p
	 * @return PermissibleBase
	 */
	public static PermissibleBase getBase(Permissible p) {
		try {
			return (PermissibleBase) base.get(p);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Return the info, direct reference!
	 * @param pb
	 * @return Map<String, PermissionAttachmentInfo>
	 */
	public static Map<String, PermissionAttachmentInfo> getInfo(PermissibleBase pb) {
		try {
			return (Map<String, PermissionAttachmentInfo>) basePermissions.get(pb);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Return the attachments, direct reference!
	 * @param pb
	 * @return List<PermissionAttachment>
	 */
	public static List<PermissionAttachment> getAttachments(PermissibleBase pb) {
		try {
			return (List<PermissionAttachment>) attachments.get(pb);
		} catch (Exception e) {
			return null;
		}
	}

	public static void runTest(Player player, Plugin plugin) {
		long start, finish, time;
		// 1000 example permissions
		Map<String, Boolean> permissions = new HashMap<String, Boolean>();
		Set<String> keys = new HashSet<String>(permissions.keySet());
		
		for(int i=0; i<10000; i++) {
			permissions.put("example."+String.valueOf(i), true);
		}

		// superpermissions
		start = System.currentTimeMillis();
		PermissionAttachment att = player.addAttachment(plugin);
		// and obviously we iteratively add here!
		for(String key : permissions.keySet()) {
			att.setPermission(key, permissions.get(key));
		}
		finish = System.currentTimeMillis();
		time = finish-start;
		System.out.println("SuperPermissions default took: "+time+"ms.");		
		if(!player.hasPermission("example.1")) {
			System.err.println("permissions not registered!");
		}
		// cleanup
		for(String key : keys) {
			att.unsetPermission(key);
		}
		att.remove();
		if(player.hasPermission("example.1")) {
			System.err.println("permissions not unregistered!");
		}
		// supersuperpermissions
		start = System.currentTimeMillis();
		att = BukkitCompat.doBukkitPermissions(player, plugin, permissions);
		finish = System.currentTimeMillis();
		time = finish-start;
		if(!player.hasPermission("example.1")) {
			System.err.println("permissions not registered!");
		}
		// cleanup
		att.unsetPermission(player.getName());
		att.remove();
		System.out.println("SuperPermissions hack took: "+time+"ms.");		
		if(player.hasPermission("example.1")) {
			System.err.println("permissions not unregistered!");
		}
		// ourpermissions
		start = System.currentTimeMillis();
		att = BukkitCompat.setPermissions(player, plugin, permissions);
		finish = System.currentTimeMillis();
		time = finish-start;
		if(!player.hasPermission("example.1")) {
			System.err.println("permissions not registered!");
		}
		System.out.println("bPermissions default took: "+time+"ms.");
		// cleanup
		for(String key : keys) {
			att.unsetPermission(key);
		}
		att.remove();
		if(player.hasPermission("example.1")) {
			System.err.println("permissions not unregistered!");
		}
	}

}
