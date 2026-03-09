package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilePackageManager implements PackageManager {

    private final File file;
    private final YamlConfiguration yamlConfiguration;
    private final Map<String, PPackage> cache = new HashMap<String, PPackage>();

    public FilePackageManager(File file) {
        this.file = file;
        yamlConfiguration = new YamlConfiguration();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String normalizePackage(String p) {
        return p.toLowerCase(Locale.ROOT);
    }

    public PPackage getPackage(String p) {
        String key = normalizePackage(p);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        List<String> permissions = yamlConfiguration.getStringList(key);
        if (permissions != null && permissions.size() > 0) {
            cache.put(key, PPackage.loadPackage(key, permissions));
            return cache.get(key);
        }
        return null;
    }

    public void addPackage(String p, String v) {
        String key = normalizePackage(p);
        List<String> permissions = new ArrayList<String>(yamlConfiguration.getStringList(key));
        if (permissions.contains(v)) {
            return;
        }
        permissions.add(v);
        yamlConfiguration.set(key, permissions);
        try {
            yamlConfiguration.save(file);
            cache.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
