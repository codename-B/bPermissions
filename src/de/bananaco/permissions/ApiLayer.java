package de.bananaco.permissions;

import de.bananaco.permissions.handlers.Carrier;
import de.bananaco.permissions.handlers.Database;
import org.bukkit.entity.Player;

import java.util.List;

public class ApiLayer {

    public static void

    public static boolean existsPackage(String name) {
        return Packages.instance.handler.packageManager.getPackage(name) != null;
    }

    public static void addToPackage(String name, String pack) {
        Packages.instance.handler.packageManager.addPackage(name, pack);
    }

    public static boolean existsPlayer(String player, String world) {
        // sanity checking
        world = (world == null || Packages.instance.global) ? "global" : world;
        List<Carrier> carriers = Packages.instance.handler.carriers;
        for(Carrier c : carriers) {
            if(c.getName().equalsIgnoreCase(world)) {
                Database db = c.getDatabase();
                return db.hasEntry(player);
            }
        }
        return false;
    }

    public static void addPlayer(String player, String world, String value) {
        // sanity checking
        world = (world == null || Packages.instance.global) ? "global" : world;
        List<Carrier> carriers = Packages.instance.handler.carriers;
        for(Carrier c : carriers) {
            if(c.getName().equalsIgnoreCase(world)) {
                Database db = c.getDatabase();
                db.addEntry(player, value);
            }
        }
    }

}
