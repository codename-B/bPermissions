package de.bananaco.permissions.fornoobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class CommandSuggestions {

	public static Map<String, Object> pluginCommands = new HashMap<String, Object>();

	public static void calculateSimilarCommands(CommandSender sender,
			String message) {

		Map<String, Integer> distanceCmds = new HashMap<String, Integer>();

		String split = message;
		if (split.charAt(0) == '/')
			split = split.replaceFirst("/", "");
		split = split.split(" ")[0];

		if ((Bukkit.getServer().getPluginCommand(split) != null)) {

			if (CommandSuggestions.pluginCommands.size() == 0)
				CommandSuggestions.grabDefinedCommands();

			for (String cmd : CommandSuggestions.pluginCommands.keySet()) {
				int distance = LevenshteinImpl.distance(split, cmd);
				if (distance < 3) {
					distanceCmds.put(cmd, distance);
				}
			}

			HashMap<String, Object> map = new LinkedHashMap<String, Object>();
			List<String> MapKeys = new ArrayList<String>(distanceCmds.keySet());
			List<Integer> MapValues = new ArrayList<Integer>(
					distanceCmds.values());
			TreeSet<Integer> sortedSet = new TreeSet<Integer>(MapValues);
			Object[] sortedArray = sortedSet.toArray();
			int size = sortedArray.length;

			for (int i = 0; i < size; i++) {
				map.put(MapKeys.get(MapValues.indexOf(sortedArray[i])),
						sortedArray[i]);
			}

			Set<String> ref = map.keySet();
			Iterator<String> it = ref.iterator();

			if (ref.size() > 0) {

				StringBuilder sb = new StringBuilder();
				sb.append(ChatColor.AQUA + "Did you mean: "
						+ ChatColor.DARK_AQUA);

				int itc = 0;
				while (it.hasNext()) {
					itc++;
					String command = (String) it.next();
					sb.append("/" + command);
					if ((ref.size() - itc == ref.size() - 1) && ref.size() > 1)
						sb.append(" or ");
					else if (ref.size() - itc != 0)
						sb.append(", ");
				}

				sender.sendMessage(sb.toString());

			}
		}
	}

	public static void calculateSimilarCommands(
			PlayerCommandPreprocessEvent event) {

		if (event.isCancelled())
			return;

		Map<String, Integer> distanceCmds = new HashMap<String, Integer>();

		String split = event.getMessage();
		if (split.charAt(0) == '/')
			split = split.replaceFirst("/", "");
		split = split.split(" ")[0];

		if ((Bukkit.getServer().getPluginCommand(split) == null)) {

			if (CommandSuggestions.pluginCommands.size() == 0)
				CommandSuggestions.grabDefinedCommands();

			for (String cmd : CommandSuggestions.pluginCommands.keySet()) {
				int distance = LevenshteinImpl.distance(split, cmd);
				if (distance < 3) {
					distanceCmds.put(cmd, distance);
				}
			}

			HashMap<String, Object> map = new LinkedHashMap<String, Object>();
			List<String> MapKeys = new ArrayList<String>(distanceCmds.keySet());
			List<Integer> MapValues = new ArrayList<Integer>(
					distanceCmds.values());
			TreeSet<Integer> sortedSet = new TreeSet<Integer>(MapValues);
			Object[] sortedArray = sortedSet.toArray();
			int size = sortedArray.length;

			for (int i = 0; i < size; i++) {
				map.put(MapKeys.get(MapValues.indexOf(sortedArray[i])),
						sortedArray[i]);
			}

			Set<String> ref = map.keySet();
			Iterator<String> it = ref.iterator();

			if (ref.size() > 0) {

				StringBuilder sb = new StringBuilder();
				sb.append(ChatColor.AQUA + "Did you mean: "
						+ ChatColor.DARK_AQUA);

				int itc = 0;
				while (it.hasNext()) {
					itc++;
					String command = (String) it.next();
					sb.append("/" + command);
					if ((ref.size() - itc == ref.size() - 1) && ref.size() > 1)
						sb.append(" or ");
					else if (ref.size() - itc != 0)
						sb.append(", ");
				}

				event.getPlayer().sendMessage(sb.toString());

			}
		}
	}

	public static void grabDefinedCommands() {

		Map<String, Object> allCmdList = new HashMap<String, Object>();

		for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (pl.isEnabled() && pl.getDescription().getCommands() != null) {
				try {
					@SuppressWarnings("unchecked")
					Map<String, Object> cmdMap = (Map<String, Object>) pl
							.getDescription().getCommands();
					allCmdList.putAll(cmdMap);
				} catch (Exception e) {
					continue; // Just to be on the safe side
				}
			}
		}

		// System.out.println("[DYM] Loaded "+allCmdList.size()+" commands");

		CommandSuggestions.pluginCommands = allCmdList;
	}

}