package de.bananaco.permissions.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalHandler implements Carrier {

    private Database database;

    public GlobalHandler(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Handler.setup(event.getPlayer(), database);
    }

    public String getName() {
        return "global";
    }

    public Database getDatabase() {
        return database;
    }

}
