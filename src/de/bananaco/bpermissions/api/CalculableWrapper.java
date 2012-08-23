package de.bananaco.bpermissions.api;

import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MapCalculable;
import de.bananaco.bpermissions.api.util.Permission;

public abstract class CalculableWrapper extends MapCalculable {
	
	private WorldManager wm = WorldManager.getInstance();
	
	public CalculableWrapper(String name, Set<String> groups,
			Set<Permission> permissions, String world) {
		super(name, groups, permissions, world);
		
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
			getWorldObject().save();
		
		getWorldObject().setupAll();
	}

	@Override
	public void removeGroup(String group) {
		super.removeGroup(group);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			getWorldObject().save();
		
		getWorldObject().setupAll();
	}

	@Override
	public void addPermission(String permission, boolean isTrue) {
		super.addPermission(permission, isTrue);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			getWorldObject().save();
		
		getWorldObject().setupAll();
	}

	@Override
	public void removePermission(String permission) {
		super.removePermission(permission);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			getWorldObject().save();
		
		getWorldObject().setupAll();
	}

	@Override
	public void setValue(String key, String value) {
		super.setValue(key, value);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave())
			getWorldObject().save();
		
		getWorldObject().setupAll();
	}

	public void setCalculablesWithGroupDirty() {
		for(Calculable user : getWorldObject().getAll(CalculableType.USER)) {
			if(user.hasGroupRecursive(getName()))
				((User) user).setDirty(true);
		}
		for(Calculable group : getWorldObject().getAll(CalculableType.GROUP)) {
			if(group.hasGroupRecursive(getName()))
				((Group) group).setDirty(true);
		}
	}

}
