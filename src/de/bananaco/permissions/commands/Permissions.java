package de.bananaco.permissions.commands;

import de.bananaco.permissions.ApiLayer;
import de.bananaco.permissions.Packages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Permissions implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("populate")) {
            ArrayList<String> regPerms = getPermissions();
            // Do the groups first
            String admin = "admin";
            String mod = "moderator";
            String def = Packages.getDefaultPackage();
            // A package is pretty much a group
            // Let's sort the permissions into shizzledizzle
            for(World world : Bukkit.getWorlds()) {
                for(String perm : regPerms) {
                    if(perm.contains("user") || perm.contains("build")) {
                        ApiLayer.addToPackage("user", perm);
                    } else if(perm.contains(".ban") || perm.contains(".kick") || perm.contains(".mod") || perm.contains(".fly")) {
                        ApiLayer.addToPackage("moderator", perm);
                    } else {
                        ApiLayer.addToPackage("admin", perm);
                    }
                }
                String user1 = "codename_B";
                String user2 = "Notch";
                String user3 = "pyraetos";
                // now hook the ApiLayer
                ApiLayer.addPlayer(user1, world.getName(), admin);
                ApiLayer.addPlayer(user1, world.getName(), mod);
                ApiLayer.addPlayer(user2, world.getName(), mod);
                ApiLayer.addPlayer(user3, world.getName(), def);
            }
            sender.sendMessage("Created some simplistic example files");
            return true;
        }
        return false;
    }

    private ArrayList<String> getPermissions() {
        ArrayList<String> regPerms = new ArrayList<String>();
        for (Permission p : Bukkit.getServer().getPluginManager().getPermissions()) {
            if ((p.getDefault() == PermissionDefault.OP || p.getDefault() == PermissionDefault.TRUE) && !p.getName().equals("*") && !p.getName().equals("*.*"))
                regPerms.add(p.getName());
        }
        Collections.sort(regPerms, new Comparator<String>() {

            public int compare(String a, String b) {
                return a.compareTo(b);
            }

            ;
        });

        return regPerms;
    }
}
