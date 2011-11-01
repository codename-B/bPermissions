package de.bananaco.permissions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class WorldChecker {

	private final Runnable run;
	
	public WorldChecker(final Server server) {
		this.run = new Runnable() {
			public void run() {
				Player[] players = server.getOnlinePlayers();
				for(Player player : players) {
					if(check(player)) {
						SuperPermissionHandler.setupPlayer(player);
					}
				}
			}
		};
	}

	public Map<String, String> entries = new HashMap<String, String>();
	
	public boolean check(Player player) {
		String cWorld = player.getWorld().getName();
		String lWorld;
		if(entries.containsKey(player.getName())) {
			lWorld = entries.get(player.getName());
			return lWorld.equals(cWorld);
		} else {
			entries.put(player.getName(), player.getWorld().getName());
			return false;
		}
	}
	
	public Runnable getChecker() {
		return run;
	}
	
}
