package de.bananaco.permissions.commands;

import de.bananaco.permissions.ApiLayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddPackage implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 2) {
            String player = args[0], p = args[1], world = "global";
            // case SeNsItIvE
            player = Bukkit.getOfflinePlayer(player).getName();
            ApiLayer.addPlayer(player, world, p);
            sender.sendMessage("Package '"+p+"' added to Player '"+player+"'");
        } else if(args.length == 3) {
            String player = args[0], p = args[1], world = args[2];
            // case SeNsItIvE
            player = Bukkit.getOfflinePlayer(player).getName();
            ApiLayer.addPlayer(player, world, p);
            sender.sendMessage("Package '"+p+"' added to Player '"+player+"' in World '"+world+"'");
        }
        return false;
    }
}
