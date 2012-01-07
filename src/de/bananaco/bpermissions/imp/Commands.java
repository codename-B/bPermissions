package de.bananaco.bpermissions.imp;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class Commands {
	
	private final CommandSender sender;
	private final WorldManager instance = WorldManager.getInstance();
	
	private World world = null;
	private Calculable calc = null;
	
	protected Commands(CommandSender player) {
		this.sender = player;
	}
	
	protected String format(String message) {
		return Permissions.format(message);
	}
	
	public void setWorld(String w) {
		World world = instance.getWorld(w);
		// If the world does not exist
		if(world == null) {
			sender.sendMessage(format("Please select a loaded world!"));
			return;
		}
		// If a different world is selected
		if(!world.equals(this.world))
			calc = null;
		
		this.world = world;
		sender.sendMessage(format("Set selected world to "+world.getName()));
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setCalculable(CalculableType type, String c) {
		// If the world does not exist
		if(world == null) {
			sender.sendMessage(format("Please select a loaded world!"));
			return;
		}
		calc = world.get(c, type);
		sender.sendMessage(format(calc.getName()+" selected."));
	}
	
	public Calculable getCalculable() {
		return calc;
	}
	
	/*
	 * Main functions go here
	 */
	
	public void addGroup(String group) {
		calc.addGroup(group);
		sender.sendMessage(format("Added "+group+" to "+calc.getName()));
	}
	
	public void removeGroup(String group) {
		calc.removeGroup(group);
		sender.sendMessage(format("Removed "+group+" from "+calc.getName()));
	}
	
	public void setGroup(String group) {
		calc.getGroupsAsString().clear();
		calc.addGroup(group);
		sender.sendMessage(format("Set "+calc.getName()+"'s group to "+group));
	}
	
	public void listGroups() {
		List<String> groups = calc.serialiseGroups();
		String[] gr = groups.toArray(new String[groups.size()]);
		String mgr = Arrays.toString(gr);
		sender.sendMessage(format("The "+calc.getType().getName()+" "+calc.getName()+" has these groups:"));
		sender.sendMessage(mgr);
	}
	
	public void addPermission(String permission) {
		Permission perm = Permission.loadFromString(permission);
		calc.addPermission(perm.name(), perm.isTrue());
		sender.sendMessage(format("Added "+perm.toString()+" to "+calc.getName()));
	}
	
	public void removePermission(String permission) {
		calc.removePermission(permission);
		sender.sendMessage(format("Removed "+permission+" from "+calc.getName()));
	}
	
	public void listPermissions() {
		List<String> permissions = calc.serialisePermissions();
		String[] pr = permissions.toArray(new String[permissions.size()]);
		String mpr = Arrays.toString(pr);
		sender.sendMessage(format("The "+calc.getType().getName()+" "+calc.getName()+" has these permissions:"));
		sender.sendMessage(mpr);
	}
	
	public void hasPermission(String node) {
		// You can't do this on groups
		if(calc.getType() != CalculableType.USER) {
			sender.sendMessage(format("This check is only valid on users."));
			return;
		}
		User user = (User) calc;
		sender.sendMessage(format(user.getName() + " - " + node+ ": " +user.hasPermission(node)));
	}
	
	public void setValue(String key, String value) {
		calc.setValue(key, value);
		sender.sendMessage(format(key + " set to " + value + " for " + calc.getName()));
	}
	
	public void showValue(String key) {
		String value = calc.getValue(key);
		sender.sendMessage(format("Meta for "+calc.getType().getName()+" "+calc.getName()+" - "+key+": "+value));
	}
	
	/**
	 * Remind the user to save when changes are finished!
	 */
	public void save() {
		// If the world does not exist
		if(world == null) {
			sender.sendMessage(format("Please select a loaded world!"));
			return;
		}
		// Otherwise do your thang
		try {
		world.save();
		if(calc != null) {
		calc.calculateEffectivePermissions();
		calc.calculateEffectiveMeta();
		}
		sender.sendMessage(format("Saved!"));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}