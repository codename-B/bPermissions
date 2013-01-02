package de.bananaco.bpermissions.imp;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.bananaco.bpermissions.api.Calculable;
import de.bananaco.bpermissions.api.CalculableType;
import de.bananaco.bpermissions.api.World;

public class Debugger {
	
	private static Debugger instance = new Debugger();
	private boolean debug = false;
	
	public static boolean setDebug(boolean debug) {
		instance.debug = debug;
		return debug;
	}
	
	public static boolean getDebug() {
		return instance.debug;
	}
	
	public static void log(String message) {
		if(instance.debug)
			System.out.println("[debug] "+message);
	}
	
	public static void log(World world) {
		if(world == null) {
			log("No world by that name");
			return;
		}
		Set<Calculable> groups = world.getAll(CalculableType.GROUP);
		Set<Calculable> users = world.getAll(CalculableType.USER);
		log("World: "+world.getName());
		log(groups.size()+" groups and "+users.size()+" users");
		
		log("** PRINTING GROUP DEBUGGING INFO **");

		for(Calculable group : groups) {
			List<String> gs = group.serialiseGroups();
			String[] ga = gs.toArray(new String[gs.size()]);
			List<String> ps = group.serialisePermissions();
			String[] pa = ps.toArray(new String[ps.size()]);
			// Define the variables
			String name = group.getName();
			String grp = Arrays.toString(ga);
			String pss = Arrays.toString(pa);
			// Print the info
			log("Printing info for "+name);
			log("Permissions");
			log(pss);
			log("Groups");
			log(grp);
		}

		log("** PRINTING USER DEBUGGING INFO **");
		
		for(Calculable user : users) {
			List<String> gs = user.serialiseGroups();
			String[] ga = gs.toArray(new String[gs.size()]);
			List<String> ps = user.serialisePermissions();
			String[] pa = ps.toArray(new String[ps.size()]);
			// Define the variables
			String name = user.getName();
			String grp = Arrays.toString(ga);
			String pss = Arrays.toString(pa);
			// Print the info
			log("Printing info for "+name);
			log("Permissions");
			log(pss);
			log("Groups");
			log(grp);
		}
	}

}
