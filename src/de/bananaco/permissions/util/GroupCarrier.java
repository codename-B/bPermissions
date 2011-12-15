package de.bananaco.permissions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bananaco.permissions.worlds.WorldPermissions;
/**
 * This class is any object which carries groups.
 * The group references are stored by String, rather than directly
 * as this allows for full loading of all groups before the calculation
 * done by getEffectivePermissions() in Calculable without the recursive nightmare that would ensue.
 */
public abstract class GroupCarrier extends PermissionCarrier {

	private final Set<String> groups;
	private final WorldPermissions parent;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected GroupCarrier(Set<String> groups, Set<Permission> permissions,
			WorldPermissions parent) {
		super(permissions);
		this.parent = parent;
		if(groups == null)
			groups = new HashSet();
		this.groups = groups;
	}

	/**
	 * Returns the groups that the object inherits Calculated via the parent
	 * object (this is a fresh object every call)
	 * 
	 * @return Set<Group>
	 */
	@SuppressWarnings("unchecked")
	public Set<Group> getGroups() {
		@SuppressWarnings("rawtypes")
		Set<Group> groups = new HashSet();
		for (String name : this.groups) {
			Group group = parent.getGroup(name);
			if (group != null)
				groups.add(group);
		}
		return groups;
	}

	/**
	 * Returns the groups that the object inherits This is a direct reference to
	 * the object
	 * 
	 * @return
	 */
	public Set<String> getGroupsAsString() {
		return groups;
	}
	
	/**
	 * Used to make saving prettier
	 * 
	 * @return
	 */
	public List<String> serialiseGroups() {
		List<String> groups = new ArrayList<String>(getGroupsAsString());
		Collections.sort(groups,
                new Comparator<String>()
                {
                    public int compare(String f1, String f2)
                    {
                        return f1.compareTo(f2);
                    }        
                });
		return groups;
	}

}
