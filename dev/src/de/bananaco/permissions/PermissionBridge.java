package de.bananaco.permissions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;

public class PermissionBridge extends PermissionHandler {

    private Server server = Bukkit.getServer();

    private boolean internalHasPermission(Player player, String permission) {
        int index = permission.indexOf('.');
        while (index >= 0) {
            String subnodeName = permission.substring(0, index);
            if (player.hasPermission(subnodeName + ".*")) {
                return true;
            }
            index = permission.indexOf('.', index + 1);
        }
        
        return player.hasPermission("*") || player.hasPermission(permission);
    }

    @Override
    public boolean has(Player player, String permission) {
        return internalHasPermission(player, permission);
    }

    @Override
    public boolean has(String worldName, String playerName, String permission) {
        if (server.getPlayer(playerName) == null) return false;
        return internalHasPermission(server.getPlayer(playerName), permission);
    }

    @Override
    public boolean permission(Player player, String permission) {
        return internalHasPermission(player, permission);
    }
    
    public boolean permission(String worldName, Player player, String permission){
        return internalHasPermission(player, permission);
    }

    @Override
    public boolean permission(String worldName, String playerName, String permission) {
        if (server.getPlayer(playerName) == null) return false;
        return internalHasPermission(server.getPlayer(playerName), permission);
    }

    @Override
    public String getGroup(String world, String userName) {
        return null;
    }

    @Override
    public String[] getGroups(String world, String userName) {
        return new String[] {};
    }

    @Override
    public boolean inGroup(String world, String userName, String groupName) {
        return false;
    }

    @Override
    public boolean inGroup(String name, String group) {
        return false;
    }

    @Override
    public boolean inSingleGroup(String world, String userName, String groupName) {
        return false;
    }

    @Override
    public String getGroupPrefix(String world, String groupName) {
        return "";
    }

    @Override
    public String getGroupSuffix(String world, String groupName) {
        return "";
    }

    @Override
    public boolean canGroupBuild(String world, String groupName) {
        return true;
    }

    @Override
    public String getGroupPermissionString(String world, String groupName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public int getGroupPermissionInteger(String world, String groupName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean getGroupPermissionBoolean(String world, String groupName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public double getGroupPermissionDouble(String world, String groupName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public String getUserPermissionString(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public int getUserPermissionInteger(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean getUserPermissionBoolean(String world, String userName, String permission) {
        return this.has(world, userName, permission);
    }

    @Override
    public double getUserPermissionDouble(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public String getPermissionString(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public int getPermissionInteger(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean getPermissionBoolean(String world, String userName, String permission) {
        return this.has(world, userName, permission);
    }
    
    @Override
    public double getPermissionDouble(String world, String userName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void addUserPermission(String world, String user, String node) {
    }

    @Override
    public void removeUserPermission(String world, String user, String node) {
    }

    /*
     * Here came unneccesary for implementation stuff
     */
    @Override
    public void addGroupInfo(String world, String group, String node, Object data) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void removeGroupInfo(String world, String group, String node) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void setDefaultWorld(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean loadWorld(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void forceLoadWorld(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean checkWorld(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void load() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void load(String world, Configuration config) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean reload(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    // Cache
    @Override
    public void setCache(String world, Map<String, Boolean> Cache) {
        server.getLogger().warning("[bPermissions] setCache item are internal Permissions plugin stuff. Nag plugin author.");
    }

    @Override
    public void setCacheItem(String world, String player, String permission, boolean data) {
        server.getLogger().warning("[bPermissions] setCacheItem item are internal Permissions plugin stuff. Nag plugin author.");
    }

    @Override
    public Map<String, Boolean> getCache(String world) {
        server.getLogger().warning("[bPermissions] setCacheItem item are internal Permissions plugin stuff. Nag plugin author.");
        return new HashMap<String, Boolean>();
    }

    @Override
    public boolean getCacheItem(String world, String player, String permission) {
        server.getLogger().warning("[bPermissions] getCacheItem item are internal Permissions plugin stuff. Nag plugin author.");
        return false;
    }

    @Override
    public void removeCachedItem(String world, String player, String permission) {
        server.getLogger().warning("[bPermissions] removeCachedItem item are internal Permissions plugin stuff. Nag plugin author.");
    }

    @Override
    public void clearCache(String world) {
        server.getLogger().warning("[bPermissions] clearCache item are internal Permissions plugin stuff. Nag plugin author.");
    }

    @Override
    public void clearAllCache() {
        server.getLogger().warning("[bPermissions] clearAllCache item are internal Permissions plugin stuff. Nag plugin author.");
    }

    @Override
    public void save(String world) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void saveAll() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    /**
     * Loads the fake Permissions plugin for plugins which use getPlugin("Permissions")     
     */
    public static void loadPseudoPlugin(JavaPlugin parent, ClassLoader classLoader) {
        // create a pseudo-Permissions plugin as compatibility layer
        JavaPluginLoader pluginLoader = (JavaPluginLoader)parent.getPluginLoader();
        PluginClassLoader pluginClassLoader = new PluginClassLoader(pluginLoader, new java.net.URL[]{}, classLoader.getParent());
        String version = com.nijikokun.bukkit.Permissions.Permissions.version;
        PluginDescriptionFile description = new PluginDescriptionFile("Permissions", version, "com.nijikokun.bukkit.Permissions.Permissions");
        
        com.nijikokun.bukkit.Permissions.Permissions permissions = new com.nijikokun.bukkit.Permissions.Permissions();
        permissions.doInitialize(pluginLoader, Bukkit.getServer(), description, null, null, pluginClassLoader);
        try {
            permissions.onLoad();
        } catch (RuntimeException e) {
            Logger.getLogger("Minecraft").warning("["+parent.getDescription().getName()+"] Exception attempting to initialize compatibility layer");
        }
        
        // use reflection to add the pseudo-Permissions plugin to the PluginManager
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        List<Plugin> plugins = getField(pluginManager, "plugins");
        Map<String, Plugin> lookupNames = getField(pluginManager, "lookupNames");
        plugins.add(permissions);
        lookupNames.put(description.getName(), permissions);
    }   
    
    @SuppressWarnings("unchecked")
    private static <T> T getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
