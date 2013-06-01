package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class WorldHandler implements Listener  {

    private final Database database;
    private final String world;

    public WorldHandler(Database database, World world) {
        this.database = database;
        this.world = world.getName();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer().getWorld().getName().equals(world)) {
            Handler.setup(event.getPlayer(), database);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if(event.getPlayer().getWorld().getName().equals(world)) {
            Handler.setup(event.getPlayer(), database);
        }
    }

}

