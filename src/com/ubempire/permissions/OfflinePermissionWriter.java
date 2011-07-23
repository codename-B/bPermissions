package com.ubempire.permissions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.util.config.Configuration;

public class OfflinePermissionWriter {
	private String PlayerName;
	private String WorldName;
	private Permissions Permissions;
	private Configuration GroupConfig;
	private Configuration PlayerConfig;
	private String GroupFileName;
	private String GroupName=null;

	OfflinePermissionWriter(String PlayerName, String WorldName, Permissions Permissions) {
		this.PlayerName = PlayerName;
		this.WorldName = WorldName;
		this.Permissions = Permissions;
	}
	
	OfflinePermissionWriter(boolean nothing, String GroupName, String WorldName, Permissions Permissions) {
		this.WorldName = WorldName;
		this.GroupName = GroupName;
		this.Permissions = Permissions;
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
		if(GroupName==null)
		{
		String playerGroup = playerConfig.getString(PlayerName,
				null);
		if(playerGroup==null)
		{
		playerConfig.setProperty(PlayerName, Permissions.defaultGroup);
		playerConfig.save();
		playerGroup = playerConfig.getString(PlayerName,
				Permissions.defaultGroup);
		
		}
		}
		this.GroupConfig = groupConfig;
		this.PlayerConfig = playerConfig;
		this.GroupFileName = groupFile.getName();
	}

	public void setGroup(String Group) {
		PlayerConfig.setProperty(PlayerName, Group);
	}
	public List<String> getNodes()
	{
	if(GroupName==null)
	return null;
	return GroupConfig.getStringList(GroupName, null);
		
	}
	public void addNode(String node) {
		if(GroupName==null)
			return;
		List<String> gNodes = GroupConfig.getStringList(GroupName, null);
		gNodes.add(node);
		GroupConfig.setProperty(GroupName, gNodes);
		GroupConfig.save();
	}

	public void removeNode(String node) {
		if(GroupName==null)
			return;
		List<String> gNodes = GroupConfig.getStringList(GroupName, null);
		if (gNodes.contains(node))
			gNodes.remove(node);
		GroupConfig.setProperty(GroupName, gNodes);
		GroupConfig.save();
	}
	
	public String getGroupFileName()
	{
	return GroupFileName;
	}

	public void save() {
		GroupConfig.save();
		PlayerConfig.save();
	}

}
