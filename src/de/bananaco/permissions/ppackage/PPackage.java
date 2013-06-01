package de.bananaco.permissions.ppackage;

import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;

// container of permissions, bringer of joy, harbinger of perks, professor of powerups, lord of the pets...
public class PPackage {

    // big friendly method
    public static PPackage loadPackage(String name, List<String> perms) {
        // logic checking on things
        if (name == null) {
            // what more information need be given?
            throw new PPackageException("Attempted to register a null name.");
        }
        if (perms == null) {
            // a bit more detail
            throw new PPackageException("Attempted to register package '" + name + "' with a null package.");
        }
        if (perms.size() == 0) {
            // things shouldn't be empty
            throw new PPackageException("Attempted to register the empty package '" + name + "'.");
        }
        // convert List<String> to List<Permission>
        List<PPermission> permissions = new ArrayList<PPermission>();
        for (String perm : perms) {
            permissions.add(Util.loadPerm(perm));
        }
        // and now we can be friends again
        return new PPackage(name, permissions);
    }

    private final String name;
    private final List<PPermission> permissions = new ArrayList<PPermission>();

    // we want to use the static methods for this
    private PPackage(String name, List<PPermission> permissions) {
        this.name = name;
        this.permissions.addAll(permissions);
    }

    // simple, what more could you want?
    public String getName() {
        return name;
    }

    // new object returned here
    public List<PPermission> getPermissions() {
        return new ArrayList<PPermission>(permissions);
    }
}
