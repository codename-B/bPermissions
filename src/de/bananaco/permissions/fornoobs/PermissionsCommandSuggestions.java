package de.bananaco.permissions.fornoobs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.bananaco.permissions.Permissions;

public class PermissionsCommandSuggestions {

	private static Set<String> commands = null;
	private static Set<String> listCommands = null;
	private static Set<String> worldCommand = null;
	private static Set<String> worldCommands = null;

	public static boolean contains(String[] strings, Set<String> set) {
		for (String s : set)
			for (String string : strings)
				if (s.equalsIgnoreCase(string))
					return true;
		return false;
	}

	public static void grabCommands() {
		commands = Permissions.getCommands();
	}

	public static void grabListCommands() {
		listCommands = Permissions.getListCommands();
	}

	public static void grabWorldCommand() {
		worldCommand = new HashSet<String>();
		worldCommand.add(Permissions.getWorldCommand());
	}

	public static void grabWorldCommands() {
		worldCommands = Permissions.getWorldCommands();
	}

	public static String suggestSimilarCommands(CommandSender sender,
			String[] args, String label) {
		if (commands == null)
			grabCommands();
		if (worldCommands == null)
			grabWorldCommands();
		if (listCommands == null)
			grabListCommands();
		if (worldCommand == null)
			grabWorldCommand();

		int length = 4;

		String[] nargs = new String[length];

		StringBuilder sb = new StringBuilder().append("/" + label + " ");
		for (int i = 0; i < args.length; i++) {
			String c = args[i];
			if (i <= 1) {
				int dis = 0;
				Set<String> com;
				if (i == 0)
					com = worldCommands;
				else
					com = commands;

				for (String d : com) {
					int dist = 100 - LevenshteinImpl.distance(c, d);

					if (dist > dis) {
						dis = dist;
						nargs[i] = d;
					}
				}
			}
		}
		if (contains(nargs, listCommands)) {
			length = 3;
		}
		if (contains(nargs, worldCommand)) {
			length = length + 1;
		}

		String[] targs = new String[length];
		for (int i = 0; i < length; i++)
			if (i < nargs.length)
				targs[i] = nargs[i];
			else
				targs[i] = "[value]";

		for (String c : targs) {
			if (c == null)
				c = "[value]";
			sb.append(c).append(" ");
		}
		return sb.toString();
	}

}
