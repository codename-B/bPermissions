package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;

public interface PackageManager {

    public PPackage getPackage(String p);

    public void addPackage(String p, String perm);

}
