package de.bananaco.permissions.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalHandler implements Listener {

    private Database database;

    public GlobalHandler(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Handler.setup(event.getPlayer(), database);
    }


}
