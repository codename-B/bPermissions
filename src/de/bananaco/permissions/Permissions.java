package de.bananaco.permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.permissions.fornoobs.BackupPermissionsCommand;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Permissions {
	
	private static WorldPermissionsManager wpm = new WorldPermissionsManager();
	private static InfoReader info = new InfoReader();
	
	private final JavaPlugin plugin;
	private ForNoobs noob;
	private BackupPermissionsCommand backup;
	
	public Permissions(JavaPlugin plugin) {
		backup = new BackupPermissionsCommand(plugin);
		noob = new ForNoobs(plugin);
		this.plugin = plugin;
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
			if (args[0].equals("helpme")) {
				noob.addAll();
				sender.sendMessage("Attempted to setup default groups - please view your default users.yml and groups.yml files");
				return true;
			}
			if (args[0].equals("backup")) {
				backup.backup();
				sender.sendMessage("Permissions files backed up, share this zip with codename_B if you have issues.");
				return true;
			}
		}
		return false;
	}

}
