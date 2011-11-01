package de.bananaco.permissions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.iplock.IpLock;

public class PermissionsPlayerListener extends PlayerListener {

	private final Permissions permissions;

	public PermissionsPlayerListener(Permissions permissions) {
		this.permissions = permissions;
	}

	public boolean can(Player player) {
		return (player.hasPermission("bPermissions.build")
				|| player.hasPermission("bPermissions.admin") || player.isOp());
	}

	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		InfoReader info = Permissions.getInfoReader();
		String prefix = info.getPrefix(player), suffix = info.getSuffix(player);

		for (int i = 0; i < ChatColor.values().length; i++) {
			if (prefix.contains("&" + i)) {
				prefix = prefix.replaceAll("&" + i, ChatColor.getByCode(i)
						.toString());
			}
			if (suffix.contains("&" + i)) {
				suffix = suffix.replaceAll("&" + i, ChatColor.getByCode(i)
						.toString());
			}
		}

		String pr = " ", su = " ";
		ChatColor pre = ChatColor.GRAY;
		if (player.hasPermission("bPermissions.build"))
			pre = ChatColor.WHITE;
		if (player.hasPermission("bPermissions.admin"))
			pre = ChatColor.GOLD;
		if (prefix.equals(""))
			pr = "";
		if (suffix.equals(""))
			su = "";
		ChatColor aft = ChatColor.WHITE;

		String message = event.getMessage();
		if (message.toLowerCase().contains("codename_b"))
			message = message.replaceAll("codename_[bB]", "Banana-King");
		String format = prefix + pr + pre + player.getName() + aft + su
				+ suffix + ChatColor.GREEN + " >> " + message;
		format = format.replaceAll("%", "%%");
		event.setFormat(format);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!can(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!can(event.getPlayer()))
			event.setCancelled(true);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!permissions.isEnabled())
			return;
		if (event.getPlayer() == null)
			return;
		if (!permissions.useIpLock)
			return;

		Player player = event.getPlayer();
		if (player.hasPermission("bPermissions.iplock.lock")) {
			IpLock iplock = permissions.iplock;
			if (iplock.hasEntry(player)) {
				if (iplock.isIpLocked(player)) {
					player.sendMessage("Please login before you are kicked!");
					iplock.startTimeout(player);
				}
			} else {
				player.sendMessage("Please set a password!");
			}
		}
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		if (!permissions.isEnabled())
			return;
		if (event.getPlayer() == null)
			return;
		if (event.getPlayer().getLocation() == null)
			return;
		PermissionSet ps = permissions.pm.getPermissionSet(event.getPlayer()
				.getLocation().getWorld());
		SuperPermissionHandler.setupPlayer(event.getPlayer(),
				ps.getPlayerNodes(event.getPlayer()));
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!permissions.isEnabled())
			return;
		if (event.isCancelled())
			return;
		if (event.getPlayer() == null)
			return;
		if (event.getTo() == null)
			return;
		if (event.getFrom().getWorld() == event.getTo().getWorld())
			return;
		PermissionSet ps = permissions.pm.getPermissionSet(event.getTo()
				.getWorld());
		SuperPermissionHandler.setupPlayer(event.getPlayer(),
				ps.getPlayerNodes(event.getPlayer()));
	}
}