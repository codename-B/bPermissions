package de.bananaco.bpermissions.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
/**
 * This is the class to extend for new implementations
 * of bPermissions.
 * 
 * With this class, other ways to load/save permissions will become
 * easily available (hopefully)...
 */
public abstract class World {
	
	private final Map<String, Group> groups;
	private final Map<String, User> users;
	private final String world;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public World(String world) {
		this.world = world;
		this.users = new HashMap();
		this.groups = new HashMap();
	}
	/**
	 * Make sure you call .calculateEffectivePermissions
	 * for all the users once this is done!
	 * 
	 * You can just call add(Calculable) here with the objects
	 * you create.
	 * @return boolean
	 */
	public abstract boolean load();
	/**
	 * This should be as efficient as possible, can even be threaded if you really desire.
	 * This is an attempt to increase compatability with everything!
	 * @return boolean
	 */
	public abstract boolean save();
	
	/**
	 * Used to check if the World contains an entry for said Calculable
	 * @param name
	 * @param type
	 * @return boolean
	 */
	public boolean contains(String name, CalculableType type) {
		// A quick lowercase here
		name = name.toLowerCase();
		// And now we check
		if(type == CalculableType.USER) {
			return users.containsKey(name);
		} else if (type == CalculableType.GROUP) {
			return groups.containsKey(name);
		}
		return false;
	}
	
	public Group getGroup(String name) {
		return (Group) get(name, CalculableType.GROUP);
	}
	
	public User getUser(String name) {
		return (User) get(name, CalculableType.USER);
	}
	
	/**
	 * Used to get the contained Calculable (contains should be used first)
	 * @param name
	 * @param type
	 * @return Calculable (Group/User)
	 */
	public Calculable get(String name, CalculableType type) {
		// A quick lowercase here
		name = name.toLowerCase();
		// And now we check
		if(type == CalculableType.USER) {
			if(!users.containsKey(name))
			add(new User(name, null, null, getName()));
			return users.get(name);
		} else if (type == CalculableType.GROUP) {
			if(!groups.containsKey(name))
			add(new Group(name, null, null, getName()));
			return groups.get(name);
		}
		return null;
	}
	
	/**
	 * Used to grab a complete set of the contained Calculable from
	 * the World.
	 * Should never return null but may return an empty Set<Calculable>
	 * Returns a new Set with direct references to the object.
	 * @param type
	 * @return Set<Calculable>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<Calculable> getAll(CalculableType type) {
		Set<Calculable> entries = new HashSet();
		// And now we grab
		if(type == CalculableType.USER) {
			for(String key : users.keySet()) {
					entries.add(users.get(key));
			}
			return entries;
		}
		else if (type == CalculableType.GROUP) {
			for(String key : groups.keySet()) {
				entries.add(groups.get(key));
			}
			return entries;
		}
		return entries;
	}
	
	/**
	 * This adds the Calculable to either groups or users depending
	 * on if the calculable is an instance of either.
	 * This is not directly checked and instead getType() is relied upon to be correct.
	 * If the calculable is not an instance of a group or a user, it is
	 * not added.
	 * This means you cannot add base calculables (or any other class which
	 * extends calculable) to this.
	 * @param calculable
	 */
	public void add(Calculable calculable) {
		if (calculable.getType() == CalculableType.USER)
			users.put(calculable.getNameLowerCase(), (User) calculable);
		else if (calculable.getType() == CalculableType.GROUP)
			groups.put(calculable.getNameLowerCase(), (Group) calculable);
		else
			System.err.println("Calculable not instance of User or Group!");
	}
	
	/**
	 * Returns the world name
	 * @return String
	 */
	public String getName() {
		return world;
	}
	
	/**
	 * Shows if the world is THIS world
	 * @param world
	 * @return boolean
	 */
	public boolean equalsWorld(String world) {
		return world.equalsIgnoreCase(this.world);
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o.hashCode() == hashCode();
	}

}
