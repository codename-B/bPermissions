package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;

import java.util.List;

public interface Database extends PackageManager {

    public boolean isASync();

    public List<PPackage> getPackages(String player) throws Exception;

    public boolean hasEntry(String player);

    public void addEntry(String player, String entry);

}
