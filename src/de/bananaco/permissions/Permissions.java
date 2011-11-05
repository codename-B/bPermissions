package de.bananaco.permissions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.sizeof.SizeOf;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.ubempire.binfo.PlayerInfo;

import de.bananaco.help.Help;
import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.debug.MCMA;
import de.bananaco.permissions.fornoobs.BackupPermissionsCommand;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.fornoobs.PermissionsCommandSuggestions;
import de.bananaco.permissions.fornoobs.Tutorial;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.iplock.IpLock;
import de.bananaco.permissions.override.MonkeyListener;
import de.bananaco.permissions.override.SpoutMonkey;
import de.bananaco.permissions.tracks.Tracks;
import de.bananaco.permissions.worlds.HasPermission;
import de.bananaco.permissions.worlds.PermissionClass;
import de.bananaco.permissions.worlds.WorldPermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

import de.bananaco.permissions.oldschool.Configuration;

public class Permissions extends JavaPlugin {

	private static Set<String> commands = new HashSet<String>();

	public static final Field entity_bukkitEntity;
	private static InfoReader info;
	private static Set<String> listCommands = new HashSet<String>();

	private static WorldPermissionsManager perm;
	private static String sworldCommand;
	/**
	 * Whether to use MonkeyPlayer class to proxy CraftPlayer. Will only be true
	 * if CraftPlayer was found.
	 */
	public static final boolean useMonkeyPlayer;
	private static Set<String> worldCommands = new HashSet<String>();
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

	public static Set<String> getCommands() {
		return commands;
	}

	public static InfoReader getInfoReader() {
		return info;
	}
	public static Set<String> getListCommands() {
		return listCommands;
	}
	public static String getWorldCommand() {
		return sworldCommand;
	}
	public static Set<String> getWorldCommands() {
		return worldCommands;
	}
	public static WorldPermissionsManager getWorldPermissionsManager() {
		return Permissions.perm;
	}

	public String addGroup;
	public String addNode;
	public Configuration c;
	public boolean cacheValues;
	public String database = "bPermissions";
	public String demotePlayer;
	public boolean formatChat;
	public String globalCommand;
	public GlobalCommands globalExec;
	public String hostname = "localhost";
	public ImportManager im;
	public IpLock iplock;
	public String listGroup;
	public String inGroup;
	public String listNode;
	public String localCommand;
	public LocalCommands localExec;
	public String lock;

	public Map<String, String> mirror;
	public final MonkeyListener monkeylistener = new MonkeyListener(this);
	public boolean overridePlayer;

	public String password = "minecraft";

	public PermissionsExec permissionsExec;

	public WorldPermissionsManager pm;

	public String port = "3306";

	public String promotePlayer;

	public String removeGroup;

	public String removeNode;
	public String setGroup;
	public boolean suggestSimilarCommands;
	public Tracks tracks;
	public Tutorial tutorial;

	public String unlock;

	public boolean useIpLock;
	
	public static boolean idiotVariable = false;

	public String username = "minecraft";

	public String worldCommand;

	public WorldCommands worldExec;

	public WorldPermissionSet wps;
	
