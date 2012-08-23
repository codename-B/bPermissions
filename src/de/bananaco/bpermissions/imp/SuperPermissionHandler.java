package de.bananaco.bpermissions.imp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.CalculableType;
/**
 * Handles all the superperms registering/unregistering
 * for PermissionAttachments (it's basically just somewhere
 * to stick all the nasty SuperPerms stuff that wouldn't exist
 * if SuperPerms was a more flexible system.
 * 
 * What's wrong with a PermissionProvider interface where we can
 * register a single PermissionProvider?!
 */
public class SuperPermissionHandler implements Listener {

	private WorldManager wm = WorldManager.getInstance();
	//private Map<Integer, PermissionAttachment> attachments = new HashMap<Integer, PermissionAttachment>();
	private Permissions plugin;

	private static Field permissions;
	private static Field base;
	private static Field basePermissions;
	private static Field attachments;
	
	static {
		try {
			permissions = PermissionAttachment.class.getDeclaredField("permissions");
			permissions.setAccessible(true);
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
	 * This is put in place until such a time as Bukkit pull 466 is implemented
	 * https://github.com/Bukkit/Bukkit/pull/466
	 */
	public static void setPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) {
		try {
			doSetPermissions(p, plugin, perm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void doSetPermissions(Permissible p, Plugin plugin, Map<String, Boolean> perm) throws Exception {
		// Grab a reference to the original object
		//Map<String, Boolean> orig = (Map<String, Boolean>) permissions.get(att);
		// Clear the map (faster than removing the attachment and recalculating)
		//orig.clear();
		// Then whack our map into there
		//orig.putAll(perm);
		// * NEW CODE *
		PermissibleBase pb = (PermissibleBase) base.get(p);
		// I know, it's a lot more reflection
		Map<String, PermissionAttachmentInfo> info = (Map<String, PermissionAttachmentInfo>) basePermissions.get(pb);
		info.clear();
		// What can I do? Even more reflection! This also slows things down by about 2ms (but in the scale of things it works, yay!)
		List<PermissionAttachment> delete = new ArrayList<PermissionAttachment>();
		List<PermissionAttachment> attach = (List<PermissionAttachment>) attachments.get(pb);
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
		
		PermissionAttachment att = pb.addAttachment(plugin);
		permissions.set(att, perm);
		pb.recalculatePermissions();
	}

	// Main constructor

	protected SuperPermissionHandler(Permissions plugin) {
		this.plugin = plugin;
		// This next bit is simply to make bPermissions.* work with superperms, since I now have my bulk adding, I will concede to this
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		children.put("bPermissions.admin", true);
		Permission permission = new Permission("bPermissions.*", PermissionDefault.OP, children);
		plugin.getServer().getPluginManager().addPermission(permission);
	}

	/**
	 * A guaranteed way to setup all players in the server in one fell swoop
	 */
	public void setupAllPlayers() {
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			setupPlayer(player, wm.getWorld(player.getWorld().getName()));
		}
	}

	/**
	 * Set up the Player via the specified World object
	 * (note this is a bPermissions world, not a Bukkit world)
	 * @param player
	 * @param world
	 */
	public void setupPlayer(Player player, World world) {
		if(!plugin.isEnabled())
			return;

		long time = System.currentTimeMillis();
		// Some null checks, I guess?
		if(world == null) {
			System.err.println("Unable to setup! null world!");
			return;
		}
		if(player == null) {
			System.err.println("Unable to setup! null player!");
			return;
		}
		if(world.getUser(player.getName()) == null) {
			System.err.println("Unable to setup! null user!");
			return;
		}
		
		
		// Grab the pre-calculated effectivePermissions from the User object
		// Then whack it onto the player
		// TODO wait for the bukkit team to get their finger out, we'll use our reflection here!
		Map<String, Boolean> perms = ApiLayer.getEffectivePermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
		setPermissions(player, plugin, perms);

		// Set the metadata?
		String prefix = ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "prefix");
		String suffix = ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "suffix");
		// WTF
		player.setMetadata("prefix", new FixedMetadataValue(Permissions.instance, prefix));
		player.setMetadata("suffix", new FixedMetadataValue(Permissions.instance, suffix));

		// WHAT IS THIS I DONT EVEN
		long finish = System.currentTimeMillis()-time;
		Debugger.log("Setup superperms for "+player.getName()+". took "+finish+"ms.");
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		// In theory this should be all we need to detect world, it isn't cancellable so... should be fine?
		setupPlayer(event.getPlayer(), wm.getWorld(event.getPlayer().getWorld().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		// Just to be doubly sure, I guess
		if(event.getFrom().getWorld() != event.getTo().getWorld()) {
			// schedule a check of the players permissions 1 tick after the teleport
			final Player player = event.getPlayer();
			final org.bukkit.World start = event.getFrom().getWorld();
			// setup the player
			Runnable r = new Runnable() {
				public void run() {
					if(start != player.getWorld()) {
						setupPlayer(player, wm.getWorld(player.getWorld().getName()));
					}
				}
			};
			// must be sync
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r, 1);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		setupPlayer(event.getPlayer(), wm.getWorld(event.getPlayer().getWorld().getName()));		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		setupPlayer(event.getPlayer(), wm.getWorld(event.getPlayer().getWorld().getName()));		
	}

	public void setupPlayer(String name) {
		if(Bukkit.getPlayer(name) != null) {
			Player player = Bukkit.getPlayer(name);
			this.setupPlayer(player, wm.getWorld(player.getWorld().getName()));
		}
	}
}