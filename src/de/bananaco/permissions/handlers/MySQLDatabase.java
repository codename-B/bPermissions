package de.bananaco.permissions.handlers;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.mysql.MySQLHandler;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase implements Database {

    private final String tag;
    private final MySQLHandler handler;
    private final PackageManager packageManager;

    public MySQLDatabase(String tag, MySQLHandler handler, PackageManager packageManager) {
        this.tag = tag;
        this.handler = handler;
        this.packageManager = packageManager;
    }

    public boolean isASync() {
        return true;
    }

    public List<PPackage> getPackages(Player player) throws Exception {
        List<PPackage> packages = new ArrayList<PPackage>();
        if (hasEntry(player)) {
            for (String pack : handler.getEntries(player.getName(), tag)) {
                if (getPackage(pack) != null) {
                    packages.add(getPackage(pack));
                }
            }
        } else {
            // load default
            if (getPackage(Packages.getDefaultPackage()) != null) {
                packages.add(getPackage(Packages.getDefaultPackage()));
            }
        }
        return packages;
    }

    public boolean hasEntry(Player player) {
        return false;
    }

    public void createEntry(Player player) {

    }

    public void setEntry(Player player, List<PPackage> packages) {

    }

    public PPackage getPackage(String p) {
        return packageManager.getPackage(p);
    }


}
