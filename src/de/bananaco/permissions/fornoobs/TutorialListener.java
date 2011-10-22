package de.bananaco.permissions.fornoobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TutorialListener extends PlayerListener {
	private final Map<String, Integer> players;
	private final Set<String> playerlist;
	
	public TutorialListener(JavaPlugin plugin) {
		players = new HashMap<String, Integer>();
		playerlist = new HashSet<String>();
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this, Priority.Normal, plugin);
	}
	
	@Override
	public abstract void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event);
	
	public boolean isEnabled(Player player) {
		return playerlist.contains(player.getName());
	}
	
	public void enable(Player player) {
		playerlist.add(player.getName());
	}
	
	public void disable(Player player) {
		playerlist.remove(player.getName());
	}
	
	public int getStage(Player player) {
		if(players.containsKey(player.getName()))
			return players.get(player.getName());
		return 0;
	}
	
	public void incrementStage(Player player) {
		players.put(player.getName(), (getStage(player)+1));
	}
	
	public void decrementStage(Player player) {
		players.put(player.getName(), (getStage(player)-1));
	}
	
	public void remove(Player player) {
		players.remove(player.getName());
	}
	
}
