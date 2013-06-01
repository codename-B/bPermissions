package de.bananaco.permissions;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class PackageLoadEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final List<PPackage> packages;

    public PackageLoadEvent(Player player, List<PPackage> packages) {
        super(player);
        this.packages = packages;
    }

    public List<PPackage> getPackages() {
        return packages;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}