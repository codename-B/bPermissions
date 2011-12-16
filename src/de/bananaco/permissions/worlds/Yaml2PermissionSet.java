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

public class Yaml2PermissionSet extends WorldPermissions {

	private static final String GROUPS = "groups";
	private static final String PERMISSIONS = "permissions";

	private static final String USERS = "users";
	
	private YamlConfiguration uconfig = new YamlConfiguration();
	private YamlConfiguration gconfig = new YamlConfiguration();
	
	private final File ufile = new File("plugins/bPermissions/" + getWorldName()
			+ "/users.yml");
	private final File gfile = new File("plugins/bPermissions/" + getWorldName()
			+ "/groups.yml");

	public Yaml2PermissionSet(World world, Permissions plugin) {
		super(world, plugin);
	}

	@Override
	public String getDefaultGroup() {
		if(gconfig != null)
		return gconfig.getString("default", "default");
		return "default";
	}

	public boolean load() {
		try {
			loadUnsafe();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadUnsafe() throws Exception {

		if (!ufile.exists()) {
			if (ufile.getParentFile() != null)
				ufile.getParentFile().mkdirs();
			ufile.createNewFile();
			gfile.createNewFile();
		}
		uconfig = new YamlConfiguration();
		gconfig = new YamlConfiguration();
		
		uconfig.load(ufile);
		gconfig.load(gfile);

		ConfigurationSection usersConfig = uconfig
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

		ConfigurationSection groupsConfig = gconfig
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

	public boolean save() {
		try {
			saveUnsafe();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void saveUnsafe() throws Exception {

		if (!ufile.exists()) {
			ufile.getParentFile().mkdirs();
			ufile.createNewFile();
			gfile.createNewFile();
		}
		String def = getDefaultGroup();
		
		uconfig = new YamlConfiguration();
		gconfig = new YamlConfiguration();
		
		gconfig.set("default", def);

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
			uconfig.set(USERS + "." + name + "." + PERMISSIONS, user.serialisePermissions());
			uconfig.set(USERS + "." + name + "." + GROUPS, user.serialiseGroups());
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
			gconfig.set(GROUPS + "." + name + "." + PERMISSIONS, group.serialisePermissions());
			gconfig.set(GROUPS + "." + name + "." + GROUPS, group.serialiseGroups());
		}
		
		uconfig.save(ufile);
		gconfig.save(gfile);
	}

}
