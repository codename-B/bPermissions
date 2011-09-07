package de.bananaco.permissions.info;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import com.ubempire.binfo.PlayerInfo;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class InfoReader implements PlayerInfo {
	
	private WorldPermissionsManager wpm;
	
	public InfoReader() {
	}
	
	public void instantiate() {
		wpm = Permissions.getWorldPermissionsManager();
	}
	
	public String getGroupPrefix(String group, String world) {
		String prefix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(world).getGroupNodes(group)) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("prefix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	prefix = index[2];
	        }
	        }
		}
		return prefix;
	}
	
	public String getGroupSuffix(String group, String world) {
		String suffix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(world).getGroupNodes(group)) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("suffix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	suffix = index[2];
	        }
	        }
		}
		return suffix;
	}
	
	public String getPrefix(Player player) {
		String prefix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(player.getWorld()).getPlayerNodes(player) != null ? wpm.getPermissionSet(player.getWorld()).getPlayerNodes(player) : new ArrayList<String>()) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("prefix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	prefix = index[2];
	        }
	        }
		}
		return prefix;
	}
	
	public String getSuffix(Player player) {
		String suffix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(player.getWorld()).getPlayerNodes(player) != null ? wpm.getPermissionSet(player.getWorld()).getPlayerNodes(player) : new ArrayList<String>()) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("suffix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	suffix = index[2];
	        }
	        }
		}
		return suffix;
	}
	
	public String getPrefix(String player, String world) {
		String prefix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(world).getPlayerNodes(player) != null ? wpm.getPermissionSet(world).getPlayerNodes(player) : new ArrayList<String>()) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("prefix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	prefix = index[2];
	        }
	        }
		}
		return prefix;
	}
	
	public String getSuffix(String player, String world) {
		String suffix = "";
		int priority = -1;
		for(String set : wpm.getPermissionSet(world).getPlayerNodes(player) != null ? wpm.getPermissionSet(world).getPlayerNodes(player) : new ArrayList<String>()) {
			if(!set.startsWith("^")) {
			String name = set;
	        String[] index = name.split("\\.", 4);
	        if(index.length==3 && index[0].equals("suffix")) {
	        	int pr = Integer.parseInt(index[1]);
	        	if(pr > priority)
	        	suffix = index[2];
	        }
	        }
		}
		return suffix;
	}

}
