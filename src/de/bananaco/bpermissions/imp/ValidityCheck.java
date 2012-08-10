package de.bananaco.bpermissions.imp;

import java.util.List;

import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class ValidityCheck {
	
	WorldManager wm = WorldManager.getInstance();
	
	boolean isValid = false;
	
	public ValidityCheck(Player promoter, PromotionTrack track, String tName, String player, String world) {
		List<String> groups = track.getGroups(tName);
		User u = wm.getWorld(world).getUser(player);
		boolean contains = false;
		// are any of the players groups this group?
		for(String g : u.getGroupsAsString()) {
			if(contains(g, groups)) {
				contains = true;
			}
		}
		boolean priorities = true;
		// now the priorities checker
		if(promoter != null) {
			User p = wm.getWorld(world).getUser(promoter.getName());
			Debugger.log(u.getName()+":"+u.getPriority());
			Debugger.log(p.getName()+":"+p.getPriority());
			if(p.getPriority() <= u.getPriority()) {
				priorities = false;
			}
		}
		isValid = contains && priorities;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	private boolean contains(String name, List<String> groups) {
		for(String g : groups) {
			if(g.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

}
