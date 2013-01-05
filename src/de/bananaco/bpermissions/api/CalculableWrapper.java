package de.bananaco.bpermissions.api;

import java.util.Set;

import de.bananaco.bpermissions.imp.Debugger;


public abstract class CalculableWrapper extends MapCalculable {

	private WorldManager wm = WorldManager.getInstance();

	public CalculableWrapper(String name, Set<String> groups,
			Set<Permission> permissions, String world) {
		super(name, groups, permissions, world);

	}

	public boolean hasPermission(String node) {
		node = node.toLowerCase();
		boolean allowed = Calculable.hasPermission(node, getMappedPermissions());
		return allowed;
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
		if(wm.getAutoSave()) {
			getWorldObject().save();
			getWorldObject().setupAll();
		}
	}

	@Override
	public void removeGroup(String group) {
		super.removeGroup(group);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave()) {
			getWorldObject().save();
			getWorldObject().setupAll();
		}
	}

	@Override
	public void addPermission(String permission, boolean isTrue) {
		super.addPermission(permission, isTrue);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave()) {
			getWorldObject().save();
			getWorldObject().setupAll();
		}
	}

	@Override
	public void removePermission(String permission) {
		super.removePermission(permission);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave()) {
			getWorldObject().save();
			getWorldObject().setupAll();
		}
	}

	@Override
	public void setValue(String key, String value) {
		super.setValue(key, value);
		setDirty(true);
		setCalculablesWithGroupDirty();
		if(wm.getAutoSave()) {
			getWorldObject().save();
			getWorldObject().setupAll();
		}
	}

	public void setCalculablesWithGroupDirty() {
		Set<Calculable> users = getWorldObject().getAll(CalculableType.USER);
		Set<Calculable> groups = getWorldObject().getAll(CalculableType.GROUP);
		if(users == null || users.size() == 0) {
			Debugger.log("Error setting users dirty");
		} else {
			for(Calculable user : users) {
				if(user.hasGroupRecursive(getName()))
					((User) user).setDirty(true);
			}
		}
		if(groups == null || groups.size() == 0) {
			Debugger.log("Error setting groups dirty");
		} else {
			for(Calculable group : groups) {
				if(group.hasGroupRecursive(getName()))
					((Group) group).setDirty(true);
			}
		}
	}

}
