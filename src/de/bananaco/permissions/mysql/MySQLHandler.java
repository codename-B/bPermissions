package de.bananaco.permissions.mysql;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.handlers.Handler;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MySQLHandler {

    public final String PACKAGE_TABLE = "package_table";
    public final String DATA_TABLE = "data_table";

    private String user = "";
    private String database = "";
    private String password = "";
    private String port = "";
    private String hostname = "";
    // which tables to check and create if not exist
    private Handler.DBType packageType;
    private Handler.DBType databaseType;

    public void loadSettings(Packages plugin) {
        // and which ones to load in the first place too...
        packageType = plugin.packageType;
        databaseType = plugin.databaseType;
        // now load
        plugin.getConfig().set("mysql.user", user = plugin.getConfig().getString("mysql.user", "user"));
        plugin.getConfig().set("mysql.database", database = plugin.getConfig().getString("mysql.database", "database"));
        plugin.getConfig().set("mysql.password", password = plugin.getConfig().getString("mysql.password", "password"));
        plugin.getConfig().set("mysql.port", port = plugin.getConfig().getString("mysql.port", "port"));
        plugin.getConfig().set("mysql.hostname", hostname = plugin.getConfig().getString("mysql.hostname", "hostname"));
        // save changes
        plugin.saveConfig();
        // handle mysql table creation if necessary
        if(packageType == Handler.DBType.MYSQL && !hasTable(PACKAGE_TABLE)) {
            createPackageTable();
        }
        if(databaseType == Handler.DBType.MYSQL && !hasTable(DATA_TABLE)) {
            createDatabaseTable();
        }
    }

    // TODO fill in SQL queries for these things

    // table management stuff

    public void createPackageTable() {
        String query = "CREATE TABLE "+PACKAGE_TABLE+" (\n" +
                "         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "         package VARCHAR(32),\n" +
                "         permission VARCHAR(32),\n" +
                "         cur_timestamp TIMESTAMP(8)\n" +
                "       );";
    }

    public void createDatabaseTable() {
        String query = "CREATE TABLE "+DATA_TABLE+" (\n" +
                "         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "         player VARCHAR(32),\n" +
                "         world VARCHAR(32),\n" +
                "         package VARCHAR(32),\n" +
                "         cur_timestamp TIMESTAMP(8)\n" +
                "       );";
    }

    public boolean hasTable(String table) {
        String query = "IF object_id('"+table+"', 'U') is not null\n" +
                "       PRINT 'true!'\n" +
                "       ELSE\n" +
                "       PRINT 'false'";
        return true;
    }

    // PackageManager stuff

    public List<PPackage> getPPackages() {
        return null;
    }

    // Database stuff

    public void addEntry(String player, String value) {

    }

    public void addEntry(String player, String world, String value) {

    }

    public List<String> getEntries(String player) {
        return null;
    }

    public List<String> getEntries(String player, String world) {
        return null;
    }
}
