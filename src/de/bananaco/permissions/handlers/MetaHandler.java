package de.bananaco.permissions.handlers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class MetaHandler {

    private Map<String, MetaData> worlds = null;
    private MetaData global = null;

    private JavaPlugin plugin;

    public MetaHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getMeta(String world, String player, String key) {
        if(global != null) {
            return global.calculateMeta(player, key);
        } else if(worlds.get(world) != null) {
            return worlds.get(world).calculateMeta(player, key);
        } else {
            return null;
        }
    }

    public void setGlobalMeta(Database data) {
        this.global = new FileMetaData(new File(plugin.getDataFolder(), "global_meta.yml"), data);
    }

    public void setWorldMeta(Database data, String world) {
        MetaData meta = new FileMetaData(new File(plugin.getDataFolder(), world+"_meta.yml"), data);
        worlds.put(world, meta);
    }

}
