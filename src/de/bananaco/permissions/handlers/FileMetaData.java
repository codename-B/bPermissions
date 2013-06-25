package de.bananaco.permissions.handlers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileMetaData extends MetaWrapper {

    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public FileMetaData(File file, Database database) {
        super(database);
        this.file = file;
        yamlConfiguration = new YamlConfiguration();
        try {
            if(!file.getParentFile().exists()) {
                file.mkdirs();
            }
            if(!file.exists()) {
                file.createNewFile();
            }
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMeta(String pack, String key, String meta) {
        yamlConfiguration.set(pack+"."+key, meta);
        try {

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getMeta(String pack, String key) {
        return yamlConfiguration.getString(pack+"."+key);
    }

}
