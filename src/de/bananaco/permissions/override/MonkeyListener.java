package de.bananaco.permissions.override;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

import de.bananaco.permissions.Permissions;

public class MonkeyListener extends PlayerListener {

	public final Permissions plugin;

	public MonkeyListener(final Permissions plugin) {
		this.plugin = plugin;
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		if (Permissions.useMonkeyPlayer
				&& plugin.overridePlayer
				&& plugin.getServer().getPluginManager().getPlugin("Spout") == null) {
			Player player = event.getPlayer();
			if (!(player instanceof CraftPlayer)) {
				System.err.println("Player is not an instance of CraftPlayer! "
						+ player.getName());
				return;
			}
			MonkeyPlayer newPlayer = new MonkeyPlayer((CraftPlayer) player);
			try {
				Permissions.entity_bukkitEntity.set(newPlayer.getHandle(),
						newPlayer);
			} catch (IllegalArgumentException e) {
				System.err
						.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err
						.println("Error while attempting to replace CraftPlayer with MonkeyPlayer");
				e.printStackTrace();
			}
		}
	}
}
