package de.bananaco.permissions.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.bananaco.permissions.Permissions;

public class GlobalCommands {
	private final Permissions plugin;

	public GlobalCommands(Permissions plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		for (World wd : plugin.getServer().getWorlds()) {
			String world = wd.getName();
			if(!plugin.permissionsExec.exec(sender, args, world))
				if(plugin.suggestSimilarCommands)
					return plugin.suggest(sender, command, args, label);
		}
		return true;
	}
}
