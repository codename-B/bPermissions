package de.bananaco.permissions.handlers;

import de.bananaco.permissions.mysql.MySQLHandler;
import de.bananaco.permissions.ppackage.PPackage;

import java.util.HashMap;
import java.util.Map;

public class MySQLPackageManager implements PackageManager {

    private final MySQLHandler handler;
    private final Map<String, PPackage> cache = new HashMap<String, PPackage>();

    public MySQLPackageManager(MySQLHandler handler) {
        this.handler = handler;
    }

    public PPackage getPackage(String p) {
        if (cache.containsKey(p)) {
            return cache.get(p);
        }
        PPackage pack = handler.getPPackage(p);
        if (pack != null) {
            cache.put(p, pack);
            return cache.get(p);
        } else {
            return null;
        }
    }

    public void addPackage(String p, String perm) {
        handler.addEntry(p, perm);
    }

}
