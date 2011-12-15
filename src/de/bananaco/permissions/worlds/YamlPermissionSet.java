package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.RecursiveGroupException;
import de.bananaco.permissions.util.User;

public class YamlPermissionSet extends WorldPermissions {

	private static final String GROUPS = "groups";
	private static final String PERMISSIONS = "permissions";
	private static final String USERS = "users";
	
	private YamlConfiguration config = new YamlConfiguration();
	private final File file = new File("plugins/bPermissions/" + getWorldName()
			+ ".yml");

	public YamlPermissionSet(World world, Permissions plugin) {
		super(world, plugin);
	}

	@Override
	public String getDefaultGroup() {
		if(config != null)
		return config.getString("default", "default");
		return "default";
	}

	public void load() {
		try {
			loadUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadUnsafe() throws Exception {

		if (!file.exists()) {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		config = new YamlConfiguration();
		config.load(file);

		ConfigurationSection usersConfig = config
				.getConfigurationSection(USERS);
		if (usersConfig != null) {

			Set<String> names = usersConfig.getKeys(false);

			for (String name : names) {
				List<String> nPerm = usersConfig.getStringList(name + "."
						+ PERMISSIONS);
				List<String> nGroup = usersConfig.getStringList(name + "."
						+ GROUPS);
				Set<Permission> perms = Permission.loadFromString(nPerm);
				// Create the new user 
				add(new User(name, nGroup, perms, this));
			}

		}

		ConfigurationSection groupsConfig = config
				.getConfigurationSection(GROUPS);
		if (groupsConfig != null) {

			Set<String> names = groupsConfig.getKeys(false);

			for (String name : names) {
				List<String> nPerm = groupsConfig.getStringList(name + "."
						+ PERMISSIONS);
				List<String> nGroup = groupsConfig.getStringList(name + "."
						+ GROUPS);
				Set<Permission> perms = Permission.loadFromString(nPerm);
				// Create the new group
				this.add(new Group(name, nGroup, perms, this));
			}

		}

		for (User user : getUsers()) {
			try {
			user.calculateEffectivePermissions();
			} catch (RecursiveGroupException e) {
				System.err.println(e.getMessage());
			}
		}

		for (Group group : getGroups()) {
			try {
			group.calculateEffectivePermissions();
			} catch (RecursiveGroupException e) {
				System.err.println(e.getMessage());
			}
		}

	}

	public void save() {
		try {
			saveUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveUnsafe() throws Exception {

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		String def = getDefaultGroup();
		
		config = new YamlConfiguration();

		config.set("default", def);
		
		Set<User> usr = getUsers();
		// Sort them :D
		List<User> users = new ArrayList<User>(usr);
		Collections.sort(users,
                new Comparator<User>()
                {
                    public int compare(User f1, User f2)
                    {
                        return f1.getName().compareTo(f2.getName());
                    }        
                });
		
		for (User user : users) {
			String name = user.getName();
			config.set(USERS + "." + name + "." + PERMISSIONS, user.serialisePermissions());
			config.set(USERS + "." + name + "." + GROUPS, user.serialiseGroups());
			
		}

		Set<Group> grp = getGroups();
		// Sort them :D
		List<Group> groups = new ArrayList<Group>(grp);
		Collections.sort(groups,
                new Comparator<Group>()
                {
                    public int compare(Group f1, Group f2)
                    {
                        return f1.getName().compareTo(f2.getName());
                    }        
                });
		
		for (Group group : groups) {
			String name = group.getName();
			config.set(GROUPS + "." + name + "." + PERMISSIONS, group.serialisePermissions());
			config.set(GROUPS + "." + name + "." + GROUPS, group.serialiseGroups());
		}

		config.save(file);
	}

}
