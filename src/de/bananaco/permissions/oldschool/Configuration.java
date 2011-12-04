package de.bananaco.permissions.oldschool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Substitute for the old Bukkit configuration object Replicates the old
 * functionality in the new object
 * 
 * @author codename_B
 * 
 */
@SuppressWarnings("unchecked")
public class Configuration extends YamlConfiguration {
	private final File file;

	/**
	 * A substitute for getConfiguration();
	 * 
	 * @author desht
	 * @param plugin
	 */
	public Configuration(JavaPlugin plugin) {
		this(new File(plugin.getDataFolder(), "config.yml"));
		load();
	}

	public Configuration(String fileName) {
		this(new File(fileName));
	}

	public Configuration(File file) {
		super();
		if (file == null)
			System.err.println("File should not be null!");
		this.file = file;
	}

	public void load() {
		try {
			// First do checks to create the initial file
			if (!file.exists()) {
				if (file.getParentFile() != null)
					file.getParentFile().mkdirs();
				if (file.createNewFile())
					super.save(file);
				else
					throw new Exception("Cannot load: File can not be created!");
			}
			// Then do checks to save the file
			super.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			if (!file.exists()) {
				if (file.getParentFile() != null)
					file.getParentFile().mkdirs();
				if (file.createNewFile())
					super.save(file);
				else
					throw new Exception("Cannot save: File can not be created!");
			} else
				super.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getKeys() {
		Set<String> keys = super.getKeys(false);
		List<String> lkeys = new ArrayList<String>();
		if (keys != null)
			for (String key : keys)
				lkeys.add(key);
		return lkeys;
	}

	public List<String> getKeys(String path) {
		List<String> lkeys = new ArrayList<String>();
		ConfigurationSection cs = super.getConfigurationSection(path);

		if (cs == null)
			return lkeys;

		Set<String> keys = cs.getKeys(false);

		if (keys != null)
			for (String key : keys)
				lkeys.add(key);
		return lkeys;
	}

	public void setProperty(String path, Object object) {
		super.set(path, object);
	}

	/**
	 * Should work according to the javadocs, but doesn't. Fixed in the latest
	 * bukkit builds
	 * 
	 * @param path
	 */
	public void removeProperty(String path) {
		try {
			super.set(path, null);
		} catch (Exception e) {
		}
	}

	public List<String> getStringList(String path, List<String> def) {
		load();
		if (def == null)
			def = new ArrayList<String>();

		List<?> list = super.getList(path, def);
		if (list == null)
			return def;
		try {
			List<String> sList = (List<String>) list;
			return sList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

}