	private BackupPermissionsCommand bpc;

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
		if (args.length == 0 && sender.hasPermission("bPermissions.admin")) {
			sender.sendMessage("Type: \"/" + label
					+ " tutorial\" in-game for help");
			return true;
		}
		if (args.length == 1 && sender instanceof Player) {
			Player player = (Player) sender;
			if (args[0].equalsIgnoreCase("tutorial"))
				if (player.hasPermission("bPermissions.admin")) {
					player.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE
							+ "bPermissions tutorial" + ChatColor.GREEN + "--");
					player.sendMessage(ChatColor.BLUE
							+ "bPermissions tutorial started.");
					player.sendMessage(ChatColor.BLUE
							+ "A basic command to start with");
					player.sendMessage(ChatColor.BLUE
							+ "To list the permission nodes of a group, try it!");
					player.sendMessage(ChatColor.WHITE
							+ "/permissions global lsnode "
							+ Permissions.getWorldPermissionsManager()
									.getPermissionSet(player.getWorld())
									.getDefaultGroup());
					tutorial.enable(player);
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "Nice try buckaroo!");
					return true;
				}
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("lock")
				&& sender instanceof Player && useIpLock) {
			Player player = (Player) sender;
			if (!player.hasPermission("bPermissions.iplock.lock")
					|| iplock.kickPlayers.contains(player.getName())) {
				player.sendMessage("Nope.");
				return true;
			}
			if (iplock.hasEntry(player)) {
				iplock.createEntry(player, args[1]);
				sender.sendMessage("Your entry has been reset");
				return true;
			} else {
				iplock.createEntry(player, args[1]);
				sender.sendMessage("A new entry was created with your password");
				return true;
			}
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("unlock")
				&& sender instanceof Player && useIpLock) {
			Player player = (Player) sender;
			if (!player.hasPermission("bPermissions.iplock.lock")) {
				player.sendMessage("Nope.");
				return true;
			}
			if (!iplock.kickPlayers.contains(player.getName())) {
				sender.sendMessage("You are already logged in!");
				return true;
			}
			if (iplock.hasEntry(player)) {
				boolean isPassword = iplock.isPassword(player, args[1]);
				if (isPassword) {
					sender.sendMessage("Welcome, Professor.");
					iplock.stopTimeout(player);
					iplock.addIp(player);
					return true;
				} else {
					sender.sendMessage("Incorrect password! Attempt logged!");
					log(player.getAddress().toString()
							+ " attempted to login to account "
							+ player.getName());
					return true;
				}
			} else {
				sender.sendMessage("You can't do this.");
				return true;
			}
		}
		if (args.length == 3 && args[0].equalsIgnoreCase(promotePlayer)) {
			String player = args[1];
			String track = args[2];
			String permission = "bPermissions.promote." + track;
			if (sender instanceof Player) {
				if (!(sender.hasPermission(permission))) {
					sender.sendMessage("Nopromotion.");
					return true;
				}
			}
			if (tracks.promote(player, track)) {
				sender.sendMessage(player + " promoted via " + track);
				return true;
			} else {
				sender.sendMessage("Please check tracks.yml");
				return true;
			}
		}
		if (args.length == 3 && args[0].equalsIgnoreCase(demotePlayer)) {
			String player = args[1];
			String track = args[2];
			String permission = "bPermissions.demote." + track;
			if (sender instanceof Player) {
				if (!sender.hasPermission(permission)) {
					sender.sendMessage("Nodemotion.");
					return true;
				}
			}
			if (tracks.demote(player, track)) {
				sender.sendMessage(player + " demoted via " + track);
				return true;
			} else {
				sender.sendMessage("Please check tracks.yml");
				return true;
			}
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("bPermissions.admin")
						|| sender.hasPermission("bPermissions.reload")
						|| !(sender instanceof Player)) {
					for (PermissionSet ps : pm.getPermissionSets())
						ps.reload();
					info.clear();
					HasPermission.clearCache();
					sender.sendMessage("Permissions reloaded.");
					return true;
				} else {
					sender.sendMessage("Reload? Nope.");
					return true;
				}
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
		if(args.length > 1) {
			if(args[0].equalsIgnoreCase("haspermission")) {
				if(args.length == 2) {
					boolean perm = sender.hasPermission(args[1]);
					sender.sendMessage(ChatColor.AQUA+args[1]+ChatColor.GREEN+":"+ChatColor.AQUA+perm);
					return true;
				} else {
					Player player = getServer().getPlayer(args[1]);
					boolean perm = false;
					if(player == null) {
						perm = HasPermission.has(args[1], getServer().getWorlds().get(0).getName(), args[2]);
					} else {
						perm = sender.hasPermission(args[2]);
					}
						PermissionClass.isRangePermission(args[2]);
						sender.sendMessage(ChatColor.AQUA+args[2]+ChatColor.GREEN+":"+ChatColor.AQUA+perm);
						return true;	
					}
				}
		}
		
		if (args.length > 0) {
			if (args.length == 1) {
				if (args[0].equals("helpme")) {
					new ForNoobs(this).addAll();
					sender.sendMessage("Attempted to setup default groups - please view your worldname.yml files");
					return true;
				}
				if(args[0].equals("backup")) {
					bpc.backup();
					sender.sendMessage("Permissions files backed up, share this zip with codename_B if you have issues.");
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
					if (args[1].equalsIgnoreCase("pex")) {
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
		}
		if (suggestSimilarCommands) {
			return suggest(sender, command, args, label);
		} else {
			sender.sendMessage("Please check out the tutorial for help.");
		}

		return false;
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		log("Disabled");
	}

	@Override
	public void onEnable() {		
                com.arandomappdev.bukkitstats.CallHome.load(this);
		Help.load(this);
		SuperPermissionHandler.setPlugin(this);
		
		sanityCheck();
		
		bpc = new BackupPermissionsCommand(this);
		
		mirror = new HashMap<String, String>();

		im = new ImportManager(this);
		setupConfig();

		log("Using " + wps.toString() + " for Permissions");

		setupCommands();
		if (pm == null)
			pm = new WorldPermissionsManager(this);

		pm.engage();

		if (info == null)
			info = new InfoReader();

		info.instantiate();
		PermissionsPlayerListener pl = new PermissionsPlayerListener(this);

		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_COMMAND_PREPROCESS,
				new CommandPreprocess(this), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN,
				pl, Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN,
				pl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_TELEPORT, pl, Priority.Monitor, this);

		if (formatChat)
			getServer().getPluginManager().registerEvent(
					Event.Type.PLAYER_CHAT, pl, Priority.Normal, this);

		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT_ENTITY, pl, Priority.Normal, this);

		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN,
				monkeylistener, Priority.Lowest, this);

		if (this.overridePlayer
				&& getServer().getPluginManager().getPlugin("Spout") != null) {
			log("Spout detected, registering PlayerPermissionEvent");
			getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT,
					new SpoutMonkey(), Priority.Normal, this);
		}

		tutorial = new Tutorial(this);
		// The tutorial
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_COMMAND_PREPROCESS, tutorial,
				Priority.Normal, this);

		// Just some extra stuff
		iplock = new IpLock(this);
		tracks = new Tracks(this);

		registerPermissions();

		log("Enabled");
		// Do static things
		try {
		log("Using "+SizeOf.humanReadable(SizeOf.deepSizeOf(this))+" ram");
		} catch (Exception e) {
			log("SizeOf.jar not in startup path, skipping!");
			log("To enable bPermissions to track its memory usage, add SizeOf.jar to the startup script");
			log("See http://sizeof.sourceforge.net/ for details");
		}

	}

	@Override
	public void onLoad() {
		pm = new WorldPermissionsManager(this);

		perm = pm;

		info = new InfoReader();
		getServer().getServicesManager().register(PlayerInfo.class, info, this,
				ServicePriority.Normal);
		PermissionBridge.loadPseudoPlugin(this, getClassLoader());
	}

	public void registerPermissions() {

		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.admin", PermissionDefault.OP));
		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.build", PermissionDefault.OP));

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

	public void setupCommands() {
		this.globalExec = new GlobalCommands(this);
		this.localExec = new LocalCommands(this);
		this.worldExec = new WorldCommands(this);
		this.permissionsExec = new PermissionsExec(this);
	}

	public void setupConfig() {
		c = new Configuration(this);

		List<String> mirrors = c.getKeys("mirrors");
		if (mirrors != null)
			for (String world : mirrors)
				mirror.put(world, c.getString("mirrors." + world));

		overridePlayer = c.getBoolean("override-player", false);

		globalCommand = c.getString("commands.global-command", "global");
		localCommand = c.getString("commands.local-command", "local");
		worldCommand = c.getString("commands.world-command", "world");

		promotePlayer = c.getString("commands.promote-player", "promote");
		demotePlayer = c.getString("commands.demote-player", "demote");

		addGroup = c.getString("commands.add-group", "addgroup");
		setGroup = c.getString("commands.set-group", "setgroup");
		removeGroup = c.getString("commands.remove-group", "rmgroup");
		listGroup = c.getString("commands.list-group", "lsgroup");
		inGroup = c.getString("commands.in-group", "ingroup");

		addNode = c.getString("commands.add-node", "addnode");
		removeNode = c.getString("commands.remove-node", "rmnode");
		listNode = c.getString("commands.list-node", "lsnode");

		hostname = c.getString("sql.hostname", hostname);
		c.setProperty("sql.hostname", hostname);
		port = c.getString("sql.port", port);
		c.setProperty("sql.port", port);
		database = c.getString("sql.database", database);
		c.setProperty("sql.database", database);
		username = c.getString("sql.username", username);
		c.setProperty("sql.username", username);
		password = c.getString("sql.password", password);
		c.setProperty("sql.password", password);

		useIpLock = c.getBoolean("use-iplock", false);
		lock = c.getString("commands.lock", "lock");
		unlock = c.getString("commands.unlock", "unlock");

		formatChat = c.getBoolean("format-chat", false);

		cacheValues = c.getBoolean("cache-values", true);

		idiotVariable = c.getBoolean("lowercase-all", false);
		
		c.setProperty("lowercase-all", idiotVariable);
		
		boolean debug = c.getBoolean("enable-debug", true);
		Debugger.setDebugging(debug);
		c.setProperty("enable-debug", debug);
		
		boolean mcma = c.getBoolean("enable-mcma", false);
		MCMA.setDebugging(debug);
		c.setProperty("enable-mcma", mcma);

		suggestSimilarCommands = c.getBoolean("suggest-similar-commands", true);

		c.setProperty("cache-values", cacheValues);
		// c.setProperty("use-bml", bml);
		if (c.getBoolean("use-bml", false)) {
			wps = WorldPermissionSet.BML;
		} else {
			c.setProperty("permission-type",
					c.getString("permission-type", "yaml"));
			wps = WorldPermissionSet.getSet(c.getString("permission-type"));
		}
		c.removeProperty("use-bml");
		c.setProperty("override-player", overridePlayer);

		c.setProperty("suggest-similar-commands", suggestSimilarCommands);

		c.setProperty("commands.global-command", globalCommand);
		c.setProperty("commands.local-command", localCommand);
		c.setProperty("commands.world-command", worldCommand);

		c.setProperty("commands.promote-player", promotePlayer);
		c.setProperty("commands.demote-player", demotePlayer);

		c.setProperty("commands.set-group", setGroup);
		c.setProperty("commands.add-group", addGroup);
		c.setProperty("commands.remove-group", removeGroup);
		c.setProperty("commands.list-group", listGroup);
		c.setProperty("commands.in-group", inGroup);

		c.setProperty("commands.add-node", addNode);
		c.setProperty("commands.remove-node", removeNode);
		c.setProperty("commands.list-node", listNode);

		c.setProperty("commands.unlock", unlock);
		c.setProperty("commands.lock", lock);
		c.setProperty("use-iplock", useIpLock);

		c.setProperty("format-chat", formatChat);

		c.save();
		
		sworldCommand = this.worldCommand;

		worldCommands.add(this.globalCommand);
		worldCommands.add(this.localCommand);
		worldCommands.add(this.worldCommand);

		commands.add(this.setGroup);
		commands.add(this.addGroup);
		commands.add(this.removeGroup);

		commands.add(this.addNode);
		commands.add(this.removeNode);

		commands.add(this.listGroup);
		commands.add(this.listNode);
		commands.add(this.inGroup);

		listCommands.add(this.listGroup);
		listCommands.add(this.listNode);
		listCommands.add(this.inGroup);
	}

	public boolean suggest(CommandSender sender, Command command,
			String[] args, String label) {
		String message = PermissionsCommandSuggestions.suggestSimilarCommands(
				sender, args, label);
		sender.sendMessage(ChatColor.BLUE + "[bPermissions] " + ChatColor.AQUA
				+ "Did you mean:");
		sender.sendMessage(ChatColor.AQUA + message);
		return true;
	}
}