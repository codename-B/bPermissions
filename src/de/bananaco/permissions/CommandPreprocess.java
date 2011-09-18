package de.bananaco.permissions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandPreprocess extends PlayerListener {
	private final Permissions permissions;
	CommandPreprocess(Permissions permissions) {
		this.permissions = permissions;
		permissions.getServer().getPluginManager().registerEvent(Event.Type.SERVER_COMMAND, new ExtraCommandPreprocess(permissions), Priority.Normal, permissions);
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		message = message.replace("/", "");
		String command = message.split(" ")[0];
		if((command.equalsIgnoreCase(permissions.addGroup))
		||(command.equalsIgnoreCase(permissions.setGroup))
			||(command.equalsIgnoreCase(permissions.removeGroup))
				||(command.equalsIgnoreCase(permissions.listGroup))
					||(command.equalsIgnoreCase(permissions.addNode))
						||(command.equalsIgnoreCase(permissions.removeNode))
							||(command.equalsIgnoreCase(permissions.listNode))) {
			event.setCancelled(true);
			player.chat("/p global "+message);
		}
	
	}

}
class ExtraCommandPreprocess extends ServerListener {
	private final Permissions permissions;
	ExtraCommandPreprocess(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public void onServerCommand(ServerCommandEvent event) {
		String message = event.getCommand();
		String command = message.split(" ")[0];
		if((command.equalsIgnoreCase(permissions.addGroup))
		||(command.equalsIgnoreCase(permissions.setGroup))
			||(command.equalsIgnoreCase(permissions.removeGroup))
				||(command.equalsIgnoreCase(permissions.listGroup))
					||(command.equalsIgnoreCase(permissions.addNode))
						||(command.equalsIgnoreCase(permissions.removeNode))
							||(command.equalsIgnoreCase(permissions.listNode))) {
			event.setCommand("p global "+message);
		}
	}
}