package de.bananaco.bpermissions.imp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
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

	private WorldChecker checker;

	private static Field permissions;

	static {
		try {
			permissions = PermissionAttachment.class.getDeclaredField("permissions");
			permissions.setAccessible(true);
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
	public static void setPermissions(PermissionAttachment att, Map<String, Boolean> perm) {
		try {
			doSetPermissions(att, perm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void doSetPermissions(PermissionAttachment att, Map<String, Boolean> perm) throws Exception {
		// Grab a reference to the original object
		Map<String, Boolean> orig = (Map<String, Boolean>) permissions.get(att);
		// Clear the map (faster than removing the attachment and recalculating)
		orig.clear();
		// Then whack our map into there
		orig.putAll(perm);
		// That's all folks!
		att.getPermissible().recalculatePermissions();
	}

	// Main constructor

	protected SuperPermissionHandler(Permissions plugin) {
		this.plugin = plugin;
		// This next bit is simply to make bPermissions.* work with superperms, since I now have my bulk adding, I will concede to this
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		children.put("bPermissions.admin", true);
		Permission permission = new Permission("bPermissions.*", PermissionDefault.OP, children);
		plugin.getServer().getPluginManager().addPermission(permission);
		checker = new WorldChecker(plugin.getServer(), this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, checker, 10, 10);
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
		// possible cleanup
		Set<PermissionAttachment> att2 = new HashSet<PermissionAttachment>();
		for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
			if(info != null && info.getAttachment() != null && info.getAttachment().getPlugin() == plugin) {
				att2.add(info.getAttachment());
			}
		}
		if(att2.size() > 0) {
			for(PermissionAttachment at : att2) {
				at.remove();
			}
		}
		
		// Grab the pre-calculated effectivePermissions from the User object
		// Then whack it onto the player
		// TODO wait for the bukkit team to get their finger out, we'll use our reflection here!
		PermissionAttachment att = player.addAttachment(plugin);
		Map<String, Boolean> perms = ApiLayer.getEffectivePermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
		perms.put("world."+player.getWorld().getName(), true);
		
		Debugger.log("world."+world.getName()+" set for "+player.getName());
		setPermissions(att, perms);

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