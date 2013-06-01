package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilePackageManager implements PackageManager {

    private final YamlConfiguration yamlConfiguration;
    private final Map<String, PPackage> cache = new HashMap<String, PPackage>();

    public FilePackageManager(File file) {
        yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PPackage getPackage(String p) {
        if(cache.containsKey(p)) {
            return cache.get(p);
        }
        List<String> permissions = null;
        if((permissions = yamlConfiguration.getStringList(p.toLowerCase())) != null) {
            cache.put(p, PPackage.loadPackage(p.toLowerCase(), permissions));
            return cache.get(p);
        } else {
            return null;
        }
    }

}
