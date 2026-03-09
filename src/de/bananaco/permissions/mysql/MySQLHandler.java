package de.bananaco.permissions.mysql;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.handlers.Handler;
import de.bananaco.permissions.ppackage.PPackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MySQLHandler {

    public final String PACKAGE_TABLE = "package_table";
    public final String DATA_TABLE = "data_table";

    private String user = "";
    private String database = "";
    private String password = "";
    private String port = "";
    private String hostname = "";
    private Handler.DBType packageType;
    private Handler.DBType databaseType;
    private Connection c = null;

    public void loadSettings(Packages plugin) {
        packageType = plugin.packageType;
        databaseType = plugin.databaseType;
        if (packageType == Handler.DBType.MYSQL || databaseType == Handler.DBType.MYSQL) {
            plugin.getConfig().set("mysql.user", user = plugin.getConfig().getString("mysql.user", "user"));
            plugin.getConfig().set("mysql.database", database = plugin.getConfig().getString("mysql.database", "database"));
            plugin.getConfig().set("mysql.password", password = plugin.getConfig().getString("mysql.password", "password"));
            plugin.getConfig().set("mysql.port", port = plugin.getConfig().getString("mysql.port", "port"));
            plugin.getConfig().set("mysql.hostname", hostname = plugin.getConfig().getString("mysql.hostname", "hostname"));
            plugin.saveConfig();
            MySQL MySQL = new MySQL(hostname, port, database, user, password);
            c = MySQL.open();
            if (packageType == Handler.DBType.MYSQL && !hasTable(PACKAGE_TABLE)) {
                if (!hasTable(PACKAGE_TABLE)) {
                    createPackageTable();
                }
            }
            if (databaseType == Handler.DBType.MYSQL && !hasTable(DATA_TABLE)) {
                if (!hasTable(DATA_TABLE)) {
                    createDatabaseTable();
                }
            }
        }
    }

    private String normalizePlayer(String player) {
        return player.toLowerCase(Locale.ROOT);
    }

    public void createPackageTable() {
        String query = "CREATE TABLE " + PACKAGE_TABLE + " (\n" +
                "         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "         package VARCHAR(32),\n" +
                "         permission VARCHAR(32)\n" +
                "       );";
        try {
            Statement s = c.createStatement();
            s.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDatabaseTable() {
        String query = "CREATE TABLE " + DATA_TABLE + " (\n" +
                "         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "         player VARCHAR(32),\n" +
                "         world VARCHAR(32),\n" +
                "         package VARCHAR(32)\n" +
                "       );";
        try {
            Statement s = c.createStatement();
            s.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasTable(String table) {
        String query = "SHOW TABLES LIKE '" + table + "'";
        try {
            Statement s = c.createStatement();
            ResultSet results = s.executeQuery(query);
            if (results.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PPackage getPPackage(String p) {
        String query = "SELECT permission FROM " + PACKAGE_TABLE + " WHERE package='" + p + "'";
        List<String> permissions = new ArrayList<String>();
        try {
            Statement s = c.createStatement();
            ResultSet results = s.executeQuery(query);
            while (results.next()) {
                String perm = results.getString("permission");
                permissions.add(perm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (permissions.size() == 0) {
            return null;
        }
        return PPackage.loadPackage(p, permissions);
    }

    public List<String> getEntries(String player, String tag) {
        List<String> packages = new ArrayList<String>();
        try {
            PreparedStatement ps = c.prepareStatement("SELECT package FROM " + DATA_TABLE + " WHERE LOWER(player)=? AND world=?");
            ps.setString(1, normalizePlayer(player));
            ps.setString(2, tag);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String pack = results.getString("package");
                packages.add(pack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packages.size() == 0) {
            return null;
        }
        return packages;
    }

    public void addEntry(String p, String permission) {
        if (hasEntry(p, permission)) {
            return;
        }
        try {
            PreparedStatement ps = c.prepareStatement("INSERT INTO " + PACKAGE_TABLE + " (package, permission) VALUES (?, ?);");
            ps.setString(1, p);
            ps.setString(2, permission);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String player, String world, String value) {
        if (hasEntry(player, world, value)) {
            return;
        }
        try {
            PreparedStatement ps = c.prepareStatement("INSERT INTO " + DATA_TABLE + " (player, world, package) VALUES (?, ?, ?);");
            ps.setString(1, normalizePlayer(player));
            ps.setString(2, world);
            ps.setString(3, value);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasEntry(String p, String permission) {
        String query = "SELECT permission FROM " + PACKAGE_TABLE + " WHERE package='" + p + "' AND permission='" + permission + "'";
        try {
            Statement s = c.createStatement();
            ResultSet results = s.executeQuery(query);
            return results.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasEntry(String player, String world, String value) {
        try {
            PreparedStatement ps = c.prepareStatement("SELECT package FROM " + DATA_TABLE + " WHERE LOWER(player)=? AND world=? AND package=?");
            ps.setString(1, normalizePlayer(player));
            ps.setString(2, world);
            ps.setString(3, value);
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeEntries(String p) {
        String query = "DELETE FROM " + PACKAGE_TABLE + " WHERE package='" + p + "'";
        try {
            Statement s = c.createStatement();
            s.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeEntries(String player, String world) {
        try {
            PreparedStatement ps = c.prepareStatement("DELETE FROM " + DATA_TABLE + " WHERE LOWER(player)=? AND world=?");
            ps.setString(1, normalizePlayer(player));
            ps.setString(2, world);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
