package de.bananaco.permissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import de.bananaco.permissions.Permissions;

public class WorldCommands {
	private final Permissions plugin;

	public WorldCommands(Permissions plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String world = args[args.length - 1];
		return plugin.permissionsExec.exec(sender, args, world);
	}

}
