package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;

import java.util.List;

public interface Database extends PackageManager {

    public boolean isASync();

    public List<PPackage> getPackages(Player player) throws Exception;

    public boolean hasEntry(Player player);

    public void createEntry(Player player);

}
