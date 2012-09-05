package de.bananaco.bpermissions.imp;

import java.io.File;
/**
 * This creates and propagates the default users.yml and groups.yml
 * 
 * This will be filled on the command /permissions helpme (yes, we brought it back)
 */
public class DefaultWorld extends YamlWorld {
	
	public DefaultWorld(Permissions permissions) {
		super("*", permissions, new File("plugins/bPermissions/"));
	}

}
