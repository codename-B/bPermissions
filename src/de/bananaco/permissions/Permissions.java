package de.bananaco.permissions;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.fornoobs.BackupPermissionsCommand;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public abstract class Permissions extends JavaPlugin {
	
	private static WorldPermissionsManager wpm = new WorldPermissionsManager();
	private static InfoReader info = new InfoReader();
	
	private JavaPlugin plugin;
	private ForNoobs noob;
	private BackupPermissionsCommand backup;
	private ImportManager im;
	public String addGroup;
	public String addGroupToGroup;
	public String setGroup;
	public String removeGroup;
	public String removeGroupFromGroup;
	public String listGroup;
	public String listGroupGroup;
	public String inGroup;
	public String addNode;
	public String addPlayerNode;
	public String removeNode;
	public String removePlayerNode;
	public String listNode;
	public String listPlayerNode;
	
	private String globalCommand;
	private String localCommand;
	private String worldCommand;
	
	public WorldPermissionsManager pm;
	public PermissionsExec permissionsExec = new PermissionsExec(this);
	
	private GlobalCommands globalExec = new GlobalCommands(this);
	private WorldCommands worldExec = new WorldCommands(this);
	private LocalCommands localExec = new LocalCommands(this);
	
	public void enable(JavaPlugin plugin) {
		backup = new BackupPermissionsCommand(plugin);
		noob = new ForNoobs(plugin);
		im = new ImportManager(plugin);
		this.plugin = plugin;
		this.pm = Permissions.wpm;
		try {
			setupConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setupConfig() throws Exception {
		YamlConfiguration c = new YamlConfiguration();
		
		File cf = new File("plugins/bPermissions/config_old.yml");
		if(!cf.exists())
			cf.createNewFile();
		
		c.load(cf);

		globalCommand = c.getString("commands.global-command", "global");
		localCommand = c.getString("commands.local-command", "local");
		worldCommand = c.getString("commands.world-command", "world");

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

		c.set("commands.global-command", globalCommand);
		c.set("commands.local-command", localCommand);
		c.set("commands.world-command", worldCommand);

		c.set("commands.set-group", setGroup);
		c.set("commands.add-group", addGroup);
		c.set("commands.add-group-to-group", addGroupToGroup);
		c.set("commands.remove-group", removeGroup);
		c.set("commands.remove-group-from-group", removeGroupFromGroup);
		c.set("commands.list-group", listGroup);
		c.set("commands.list-group-group", listGroupGroup);
		c.set("commands.in-group", inGroup);

		c.set("commands.add-node", addNode);
		c.set("commands.remove-node", removeNode);
		c.set("commands.list-node", listNode);

		c.set("commands.add-player-node", addPlayerNode);
		c.set("commands.remove-player-node", removePlayerNode);

		c.save(cf);
		
	}
	
	public static WorldPermissionsManager getWorldPermissionsManager() {
		return wpm;
	}
	
	public static InfoReader getInfoReader() {
		return info;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public ForNoobs getForNoobs() {
		return noob;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("helpme")) {
				noob.addAll();
				sender.sendMessage("Attempted to setup default groups - please view your default users.yml and groups.yml files");
				return true;
			}
			if (args[0].equalsIgnoreCase("backup")) {
				backup.backup();
				sender.sendMessage("Permissions files backed up, share this zip with codename_B if you have issues.");
				return true;
			}
		}
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("import")) {
				if(args[1].equalsIgnoreCase("yml")) {
					try {
						im.importYML();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(args[1].equalsIgnoreCase("p3")) {
					try {
						im.importPermissions3();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage("Import attempted, view console for results");
				sender.sendMessage("Note: if the files do not exist, you will see a FileNotFoundException");
				return true;
			}
		}
		
		if(args.length>=3)
		if (args[0].equalsIgnoreCase(this.globalCommand))
			return this.globalExec.onCommand(sender, command, label, args);
		else if (args[0].equalsIgnoreCase(this.worldCommand))
			return this.worldExec.onCommand(sender, command, label, args);
		else if (args[0].equalsIgnoreCase(this.localCommand)
				&& sender instanceof Player)
			return this.localExec.onCommand((Player) sender, command,
					label, args);
		
		return false;
	}

}
