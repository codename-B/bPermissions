package de.bananaco.permissions.handlers;

import de.bananaco.permissions.ppackage.PPackage;

public abstract class MetaWrapper implements MetaData {

    private final Database database;

    public MetaWrapper(Database database) {
        this.database = database;
    }

    public String calculateMeta(String player, String key) {
        try {
            for(PPackage pPackage : database.getPackages(player)) {
                // return first not null result
                String meta = this.getMeta(pPackage.getName(), key);
                if(meta != null) {
                    return meta;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
