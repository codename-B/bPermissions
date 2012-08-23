package de.bananaco.bpermissions.imp;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 */
	public static void setPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) {
		try {
			doSetPermissions(p, plugin, perm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Internal method, called from setPermissions()
	 * @param p
	 * @param plugin
	 * @param perm
	 * @throws Exception
	 */
	private static void doSetPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) throws Exception {
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
		// 1000 example permissions
		Map<String, Boolean> permissions = new HashMap<String, Boolean>();
		for(int i=0; i<1000; i++) {
			permissions.put("example."+String.valueOf(i), true);
		}
		
		// superpermissions
		long startS = System.currentTimeMillis();
		PermissionAttachment att = player.addAttachment(plugin);
		// and obviously we iteratively add here!
		for(String key : permissions.keySet()) {
			att.setPermission(key, permissions.get(key));
		}
		long finishS = System.currentTimeMillis();
		// cleanup
		for(String key : permissions.keySet()) {
			att.unsetPermission(key);
		}
		att.remove();
		
		// ourpermissions
		long startF = System.currentTimeMillis();
		BukkitCompat.setPermissions(player, plugin, permissions);
		long finishF = System.currentTimeMillis();
		
		// final times
		long timeS = finishS-startS;
		long timeF = finishF-startF;
		System.out.println("SuperPermissions default took: "+timeS+"ms.");
		System.out.println("bPermissions default took: "+timeF+"ms.");
	}

}
