package de.bananaco.permissions.tracks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class Tracks {
	
	private Configuration c;
	private WorldPermissionsManager wpm;
	private Permissions p;
	private List<String> tracks;
	
	private final String defaultPromoteMessage = "You have been promoted!";
	private final String defaultDemoteMessage = "You have been demoted!";
	
	public Tracks(Permissions p) {
		this.p = p;
		this.c = new Configuration(new File("plugins/bPermissions/tracks.yml"));
		this.wpm = Permissions.getWorldPermissionsManager();
		c.load();
		if(isEmpty())
			setupDefaults();
		c.save();
		for(String permission : getPermissions())
			p.getServer().getPluginManager().addPermission(new Permission(permission, PermissionDefault.OP));
	}
	
	private boolean isEmpty() {
		if(c.getKeys() == null)
			return true;
		if(c.getKeys().size() == 0)
			return true;
		return false;
	}
	
	private List<String> getPermissions() {
		List<String> permissions = new ArrayList<String>();
		List<String> keys = c.getKeys("tracks");
		if(keys != null)
			for(String key : keys) {
				permissions.add("bPermissions.promote."+key);
				permissions.add("bPermissions.demote."+key);
			}
		if(keys != null)
			this.tracks = keys;
		else
			this.tracks = new ArrayList<String>();
		return permissions;
	}
	
	private void setupDefaults() {
		String[] worlds;
		int i = p.getServer().getWorlds().size();
		worlds = new String[i];
		for(int m=0; m<i; m++)
		worlds[m] = p.getServer().getWorlds().get(m).getName();
		
		addTrack("admin",Arrays.asList("moderator","admin"), Arrays.asList(worlds), "You are now an admin boi!", "Not so fast :(");
		addTrack("moderator",Arrays.asList("moderator"), Arrays.asList(worlds), "You have been promoted to moderator!", "Bad boy!");
	}
	
	public boolean promote(String player, String track) {
		// Return false if nothing will happen
		if(!tracks.contains(track))
			return false;
		// The groups
		List<String> groups = c.getStringList("tracks."+track+".groups", new ArrayList<String>());
		List<String> worldStrings = c.getStringList("tracks."+track+".worlds", new ArrayList<String>());		
		// The worlds
		List<World> worlds = new ArrayList<World>();
		// Return false if nothing will happen
		if(groups.size() == 0 || worldStrings.size() == 0)
			return false;
		// Setting up the actual worlds
		for(String world : worldStrings)
			if(p.getServer().getWorld(world) != null)
				worlds.add(p.getServer().getWorld(world));
		// The message		
		String message = c.getString("tracks."+track+".promote-message", defaultDemoteMessage);
		// Sending the message
		Player pl = p.getServer().getPlayer(player);
		if(pl != null) {
		pl.sendMessage(ChatColor.GREEN+message);	
		player = pl.getName();
		}
		// Doing the groups
		for(World world : worlds) {
			PermissionSet ps = wpm.getPermissionSet(world);
			for(String group : groups)
				ps.addGroup(player, group);
		}
		return true;
	}
	
	public boolean demote(String player, String track) {
		// Return false if nothing will happen
		if(!tracks.contains(track))
			return false;
		// The groups
		List<String> groups = c.getStringList("tracks."+track+".groups", new ArrayList<String>());
		List<String> worldStrings = c.getStringList("tracks."+track+".worlds", new ArrayList<String>());		
		// The worlds
		List<World> worlds = new ArrayList<World>();
		// Return false if nothing will happen
		if(groups.size() == 0 || worldStrings.size() == 0)
			return false;
		// Settings up the actual worlds
		for(String world : worldStrings)
			if(p.getServer().getWorld(world) != null)
				worlds.add(p.getServer().getWorld(world));
		// The message		
		String message = c.getString("tracks."+track+".promote-message", defaultPromoteMessage);
		// Sending the message
		Player pl = p.getServer().getPlayer(player);
		if(pl != null) {
		pl.sendMessage(ChatColor.RED+message);	
		player = pl.getName();
		}
		// Doing the groups
		for(World world : worlds) {
			PermissionSet ps = wpm.getPermissionSet(world);
			for(String group : groups)
				ps.removeGroup(player, group);
		}
		return true;
	}
	
	public void addTrack(String trackname, List<String> groups, List<String> worlds, String promoteMessage, String demoteMessage) {
		c.setProperty("tracks."+trackname+".groups", groups);
		c.setProperty("tracks."+trackname+".worlds", worlds);
		c.setProperty("tracks."+trackname+".promote-message", promoteMessage);
		c.setProperty("tracks."+trackname+".demote-message", demoteMessage);
		c.save();
	}
}
