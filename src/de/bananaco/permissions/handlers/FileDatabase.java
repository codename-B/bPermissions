package de.bananaco.permissions.handlers;

import de.bananaco.permissions.Packages;
import de.bananaco.permissions.ppackage.PPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private List<String> getPackages(File file) throws Exception {
        List<String> packages = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String pack = null;
        while ((pack = br.readLine()) != null) {
            if (!pack.isEmpty()) {
                packages.add(pack);
            }
        }
        br.close();
        return packages;
    }

    private File getPlayerFile(String player) {
        String normalizedName = normalizePlayer(player) + ".txt";
        File normalizedFile = new File(root, normalizedName);
        if (normalizedFile.exists()) {
            return normalizedFile;
        }
        File exactFile = new File(root, player + ".txt");
        if (exactFile.exists()) {
            return exactFile;
        }
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equalsIgnoreCase(normalizedName)) {
                    return file;
                }
            }
        }
        return normalizedFile;
    }

    private String normalizePlayer(String player) {
        return player.toLowerCase(Locale.ROOT);
    }

    public List<PPackage> getPackages(String player) throws Exception {
        File file = getPlayerFile(player);
        List<PPackage> packages = new ArrayList<PPackage>();
        if (file.exists()) {
            for (String pack : getPackages(file)) {
                if (getPackage(pack) != null) {
                    packages.add(getPackage(pack));
                }
            }
        } else {
            if (getPackage(Packages.getDefaultPackage()) != null) {
                packages.add(getPackage(Packages.getDefaultPackage()));
            }
        }
        return packages;
    }

    public boolean hasEntry(String player) {
        return getPlayerFile(player).exists();
    }

    public void addEntry(String player, String entry) {
        File file = getPlayerFile(player);
        if (!file.exists()) {
            try {
                root.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            List<String> packages = getPackages(file);
            if (packages.contains(entry)) {
                return;
            }
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (String pack : packages) {
                pw.println(pack);
            }
            pw.println(entry);
            pw.close();
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
