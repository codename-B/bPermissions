package de.bananaco.permissions.worlds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.util.Group;
import de.bananaco.permissions.util.Permission;
import de.bananaco.permissions.util.User;

public class YamlPermissionSet extends WorldPermissions {

	private final YamlConfiguration config = new YamlConfiguration();
	private final File file = new File("plugins/bPermissions/" + getWorldName()
			+ ".yml");

	private static final String PERMISSIONS = "permissions";
	private static final String GROUPS = "groups";
	private static final String USERS = "users";

	public YamlPermissionSet(World world, Permissions plugin) {
		super(world, plugin);
	}

	public void load() {
		try {
			loadUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			saveUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveUnsafe() throws Exception {

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		Set<User> users = getUsers();

		for (User user : users) {
			String name = user.getName();
			config.set(USERS + "." + name + "." + PERMISSIONS, new ArrayList(
					user.getPermissionsAsString()));
			config.set(USERS + "." + name + "." + GROUPS,
					new ArrayList(user.getGroupsAsString()));
		}

		Set<Group> groups = getGroups();

		for (Group group : groups) {
			String name = group.getName();
			config.set(GROUPS + "." + name + "." + PERMISSIONS, new ArrayList(
					group.getPermissionsAsString()));
			config.set(GROUPS + "." + name + "." + GROUPS,
					new ArrayList(group.getGroupsAsString()));
		}

		config.save(file);
	}

	private void loadUnsafe() throws Exception {

		if (!file.exists()) {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}

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
			user.calculateEffectivePermissions();
		}

		for (Group group : getGroups()) {
			group.calculateEffectivePermissions();
		}

	}

	@Override
	public void reload() {
		load();
		save();
	}

	@Override
	public String getDefaultGroup() {
		return config.getString("default", "default");
	}

}
