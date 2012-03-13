package de.bananaco.bpermissions.spout;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.spout.api.util.config.Configuration;
/**
 * This preparses the yaml and fixes common errors before loading the file
 */
public class YamlConfiguration {

	private Configuration config = null;
	
	public YamlConfiguration() {

	}
	
	public YamlConfiguration(File file) {
		config = new Configuration(file);
	}

	public Object get(String string, Object defaultValue) {
		Object value = config.getValue(string);
		if(value == null)
			return defaultValue;
		return value;
	}

	public void set(String string, Object value) {
		config.setValue(string, value);
	}
	
	public void load(File file) {
		config = new Configuration(file);
		config.load();
	}
	
	public void save(File file) throws Exception {
		if(!file.exists()) {
			file.mkdirs();
			file.createNewFile();
		}
		config.save();
	}

	public String getString(String string, String string2) {
		return config.getString(string, string2);
	}

	public Object getNode(String path) {
		return config.getNode(path, null);
	}
	
	public Set<String> getKeys(String path) {
		return config.getKeys(path);
	}

	public List<String> getStringList(String string) {
		return config.getStringList(string);
	}

	public Object get(String key) {
		return config.getValue(key);
	}

	public String getString(String key) {
		return config.getString(key);
	}

	public boolean getBoolean(String string, boolean def) {
		return config.getBoolean(string, def);
	}

	public boolean getBoolean(String string) {
		return config.getBoolean(string);
	}
}
