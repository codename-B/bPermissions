package com.ubempire.permissions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class PermissionReader {
	private String PlayerName;
	private String WorldName;
	private Permissions Permissions;
	private List<String> PlayerPermissions;
	private String PlayerGroup;
	private String GroupFileName;

	PermissionReader(Player Player, Permissions Permissions) {
		this.PlayerName = Player.getName();
		this.WorldName = Player.getWorld().getName();
		this.Permissions = Permissions;
		this.PlayerPermissions = null;
		this.PlayerGroup = Permissions.defaultGroup;
	}

	/**
	 * This should be called first. Reads the corresponding world.yml files.
	 */
	public void readNodes() {
		File groupFile = new File(Permissions.dataFolder + "groups/"
				+ WorldName + ".yml");
		File playerFile = new File(Permissions.dataFolder + "players/"
				+ WorldName + ".yml");
		if (!(groupFile.exists() || playerFile.exists())) {
			groupFile = new File(Permissions.dataFolder + "defaultGroups.yml");
			playerFile = new File(Permissions.dataFolder + "defaultPlayers.yml");
			if (!(groupFile.exists() || playerFile.exists())) {
				groupFile.getParentFile().mkdirs();
				playerFile.getParentFile().mkdirs();
				try {
					groupFile.createNewFile();
					playerFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Configuration groupConfig = new Configuration(groupFile);
		Configuration playerConfig = new Configuration(playerFile);
		groupConfig.load();
		playerConfig.load();
		String playerGroup = playerConfig.getString(PlayerName,
				null);
		if(playerGroup==null)
		{
		playerConfig.setProperty(PlayerName, Permissions.defaultGroup);
		playerConfig.save();
		playerGroup = playerConfig.getString(PlayerName,
				Permissions.defaultGroup);
		
		}
		List<String> PlayerPermissions = groupConfig.getStringList(playerGroup,
				null);
		this.PlayerPermissions = PlayerPermissions;
		this.PlayerGroup = playerGroup;
		this.GroupFileName = groupFile.getName();
	}

	/**
	 * This should be called second. It will return the corresponding nodes
	 * 
	 * @return List<String> PlayerPermissions
	 */
	public List<String> getNodes() {
		return PlayerPermissions;
	}

	public String getGroup() {
		return PlayerGroup;
	}

	public String getPlayer() {
		return PlayerName;
	}

	public String getWorld() {
		return WorldName;
	}
	
	public String getGroupFileName()
	{
	return GroupFileName;
	}
}
