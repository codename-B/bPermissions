package de.bananaco.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.help.Help;
import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.debug.MCMA;
import de.bananaco.permissions.fornoobs.BackupPermissionsCommand;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.fornoobs.PermissionsCommandSuggestions;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.PermissionClass;

import de.bananaco.permissions.worlds.WorldPermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

import de.bananaco.permissions.oldschool.Configuration;

public class Permissions extends JavaPlugin {

	private static Set<String> commands = new HashSet<String>();

	public static boolean idiotVariable = false;
	private static InfoReader info;

	private static Set<String> listCommands = new HashSet<String>();
	private static WorldPermissionsManager perm;

	private static String sworldCommand;

	private static Set<String> worldCommands = new HashSet<String>();

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
	public String addGroupToGroup;
	public String addNode;
	public String addPlayerNode;
	private BackupPermissionsCommand bpc;
	public Configuration c;
	public boolean cacheValues;
	public String database = "bPermissions";
	public String demotePlayer;
	public String globalCommand;
	public GlobalCommands globalExec;

	public String hostname = "localhost";
	public ImportManager im;
	public String inGroup;
	public String listGroup;
	public String listGroupGroup;
	public String listNode;
	public String listPlayerNode;

	public String localCommand;

	public LocalCommands localExec;

	public Map<String, String> mirror;

	public String password = "minecraft";

	public PermissionsExec permissionsExec;

	public WorldPermissionsManager pm;

	public String port = "3306";
	public String promotePlayer;

	public String removeGroup;
	public String removeGroupFromGroup;
	public String removeNode;

	public String removePlayerNode;

	public String setGroup;

	public boolean suggestSimilarCommands;

	public String username = "minecraft";

	public String worldCommand;

	public WorldCommands worldExec;

	public WorldPermissionSet wps;

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

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("bPermissions.admin")
						|| sender.hasPermission("bPermissions.reload")
						|| !(sender instanceof Player)) {
					for (PermissionSet ps : pm.getPermissionSets())
						ps.reload();

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
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("haspermission")) {
				if (args.length == 2) {
					boolean perm = sender.hasPermission(args[1]);
					sender.sendMessage(ChatColor.AQUA + args[1]
							+ ChatColor.GREEN + ":" + ChatColor.AQUA + perm);
					return true;
				} else {
					Player player = getServer().getPlayer(args[1]);
					boolean perm = false;
					if (player == null) {
						perm = false;
						// perm = HasPermission.has(args[1],
						// getServer().getWorlds().get(0).getName(), args[2]);
					} else {
						perm = sender.hasPermission(args[2]);
					}
					PermissionClass.isRangePermission(args[2]);
					sender.sendMessage(ChatColor.AQUA + args[2]
							+ ChatColor.GREEN + ":" + ChatColor.AQUA + perm);
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
				if (args[0].equals("backup")) {
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
		registerPermissions();

		Help.load(this);
		SuperPermissionHandler.setPlugin(this);

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

		// NEW THINGS
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_TELEPORT, pl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN,
				pl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_CHANGED_WORLD, pl, Priority.Monitor, this);

		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(
				Event.Type.PLAYER_INTERACT_ENTITY, pl, Priority.Normal, this);

		log("Enabled");
	}

	@Override
	public void onLoad() {
		pm = new WorldPermissionsManager(this);
		perm = pm;
		info = new InfoReader();
	}

	private void registerPermissions() {

		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.admin", PermissionDefault.OP));
		getServer().getPluginManager().addPermission(
				new Permission("bPermissions.build", PermissionDefault.OP));

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

		globalCommand = c.getString("commands.global-command", "global");
		localCommand = c.getString("commands.local-command", "local");
		worldCommand = c.getString("commands.world-command", "world");

		promotePlayer = c.getString("commands.promote-player", "promote");
		demotePlayer = c.getString("commands.demote-player", "demote");

		addGroup = c.getString("commands.add-group", "addgroup");
		addGroupToGroup = c.getString("commands.add-group-to-group",
				"addgroupgroup");
		setGroup = c.getString("commands.set-group", "setgroup");
		removeGroup = c.getString("commands.remove-group", "rmgroup");
		removeGroupFromGroup = c.getString("commands.remove-group-from-group",
				"rmgroupgroup");
		listGroup = c.getString("commands.list-group", "lsgroup");
		listGroupGroup = c.getString("commands.list-group-group",
				"lsgroupgroup");
		inGroup = c.getString("commands.in-group", "ingroup");

		addNode = c.getString("commands.add-node", "addnode");
		addPlayerNode = c.getString("commands.add-player-node", "addplnode");
		removeNode = c.getString("commands.remove-node", "rmnode");
		removePlayerNode = c.getString("commands.remove-player-node",
				"rmplnode");
		listNode = c.getString("commands.list-node", "lsnode");
		listPlayerNode = c.getString("commands.list-player-node", "lsplnode");

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

		c.setProperty("permission-type", c.getString("permission-type", "yaml"));
		wps = WorldPermissionSet.getSet(c.getString("permission-type"));

		c.removeProperty("use-bml");
		c.removeProperty("override-player");
		c.removeProperty("cache-values");

		c.setProperty("suggest-similar-commands", suggestSimilarCommands);

		c.setProperty("commands.global-command", globalCommand);
		c.setProperty("commands.local-command", localCommand);
		c.setProperty("commands.world-command", worldCommand);

		c.setProperty("commands.promote-player", promotePlayer);
		c.setProperty("commands.demote-player", demotePlayer);

		c.setProperty("commands.set-group", setGroup);
		c.setProperty("commands.add-group", addGroup);
		c.setProperty("commands.add-group-to-group", addGroupToGroup);
		c.setProperty("commands.remove-group", removeGroup);
		c.setProperty("commands.remove-group-from-group", removeGroupFromGroup);
		c.setProperty("commands.list-group", listGroup);
		c.setProperty("commands.list-group-group", listGroupGroup);
		c.setProperty("commands.in-group", inGroup);

		c.setProperty("commands.add-node", addNode);
		c.setProperty("commands.remove-node", removeNode);
		c.setProperty("commands.list-node", listNode);

		c.setProperty("commands.add-player-node", addPlayerNode);
		c.setProperty("commands.remove-player-node", removePlayerNode);
		c.setProperty("commands.list-player-node", listPlayerNode);

		c.removeProperty("format-chat");

		c.save();

		sworldCommand = this.worldCommand;

		worldCommands.add(this.globalCommand);
		worldCommands.add(this.localCommand);
		worldCommands.add(this.worldCommand);

		commands.add(this.setGroup);
		commands.add(this.addGroup);
		commands.add(this.removeGroup);
		commands.add(this.addGroupToGroup);
		commands.add(this.removeGroupFromGroup);

		commands.add(this.addNode);
		commands.add(this.removeNode);
		commands.add(this.addPlayerNode);
		commands.add(this.removePlayerNode);

		commands.add(this.listGroup);
		commands.add(this.listNode);
		commands.add(this.inGroup);

		listCommands.add(this.listGroup);
		listCommands.add(this.listNode);
		listCommands.add(this.listGroupGroup);
		listCommands.add(this.listPlayerNode);
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