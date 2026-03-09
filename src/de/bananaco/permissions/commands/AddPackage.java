package de.bananaco.permissions.commands;

import de.bananaco.permissions.ApiLayer;
import de.bananaco.permissions.Packages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddPackage implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player onlinePlayer = findOnlinePlayer(args[0]);
            String player = onlinePlayer != null ? onlinePlayer.getName() : args[0];
            String p = args[1];
            String world = "global";
            ApiLayer.addPlayer(player, world, p);
            refreshPlayer(onlinePlayer);
            sender.sendMessage("Package '" + p + "' added to Player '" + player + "'");
            return true;
        } else if (args.length == 3) {
            Player onlinePlayer = findOnlinePlayer(args[0]);
            String player = onlinePlayer != null ? onlinePlayer.getName() : args[0];
            String p = args[1];
            String world = args[2];
            ApiLayer.addPlayer(player, world, p);
            refreshPlayer(onlinePlayer);
            sender.sendMessage("Package '" + p + "' added to Player '" + player + "' in World '" + world + "'");
            return true;
        }
        return false;
    }

    private Player findOnlinePlayer(String input) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(input)) {
                return onlinePlayer;
            }
        }
        return null;
    }

    private void refreshPlayer(Player player) {
        if (player != null) {
            Packages.instance.handler.loadPlayer(player);
        }
    }
}
