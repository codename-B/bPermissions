package de.bananaco.permissions.handlers;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WorldHandler implements Carrier {

    private final Database database;
    private final String world;

    public WorldHandler(Database database, World world) {
        this.database = database;
        this.world = world.getName();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().equals(world)) {
            Handler.setup(event.getPlayer(), database);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getName().equals(world)) {
            Handler.setup(event.getPlayer(), database);
        }
    }

    public String getName() {
        return world;
    }

    public Database getDatabase() {
        return database;
    }

}

