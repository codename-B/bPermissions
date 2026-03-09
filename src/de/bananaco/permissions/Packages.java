package de.bananaco.permissions;

import de.bananaco.permissions.commands.AddPackage;
import de.bananaco.permissions.commands.Permissions;
import de.bananaco.permissions.handlers.Handler;
import de.bananaco.permissions.ppackage.PPackage;
import de.bananaco.permissions.ppackage.PPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Packages extends JavaPlugin implements Listener {

    private static String defaultPackage = "default";
    public static Packages instance = null;

    public static String getDefaultPackage() {
        return defaultPackage;
    }

    public static Handler.DBType getType(String key) {
        return Handler.DBType.valueOf(key.toUpperCase());
    }

    public static String getType(Handler.DBType key) {
        return key.name().toLowerCase();
    }

    public static Handler.MetaType getMeta(String key) {
        return Handler.MetaType.valueOf(key.toUpperCase());
    }

    public static String getMeta(Handler.MetaType key) {
        return key.name().toLowerCase();
    }

    private final Map<UUID, PermissionAttachment> permissions = new HashMap<UUID, PermissionAttachment>();
    public Handler handler = null;
    public Handler.DBType packageType;
    public Handler.DBType databaseType;
    public Handler.MetaType metaType;
    public boolean global = true;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        reloadPermissionsState();
        getCommand("permissions").setExecutor(new Permissions());
        getCommand("addpackage").setExecutor(new AddPackage());
    }

    public void reloadPermissionsState() {
        if (handler != null) {
            handler.unregister();
            handler = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            unregister(player);
        }
        reloadConfig();
        getConfig().set("defaultPackage", defaultPackage = getConfig().getString("defaultPackage", defaultPackage));
        getConfig().set("global", global = getConfig().getBoolean("global", global));
        packageType = getType(getConfig().getString("packageType", getType(Handler.DBType.FILE)));
        databaseType = getType(getConfig().getString("databaseType", getType(Handler.DBType.FILE)));
        metaType = getMeta(getConfig().getString("metaType", getMeta(Handler.MetaType.NONE)));
        getConfig().set("packageType", getType(packageType));
        getConfig().set("databaseType", getType(databaseType));
        getConfig().set("metaType", getMeta(metaType));
        saveConfig();
        handler = new Handler(this, global, metaType.equals(Handler.MetaType.FILE), packageType, databaseType);
        for (Player player : Bukkit.getOnlinePlayers()) {
            register(player);
            handler.loadPlayer(player);
        }
    }

    @Override
    public void onDisable() {
        if (handler != null) {
            handler.unregister();
            handler = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            unregister(player);
        }
    }

    @EventHandler
    public void onPackageLoad(PackageLoadEvent event) {
        setPermissions(event.getPlayer(), event.getPackages());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        register(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        unregister(event.getPlayer());
    }

    private void register(Player player) {
        PermissionAttachment existing = permissions.remove(player.getUniqueId());
        if (existing != null) {
            player.removeAttachment(existing);
        }
        permissions.put(player.getUniqueId(), player.addAttachment(this));
    }

    private void unregister(Player player) {
        PermissionAttachment attachment = permissions.remove(player.getUniqueId());
        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }

    private void setPermissions(Player player, List<PPackage> packages) {
        if (player == null) {
            return;
        }
        PermissionAttachment attachment = permissions.get(player.getUniqueId());
        if (attachment == null) {
            System.err.println("Calculating permissions on " + player.getName() + ": attachment was null");
            return;
        }
        for (String key : new HashSet<String>(attachment.getPermissions().keySet())) {
            attachment.unsetPermission(key);
        }
        for (PPackage pack : packages) {
            for (PPermission perm : pack.getPermissions()) {
                attachment.setPermission(perm.getName().toLowerCase(), perm.isTrue());
            }
        }
        player.recalculatePermissions();
    }
}
