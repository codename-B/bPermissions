package de.bananaco.permissions.handlers;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileDatabase implements Database {

    private final File root;
    private final PackageManager packageManager;

    public FileDatabase(File root, PackageManager packageManager) {
        this.root = root;
        this.packageManager = packageManager;
    }

    public boolean isASync() {
        return false;
    }

    public List<PPackage> getPackages(Player player) throws Exception {
        List<PPackage> packages = new ArrayList<PPackage>();
        if (hasEntry(player)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(root, player.getName() + ".txt"))));
            String pack = null;
            while ((pack = br.readLine()) != null) {
                if (getPackage(pack) != null) {
                    packages.add(getPackage(pack));
                }
            }
            br.close();
        } else {
            // load default
            if (getPackage(Packages.getDefaultPackage()) != null) {
                packages.add(getPackage(Packages.getDefaultPackage()));
            }
        }
        return packages;
    }

    public boolean hasEntry(Player player) {
        return new File(root, player.getName() + ".txt").exists();
    }

    public void createEntry(Player player) {
        File file = new File(root, player.getName() + ".txt");
        if (!file.exists()) {
            try {
                root.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setEntry(Player player, List<PPackage> packages) {
        File file = new File(root, player.getName() + ".txt");
        // TODO fill in
    }

    public PPackage getPackage(String p) {
        return packageManager.getPackage(p);
    }

}
