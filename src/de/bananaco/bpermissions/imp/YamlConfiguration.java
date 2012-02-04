package de.bananaco.bpermissions.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
/**
 * This preparses the yaml and fixes common errors before loading the file
 */
public class YamlConfiguration extends org.bukkit.configuration.file.YamlConfiguration{

	@Override
	public void load(File file) throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		new YamlFile(file, true);
		super.load(file);
	}

}
