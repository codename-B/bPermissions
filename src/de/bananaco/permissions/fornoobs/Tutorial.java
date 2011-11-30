package de.bananaco.permissions.fornoobs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import de.bananaco.permissions.Permissions;

public class Tutorial extends TutorialListener {
	Permissions plugin;

	public Tutorial(Permissions plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	String lastMessage = "";

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("bPermissions.admin") && isEnabled(player)) {
			event.setCancelled(true);
			if (event.getMessage() == lastMessage)
				return;
			lastMessage = event.getMessage();
			System.out.println("Lastmessage:" + lastMessage);
			int stage = this.getStage(player);
			if (stage == 0)
				stageOne(event);
			else if (stage == 1)
				stageTwo(event);
			else if (stage == 2)
				stageThree(event);
			else if (stage == 3)
				stageFour(event);
		}
	}

	public void stageOne(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE
				+ "bPermissions tutorial" + ChatColor.GREEN + "--");
		player.sendMessage(ChatColor.BLUE
				+ "To add a group to a player, try it!");
		player.sendMessage(ChatColor.WHITE + "/permissions "
				+ plugin.globalCommand + " " + plugin.addGroup + " admin "
				+ player.getName());
		this.incrementStage(player);
	}

	public void stageTwo(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event
				.getMessage()
				.toLowerCase()
				.endsWith(
						(plugin.globalCommand + " " + plugin.addGroup
								+ " admin " + player.getName()).toLowerCase())) {
			player.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE
					+ "bPermissions tutorial" + ChatColor.GREEN + "--");
			player.sendMessage(ChatColor.BLUE
					+ "To add a permission node to a group, try it!");
			player.sendMessage(ChatColor.WHITE + "/permissions "
					+ plugin.globalCommand + " " + plugin.addNode
					+ " bPermissions.admin admin");
			this.incrementStage(player);
		} else {
			player.sendMessage(ChatColor.RED + "Try again!");
			this.decrementStage(player);
			stageOne(event);
		}
	}

	public void stageThree(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event
				.getMessage()
				.toLowerCase()
				.endsWith(
						(plugin.globalCommand + " " + plugin.addNode + " bPermissions.admin admin")
								.toLowerCase())) {
			player.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE
					+ "bPermissions tutorial" + ChatColor.GREEN + "--");
			player.sendMessage(ChatColor.BLUE
					+ "If you wanted to remove a permission node from a group for one world only, try it!");
			player.sendMessage(ChatColor.WHITE + "/permissions "
					+ plugin.worldCommand + " " + plugin.removeNode
					+ " bPermissions.build default "
					+ player.getWorld().getName());
			this.incrementStage(player);
		} else {
			player.sendMessage(ChatColor.RED + "Try again!");
			this.decrementStage(player);
			stageTwo(event);
		}
	}

	public void stageFour(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event
				.getMessage()
				.toLowerCase()
				.endsWith(
						(plugin.worldCommand + " " + plugin.removeNode
								+ " bpermissions.build default " + player
								.getWorld().getName()).toLowerCase())) {
			player.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE
					+ "bPermissions tutorial" + ChatColor.GREEN + "--");
			player.sendMessage(ChatColor.BLUE + "Tutorial complete!");
			player.sendMessage(ChatColor.BLUE
					+ "For more help visit the page on BukkitDev.");
			player.sendMessage(ChatColor.BLUE
					+ "http://dev.bukkit.org/server-mods/bpermissions");
			// A little bit of cleanup
			this.disable(player);
			this.remove(player);
		} else {
			player.sendMessage(ChatColor.RED + "Try again!");
			this.decrementStage(player);
		}
	}

}
