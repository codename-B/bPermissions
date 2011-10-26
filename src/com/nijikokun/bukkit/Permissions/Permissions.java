package com.nijikokun.bukkit.Permissions;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginDescriptionFile;

import com.nijiko.permissions.PermissionHandler;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Legacy Permissions compatibility layer
 */
/**
 * Permissions 2.x Copyright (C) 2011 Matt 'The Yeti' Burnett
 * <admin@theyeticave.net> Original Credit & Copyright (C) 2010 Nijikokun
 * <nijikokun@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Permissions Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Permissions Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Permissions Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class Permissions extends JavaPlugin {

	public static Plugin instance = null;
	public static PermissionHandler Security;

	public static String version = "2.7.2";

	public Permissions() {
		super();
		Permissions.instance = this;
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

	@Override
	public void onLoad() {
		Security = this.getHandler();
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
		Security = null;
	}

	public PermissionHandler getHandler() {
		if (Security == null) {
			Security = new de.bananaco.permissions.PermissionBridge();
		}
		return Security;
	}
}
