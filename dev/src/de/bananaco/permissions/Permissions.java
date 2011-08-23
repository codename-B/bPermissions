package de.bananaco.permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import de.bananaco.permissions.commands.GlobalCommands;
import de.bananaco.permissions.commands.LocalCommands;
import de.bananaco.permissions.commands.WorldCommands;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Permissions extends JavaPlugin {
	
	public WorldPermissionsManager pm;
	private static WorldPermissionsManager perm;
	
	public Configuration c;
	public WorldCommands worldExec;
	public LocalCommands localExec;
	public GlobalCommands globalExec;
	public PermissionsExec permissionsExec;
	public String globalCommand;
	public String localCommand;
	public String worldCommand;
	public String addGroup = "addgroup";
	public String removeGroup = "rmgroup";
	public String listGroup = "lsgroup";
	public String addNode = "addnode";
	public String removeNode = "rmnode";
	public String listNode = "lsnode";
	
	@Override
	public void onDisable() {
	log("Disabled");
	}

	@Override
	public void onEnable() {
	setupConfig();
	setupCommands();
	pm = new WorldPermissionsManager(this);
	perm = pm;
	log("Enabled");
	}
	
	/**
	 * Just the logger man
	 * @param input
	 */
	public void log(Object input) {
		System.out.println("[bPermissions] "+String.valueOf(input));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(args.length>0) {
	if(args[0].equalsIgnoreCase(this.globalCommand))
		return this.globalExec.onCommand(sender, command, label, args);
	else if(args[0].equalsIgnoreCase(this.worldCommand))
		return this.worldExec.onCommand(sender, command, label, args);
	else if(args[0].equalsIgnoreCase(this.localCommand) && sender instanceof Player)
		return this.localExec.onCommand((Player) sender, command, label, args);
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
	globalCommand = c.getString("commands.global","global");
	localCommand = c.getString("commands.local","local");
	worldCommand = c.getString("commands.world","world");
	c.setProperty("commands.global", globalCommand);
	c.setProperty("commands.local", localCommand);
	c.setProperty("commands.world", worldCommand);
	}
	
	public static WorldPermissionsManager getWorldPermissionsManager() {
	return Permissions.perm;
	}
}
