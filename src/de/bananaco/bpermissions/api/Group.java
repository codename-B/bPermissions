package de.bananaco.bpermissions.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MapCalculable;
import de.bananaco.bpermissions.api.util.Permission;

/**
 * The Group object extends Calculable which allows recursive inheritance of unlimited depth.
 * This is the new bPermissions, and it is awesome.
 */
public class Group extends MapCalculable {

	private World w;
	private WorldManager wm = WorldManager.getInstance();
	
	public Group(String name, World w) {
		this(name, null, null, w.getName(), w);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Group(String name, List<String> groups, Set<Permission> permissions,
			String world, World w) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, world);
		this.w = w;
	}

	@Override
	public CalculableType getType() {
		return CalculableType.GROUP;
	}

	public boolean hasPermission(String node) {
		node = node.toLowerCase();
		boolean allowed = internalHasPermission(node);
		return allowed;
	}

	private boolean internalHasPermission(String node) {
		Map<String, Boolean> perms = getMappedPermissions();
		
		if(perms.containsKey(node))
			return perms.get(node);
		
		String permission = node;
		int index = permission.lastIndexOf('.');
		while (index >= 0) {
			permission = permission.substring(0, index);
			String wildcard = permission + ".*";
			if(perms.containsKey(wildcard))
				return perms.get(wildcard);
			index = permission.lastIndexOf('.');
		}
		if(perms.containsKey("*"))
			return perms.get("*");
		return false;
	}
	
	/*
	 * These methods are added
	 * to allow auto-saving of
	 * the World on any changes
	 */
	
	@Override
	public void addGroup(String group) {
		super.addGroup(group);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void removeGroup(String group) {
		super.removeGroup(group);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void addPermission(String permission, boolean isTrue) {
		super.addPermission(permission, isTrue);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void removePermission(String permission) {
		super.removePermission(permission);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void setValue(String key, String value) {
		super.setValue(key, value);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	protected World getWorldObject() {
		return w;
	}

	public void setCalculablesWithGroupDirty() {
		for(Calculable user : w.getAll(CalculableType.USER)) {
			if(user.hasGroupRecursive(getName()))
				((User) user).setDirty(true);
		}
		for(Calculable group : w.getAll(CalculableType.GROUP)) {
			if(group.hasGroupRecursive(getName()))
				((Group) group).setDirty(true);
		}
	}
	
}
