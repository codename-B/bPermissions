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
	private ImportManager im;
	
	public Permissions(JavaPlugin plugin) {
		backup = new BackupPermissionsCommand(plugin);
		noob = new ForNoobs(plugin);
		im = new ImportManager(plugin);
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
				sender.sendMessage("Import attempted, view console for results");
				sender.sendMessage("Note: if the files do not exist, you will see a FileNotFoundException");
				return true;
				}
			}
		}
		return false;
	}

}
