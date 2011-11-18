package de.bananaco.permissions;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import de.bananaco.permissions.worlds.WorldPermissionsManager;
/**
 * Time to check the worlds with our
 * new functions ;) what fun!
 * @author codename_B
 *
 */
public class PermissionsRunnable extends Thread {

	Server server = Bukkit.getServer();
	WorldPermissionsManager wpm = Permissions.getWorldPermissionsManager();
	boolean isRunning = true;
	
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	@Override
	public void run() {
		while(isRunning) {
			// Here we go loopedeyloo!
			Player[] players = server.getOnlinePlayers();
				for(Player player : players) {
					if(!wpm.isCorrect(player)) {
						SuperPermissionHandler.setupPlayer(player);
					}
				}
			try {
				sleep(20);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}

}
