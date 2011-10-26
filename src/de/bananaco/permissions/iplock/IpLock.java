package de.bananaco.permissions.iplock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import de.bananaco.permissions.oldschool.Configuration;
import de.bananaco.permissions.Permissions;

public class IpLock {

	private Configuration c;
	public int loginTimeout;
	private Permissions p;
	public HashSet<String> kickPlayers;

	public IpLock(Permissions p) {
		this.kickPlayers = new HashSet<String>();
		p.getServer()
				.getPluginManager()
				.addPermission(
						new Permission("bPermissions.iplock.lock",
								PermissionDefault.OP));
		this.p = p;
		this.c = new Configuration(new File("plugins/bPermissions/iplock.yml"));
		loadConfig();
	}

	public void loadConfig() {
		c.load();
		loginTimeout = c.getInt("login-timeout", 5);
		c.setProperty("login-timeout", loginTimeout);
		c.save();
	}

	public void startTimeout(final Player player) {
		kickPlayers.add(player.getName());
		p.getServer().getScheduler()
				.scheduleAsyncDelayedTask(p, new Runnable() {
					public void run() {
						if (player != null) {
							if (kickPlayers.contains(player.getName()))
								player.kickPlayer("Login faster!");
						}
					}
				}, loginTimeout * 20);
	}

	public void stopTimeout(Player player) {
		if (kickPlayers.contains(player.getName()))
			kickPlayers.remove(player.getName());
	}

	public void createEntry(Player player, String password) {
		List<String> ips = new ArrayList<String>();
		ips.add(player.getAddress().getHostName());

		c.setProperty("data." + player.getName() + ".ips", ips);
		c.setProperty("data." + player.getName() + ".hash", password.hashCode());
		c.save();
	}

	public void addIp(Player player) {
		if (!hasEntry(player))
			return;

		List<String> ips = c.getStringList("data." + player.getName() + ".ips",
				new ArrayList<String>());
		if (ips.contains(player.getAddress().getHostName()))
			return;

		ips.add(player.getAddress().getHostName());
		c.setProperty("data." + player.getName() + ".ips", ips);
		c.save();
	}

	public boolean isPassword(Player player, String password) {
		if (!hasEntry(player))
			return false;

		int hash = c.getInt("data." + player.getName() + ".hash", 0);
		int phash = password.hashCode();
		if (hash == phash)
			return true;
		return false;
	}

	public boolean isIpLocked(Player player) {
		if (!hasEntry(player))
			return false;

		List<String> ips = c.getStringList("data." + player.getName() + ".ips",
				new ArrayList<String>());
		if (ips.contains(player.getAddress().getHostName()))
			return false;
		return true;
	}

	public boolean hasEntry(Player player) {
		List<String> keys = c.getKeys("data." + player.getName());
		if (keys == null || keys.size() == 0)
			return false;
		return true;
	}

}
