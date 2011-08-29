package de.bananaco.permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;


import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Permissions extends JavaPlugin {

	public WorldPermissionsManager pm;
	private static WorldPermissionsManager perm;
	public ImportManager im;
	
	public Configuration c;
	public WorldCommands worldExec;
	public LocalCommands localExec;
	public GlobalCommands globalExec;
	public PermissionsExec permissionsExec;
	public String globalCommand;
	public String localCommand;
	public String worldCommand;
	public String addGroup;
	public String removeGroup;
	public String listGroup;
	public String addNode;
	public String removeNode;
	public String listNode;
	
	public boolean bml;
	
	@Override
	public void onLoad() {
	    PermissionBridge.loadPseudoPlugin(this, getClassLoader());
	}
	
	@Override
	public void onDisable() {
		log("Disabled");
	}

	@Override
	public void onEnable() {
		im = new ImportManager(this);
		setupConfig();
		setupCommands();
		pm = new WorldPermissionsManager(this);
		perm = pm;
		PermissionsPlayerListener pl = new PermissionsPlayerListener(this);

		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, pl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, pl, Priority.Normal, this);
		
		log("Enabled");
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions "+this.getDescription().getVersion()+"] " + String.valueOf(input));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		boolean allowed = true;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			allowed = (player.hasPermission("bPermissions.admin") || player.isOp());
		}
		if (!allowed) {
			sender.sendMessage("Are you sure you're doing that right?");
			return true;
		}
		if (args.length > 0) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					pm.addAllWorlds();
					sender.sendMessage("Permissions reloaded.");
					return true;
				}
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("import")) {
					if(args[1].equalsIgnoreCase("p3")) {
						sender.sendMessage("Ok? Here goes!");
						im.importPermissions3();
						return true;
					}
					if(args[1].equalsIgnoreCase("gm")) {
						sender.sendMessage("Ok? Here goes!");
						im.importGroupManager();
						return true;
					}
					if(args[1].equalsIgnoreCase("yml")) {
						sender.sendMessage("Ok? Here goes!");
						im.importYML();
						return true;
					}
					if(args[1].equalsIgnoreCase("pb")) {
						sender.sendMessage("Ok? Here goes!");
						im.importPermissionsBukkit();
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase(this.globalCommand))
				return this.globalExec.onCommand(sender, command, label, args);
			else if (args[0].equalsIgnoreCase(this.worldCommand))
				return this.worldExec.onCommand(sender, command, label, args);
			else if (args[0].equalsIgnoreCase(this.localCommand)
					&& sender instanceof Player)
				return this.localExec.onCommand((Player) sender, command,
						label, args);
			else
				sender.sendMessage("Are you sure you're doing that right?");
		}
		return false;
	}

	public void setupCommands() {
		this.globalExec = new GlobalCommands(this);
		this.localExec = new LocalCommands(this);
		this.worldExec = new WorldCommands(this);
		this.permissionsExec = new PermissionsExec(this);
	}

	public void setupConfig() {
		c = this.getConfiguration();
		
		bml = c.getBoolean("use-bml", false);
		
		globalCommand = c.getString("commands.global-command", "global");
		localCommand = c.getString("commands.local-command", "local");
		worldCommand = c.getString("commands.world-command", "world");

		addGroup = c.getString("commands.add-group", "addgroup");
		removeGroup = c.getString("commands.remove-group", "rmgroup");
		listGroup = c.getString("commands.list-group", "lsgroup");

		addNode = c.getString("commands.add-node", "addnode");
		removeNode = c.getString("commands.remove-node", "rmnode");
		listNode = c.getString("commands.list-node", "lsnode");

		c.setProperty("use-bml", bml);
		
		c.setProperty("commands.global-command", globalCommand);
		c.setProperty("commands.local-command", localCommand);
		c.setProperty("commands.world-command", worldCommand);

		c.setProperty("commands.add-group", addGroup);
		c.setProperty("commands.remove-group", removeGroup);
		c.setProperty("commands.list-group", listGroup);

		c.setProperty("commands.add-node", addNode);
		c.setProperty("commands.remove-node", removeNode);
		c.setProperty("commands.list-node", listNode);

		c.save();
	}

	public static WorldPermissionsManager getWorldPermissionsManager() {
		return Permissions.perm;
	}
}
class PermissionsPlayerListener extends PlayerListener {
	private final Permissions permissions;
	public PermissionsPlayerListener(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public boolean can(Player player) {
		return (player.hasPermission("bPermissions.build") || player.hasPermission("bPermissions.admin") || player.isOp());
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(!can(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!can(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(!permissions.isEnabled())
			return;
		if(event.isCancelled())
			return;
		if(event.getPlayer() == null)
			return;
		if(event.getTo() == null)
			return;
		PermissionSet ps = permissions.pm.getPermissionSet(event.getTo().getWorld());
		new SuperPermissionHandler(event.getPlayer()).setupPlayer(ps.getPlayerNodes(event.getPlayer()), permissions);
	}
}