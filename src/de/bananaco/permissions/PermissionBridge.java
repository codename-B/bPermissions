package de.bananaco.permissions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;

import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.interfaces.PermissionSet;

public class PermissionBridge extends PermissionHandler {

    private final Server server = Bukkit.getServer();
    private Permissions permissions;
    private InfoReader infoReader = Permissions.getInfoReader();
    
    /**
     * internal permissions check method
     */
    private boolean internalHasPermission(Player player, String permission) {
        if (player.isPermissionSet(permission))
            return player.hasPermission(permission);
        
        int index = permission.lastIndexOf('.');
        while (index >= 0) {
            permission = permission.substring(0, index);
            String wildcard = permission + ".*";
            if (player.isPermissionSet(wildcard))
                return player.hasPermission(wildcard);
            index = permission.lastIndexOf('.');
        }
        return player.hasPermission("*");
    }

    @Override
    public boolean has(Player player, String permission) {
        return permission(player, permission);
    }

    @Override
    public boolean has(String worldName, String playerName, String permission) {
        return permission(worldName, playerName, permission);
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
        Player player = server.getPlayer(playerName);
        return player != null && internalHasPermission(player, permission);
    }
    
    /**
     * internal getGroups, used by the other methods
     */
    private List<String> internalGetGroups(String world, String userName) {
        PermissionSet ps = permissions.pm.getPermissionSet(world);
        return ps.getGroups(userName);
    }
    
    @Override
    public String getGroup(String world, String userName) {
        List<String> groups = internalGetGroups(world, userName);
        return groups.isEmpty() ? null : groups.get(groups.size()-1);
    }

    @Override
    public String[] getGroups(String world, String userName) {
        return internalGetGroups(world, userName).toArray(new String[0]);
    }

    @Override
    public boolean inGroup(String world, String userName, String groupName) {
        List<String> groups = internalGetGroups(world, userName);
        for (String group : groups)
            if (group.equalsIgnoreCase(groupName))
                return true;
        return false;
    }

    @Override
    public boolean inGroup(String name, String group) {
        World world;    // get the world the player is in, or the main world
        Player player = server.getPlayer(name);
        if (player != null)
            world = player.getWorld();
        else
            world = server.getWorlds().get(0);
        return inGroup(world.getName(), name, group);
    }

    @Override
    public boolean inSingleGroup(String world, String userName, String groupName) {
        List<String> groups = internalGetGroups(world, userName);
        return groups.size() == 1 && groups.get(0).equalsIgnoreCase(groupName);
    }

    @Override
    public String getGroupPrefix(String world, String groupName) {
    	return infoReader.getGroupPrefix(groupName, world);
    }

    @Override
    public String getGroupSuffix(String world, String groupName) {
        return infoReader.getGroupSuffix(groupName, world);
    }

    @Override
    public boolean canGroupBuild(String world, String groupName) {
        return true;
    }

    @Override
    public String getGroupPermissionString(String world, String groupName, String permission) {
        unsupportedOperation();
        return null;
    }

    @Override
    public int getGroupPermissionInteger(String world, String groupName, String permission) {
        unsupportedOperation();
        return 0;
    }

    @Override
    public boolean getGroupPermissionBoolean(String world, String groupName, String permission) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public double getGroupPermissionDouble(String world, String groupName, String permission) {
        unsupportedOperation();
        return 0.0;
    }

    @Override
    public String getUserPermissionString(String world, String userName, String permission) {
        unsupportedOperation();
        return null;
    }

    @Override
    public int getUserPermissionInteger(String world, String userName, String permission) {
        unsupportedOperation();
        return 0;
    }

    @Override
    public boolean getUserPermissionBoolean(String world, String userName, String permission) {
        return this.has(world, userName, permission);
    }

    @Override
    public double getUserPermissionDouble(String world, String userName, String permission) {
        unsupportedOperation();
        return 0.0;
    }

    @Override
    public String getPermissionString(String world, String userName, String permission) {
        unsupportedOperation();
        return null;
    }

    @Override
    public int getPermissionInteger(String world, String userName, String permission) {
        unsupportedOperation();
        return 0;
    }

    @Override
    public boolean getPermissionBoolean(String world, String userName, String permission) {
        return this.has(world, userName, permission);
    }
    
    @Override
    public double getPermissionDouble(String world, String userName, String permission) {
        unsupportedOperation();
        return 0.0;
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
        unsupportedOperation();
    }

    @Override
    public void removeGroupInfo(String world, String group, String node) {
        unsupportedOperation();
    }

    @Override
    public void setDefaultWorld(String world) {
        unsupportedOperation();
    }

    @Override
    public boolean loadWorld(String world) {
        return unsupportedOperation();
    }

    @Override
    public void forceLoadWorld(String world) {
        unsupportedOperation();
    }

    @Override
    public boolean checkWorld(String world) {
        return unsupportedOperation();
    }

    @Override
    public void load() {
        unsupportedOperation();
    }

    @Override
    public void load(String world, Configuration config) {
        unsupportedOperation();
    }

    @Override
    public boolean reload(String world) {
        return unsupportedOperation();
    }

    // Cache
    private boolean unsupportedOperation() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    private void nagWarning(String method) {
        server.getLogger().warning("[bPermissions] " + method + " item are internal Permissions plugin stuff. Nag plugin author.");
    }
    
    @Override
    public void setCache(String world, Map<String, Boolean> Cache) {
        nagWarning("setCache");
    }

    @Override
    public void setCacheItem(String world, String player, String permission, boolean data) {
        nagWarning("setCacheItem");
    }

    @Override
    public Map<String, Boolean> getCache(String world) {
        nagWarning("getCache");
        return new HashMap<String, Boolean>();
    }

    @Override
    public boolean getCacheItem(String world, String player, String permission) {
        nagWarning("getCacheItem");
        return false;
    }

    @Override
    public void removeCachedItem(String world, String player, String permission) {
        nagWarning("removeCachedItem");
    }

    @Override
    public void clearCache(String world) {
        nagWarning("clearCache");
    }

    @Override
    public void clearAllCache() {
        nagWarning("clearAllCache");
    }

    @Override
    public void save(String world) {
        unsupportedOperation();
    }

    @Override
    public void saveAll() {
        unsupportedOperation();
    }

    @Override
    public void reload() {
        unsupportedOperation();
    }
    
    /**
     * Loads the fake Permissions plugin for plugins which use getPlugin("Permissions")     
     */
    protected static void loadPseudoPlugin(Permissions parent, ClassLoader classLoader) {
        // create a pseudo-Permissions plugin as compatibility layer
        JavaPluginLoader pluginLoader = (JavaPluginLoader)parent.getPluginLoader();
        PluginClassLoader pluginClassLoader = new PluginClassLoader(pluginLoader, new java.net.URL[]{}, classLoader.getParent());
        String version = com.nijikokun.bukkit.Permissions.Permissions.version;
        PluginDescriptionFile description = new PluginDescriptionFile("Permissions", version, "com.nijikokun.bukkit.Permissions.Permissions");
        
        com.nijikokun.bukkit.Permissions.Permissions permissions = new com.nijikokun.bukkit.Permissions.Permissions();
        permissions.doInitialize(pluginLoader, Bukkit.getServer(), description, null, null, pluginClassLoader);
        try {
            permissions.onLoad();
            
            PermissionBridge bridge = (PermissionBridge)permissions.getHandler();
            bridge.permissions = parent;
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
