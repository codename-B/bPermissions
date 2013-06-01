package de.bananaco.permissions.handlers;

import org.bukkit.event.Listener;

public interface Carrier extends Listener {

    public String getName();

    public Database getDatabase();

}
