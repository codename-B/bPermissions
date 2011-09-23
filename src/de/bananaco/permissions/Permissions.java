package de.bananaco.permissions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.ubempire.binfo.PlayerInfo;

import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.override.MonkeyListener;
import de.bananaco.permissions.override.SpoutMonkey;
import de.bananaco.permissions.tracks.Tracks;
import de.bananaco.permissions.worlds.WorldPermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Permissions extends JavaPlugin {

	public final MonkeyListener monkeylistener = new MonkeyListener(this);

	/**
	 * Whether to use MonkeyPlayer class to proxy CraftPlayer. Will only be true
	 * if CraftPlayer was found.
	 */
	public static final boolean useMonkeyPlayer;
	public static final Field entity_bukkitEntity;
	static {
		boolean result = true;
		try {
			Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
		} catch (ClassNotFoundException e) {
			System.err
					.println("Cannot use MonkeyPlayer unless on CraftBukkit! Not attempting to use!");
			result = false;
		}
		Field field_bukkitEntity = null;
		try {
			@SuppressWarnings("rawtypes")
			Class class_Entity = Class.forName("net.minecraft.server.Entity");

			field_bukkitEntity = class_Entity.getDeclaredField("bukkitEntity");
			field_bukkitEntity.setAccessible(true);
		} catch (ClassNotFoundException e) {
			System.err
					.println("net.minecrat.server.Entity missing, cannot use MonkeyPlayer!");
			result = false;
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			System.err
					.println("net.minecrat.server.Entity missing field bukkitEntity, cannot use MonkeyPlayer!");
			result = false;
			e.printStackTrace();
		}
		useMonkeyPlayer = result;
		if (useMonkeyPlayer) {
			entity_bukkitEntity = field_bukkitEntity;
		} else {
			entity_bukkitEntity = null;
		}
	}

	public WorldPermissionsManager pm;
	private static WorldPermissionsManager perm;
	private static InfoReader info;
	public ImportManager im;

	public Tracks tracks;
	
	public String hostname = "localhost";
	public String port = "3306";
	public String database = "bPermissions";
	public String username = "minecraft";
	public String password = "minecraft";
	
	public Configuration c;
	public WorldCommands worldExec;
	public LocalCommands localExec;
	public GlobalCommands globalExec;
	public PermissionsExec permissionsExec;
	public String globalCommand;
	public String localCommand;
	public String worldCommand;
	public String addGroup;
	public String setGroup;
	public String removeGroup;
	public String listGroup;
	public String addNode;
	public String removeNode;
	public String listNode;
	public String promotePlayer;
	public String demotePlayer;
	
	public WorldPermissionSet wps;

	public Map<String, String> mirror;

	public boolean overridePlayer;

	@Override
	public void onLoad() {
		
		info = new InfoReader();
		getServer().getServicesManager().register(PlayerInfo.class, info, this,
				ServicePriority.Normal);
		PermissionBridge.loadPseudoPlugin(this, getClassLoader());
	}

	@Override
	public void onDisable() {
		log("Disabled");
	}

	@Override
	public void onEnable() {
		
		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.admin", PermissionDefault.OP));
		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.build", PermissionDefault.OP));
		
		sanityCheck();
		
		mirror = new HashMap<String, String>();

		im = new ImportManager(this);
		setupConfig();
		
		log("Using " + wps.toString() + " for Permissions");
		
		setupCommands();
		
		pm = new WorldPermissionsManager(this);
		perm = pm;
		
		if(info == null)
			info = new InfoReader();
		
		info.instantiate();
		PermissionsPlayerListener pl = new PermissionsPlayerListener(this);
		
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_COMMAND_PREPROCESS, new CommandPreprocess(this), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_LOGIN, pl, Priority.Low, this);
		
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_TELEPORT, pl, Priority.Monitor, this);
		
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT_ENTITY, pl, Priority.Normal, this);
		
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN,
				monkeylistener, Priority.Lowest, this);
		
		if(this.overridePlayer && getServer().getPluginManager().getPlugin("Spout") != null) {
			log("Spout detected, registering PlayerPermissionEvent");
			getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new SpoutMonkey(), Priority.Normal, this);
		}
		
		tracks = new Tracks(this);
		
		log("Enabled");
	}

	/**
	 * May as well include this - it'll let even the most lazy of server owners
	 * know it's time to get rid of bInfo
	 */
	public void sanityCheck() {
		if (getServer().getPluginManager().getPlugin("bInfo") != null) {
			getServer().getScheduler().scheduleAsyncRepeatingTask(this,
					new Runnable() {

						@Override
						public void run() {
							getServer()
									.broadcastMessage(
											ChatColor.RED
													+ "bInfo is installed! Please uninstall it for bPermissions to work correctly!");
							System.err
									.println("bInfo is installed! Please uninstall it for bPermissions to work correctly!");
						}

					}, 100, 100);
		}
	}

	/**
	 * Just the logger man
	 * 
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions "
				+ this.getDescription().getVersion() + "] "
				+ String.valueOf(input));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		boolean allowed = true;
		
		if(args.length == 3 && args[0].equalsIgnoreCase(promotePlayer)) {
			String player = args[1];
			String track = args[2];
			String permission = "bPermissions.promote."+track;
			if(sender instanceof Player) {
				if(!sender.hasPermission(permission)) {
					sender.sendMessage("Nopromotion.");
					return true;
				}
			}
			if(tracks.promote(player, track)) {
				sender.sendMessage(player + "promoted via "+track);
			return true;
			}
			else {
				sender.sendMessage("Please check tracks.yml");
			return true;
			}
		}
		if(args.length == 3 && args[0].equalsIgnoreCase(demotePlayer)) {
			String player = args[1];
			String track = args[2];
			String permission = "bPermissions.demote."+track;
			if(sender instanceof Player) {
				if(!sender.hasPermission(permission)) {
					sender.sendMessage("Nodemotion.");
					return true;
				}
			}
			if(tracks.demote(player, track)) {
				sender.sendMessage(player + "demoted via "+track);
			return true;
			} else {
				sender.sendMessage("Please check tracks.yml");
			return true;
			}
		}
		
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			allowed = (player.hasPermission("bPermissions.admin") || player
					.isOp());
		}
		if (!allowed) {
			sender.sendMessage("Are you sure you're doing that right?");
			return true;
		}
		if (args.length > 0) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					pm.addAllWorlds();
					sender.sendMessage("Permissions reloaded.");
					return true;
				}
				if(args[0].equals("helpme")) {
					new ForNoobs(this).addAll();
					sender.sendMessage("Attempted to setup default groups - please view your worldname.yml files");
					return true;
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("import")) {
					if (args[1].equalsIgnoreCase("p3")) {
						sender.sendMessage("Ok? Here goes!");
						im.importPermissions3();
						return true;
					}
					if (args[1].equalsIgnoreCase("gm")) {
						sender.sendMessage("Ok? Here goes!");
						im.importGroupManager();
						return true;
					}
					if (args[1].equalsIgnoreCase("yml")) {
						sender.sendMessage("Ok? Here goes!");
						im.importYML();
						return true;
					}
					if (args[1].equalsIgnoreCase("pb")) {
						sender.sendMessage("Ok? Here goes!");
						im.importPermissionsBukkit();
						return true;
					}
					if(args[1].equalsIgnoreCase("pex")) {
						sender.sendMessage("Why did you use PEX? You fool!");
						im.importPEX();
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

		List<String> mirrors = c.getKeys("mirrors");
		if (mirrors != null)
			for (String world : mirrors)
				mirror.put(world, c.getString("mirrors." + world));

		overridePlayer = c.getBoolean("override-player", false);

		globalCommand = c.getString("commands.global-command", "global");
		localCommand = c.getString("commands.local-command", "local");
		worldCommand = c.getString("commands.world-command", "world");

		promotePlayer = c.getString("promote-player","promote");
		demotePlayer = c.getString("demote-player","demote");
		
		addGroup = c.getString("commands.add-group", "addgroup");
		setGroup = c.getString("commands.set-group", "setgroup");
		removeGroup = c.getString("commands.remove-group", "rmgroup");
		listGroup = c.getString("commands.list-group", "lsgroup");

		addNode = c.getString("commands.add-node", "addnode");
		removeNode = c.getString("commands.remove-node", "rmnode");
		listNode = c.getString("commands.list-node", "lsnode");
		
		hostname = c.getString("sql.hostname", hostname);
		port = c.getString("sql.port", port);
		database = c.getString("sql.database", database);
		username = c.getString("sql.username", username);
		password = c.getString("sql.password", password);
		
		//c.setProperty("use-bml", bml);
		if(c.getBoolean("use-bml", false)) {
			wps = WorldPermissionSet.BML;
		} else {
			c.setProperty("permission-type", c.getString("permission-type", "yaml"));
			wps = WorldPermissionSet.getSet(c.getString("permission-type"));
		}
		c.removeProperty("use-bml");
		c.setProperty("override-player", overridePlayer);

		c.setProperty("commands.global-command", globalCommand);
		c.setProperty("commands.local-command", localCommand);
		c.setProperty("commands.world-command", worldCommand);

		c.setProperty("promote-player", promotePlayer);
		c.setProperty("demote-player", demotePlayer);
		
		c.setProperty("commands.set-group", setGroup);
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

	public static InfoReader getInfoReader() {
		return info;
	}
}