package de.bananaco.permissions.fornoobs;

import java.util.Set;

import org.bukkit.command.CommandSender;

import de.bananaco.permissions.Permissions;

public class PermissionsCommandSuggestions {
	
	private static Set<String> commands = null;
	private static Set<String> worldCommands = null;
	private static Set<String> listCommands = null;
	
	public static void grabCommands() {
		commands = Permissions.getCommands();
	}
	
	public static void grabWorldCommands() {
		worldCommands = Permissions.getWorldCommands();
	}
	
	public static void grabListCommands() {
		listCommands = Permissions.getListCommands();
	}
	
	public static String suggestSimilarCommands(CommandSender sender, String[] args, String label) {
		if(commands == null)
			grabCommands();
		if(worldCommands == null)
			grabWorldCommands();
		if(listCommands == null)
			grabListCommands();
		
		int length = 4;
		if(args.length>length)
			length = 5;
		
		String[] nargs = new String[length];
		
		StringBuilder sb = new StringBuilder().append("/"+label+" ");
		for(int i=0; i<args.length; i++) {
			String c = args[i];
			if(i<=1) {
			int dis = 0;
			Set<String> com;
			if(i==0)
				com = worldCommands;
			else
				com = commands;
			
			for(String d : com) {
			int dist = 100-LevenshteinImpl.distance(c, d);
			System.out.println(d+":"+dist);
			if(dist>dis) {
				dis = dist;
				nargs[i] = d;
			}
			}
		}
		}
		if(contains(nargs, listCommands)) {
			length = 3;
		}
		String[] targs = new String[length];
		for(int i=0; i<targs.length; i++)
			if(nargs[i] != null)
			targs[i] = nargs[i];
			else
			targs[i] = "[value]";
		
		for(String c : targs) {
			sb.append(c).append(" ");
		}
		return sb.toString();
	}
	
	public static boolean contains(String[] strings, Set<String> set) {
		for(String s : set)
			for(String string : strings)
			if(s.equalsIgnoreCase(string))
				return true;
		return false;
	}

}
