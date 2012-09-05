package de.bananaco.bpermissions.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.User;
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

	// this is used to counter for those occasions where the setup errors
	static class SuperPermissionReloader implements Runnable {

		private final SuperPermissionHandler handler;
		
		public SuperPermissionReloader(SuperPermissionHandler handler) {
			this.handler = handler;
		}

		// if the player has no "bpermissions" PermissionAttachment, fix that
		public void run() {
			// now need to check if there is no-one online!
			if(Bukkit.getOnlinePlayers().length == 0)
				return;
			// let us run this as an async task for now
			List<Player> update = new ArrayList<Player>();
			for(Player player : Bukkit.getOnlinePlayers()) {
				PermissibleBase pb = BukkitCompat.getBase(player);
				List<PermissionAttachment> attach = BukkitCompat.getAttachments(pb);
				boolean up = true;
				for(PermissionAttachment att : attach) {
					if(att.getPlugin().getName().equalsIgnoreCase("bpermissions")) {
						up = false;
					}
				}
				// do we update?
				if(up) {
					update.add(player);
				}
			}
			// and update those players who have failed to meet the cut!
			if(update.size() > 0) {
				Debugger.log("Force-updating "+update.size()+" players!");
				for(Player player : update) {
					handler.setupPlayer(player.getName());
				}
				update.clear();
			}
		}

	}

	private WorldManager wm = WorldManager.getInstance();
	//private Map<Integer, PermissionAttachment> attachments = new HashMap<Integer, PermissionAttachment>();
	private Permissions plugin;

	/**
	 * This is put in place until such a time as Bukkit pull 466 is implemented
	 * https://github.com/Bukkit/Bukkit/pull/466
	 */
	public synchronized static void setPermissions(final Permissible p, final Plugin plugin, final Map<String, Boolean> perm) {
		BukkitCompat.setPermissions(p, plugin, perm);
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
			setupPlayer(player);
		}
	}

	/**
	 * Set up the Player via the specified World object
	 * (note this is a bPermissions world, not a Bukkit world)
	 * @param player
	 * @param world
	 */
	public void setupPlayer(Player player) {
		if(!plugin.isEnabled())
			return;

		long time = System.currentTimeMillis();
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
		setupPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.isCancelled())
			return;
		// Just to be doubly sure, I guess
		if(!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			// schedule a check of the players permissions 1 tick after the teleport
			final Player player = event.getPlayer();
			final org.bukkit.World start = event.getFrom().getWorld();
			// setup the player
			Runnable r = new Runnable() {
				public void run() {
					if(!start.equals(player.getWorld())) {
						setupPlayer(player);
					}
				}
			};
			// must be sync
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r, 5);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(PlayerPreLoginEvent event) {
		for(World world : wm.getAllWorlds()) {
			User user = world.getUser(event.getName());
			try {
				user.calculateEffectivePermissions();
				user.calculateEffectiveMeta();
			} catch (Exception e) {
				System.err.println(e.getStackTrace()[0].toString());
			}
			Debugger.log("PlayerPreLogin setup: "+user.getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		setupPlayer(event.getPlayer());		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerJoinEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		setupPlayer(event.getPlayer());		
	}

	public void setupPlayer(String name) {
		if(Bukkit.getPlayer(name) != null) {
			Player player = Bukkit.getPlayer(name);
			this.setupPlayer(player);
		}
	}
}