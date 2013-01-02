package de.bananaco.bpermissions.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//import org.bukkit.ChatColor;

/**
 * This class contains the main calculations for
 * a GroupCarrier/PermissionCarrier.
 * 
 * It calculates inherited permissions all the way down the line of the object.
 * This does not include checking for infinite loops, you can break this if you want to.
 */
public abstract class Calculable extends CalculableMeta {

	Set<Permission> effectivePermissions;
	String name;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Calculable(String name, Set<String> groups,
			Set<Permission> permissions, String world) {
		super(groups, permissions, world);
		// TODO does this remove the ChatColor?
		this.name = name;
		this.effectivePermissions = new HashSet();
	}

	/**
	 * Debugging code: used to print the total effective Permissions of an object at a specified point in time
	 */
	protected void print() {
		String[] perms = new String[effectivePermissions.size()];
		int i=0;
		for(Permission perm : effectivePermissions) {
			if(perm == null)
				System.err.println("PERM IS NULL?");
			else if(perm.isTrue())
				perms[i] = perm.name();
			else
				perms[i] = "^"+perm.name();
			i++;
		}
		System.out.println(getName()+": "+Arrays.toString(perms));
	}

	/**
	 * Used to calculate the total permissions gained by the object
	 * @throws RecursiveGroupException 
	 */
	public void calculateEffectivePermissions() throws RecursiveGroupException {
		if(!isDirty())
			return;
		try {
			Map<String, Integer> priorities = new HashMap<String, Integer>();
			effectivePermissions.clear();
			for (String gr : serialiseGroups()) {
				Group group = getWorldObject().getGroup(gr);
				group.calculateEffectivePermissions();
				for(Permission perm : group.getEffectivePermissions()) {
					if(!priorities.containsKey(perm.nameLowerCase()) || priorities.get(perm.nameLowerCase()) < group.getPriority()) {
						priorities.put(perm.nameLowerCase(), group.getPriority());
						if(effectivePermissions.contains(perm)) {
							effectivePermissions.remove(perm);
						}
						effectivePermissions.add(perm);
					}
				}
			}
			priorities.clear();
			for(Permission perm : this.getPermissions()) {
				if(effectivePermissions.contains(perm)) {
					effectivePermissions.remove(perm);
				}
				effectivePermissions.add(perm);
			}
			//print();
		} catch (StackOverflowError e) {
			throw new RecursiveGroupException(this);
		}
	}

	/**
	 * Return the total permissions gained by the object
	 * 
	 * @return Set<Permission>
	 */
	public Set<Permission> getEffectivePermissions() {
		return effectivePermissions;
	}

	/**
	 * Returns the name of the calculable object
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the lowercased name of the calculable object
	 * 
	 * @return String
	 */
	public String getNameLowerCase() {
		return name.toLowerCase();
	}

	@Override
	public int hashCode() {
		return getNameLowerCase().hashCode();
	}

	@Override
	public String toString() {
		return name.toLowerCase();
	}

	/**
	 * Another way of checking the type
	 * besides instanceof
	 * @return CalculableType
	 */
	public abstract CalculableType getType();

	protected abstract boolean isDirty();

	public abstract boolean hasPermission(String node);

	protected abstract World getWorldObject();

	@Override
	public void clear() {
		this.effectivePermissions.clear();
		super.clear();
	}

	public static boolean hasPermission(String node, Map<String, Boolean> perms) {
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

}
