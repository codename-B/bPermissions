package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class FilePackageManager implements PackageManager {

    private final YamlConfiguration yamlConfiguration;

    public FilePackageManager(File file) {
        yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PPackage getPackage(String p) {
        List<String> permissions = null;
        if((permissions = yamlConfiguration.getStringList(p.toLowerCase())) != null) {
            return PPackage.loadPackage(p.toLowerCase(), permissions);
        } else {
            return null;
        }
    }

}
