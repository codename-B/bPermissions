package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilePackageManager implements PackageManager {

    private final File file;
    private final YamlConfiguration yamlConfiguration;
    private final Map<String, PPackage> cache = new HashMap<String, PPackage>();

    public FilePackageManager(File file) {
        this.file = file;
        yamlConfiguration = new YamlConfiguration();
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PPackage getPackage(String p) {
        if (cache.containsKey(p)) {
            return cache.get(p);
        }
        List<String> permissions = null;
        if ((permissions = yamlConfiguration.getStringList(p.toLowerCase())) != null && permissions.size() > 0) {
            cache.put(p, PPackage.loadPackage(p.toLowerCase(), permissions));
            return cache.get(p);
        } else {
            return null;
        }
    }

    public void addPackage(String p, String v) {
        List<String> permissions = yamlConfiguration.getStringList(p.toLowerCase());
        if(permissions == null) {
            permissions = new ArrayList<String>();
        }
        permissions.add(v);
        yamlConfiguration.set(p.toLowerCase(), permissions);
        try {
            yamlConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
