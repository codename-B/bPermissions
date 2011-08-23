package de.bananaco.permissions.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;

public class LocalCommands {
	private final Permissions plugin;

	public LocalCommands(Permissions plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(Player sender, Command command, String label,
			String[] args) {
		String world = sender.getWorld().getName();
		return plugin.permissionsExec.exec(sender, args, world);
	}
}
