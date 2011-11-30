package de.bananaco.permissions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import com.sk89q.bukkit.migration.PermissionsProvider;

import de.bananaco.permissions.debug.Debugger;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.HasPermission;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class WorldGuardProvider extends JavaPlugin implements PermissionsProvider {

	private Server server;
	private WorldPermissionsManager wpm;
	
	public static Plugin instance = null;

	public static String version = "1.6";
	
	@SuppressWarnings("unchecked")
	private static <T> T getField(Object object, String fieldName) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Loads the fake Permissions plugin for plugins which use
	 * getPlugin("Permissions")
	 */
	protected static void loadPseudoPlugin(Permissions parent,
			ClassLoader classLoader) {
		// create a pseudo-Permissions plugin as compatibility layer
		JavaPluginLoader pluginLoader = (JavaPluginLoader) parent
				.getPluginLoader();
		PluginClassLoader pluginClassLoader = new PluginClassLoader(
				pluginLoader, new java.net.URL[] {}, classLoader.getParent());
		String version = de.bananaco.permissions.WorldGuardProvider.version;
		PluginDescriptionFile description = new PluginDescriptionFile(
				"bPermissionsWorldGuardBridge", version,
				"de.bananaco.permissions.WorldGuardProvider");

		WorldGuardProvider provider = new WorldGuardProvider();
		provider.doInitialize(pluginLoader, Bukkit.getServer(), description,
				null, null, pluginClassLoader);
		try {
			provider.onLoad();
		} catch (RuntimeException e) {
			Logger.getLogger("Minecraft")
					.warning(
							"["
									+ parent.getDescription().getName()
									+ "] Exception attempting to initialize compatibility layer");
		}

		// use reflection to add the pseudo-Permissions plugin to the
		// PluginManager
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		List<Plugin> plugins = getField(pluginManager, "plugins");
		Map<String, Plugin> lookupNames = getField(pluginManager, "lookupNames");
		plugins.add(provider);
		lookupNames.put(description.getName(), provider);
	}

	public WorldGuardProvider() {
		super();
		WorldGuardProvider.instance = this;
	}

	public void doInitialize(PluginLoader pluginLoader, Server server,
			PluginDescriptionFile pdf, File file1, File file2,
			ClassLoader classLoader) {
		initialize(pluginLoader, server, pdf, file1, file2, classLoader);
	}

	public static Plugin getInstance() {
		if (instance == null) {
			instance = new Permissions();
		}
		return instance;
	}

	public void inst(Server server, WorldPermissionsManager wpm) {
		this.server = server;
		this.wpm = wpm;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		server = Bukkit.getServer();
		inst(Bukkit.getServer(), Permissions.getWorldPermissionsManager());
		Debugger.getDebugger().log("WorldGuard bridge enabled");
	}
	
	@Override
	public String[] getGroups(String player) {
		PermissionSet ps;
		// Still getting that wierd npe :/
		if(server == null)
			server = Bukkit.getServer();
		// STILL getting that wierd npe :/ :/
		if(wpm == null)
			wpm = Permissions.getWorldPermissionsManager();
		if(server.getPlayer(player) != null) {
			ps = wpm.getPermissionSet(server.getPlayer(player).getWorld());
		} else {
			ps = wpm.getPermissionSet(server.getWorlds().get(0));
		}
		List<String> groups = ps.getGroups(player);
		String[] sg = groups.toArray(new String[groups.size()]);
		return sg;
	}

	@Override
	public boolean hasPermission(String player, String permission) {
		if(server.getPlayer(player) != null) {
			return server.getPlayer(player).hasPermission(permission);
		}
		return HasPermission.has(player, server.getWorlds().get(0).getName(), permission);
	}

	@Override
	public boolean hasPermission(String player, String world, String permission) {
		if(server.getPlayer(player) != null && server.getPlayer(player).getWorld().getName().equals(world)) {
			return server.getPlayer(player).hasPermission(permission);
		}
		return HasPermission.has(player, server.getWorlds().get(0).getName(), permission);
	}

	@Override
	public boolean inGroup(String player, String group) {
		String[] groups = getGroups(player);
		for(String gr : groups) {
			if(gr.equals(group)) {
				return true;
			}
		}
		return false;
	}


}
