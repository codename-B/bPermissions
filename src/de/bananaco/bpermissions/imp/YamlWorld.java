package de.bananaco.bpermissions.imp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.Calculable;
import de.bananaco.bpermissions.api.CalculableType;
import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.MetaData;
import de.bananaco.bpermissions.api.Permission;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
/**
 * Here is the main YamlWorld class
 * This loads from the default users.yml and groups.yml on first
 * creation. Isn't it pretty?
 */
public class YamlWorld extends World {

	protected static final String GROUPS = "groups";
	protected static final String PERMISSIONS = "permissions";
	protected static final String META = "meta";
	protected static final String USERS = "users";

	protected YamlConfiguration uconfig = new YamlConfiguration();
	protected YamlConfiguration gconfig = new YamlConfiguration();

	private final File ufile;
	private final File gfile;

	protected final Permissions permissions;

	protected final WorldManager wm = WorldManager.getInstance();

	// If there's an error loading the files, don't save them as it overrides them!
	protected boolean error = false;
	// Only save if flagged true
	protected boolean save = false;

	public YamlWorld(String world, Permissions permissions, File root) {
		super(world);
		this.permissions = permissions;
		this.ufile = new File(root,"users.yml");
		this.gfile = new File(root,"groups.yml");
	}

	@Override
	public String getDefaultGroup() {
		if(gconfig != null)
			return gconfig.getString("default", "default");
		return "default";
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		uconfig = new YamlConfiguration();
		gconfig = new YamlConfiguration();
		try {
			saveUnsafe(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean load() {
		try {
			clear();
			loadUnsafe();
			// If it loaded correctly cancel the error
			error = false;
		} catch (Exception e) {
			error = true;
			Bukkit.getServer().broadcastMessage(ChatColor.RED+"Permissions for world:"+getName()+" did not load correctly! Please consult server.log");
			e.printStackTrace();
		}
		return true;
	}

	protected synchronized void loadUnsafe() throws Exception {
		boolean autoSave = wm.getAutoSave();
		wm.setAutoSave(false);
		if (!ufile.exists()) {
			if (ufile.getParentFile() != null)
				ufile.getParentFile().mkdirs();
			ufile.createNewFile();
			gfile.createNewFile();
		}
		uconfig = new YamlConfiguration();
		gconfig = new YamlConfiguration();
		
		long t = System.currentTimeMillis();
		uconfig.load(ufile);
		gconfig.load(gfile);
		long f = System.currentTimeMillis();
		Debugger.log("Loading files took "+(f-t)+"ms");

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
					if (keys != null && keys.size() > 0) {
						for (String key : keys) {
							user.setValue(key, meta.get(key).toString());
						}
					}
				}
				// Upload to API
				add(user);
			}
		} else {
			Debugger.log("Empty ConfigurationSection:"+USERS+":"+ufile.getPath());
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
		} else {
			Debugger.log("Empty ConfigurationSection:"+GROUPS+":"+gfile.getPath());
		}

		Debugger.log(this.getAll(CalculableType.USER).size()+" users loaded.");
		Debugger.log(this.getAll(CalculableType.GROUP).size()+" groups loaded.");

		for(Player player : this.permissions.getServer().getOnlinePlayers()) {
			String name = player.getName();
			String world = player.getWorld().getName();
			if(wm.getWorld(world) == this) {
				getUser(name).calculateEffectivePermissions();
				getUser(name).calculateEffectiveMeta();
			}
		}
		wm.setAutoSave(autoSave);
	}

	public boolean save() {
		if(error) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED+"Permissions for world:"+this.getName()+" did not load correctly, please consult server.log.");
			return false;
		}
		save = true;
		// no longer async, sorry!
		try {
			saveUnsafe(false);
			save = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	protected void saveUnsafe(boolean sort) throws Exception {
		if (!ufile.exists()) {
			ufile.getParentFile().mkdirs();
			ufile.createNewFile();
			gfile.createNewFile();
		}

		String def = getDefaultGroup();
		gconfig.set("default", def);

		Set<Calculable> usr = getAll(CalculableType.USER);
		Debugger.log(usr.size()+" users saved.");
		// Sort them :D
		List<Calculable> users = new ArrayList<Calculable>(usr);
		if(sort)
			MetaData.sort(users);

		for (Calculable user : users) {
			String name = user.getName();
			uconfig.set(USERS + "." + name + "." + PERMISSIONS, user.serialisePermissions());
			uconfig.set(USERS + "." + name + "." + GROUPS, user.serialiseGroups());
			// MetaData
			Map<String, String> meta = user.getMeta();
			if(meta.size() > 0) {
				for(String key : meta.keySet()) {
					uconfig.set(USERS + "." + name + "." + META + "." + key, meta.get(key));
				}
			}
		}

		Set<Calculable> grp = getAll(CalculableType.GROUP);
		Debugger.log(grp.size()+" groups saved.");
		// Sort them :D
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Group> groups = new ArrayList(grp);
		if(sort)
			MetaData.sort(groups);

		for (Calculable group : groups) {
			String name = group.getName();
			gconfig.set(GROUPS + "." + name + "." + PERMISSIONS, group.serialisePermissions());
			gconfig.set(GROUPS + "." + name + "." + GROUPS, group.serialiseGroups());
			// MetaData
			Map<String, String> meta = group.getMeta();
			if(meta.size() > 0) {
				for(String key : meta.keySet()) {
					gconfig.set(GROUPS + "." + name + "." + META + "." + key, meta.get(key));
				}
			}
		}

		long t = System.currentTimeMillis();
		uconfig.save(ufile);
		gconfig.save(gfile);
		long f = System.currentTimeMillis();
		Debugger.log("Saving files took "+(f-t)+"ms");
	}

	@Override
	public boolean setupAll() {
		Player[] players = Bukkit.getOnlinePlayers();
		for(Player player : players) {
			setupPlayer(player.getName());
		}
		// return true for success
		return true;
	}

	@Override
	public boolean isOnline(User user) {
		return Bukkit.getPlayer(user.getName()) != null;
	}

	@Override
	public boolean setupPlayer(String player) {
		permissions.handler.setupPlayer(player);
		return true;
	}

	@Override
	public void setDefaultGroup(String group) {
		gconfig.set("default", group);
		try {
			gconfig.save(gfile);
		} catch (IOException e) {
		}
	}

}
