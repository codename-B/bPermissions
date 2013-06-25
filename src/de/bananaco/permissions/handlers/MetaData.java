package de.bananaco.permissions.handlers;

public interface MetaData {

    public String getMeta(String pack, String key);

    public void setMeta(String pack, String key, String meta);

}
