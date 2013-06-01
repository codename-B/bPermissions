package de.bananaco.permissions.handlers;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.ppackage.PPackage;
import org.bukkit.entity.Player;

import java.io.*;
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

    public List<PPackage> getPackages(String player) throws Exception {
        File file = new File(root, player + ".txt");
        List<PPackage> packages = new ArrayList<PPackage>();
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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

    public boolean hasEntry(String player) {
        return new File(root, player + ".txt").exists();
    }

    public void addEntry(String player, String entry) {
        File file = new File(root, player + ".txt");
        if (!file.exists()) {
            try {
                root.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // now write the entry
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
            pw.append(entry+"\n");
            pw.close();;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PPackage getPackage(String p) {
        return packageManager.getPackage(p);
    }

    public void addPackage(String v, String p) {
        packageManager.addPackage(v, p);
    }

}
