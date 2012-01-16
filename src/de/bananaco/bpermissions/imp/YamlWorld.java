package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MetaData;
import de.bananaco.bpermissions.api.util.Permission;
/**
 * Here is the main YamlWorld class
 * This loads from the default users.yml and groups.yml on first
 * creation. Isn't it pretty?
 */
public class YamlWorld extends World {

	private static final String GROUPS = "groups";
	private static final String PERMISSIONS = "permissions";
	private static final String META = "meta";
	private static final String USERS = "users";
	
	private YamlConfiguration uconfig = new YamlConfiguration();;
	private YamlConfiguration gconfig = new YamlConfiguration();;
	
	private final File ufile = new File("plugins/bPermissions/" + getName()
			+ "/users.yml");
	private final File gfile = new File("plugins/bPermissions/" + getName()
			+ "/groups.yml");
	
	private final File dufile = new File("plugins/bPermissions/users.yml");
	private final File dgfile = new File("plugins/bPermissions/groups.yml");

	private final Permissions permissions;
	
	private final WorldManager wm = WorldManager.getInstance();
	
	public YamlWorld(String world, Permissions permissions) {
		super(world);
		this.permissions = permissions;
	}
	
	/**
	 * Internal method - used to read defaults from "global" users.yml and
	 * groups.yml if the files are created for the first time.
	 * 
	 * @return boolean
	 */
	private boolean readDefaults() {
		try {
			if (!dufile.exists()) {
				if (dufile.getParentFile() != null)
					dufile.getParentFile().mkdirs();
				dufile.createNewFile();
				dgfile.createNewFile();
			}
			uconfig.load(dufile);
			gconfig.load(dgfile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getDefaultGroup() {
		if(gconfig != null)
		return gconfig.getString("default", "default");
		return "default";
	}

	public boolean load() {
		try {
			// TODO check and see how much this bugs out
			// **THIS SHOULD NOW IMPLEMENT CHANGES WITHOUT THE LAG**
			clear();
			loadUnsafe();
			permissions.getServer().getPluginManager().callEvent(new WorldLoadedEvent(this));
			// TODO check and see that players are still setup
			//permissions.handler.setupAllPlayers();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadUnsafe() throws Exception {
		boolean autoSave = wm.getAutoSave();
		wm.setAutoSave(false);
		if (!ufile.exists()) {
			if (ufile.getParentFile() != null)
				ufile.getParentFile().mkdirs();
			ufile.createNewFile();
			gfile.createNewFile();
			// Let people know if something goes wrong with reading the defaults
			if (!readDefaults()) {
				System.err
						.println("Error reading default users.yml and groups.yml");
				System.err
						.println("Please report this to codename_B immediately");
			}
		} else {
			uconfig = new YamlConfiguration();
			gconfig = new YamlConfiguration();

			uconfig.load(ufile);
			gconfig.load(gfile);
		}
		/*
		 * Load the users
		 */
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
				User user = new User(name, nGroup, perms, getName(), this);
				// MetaData
				ConfigurationSection meta = usersConfig
						.getConfigurationSection(name + "." + META);
				if(meta != null) {
				Set<String> keys = meta.getKeys(false);
				if (keys != null && keys.size() > 0)
					for (String key : keys)
						user.setValue(key, meta.get(key).toString());
				}
				// Upload to API
				add(user);
			}
		}
		/*
		 * Load the groups
		 */
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
				Group group = new Group(name, nGroup, perms, getName(), this);
				// MetaData
				ConfigurationSection meta = groupsConfig
						.getConfigurationSection(name + "." + META);
				if(meta != null) {
				Set<String> keys = meta.getKeys(false);
				if (keys != null && keys.size() > 0)
					for (String key : keys)
						group.setValue(key, meta.get(key).toString());
				}
				// Upload to API
				add(group);
							
			}
		}

		//for (Calculable user : getAll(CalculableType.USER)) {
			//try {
			//	user.calculateEffectivePermissions();
				//user.calculateEffectiveMeta();
			//} catch (RecursiveGroupException e) {
			//	System.err.println(e.getMessage());
			//}
		//}
		for(Player player : this.permissions.getServer().getOnlinePlayers()) {
			String name = player.getName();
			String world = player.getWorld().getName();
			if(wm.getWorld(world) == this) {
				getUser(name).calculateEffectivePermissions();
			}
		}
		wm.setAutoSave(autoSave);
	}

	public boolean save() {
		try {
			saveUnsafe();
			load();
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

		Set<Calculable> usr = getAll(CalculableType.USER);
		// Sort them :D
		List<Calculable> users = new ArrayList<Calculable>(usr);
		MetaData.sort(users);
		
		for (Calculable user : users) {
			String name = user.getName();
			uconfig.set(USERS + "." + name + "." + PERMISSIONS, user.serialisePermissions());
			uconfig.set(USERS + "." + name + "." + GROUPS, user.serialiseGroups());
			// MetaData
			Map<String, String> meta = user.getMeta();
			if(meta.size() > 0)
				for(String key : meta.keySet())
					uconfig.set(USERS + "." + name + "." + META + "." + key, meta.get(key));
		}

		Set<Calculable> grp = getAll(CalculableType.GROUP);
		// Sort them :D
		List<Calculable> groups = new ArrayList<Calculable>(grp);
		MetaData.sort(groups);
		
		for (Calculable group : groups) {
			String name = group.getName();
			gconfig.set(GROUPS + "." + name + "." + PERMISSIONS, group.serialisePermissions());
			gconfig.set(GROUPS + "." + name + "." + GROUPS, group.serialiseGroups());
			// MetaData
			Map<String, String> meta = group.getMeta();
			if(meta.size() > 0)
				for(String key : meta.keySet())
					gconfig.set(GROUPS + "." + name + "." + META + "." + key, meta.get(key));
		}
		
		uconfig.save(ufile);
		gconfig.save(gfile);
	}

	@Override
	public boolean setupPlayer(String player) {
		permissions.handler.setupPlayer(player);
		return true;
	}

}
