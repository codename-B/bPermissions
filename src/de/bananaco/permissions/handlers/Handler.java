package de.bananaco.permissions.handlers;

import de.bananaco.permissions.PackageLoadEvent;
import de.bananaco.permissions.Packages;
import de.bananaco.permissions.mysql.MySQLHandler;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Handler {

    // TODO add MONGODB
    public static enum DBType {
        FILE,
        MYSQL;
    }

    public static enum MetaType {
        FILE,
        NONE;
    }

    private final Packages plugin;
    private final boolean global;
    private final DBType packageType;
    private final DBType databaseType;
    public PackageManager packageManager = null;
    public List<Carrier> carriers = new ArrayList<Carrier>();
    MySQLHandler handler = new MySQLHandler();
    MetaHandler meta = null;

    public Handler(Packages plugin, boolean global, boolean meta, DBType packageType, DBType databaseType) {
        this.plugin = plugin;
        this.global = global;
        this.meta = meta ? new MetaHandler(plugin) : null;
        this.packageType = packageType;
        this.databaseType = databaseType;
        setup();
        handler.loadSettings(plugin);
    }

    private Carrier add(Carrier carrier) {
        carriers.add(carrier);
        return carrier;
    }

    private void setup() {
        if (this.packageType == DBType.FILE) {
            packageManager = new FilePackageManager(new File(plugin.getDataFolder(), "packages.yml"));
        } else if (this.packageType == DBType.MYSQL) {
            packageManager = new MySQLPackageManager(handler);
        }
        if (global) {
            Database database = null;
            if (this.databaseType == DBType.FILE) {
                database = new FileDatabase(new File(plugin.getDataFolder(), "global"), packageManager);
            } else if (this.databaseType == DBType.MYSQL) {
                database = new MySQLDatabase("global", handler, packageManager);
            }
            if (meta != null) {
                meta.setGlobalMeta(database);
            }
            Bukkit.getPluginManager().registerEvents(add(new GlobalHandler(database)), plugin);
        } else {
            for (World world : Bukkit.getWorlds()) {
                Database database = null;
                if (this.databaseType == DBType.FILE) {
                    database = new FileDatabase(new File(plugin.getDataFolder(), world.getName()), packageManager);
                } else if (this.databaseType == DBType.MYSQL) {
                    database = new MySQLDatabase(world.getName(), handler, packageManager);
                }
                if (meta != null) {
                    meta.setWorldMeta(database, world.getName());
                }
                Bukkit.getPluginManager().registerEvents(add(new WorldHandler(database, world)), plugin);
            }
        }
    }

    public void loadPlayer(Player player) {
        String target = global ? "global" : player.getWorld().getName();
        for (Carrier carrier : carriers) {
            if (carrier.getName().equalsIgnoreCase(target)) {
                setup(player, carrier.getDatabase());
                return;
            }
        }
    }

    public void unregister() {
        for (Carrier carrier : carriers) {
            HandlerList.unregisterAll(carrier);
        }
        carriers.clear();
    }

    public static void setup(final Player player, final Database database) {
        if (database.isASync()) {
            Bukkit.getScheduler().runTaskAsynchronously(Packages.instance, new Runnable() {
                public void run() {
                    try {
                        final List<PPackage> packages = database.getPackages(player.getName());
                        Bukkit.getScheduler().runTask(Packages.instance, new Runnable() {
                            public void run() {
                                Bukkit.getPluginManager().callEvent(new PackageLoadEvent(player, packages));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                Bukkit.getPluginManager().callEvent(new PackageLoadEvent(player, database.getPackages(player.getName())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
