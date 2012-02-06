package de.bananaco.bpermissions.imp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class WorldChecker implements Runnable {
	
	private final Server server;
	private final SuperPermissionHandler handler;
	
	private Map<Integer, String> worlds = new HashMap<Integer, String>();
	
	protected WorldChecker(Server server, SuperPermissionHandler handler) {
		this.server = server;
		this.handler = handler;
	}
	
	private void update() {
		Player[] players = server.getOnlinePlayers().clone();
		for(Player player : players) {
			if(needsUpdating(player)) {
				updateEntry(player);
				handler.setupPlayer(player.getName());
				Debugger.log(player.getName()+" updated via the WorldChecker sync task");
			}
		}
	}
	
	private boolean hasEntry(Player player) {
		return worlds.containsKey(player.hashCode());
	}
	
	private boolean needsUpdating(Player player) {
		if(!player.isPermissionSet("world."+player.getWorld().getName())) {
			Debugger.log("Wrong world set!");
			return true;
		}
		if(!hasEntry(player)) {
			Debugger.log("First entry created!");
			return true;
		}
		if(getEntry(player).equals(player.getWorld().getName())) {
			return false;
		}
		Debugger.log("Unknown error!");
		return true;
	}
	
	private String getEntry(Player player) {
		return worlds.get(player.hashCode());
	}
	
	private void updateEntry(Player player) {
		worlds.put(player.hashCode(), player.getWorld().getName());
	}

	@Override
	public void run() {
		update();
	}

}
