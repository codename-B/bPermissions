package de.bananaco.permissions.handlers;

import de.bananaco.permissions.PackageLoadEvent;
import de.bananaco.permissions.Packages;
import de.bananaco.permissions.mysql.MySQLHandler;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {

    // TODO add MONGODB
    public static enum DBType {
        FILE,
        MYSQL;
    }

    private final Packages plugin;
    private final boolean global;
    private final DBType packageType;
    private final DBType databaseType;
    // interfaces are awesome
    private PackageManager packageManager = null;
    // mysql is not, but here it is anyway
    MySQLHandler mySQLHandler = new MySQLHandler();

    public Handler(Packages plugin, boolean global, DBType packageType, DBType databaseType) {
        this.plugin = plugin;
        // variables set in config.yml
        this.global = global;
        this.packageType = packageType;
        this.databaseType = databaseType;
    }

    private void setup() {
        // packages are all kept in one place
        if(this.packageType == DBType.FILE) {
             packageManager = new FilePackageManager(new File(plugin.getDataFolder(), "packages.yml"));
        } else if(this.packageType == DBType.MYSQL) {
            // TODO MYSQL packages
        }
        // global and world handlers here
        if(global) {
            Database database = null;
            if(this.databaseType == DBType.FILE) {
                database = new FileDatabase(new File(plugin.getDataFolder(), "global"), packageManager);
            } else if(this.databaseType == DBType.MYSQL) {
                // TODO MYSQL packages
            }
            // because we use the handy events system we don't actually have to pass around references like crazy
            Bukkit.getPluginManager().registerEvents(new GlobalHandler(database), plugin);
        } else {
            for(World world : Bukkit.getWorlds()) {
                Database database = null;
                if(this.databaseType == DBType.FILE) {
                    database = new FileDatabase(new File(plugin.getDataFolder(), world.getName()), packageManager);
                } else if(this.databaseType == DBType.MYSQL) {
                    // TODO MYSQL packages
                }
                Bukkit.getPluginManager().registerEvents(new WorldHandler(database, world), plugin);
            }
        }
    }

    // called from the appropriate listeners
    public static void setup(final Player player, final Database database) {
        if(database.isASync()) {
            Bukkit.getScheduler().runTaskAsynchronously(Packages.instance, new Runnable() {
                public void run() {
                    try {
                        final List<PPackage> packages = database.getPackages(player);
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
                Bukkit.getPluginManager().callEvent(new PackageLoadEvent(player, database.getPackages(player)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
